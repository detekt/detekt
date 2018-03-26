package io.gitlab.arturbosch.detekt.generator.collection

import io.gitlab.arturbosch.detekt.api.Debt
import io.gitlab.arturbosch.detekt.api.DetektVisitor
import io.gitlab.arturbosch.detekt.api.ThresholdRule
import io.gitlab.arturbosch.detekt.rules.empty.EmptyRule
import org.jetbrains.kotlin.kdoc.psi.impl.KDocSection
import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.psi.KtClass
import org.jetbrains.kotlin.psi.KtClassOrObject
import org.jetbrains.kotlin.psi.KtSuperTypeList
import org.jetbrains.kotlin.psi.KtValueArgument
import org.jetbrains.kotlin.psi.psiUtil.containingClass
import java.lang.reflect.Modifier

internal class RuleVisitor : DetektVisitor() {

	val containsRule
		get() = classesMap.any { it.value }
	private var description = ""
	private var nonCompliant = ""
	private var compliant = ""
	private var name = ""
	private var active = false
	private var severity = ""
	private var debt = ""
	private val configuration = mutableListOf<Configuration>()
	private val classesMap = mutableMapOf<String, Boolean>()

	fun getRule(): Rule {
		if (description.isEmpty()) {
			throw InvalidDocumentationException("Rule $name is missing a description in its KDoc.")
		}

		return Rule(name, description, nonCompliant, compliant, active, severity, debt, configuration)
	}

	override fun visitSuperTypeList(list: KtSuperTypeList) {
		val isRule = list.entries
				?.map { it.typeAsUserType?.referencedName }
				?.any { ruleClasses.contains(it) } ?: false

		val containingClass = list.containingClass()
		val className = containingClass?.name
		if (containingClass != null && className != null && !classesMap.containsKey(className)) {
			classesMap[className] = isRule
			extractIssueDocumentation(containingClass)
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
		val compliantIndex = comment.indexOf(TAG_COMPLIANT)
		if (nonCompliantIndex != -1) {
			extractNonCompliantDocumentation(comment, nonCompliantIndex)
			extractCompliantDocumentation(comment, compliantIndex)
		} else if (compliantIndex != -1) {
			throw InvalidCodeExampleDocumentationException(
					"Rule $name contains a compliant without a noncompliant code definition.")
		} else {
			description = comment
		}
	}

	private fun extractNonCompliantDocumentation(comment: String, nonCompliantIndex: Int) {
		val nonCompliantEndIndex = comment.indexOf(ENDTAG_NONCOMPLIANT)
		if (nonCompliantEndIndex == -1) {
			throw InvalidCodeExampleDocumentationException(
					"Rule $name contains a incorrect noncompliant code definition.")
		}
		description = comment.substring(0, nonCompliantIndex).trim()
		nonCompliant = comment.substring(nonCompliantIndex + TAG_NONCOMPLIANT.length, nonCompliantEndIndex)
				.trimStartingLineBreaks()
				.trimEnd()
	}

	private fun extractCompliantDocumentation(comment: String, compliantIndex: Int) {
		val compliantEndIndex = comment.indexOf(ENDTAG_COMPLIANT)
		if (compliantIndex != -1) {
			if (compliantEndIndex == -1) {
				throw InvalidCodeExampleDocumentationException(
						"Rule $name contains a incorrect compliant code definition.")
			}
			compliant = comment.substring(compliantIndex + TAG_COMPLIANT.length, compliantEndIndex)
					.trimStartingLineBreaks()
					.trimEnd()
		}
	}

	private fun extractIssueDocumentation(klass: KtClass) {
		val issueProperty = klass.getProperties().singleOrNull { it.name == "issue" }
		val initializer = issueProperty?.initializer as? KtCallExpression
		if (initializer != null) {
			val arguments = initializer.valueArguments
			if (arguments.size >= ISSUE_ARGUMENT_SIZE) {
				severity = getArgument(arguments[1], "Severity")
				val debtName = getArgument(arguments[DEBT_ARGUMENT_INDEX], "Debt")
				val debtDeclarations = Debt::class.java.declaredFields.filter { Modifier.isStatic(it.modifiers) }
				val debtDeclaration = debtDeclarations.singleOrNull { it.name == debtName }
				if (debtDeclaration != null) {
					debtDeclaration.isAccessible = true
					debt = debtDeclaration.get(Debt::class.java).toString()
				}
			}
		}
	}

	private fun getArgument(argument: KtValueArgument, name: String): String {
		var value = ""
		val text = argument.text
		val type = text.split('.')
		if (text.startsWith(name, true) && type.size == 2) {
			value = type[1]
		}
		return value
	}

	private fun findConfigurationOptions(classOrObject: KtClassOrObject) {
		val configurationTags = classOrObject.kDocSection()?.findTagsByName(TAG_CONFIGURATION) ?: emptyList()
		val configurations = configurationTags.map { it.getContent() }
				.filter {
					val valid = it.contains("-") && it.contains(configurationDefaultValueRegex)
					if (!valid) {
						throw InvalidDocumentationException("Rule $name contains an incorrect configuration option" +
								"tag in the KDoc.")
					}
					valid
				}
				.map {
					val delimiterIndex = it.indexOf('-')
					val name = it.substring(0, delimiterIndex - 1)
					val defaultValue = configurationDefaultValueRegex.find(it)?.groupValues?.get(1)?.trim() ?: ""
					val description = it.substring(delimiterIndex + 1)
							.replace(configurationDefaultValueRegex, "")
							.trim()
					Configuration(name, description, defaultValue)
				}
		configuration.addAll(configurations)
	}

	companion object {
		private val ruleClasses = listOf(
				io.gitlab.arturbosch.detekt.api.Rule::class.simpleName,
				ThresholdRule::class.simpleName,
				EmptyRule::class.simpleName
		)
		private val configurationDefaultValueRegex = "\\(default: (.+)\\)".toRegex(RegexOption.DOT_MATCHES_ALL)

		private const val TAG_ACTIVE = "active"
		private const val TAG_CONFIGURATION = "configuration"
		private const val TAG_NONCOMPLIANT = "<noncompliant>"
		private const val ENDTAG_NONCOMPLIANT = "</noncompliant>"
		private const val TAG_COMPLIANT = "<compliant>"
		private const val ENDTAG_COMPLIANT = "</compliant>"

		private const val ISSUE_ARGUMENT_SIZE = 4
		private const val DEBT_ARGUMENT_INDEX = 3
	}
}

private fun KtClassOrObject.kDocSection(): KDocSection? = docComment?.getDefaultSection()

private fun String.trimStartingLineBreaks(): String {
	var i = 0
	while (i < this.length && (this[i] == '\n' || this[i] == '\r')) {
		i++
	}
	return this.substring(i)
}
