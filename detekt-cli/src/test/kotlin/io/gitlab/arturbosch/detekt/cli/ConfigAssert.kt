package io.gitlab.arturbosch.detekt.cli

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.YamlConfig
import org.assertj.core.api.Assertions
import org.assertj.core.api.Assertions.assertThat
import org.reflections.Reflections
import java.lang.reflect.Field
import java.lang.reflect.Modifier

class ConfigAssert(
		private val config: Config,
		private val name: String,
		private val packageName: String) {

	fun assert() {
		val ymlDeclarations = getYmlRuleConfig().properties.filter { it.key != "active" }
		assertThat(ymlDeclarations).isNotEmpty
		val ruleClasses = getRuleClasses()
		assertThat(ruleClasses).isNotEmpty
		assertThat(ruleClasses).hasSize(ymlDeclarations.size)

		checkRules(ruleClasses, ymlDeclarations)
	}

	private fun checkRules(ruleClasses: List<Class<out Rule>>, ymlDeclarations: Map<String, Any>) {
		for (ruleClass in ruleClasses) {
			val ymlDeclaration = ymlDeclarations.filter { it.key == ruleClass.simpleName }
			if (ymlDeclaration.keys.size != 1) {
				Assertions.fail("${ruleClass.simpleName} rule is not correctly defined in $CONFIG_FILE")
			}

			@Suppress("UNCHECKED_CAST")
			val options = ymlDeclaration.iterator().next().value as HashMap<String, *>
			checkOptions(options, ruleClass)
		}
	}

	private fun checkOptions(ymlOptions: HashMap<String, *>, ruleClass: Class<out Rule>) {
		val configFields = ruleClass.declaredFields.filter { isPublicStaticFinal(it) && it.name != "Companion" }
		var filter = ymlOptions.filterKeys { it != "active" }
		if (filter.containsKey(THRESHOLD)) {
			assertThat(ruleClass.superclass.simpleName).isEqualTo(THRESHOLD_RULE)
			filter = filter.filterKeys { it != THRESHOLD }
		}
		for (ymlOption in filter) {
			val configField = configFields.singleOrNull { ymlOption.key == it.get(null) }
			if (configField == null) {
				Assertions.fail("${ymlOption.key} option for ${ruleClass.simpleName} rule is not correctly defined")
			}
		}
	}

	private fun getYmlRuleConfig() = config.subConfig(name) as? YamlConfig
			?: throw IllegalStateException("yaml config expected but got ${config.javaClass}")

	private fun getRuleClasses(): List<Class<out Rule>> {
		return Reflections(packageName)
				.getSubTypesOf(Rule::class.java)
				.filter { !Modifier.isAbstract(it.modifiers) }
	}

	private fun isPublicStaticFinal(it: Field): Boolean {
		val modifiers = it.modifiers
		return (Modifier.isStatic(modifiers) && Modifier.isPublic(modifiers) && Modifier.isFinal(modifiers))
	}
}

private const val THRESHOLD_RULE = "ThresholdRule"
private const val THRESHOLD = "threshold"
