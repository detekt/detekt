package dev.detekt.rules

import dev.detekt.api.Config
import dev.detekt.api.Rule
import dev.detekt.api.RuleSetProvider
import dev.detekt.api.internal.DefaultRuleSetProvider
import dev.detekt.rules.comments.CommentSmellProvider
import dev.detekt.rules.complexity.ComplexityProvider
import dev.detekt.rules.coroutines.CoroutinesProvider
import dev.detekt.rules.emptyblocks.EmptyCodeProvider
import dev.detekt.rules.exceptions.ExceptionsProvider
import dev.detekt.rules.naming.NamingProvider
import dev.detekt.rules.performance.PerformanceProvider
import dev.detekt.rules.potentialbugs.PotentialBugProvider
import dev.detekt.rules.standardlibrary.StandardLibraryProvider
import dev.detekt.rules.style.StyleGuideProvider
import io.github.classgraph.ClassGraph
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.lang.reflect.Modifier

class RuleProviderSpec {

    @Test
    fun `checks whether all rules are called in the corresponding RuleSetProvider`() {
        val providers = ClassGraph()
            .acceptPackages("dev.detekt.rules")
            .scan()
            .use { scanResult ->
                scanResult.getClassesImplementing(DefaultRuleSetProvider::class.java)
                    .loadClasses(DefaultRuleSetProvider::class.java)
            }

        assertThat(providers).hasSize(ruleMap.size)

        providers.forEach { providerType ->
            val packageName = getRulesPackageNameForProvider(providerType)
            val provider = providerType.getDeclaredConstructor().newInstance()
            val rules = getRules(provider)
            val classes = getClasses(packageName)

            assertThat(packageName).matches(getPackageName(providerType))
            assertThat(classes).withFailMessage("No rules found in $providerType").isNotEmpty

            classes.forEach { clazz ->
                val rule = rules.singleOrNull { it.javaClass.simpleName == clazz.simpleName }
                assertThat(rule)
                    .withFailMessage("Rule $clazz is not called in the corresponding RuleSetProvider $providerType")
                    .isNotNull()
            }
        }
    }
}

private val ruleMap: Map<Class<out DefaultRuleSetProvider>, String> = mapOf(
    CommentSmellProvider::class.java to "dev.detekt.rules.comments",
    ComplexityProvider::class.java to "dev.detekt.rules.complexity",
    EmptyCodeProvider::class.java to "dev.detekt.rules.emptyblocks",
    ExceptionsProvider::class.java to "dev.detekt.rules.exceptions",
    NamingProvider::class.java to "dev.detekt.rules.naming",
    PerformanceProvider::class.java to "dev.detekt.rules.performance",
    PotentialBugProvider::class.java to "dev.detekt.rules.potentialbugs",
    StandardLibraryProvider::class.java to "dev.detekt.rules.standardlibrary",
    StyleGuideProvider::class.java to "dev.detekt.rules.style",
    CoroutinesProvider::class.java to "dev.detekt.rules.coroutines",
)

private fun getRulesPackageNameForProvider(providerType: Class<out RuleSetProvider>): String {
    val packageName = ruleMap[providerType]
    assertThat(packageName)
        .withFailMessage("No rules package for provider of type $providerType was defined in the ruleMap")
        .isNotNull()
    return packageName!!
}

private fun getRules(ruleSetProvider: RuleSetProvider): List<Rule> {
    val ruleSet = ruleSetProvider.instance()
    val rules = ruleSet.rules.map { (_, provider) -> provider(Config.empty) }
    assertThat(rules).isNotEmpty
    return rules
}

private fun getClasses(packageName: String): List<Class<out Rule>> =
    ClassGraph()
        .acceptPackages(packageName)
        .scan()
        .use { scanResult ->
            scanResult.getSubclasses(Rule::class.java)
                .loadClasses(Rule::class.java)
                .filterNot { "Test" in it.name }
                .filter { !Modifier.isAbstract(it.modifiers) && !Modifier.isStatic(it.modifiers) }
        }

private fun getPackageName(clazz: Class<*>) = clazz.getName().substring(0, clazz.getName().lastIndexOf('.'))
