package io.gitlab.arturbosch.detekt.sonar.rules

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.RuleSetProvider
import io.gitlab.arturbosch.detekt.api.YamlConfig
import io.gitlab.arturbosch.detekt.cli.ClasspathResourceConverter
import io.gitlab.arturbosch.detekt.cli.DEFAULT_CONFIG
import io.gitlab.arturbosch.detekt.sonar.foundation.DETEKT_REPOSITORY
import io.gitlab.arturbosch.detekt.sonar.foundation.LOG
import org.sonar.api.rule.RuleKey
import java.util.ServiceLoader
import org.sonar.api.rule.Severity as SonarSeverity

private val CONFIG = YamlConfig.loadResource(ClasspathResourceConverter().convert(DEFAULT_CONFIG)).apply {
	LOG.info(this.toString())
}

val ALL_LOADED_RULES = ServiceLoader.load(RuleSetProvider::class.java,
		Config::javaClass.javaClass.classLoader)
		.asIterable()
		.flatMap { ruleSet ->
			val subConfig = CONFIG.subConfig(ruleSet.ruleSetId)
			ruleSet.instance(subConfig).rules
		}

val RULE_KEYS = ALL_LOADED_RULES.map { defineRuleKey(it) }

data class DetektRuleKey(val repositoryKey: String,
						 val ruleKey: String,
						 val active: Boolean) : RuleKey(repositoryKey, ruleKey)

private fun defineRuleKey(rule: Rule) = DetektRuleKey(DETEKT_REPOSITORY, rule.id, rule.active)

fun findKey(id: String) = RULE_KEYS.find { it.rule() == id }
