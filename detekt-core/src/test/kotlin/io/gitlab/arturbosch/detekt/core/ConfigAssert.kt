package io.gitlab.arturbosch.detekt.core

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.RuleSetProvider
import io.gitlab.arturbosch.detekt.api.internal.BaseRule
import io.gitlab.arturbosch.detekt.api.internal.DefaultRuleSetProvider
import io.gitlab.arturbosch.detekt.core.config.DefaultConfig
import io.gitlab.arturbosch.detekt.core.config.YamlConfig
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.fail
import org.reflections.Reflections
import java.lang.reflect.Modifier

class ConfigAssert(
    private val config: Config,
    private val name: String,
    private val packageName: String
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
                fail<String>("${ruleClass.simpleName} rule is not correctly defined in ${DefaultConfig.RESOURCE_NAME}")
            }
        }
    }

    private fun checkRulesDefinedByRuleSetProvider() {
        getRulesDefinedByRuleSet().forEach(::verifyIssueIdMatchesName)
    }

    private fun verifyIssueIdMatchesName(rule: BaseRule) {
        val clazz = rule::class.java
        assertThat(rule.ruleId)
            .withFailMessage { "rule $clazz declares the rule id ${rule.ruleId} instead of ${clazz.simpleName}" }
            .isEqualTo(clazz.simpleName)
    }

    private fun getYmlRuleConfig() = config.subConfig(name) as? YamlConfig
        ?: error("yaml config expected but got ${config.javaClass}")

    private fun getRulesDefinedByRuleSet(): List<BaseRule> {
        return getRuleSetProviderInPackageOrNull()
            ?.instance(Config.empty)
            ?.rules
            ?: emptyList()
    }

    private fun getRuleSetProviderInPackageOrNull(): RuleSetProvider? = Reflections(packageName)
        .getSubTypesOf(DefaultRuleSetProvider::class.java)
        .firstOrNull()
        ?.getDeclaredConstructor()
        ?.newInstance()

    private fun getRuleClassesInPackage(): List<Class<out Rule>> {
        return Reflections(packageName)
            .getSubTypesOf(Rule::class.java)
            .filter { !Modifier.isAbstract(it.modifiers) }
    }
}
