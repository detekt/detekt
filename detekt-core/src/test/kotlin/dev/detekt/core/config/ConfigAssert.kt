package dev.detekt.core.config

import dev.detekt.api.Config
import dev.detekt.api.Rule
import dev.detekt.api.RuleName
import dev.detekt.api.RuleSetProvider
import dev.detekt.api.internal.DefaultRuleSetProvider
import io.github.classgraph.ClassGraph
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.fail

class ConfigAssert(private val config: Config, private val name: String, private val packageName: String) {
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

        val ruleClassNames = getRuleClassNamesInPackage()
        val ruleNamesInConfig = ymlDeclarations.keys
        assertThat(ruleClassNames).containsExactlyInAnyOrderElementsOf(ruleNamesInConfig)

        checkRules(ruleClassNames, ymlDeclarations)
    }

    private fun checkRules(ruleClassNames: List<String>, ymlDeclarations: Map<String, Any>) {
        for (ruleClassName in ruleClassNames) {
            val ymlDeclaration = ymlDeclarations.filter { it.key == ruleClassName }
            if (ymlDeclaration.keys.size != 1) {
                fail<String>("$ruleClassName rule is not correctly defined in detekt-default-config.yml")
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

    private fun getYmlRuleConfig() =
        config.subConfig(name) as? YamlConfig ?: error("yaml config expected but got ${config.javaClass}")

    private fun getRulesDefinedByRuleSet(): List<Rule> =
        getRuleSetProviderInPackageOrNull()
            ?.instance()
            ?.rules
            ?.map { (_, provider) -> provider(Config.empty) }
            .orEmpty()

    private fun getRuleSetProviderInPackageOrNull(): RuleSetProvider? =
        ClassGraph()
            .enableClasspath()
            .enableClassInfo()
            .acceptPackages(packageName)
            .scan()
            .use { scanResult -> scanResult.getAllClassesImplementing(DefaultRuleSetProvider::class.java) }
            .map { classInfo -> Class.forName(classInfo.getName()).getConstructor().newInstance() as? RuleSetProvider }
            .singleOrNull()

    private fun getRuleClassNamesInPackage(): List<String> =
        ClassGraph()
            .enableClasspath()
            .enableClassInfo()
            .enableAnnotationInfo()
            .acceptPackages(packageName)
            .scan()
            .use { scanResult -> scanResult.getAllSubclasses(Rule::class.java) }
            .filter { rule -> !(rule.isAbstract || rule.hasAnnotation(Deprecated::class.java)) }
            .map { it.simpleName }
}
