package io.gitlab.arturbosch.detekt.rules

import io.gitlab.arturbosch.detekt.api.BaseRule
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.MultiRule
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.RuleSetProvider
import io.gitlab.arturbosch.detekt.rules.providers.CommentSmellProvider
import io.gitlab.arturbosch.detekt.rules.providers.ComplexityProvider
import io.gitlab.arturbosch.detekt.rules.providers.EmptyCodeProvider
import io.gitlab.arturbosch.detekt.rules.providers.ExceptionsProvider
import io.gitlab.arturbosch.detekt.rules.providers.NamingProvider
import io.gitlab.arturbosch.detekt.rules.providers.PerformanceProvider
import io.gitlab.arturbosch.detekt.rules.providers.PotentialBugProvider
import io.gitlab.arturbosch.detekt.rules.providers.StyleGuideProvider
import java.lang.reflect.Modifier
import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.it
import org.reflections.Reflections

class RuleProviderTest : Spek({

    it("commentSmellProvider") {
        RuleProviderAssert(
                CommentSmellProvider(),
                "io.gitlab.arturbosch.detekt.rules.documentation",
                Rule::class.java)
                .assert()
    }

    it("complexityProvider") {
        RuleProviderAssert(
                ComplexityProvider(),
                "io.gitlab.arturbosch.detekt.rules.complexity",
                Rule::class.java)
                .assert()
    }

    it("emptyCodeProvider") {
        RuleProviderAssert(
                EmptyCodeProvider(),
                "io.gitlab.arturbosch.detekt.rules.empty",
                Rule::class.java)
                .assert()
    }

    it("exceptionsProvider") {
        RuleProviderAssert(
                ExceptionsProvider(),
                "io.gitlab.arturbosch.detekt.rules.exceptions",
                Rule::class.java)
                .assert()
    }

    it("namingProvider") {
        RuleProviderAssert(
                NamingProvider(),
                "io.gitlab.arturbosch.detekt.rules.naming",
                Rule::class.java)
                .assert()
    }

    it("performanceProvider") {
        RuleProviderAssert(
                PerformanceProvider(),
                "io.gitlab.arturbosch.detekt.rules.performance",
                Rule::class.java)
                .assert()
    }

    it("potentialBugProvider") {
        RuleProviderAssert(
                PotentialBugProvider(),
                "io.gitlab.arturbosch.detekt.rules.bugs",
                Rule::class.java)
                .assert()
    }

    it("styleGuideProvider") {
        RuleProviderAssert(
                StyleGuideProvider(),
                "io.gitlab.arturbosch.detekt.rules.style",
                Rule::class.java)
                .assert()
    }
})

private class RuleProviderAssert<T>(
    private val provider: RuleSetProvider,
    private val packageName: String,
    private val clazz: Class<T>
) {

    fun assert() {
        val rules = getRules(provider)
        assertThat(rules).isNotEmpty
        val classes = getClasses()
        assertThat(classes).isNotEmpty
        classes
                .map { c -> rules.singleOrNull { it.javaClass.simpleName == c.simpleName } }
                .forEach {
                    if (it == null) {
                        print(rules.size); println(" rules")
                        print(classes.size); print(" classes")
                    }
                    assertThat(it).isNotNull()
                }
    }

    private fun getRules(provider: RuleSetProvider): List<BaseRule> {
        return provider.buildRuleset(Config.empty)!!.rules
                .flatMap { (it as? MultiRule)?.rules ?: listOf(it) }
    }

    private fun getClasses(): List<Class<out T>> {
        return Reflections(packageName)
                .getSubTypesOf(clazz)
                .filterNot { "Test" in it.name }
                .filter { !Modifier.isAbstract(it.modifiers) && !Modifier.isStatic(it.modifiers) }
    }
}
