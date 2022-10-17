package io.gitlab.arturbosch.detekt.rules

import io.gitlab.arturbosch.detekt.api.BaseRule
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.RuleSetProvider
import io.gitlab.arturbosch.detekt.api.internal.DefaultRuleSetProvider
import io.gitlab.arturbosch.detekt.rules.bugs.PotentialBugProvider
import io.gitlab.arturbosch.detekt.rules.complexity.ComplexityProvider
import io.gitlab.arturbosch.detekt.rules.coroutines.CoroutinesProvider
import io.gitlab.arturbosch.detekt.rules.documentation.CommentSmellProvider
import io.gitlab.arturbosch.detekt.rules.empty.EmptyCodeProvider
import io.gitlab.arturbosch.detekt.rules.exceptions.ExceptionsProvider
import io.gitlab.arturbosch.detekt.rules.naming.NamingProvider
import io.gitlab.arturbosch.detekt.rules.performance.PerformanceProvider
import io.gitlab.arturbosch.detekt.rules.style.StyleGuideProvider
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.reflections.Reflections
import java.lang.reflect.Modifier

class RuleProviderSpec {

    @Test
    fun `checks whether all rules are called in the corresponding RuleSetProvider`() {
        val reflections = Reflections("io.gitlab.arturbosch.detekt.rules")
        val providers = reflections.getSubTypesOf(DefaultRuleSetProvider::class.java)
        providers.forEach { providerType ->
            val packageName = getRulesPackageNameForProvider(providerType)
            val provider = providerType.getDeclaredConstructor().newInstance()
            val rules = getRules(provider)
            val classes = getClasses(packageName)
            classes.forEach { clazz ->
                val rule = rules.singleOrNull { it.javaClass.simpleName == clazz.simpleName }
                assertThat(rule).withFailMessage(
                    "Rule $clazz is not called in the corresponding RuleSetProvider $providerType"
                ).isNotNull()
            }
        }
    }
}

private val ruleMap: Map<Class<out DefaultRuleSetProvider>, String> = mapOf(
    CommentSmellProvider::class.java to "io.gitlab.arturbosch.detekt.rules.documentation",
    ComplexityProvider::class.java to "io.gitlab.arturbosch.detekt.rules.complexity",
    EmptyCodeProvider::class.java to "io.gitlab.arturbosch.detekt.rules.empty",
    ExceptionsProvider::class.java to "io.gitlab.arturbosch.detekt.rules.exceptions",
    NamingProvider::class.java to "io.gitlab.arturbosch.detekt.rules.naming",
    PerformanceProvider::class.java to "io.gitlab.arturbosch.detekt.rules.performance",
    PotentialBugProvider::class.java to "io.gitlab.arturbosch.detekt.rules.bugs",
    StyleGuideProvider::class.java to "io.gitlab.arturbosch.detekt.rules.style",
    CoroutinesProvider::class.java to "io.gitlab.arturbosch.detekt.rules.coroutines"
)

private fun getRulesPackageNameForProvider(providerType: Class<out RuleSetProvider>): String {
    val packageName = ruleMap[providerType]
    assertThat(packageName)
        .withFailMessage("No rules package for provider of type $providerType was defined in the ruleMap")
        .isNotNull()
    return packageName!!
}

private fun getRules(provider: RuleSetProvider): List<BaseRule> {
    val ruleSet = provider.instance(Config.empty)

    @Suppress("DEPRECATION")
    val rules = ruleSet.rules.flatMap { (it as? io.gitlab.arturbosch.detekt.api.MultiRule)?.rules ?: listOf(it) }
    assertThat(rules).isNotEmpty
    return rules
}

private fun getClasses(packageName: String): List<Class<out Rule>> {
    val classes = Reflections(packageName)
        .getSubTypesOf(Rule::class.java)
        .filterNot { "Test" in it.name }
        .filter { !Modifier.isAbstract(it.modifiers) && !Modifier.isStatic(it.modifiers) }
    assertThat(classes).isNotEmpty
    return classes
}
