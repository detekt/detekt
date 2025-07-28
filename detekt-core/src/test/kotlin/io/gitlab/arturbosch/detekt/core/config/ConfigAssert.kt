package io.gitlab.arturbosch.detekt.core.config

import io.github.classgraph.ClassGraph
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.RuleName
import io.gitlab.arturbosch.detekt.api.RuleSetProvider
import io.gitlab.arturbosch.detekt.api.internal.DefaultRuleSetProvider
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.fail
import java.lang.reflect.Modifier

class ConfigAssert(
    private val config: Config,
    private val name: String,
    private val packageName: String,
) {
    private val allowedOptions = setOf(
        Config.ACTIVE_KEY,
        Config.EXCLUDES_KEY,
        Config.INCLUDES_KEY,
        Config.AUTO_CORRECT_KEY,
        "android"
    )

    fun assert() {
        val ymlDeclarations = getYmlRuleConfig().properties.filter { it.key !in allowedOptions }
        assertThat(ymlDeclarations).isNotEmpty

        checkRulesDefinedByRuleSetProvider()

        val ruleClasses = getRuleClassesInPackage()
        val foundRulesClassNames = ruleClasses.map { it.simpleName }
        val ruleNamesInConfig = ymlDeclarations.keys
        assertThat(foundRulesClassNames).containsExactlyInAnyOrderElementsOf(ruleNamesInConfig)

        checkRules(ruleClasses, ymlDeclarations)
    }

    private fun checkRules(ruleClasses: List<Class<out Rule>>, ymlDeclarations: Map<String, Any>) {
        for (ruleClass in ruleClasses) {
            val ymlDeclaration = ymlDeclarations.filter { it.key == ruleClass.simpleName }
            if (ymlDeclaration.keys.size != 1) {
                fail<String>("${ruleClass.simpleName} rule is not correctly defined in detekt-default-config.yml")
            }
        }
    }

    private fun checkRulesDefinedByRuleSetProvider() {
        getRulesDefinedByRuleSet().forEach(::verifyIssueIdMatchesName)
    }

    private fun verifyIssueIdMatchesName(rule: Rule) {
        val clazz = rule::class.java
        assertThat(rule.ruleName)
            .withFailMessage { "rule $clazz declares the rule id ${rule.ruleName} instead of ${clazz.simpleName}" }
            .isEqualTo(RuleName(clazz.simpleName))
    }

    private fun getYmlRuleConfig() = config.subConfig(name) as? YamlConfig
        ?: error("yaml config expected but got ${config.javaClass}")

    private fun getRulesDefinedByRuleSet(): List<Rule> =
        getRuleSetProviderInPackageOrNull()
            ?.instance()
            ?.rules
            ?.map { (_, provider) -> provider(Config.empty) }
            .orEmpty()

    private fun getRuleSetProviderInPackageOrNull(): RuleSetProvider? =
        ClassGraph()
            .acceptPackages(packageName)
            .scan()
            .use { scanResult ->
                scanResult.getClassesImplementing(DefaultRuleSetProvider::class.java)
                    .loadClasses(DefaultRuleSetProvider::class.java)
                    .firstOrNull()
                    ?.getDeclaredConstructor()
                    ?.newInstance()
            }

    private fun getRuleClassesInPackage(): List<Class<out Rule>> =
        ClassGraph()
            .acceptPackages(packageName)
            .scan()
            .use { scanResult ->
                scanResult.getSubclasses(Rule::class.java)
                    .loadClasses(Rule::class.java)
                    .filter { rule ->
                        !Modifier.isAbstract(rule.modifiers) &&
                            rule.annotations.none { it.annotationClass == Deprecated::class }
                    }
            }
}
