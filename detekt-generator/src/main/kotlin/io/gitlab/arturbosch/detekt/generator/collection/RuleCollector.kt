package io.gitlab.arturbosch.detekt.generator.collection

import io.gitlab.arturbosch.detekt.api.DetektVisitor
import io.gitlab.arturbosch.detekt.api.ThresholdRule
import io.gitlab.arturbosch.detekt.rules.SubRule
import io.gitlab.arturbosch.detekt.rules.empty.EmptyRule
import org.jetbrains.kotlin.psi.KtClassOrObject
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.psi.KtSuperTypeList
import org.jetbrains.kotlin.psi.psiUtil.containingClass

/**
 * @author Marvin Ramin
 */
data class Rule(
		val name: String,
		val description: String,
		val active: Boolean,
		val configuration: List<Configuration> = listOf()
)

data class Configuration(
		val name: String,
		val description: String,
		val defaultValue: String
)

private val ruleClasses = listOf(
		io.gitlab.arturbosch.detekt.api.Rule::class.simpleName,
		SubRule::class.simpleName,
		ThresholdRule::class.simpleName,
		EmptyRule::class.simpleName
)


class RuleCollector : Collector<Rule> {
	override val items = mutableListOf<Rule>()

	override fun visit(file: KtFile) {
		val visitor = RuleVisitor()
		file.accept(visitor)

		if (visitor.containsRule) {
			items.add(visitor.getRule())
		}
	}
}

private const val TAG_ACTIVE = "active"
private const val TAG_CONFIGURATION = "configuration"
private val CONFIGURATION_DEFAULT_VALUE_REGEX = "\\(default: (.+)\\)".toRegex()

class RuleVisitor : DetektVisitor() {
	val containsRule
		get() = classesMap.any { it.value }
	private var description = ""
	private var name = ""
	private var active = false
	private val configuration = mutableListOf<Configuration>()
	private val classesMap = mutableMapOf<String, Boolean>()

	fun getRule(): Rule {
		if (description.isEmpty()) {
			println("Rule $name is missing a description")
		}

		return Rule(name, description, active, configuration)
	}

	override fun visitSuperTypeList(list: KtSuperTypeList) {
		val isRule = list.entries
				?.map { it.typeAsUserType?.referencedName }
				?.any { ruleClasses.contains(it) } ?: false

		val containingClass = list.containingClass()
		val className = containingClass?.name
		if (containingClass != null && className != null && !classesMap.containsKey(className)) {
			classesMap.put(className, isRule)
		}
		super.visitSuperTypeList(list)
	}

	override fun visitClassOrObject(classOrObject: KtClassOrObject) {
		super.visitClassOrObject(classOrObject)
		if (classesMap[classOrObject.name] != true) {
			return
		}

		name = classOrObject.name?.trim() ?: ""
		description = classOrObject.docComment?.getDefaultSection()?.getContent()?.trim() ?: ""
		active = classOrObject.docComment?.getDefaultSection()?.findTagByName(TAG_ACTIVE) != null
		findConfigurationOptions(classOrObject)
	}

	private fun findConfigurationOptions(classOrObject: KtClassOrObject) {
		val configurationTags = classOrObject.docComment?.getDefaultSection()?.findTagsByName(TAG_CONFIGURATION) ?: emptyList()
		val configurations = configurationTags.map { it.getContent() }
				.filter {
					val valid = it.contains("-") && it.contains(CONFIGURATION_DEFAULT_VALUE_REGEX)
					if (!valid) {
						println("Rule $name contains an incorrect configuration option KDoc.")
					}
					valid
				}
				.map {
					val name = it.split("-")[0].trim()
					val defaultValue = CONFIGURATION_DEFAULT_VALUE_REGEX.find(it)?.groupValues?.get(1)?.trim() ?: ""
					val description = it.split("-")[1].replace(CONFIGURATION_DEFAULT_VALUE_REGEX, "").trim()
					Configuration(name, description, defaultValue)
				}
		configuration.addAll(configurations)
	}
}
