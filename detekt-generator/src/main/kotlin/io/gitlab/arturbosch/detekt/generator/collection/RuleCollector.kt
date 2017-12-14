package io.gitlab.arturbosch.detekt.generator.collection

import io.gitlab.arturbosch.detekt.api.DetektVisitor
import io.gitlab.arturbosch.detekt.api.ThresholdRule
import io.gitlab.arturbosch.detekt.rules.empty.EmptyRule
import org.jetbrains.kotlin.kdoc.psi.impl.KDocSection
import org.jetbrains.kotlin.psi.KtClassOrObject
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.psi.KtSuperTypeList
import org.jetbrains.kotlin.psi.psiUtil.containingClass

/**
 * @author Marvin Ramin
 */
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

private val ruleClasses = listOf(
		io.gitlab.arturbosch.detekt.api.Rule::class.simpleName,
		ThresholdRule::class.simpleName,
		EmptyRule::class.simpleName
)

private const val TAG_ACTIVE = "active"
private const val TAG_CONFIGURATION = "configuration"
private const val TAG_NONCOMPLIANT = "<noncompliant>"
private const val ENDTAG_NONCOMPLIANT = "</noncompliant>"
private const val TAG_COMPLIANT = "<compliant>"
private const val ENDTAG_COMPLIANT = "</compliant>"
private val configurationDefaultValueRegex = "\\(default: (.+)\\)".toRegex(RegexOption.DOT_MATCHES_ALL)

class RuleVisitor : DetektVisitor() {
	val containsRule
		get() = classesMap.any { it.value }
	private var description = ""
	private var nonCompliant = ""
	private var compliant = ""
	private var name = ""
	private var active = false
	private val configuration = mutableListOf<Configuration>()
	private val classesMap = mutableMapOf<String, Boolean>()

	fun getRule(): Rule {
		if (description.isEmpty()) {
			println("Rule $name is missing a description")
		}

		return Rule(name, description, nonCompliant, compliant, active, configuration)
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
		active = classOrObject.kDocSection()?.findTagByName(TAG_ACTIVE) != null

		val comment = classOrObject.kDocSection()?.getContent()?.trim() ?: return
		extractRuleDocumentation(comment)
		findConfigurationOptions(classOrObject)
	}

	private fun extractRuleDocumentation(comment: String) {
		val nonCompliantIndex = comment.indexOf(TAG_NONCOMPLIANT)
		if (nonCompliantIndex != -1) {
			val nonCompliantEndIndex = comment.indexOf(ENDTAG_NONCOMPLIANT)
			if (nonCompliantEndIndex == -1) {
				throw InvalidCodeExampleDocumentationException()
			}
			description = comment.substring(0, nonCompliantIndex).trim()
			nonCompliant = comment.substring(nonCompliantIndex + TAG_NONCOMPLIANT.length, nonCompliantEndIndex)
									.trimStartingLineBreaks()
									.trimEnd()
			val compliantIndex = comment.indexOf(TAG_COMPLIANT)
			val compliantEndIndex = comment.indexOf(ENDTAG_COMPLIANT)
			if (compliantIndex != -1) {
				if (compliantEndIndex == -1) {
					throw InvalidCodeExampleDocumentationException()
				}
				compliant = comment.substring(compliantIndex + TAG_COMPLIANT.length, compliantEndIndex)
									.trimStartingLineBreaks()
									.trimEnd()
			}
		} else {
			description = comment
		}
	}

	private fun findConfigurationOptions(classOrObject: KtClassOrObject) {
		val configurationTags = classOrObject.kDocSection()?.findTagsByName(TAG_CONFIGURATION) ?: emptyList()
		val configurations = configurationTags.map { it.getContent() }
				.filter {
					val valid = it.contains("-") && it.contains(configurationDefaultValueRegex)
					if (!valid) {
						println("Rule $name contains an incorrect configuration option KDoc.")
					}
					valid
				}
				.map {
					val name = it.split("-")[0].trim()
					val defaultValue = configurationDefaultValueRegex.find(it)?.groupValues?.get(1)?.trim() ?: ""
					val description = it.split("-")[1]
							.replace(configurationDefaultValueRegex, "")
							.trim()
					Configuration(name, description, defaultValue)
				}
		configuration.addAll(configurations)
	}

	private fun KtClassOrObject.kDocSection(): KDocSection? = docComment?.getDefaultSection()

	private fun String.trimStartingLineBreaks(): String {
		var i = 0
		while (i < this.length && (this[i] == '\n' || this[i] == '\r')) {
			i++
		}
		return this.substring(i)
	}
}
