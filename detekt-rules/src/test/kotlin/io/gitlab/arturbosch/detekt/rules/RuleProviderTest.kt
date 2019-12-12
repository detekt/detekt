package io.gitlab.arturbosch.detekt.rules

import io.gitlab.arturbosch.detekt.api.BaseRule
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.MultiRule
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.RuleSetProvider
import io.gitlab.arturbosch.detekt.rules.providers.CommentSmellProvider
import io.gitlab.arturbosch.detekt.rules.providers.ComplexityProvider
import io.gitlab.arturbosch.detekt.rules.providers.ConcurrencyProvider
import io.gitlab.arturbosch.detekt.rules.providers.EmptyCodeProvider
import io.gitlab.arturbosch.detekt.rules.providers.ExceptionsProvider
import io.gitlab.arturbosch.detekt.rules.providers.NamingProvider
import io.gitlab.arturbosch.detekt.rules.providers.PerformanceProvider
import io.gitlab.arturbosch.detekt.rules.providers.PotentialBugProvider
import io.gitlab.arturbosch.detekt.rules.providers.StyleGuideProvider
import org.assertj.core.api.Assertions.assertThat
import org.reflections.Reflections
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe
import java.lang.reflect.Modifier

class RuleProviderTest : Spek({

    describe("Rule Provider") {

        it("checks whether all rules are called in the corresponding RuleSetProvider") {
            val reflections = Reflections("io.gitlab.arturbosch.detekt.rules.providers")
            val providers = reflections.getSubTypesOf(RuleSetProvider::class.java)
            providers.forEach { providerType ->
                val packageName = getRulesPackageNameForProvider(providerType)
                val provider = providerType.getDeclaredConstructor().newInstance()
                val rules = getRules(provider)
                val classes = getClasses(packageName)
                classes
                    .forEach { clazz ->
                        val rule = rules.singleOrNull { it.javaClass.simpleName == clazz.simpleName }
                        assertThat(rule).withFailMessage(
                            "Rule $clazz is not called in the corresponding RuleSetProvider $providerType")
                            .isNotNull()
                    }
            }
        }
    }
})

private val ruleMap = mapOf<Class<*>, String>(
    Pair(CommentSmellProvider().javaClass, "io.gitlab.arturbosch.detekt.rules.documentation"),
    Pair(ComplexityProvider().javaClass, "io.gitlab.arturbosch.detekt.rules.complexity"),
    Pair(EmptyCodeProvider().javaClass, "io.gitlab.arturbosch.detekt.rules.empty"),
    Pair(ExceptionsProvider().javaClass, "io.gitlab.arturbosch.detekt.rules.exceptions"),
    Pair(NamingProvider().javaClass, "io.gitlab.arturbosch.detekt.rules.naming"),
    Pair(PerformanceProvider().javaClass, "io.gitlab.arturbosch.detekt.rules.performance"),
    Pair(PotentialBugProvider().javaClass, "io.gitlab.arturbosch.detekt.rules.bugs"),
    Pair(StyleGuideProvider().javaClass, "io.gitlab.arturbosch.detekt.rules.style"),
    Pair(ConcurrencyProvider().javaClass, "io.gitlab.arturbosch.detekt.rules.concurrency")
)

private fun getRulesPackageNameForProvider(providerType: Class<out RuleSetProvider>): String {
    val packageName = ruleMap[providerType]
    assertThat(packageName)
        .withFailMessage("No rules package for provider of type $providerType was defined in the ruleMap")
        .isNotNull()
    @Suppress("UnsafeCallOnNullableType")
    return packageName!!
}

private fun getRules(provider: RuleSetProvider): List<BaseRule> {
    @Suppress("UnsafeCallOnNullableType")
    val ruleSet = provider.buildRuleset(Config.empty)!!
    val rules = ruleSet.rules.flatMap { (it as? MultiRule)?.rules ?: listOf(it) }
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
