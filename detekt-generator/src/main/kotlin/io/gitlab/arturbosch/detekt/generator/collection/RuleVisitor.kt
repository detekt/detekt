package io.gitlab.arturbosch.detekt.generator.collection

import io.gitlab.arturbosch.detekt.api.Debt
import io.gitlab.arturbosch.detekt.api.DetektVisitor
import io.gitlab.arturbosch.detekt.api.ThresholdRule
import io.gitlab.arturbosch.detekt.formatting.FormattingRule
import io.gitlab.arturbosch.detekt.generator.collection.exception.InvalidAliasesDeclaration
import io.gitlab.arturbosch.detekt.generator.collection.exception.InvalidCodeExampleDocumentationException
import io.gitlab.arturbosch.detekt.generator.collection.exception.InvalidDocumentationException
import io.gitlab.arturbosch.detekt.generator.collection.exception.InvalidIssueDeclaration
import io.gitlab.arturbosch.detekt.rules.empty.EmptyRule
import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.psi.KtClass
import org.jetbrains.kotlin.psi.KtClassOrObject
import org.jetbrains.kotlin.psi.KtSuperTypeList
import org.jetbrains.kotlin.psi.KtValueArgument
import org.jetbrains.kotlin.psi.psiUtil.containingClass
import org.jetbrains.kotlin.psi.psiUtil.getSuperNames
import java.lang.reflect.Modifier

internal class RuleVisitor : DetektVisitor() {

    val containsRule
        get() = classesMap.any { it.value }
    private var description = ""
    private var nonCompliant = ""
    private var compliant = ""
    private var name = ""
    private var active = false
    private var autoCorrect = false
    private var requiresTypeResolution = false
    private var severity = ""
    private var debt = ""
    private var aliases: String? = null
    private var parent = ""
    private val configuration = mutableListOf<Configuration>()
    private val classesMap = mutableMapOf<String, Boolean>()

    fun getRule(): Rule {
        if (description.isEmpty()) {
            throw InvalidDocumentationException("Rule $name is missing a description in its KDoc.")
        }

        return Rule(
            name = name,
            description = description,
            nonCompliantCodeExample = nonCompliant,
            compliantCodeExample = compliant,
            active = active,
            severity = severity,
            debt = debt,
            aliases = aliases,
            parent = parent,
            configuration = configuration,
            autoCorrect = autoCorrect,
            requiresTypeResolution = requiresTypeResolution
        )
    }

    override fun visitSuperTypeList(list: KtSuperTypeList) {
        val isRule = list.entries
                ?.asSequence()
                ?.map { it.typeAsUserType?.referencedName }
                ?.any { ruleClasses.contains(it) } ?: false

        val containingClass = list.containingClass()
        val className = containingClass?.name
        if (containingClass != null && className != null && !classesMap.containsKey(className)) {
            classesMap[className] = isRule
            parent = containingClass.getSuperNames().firstOrNull { ruleClasses.contains(it) } ?: ""
            extractIssueSeverityAndDebt(containingClass)
            extractAliases(containingClass)
        }
        super.visitSuperTypeList(list)
    }

    override fun visitClassOrObject(classOrObject: KtClassOrObject) {
        super.visitClassOrObject(classOrObject)
        if (classesMap[classOrObject.name] != true) {
            return
        }

        name = classOrObject.name?.trim() ?: ""

        // Use unparsed KDoc text here to check for tabs
        // Parsed [KDocSection] element contains no tabs
        if (classOrObject.docComment?.text?.contains('\t') == true) {
            throw InvalidDocumentationException("KDoc for rule $name must not contain tabs")
        }

        active = classOrObject.kDocSection()?.findTagByName(TAG_ACTIVE) != null
        autoCorrect = classOrObject.kDocSection()?.findTagByName(TAG_AUTO_CORRECT) != null
        requiresTypeResolution = classOrObject.kDocSection()?.findTagByName(TAG_REQUIRES_TYPE_RESOLUTION) != null

        val comment = classOrObject.kDocSection()?.getContent()?.trim()?.replace("@@", "@") ?: return
        extractRuleDocumentation(comment)
        configuration.addAll(classOrObject.parseConfigurationTags())
    }

    private fun extractRuleDocumentation(comment: String) {
        val nonCompliantIndex = comment.indexOf(TAG_NONCOMPLIANT)
        val compliantIndex = comment.indexOf(TAG_COMPLIANT)
        when {
            nonCompliantIndex != -1 -> {
                extractNonCompliantDocumentation(comment, nonCompliantIndex)
                extractCompliantDocumentation(comment, compliantIndex)
            }
            compliantIndex != -1 -> throw InvalidCodeExampleDocumentationException(
                    "Rule $name contains a compliant without a noncompliant code definition")
            else -> description = comment
        }
    }

    private fun extractNonCompliantDocumentation(comment: String, nonCompliantIndex: Int) {
        val nonCompliantEndIndex = comment.indexOf(ENDTAG_NONCOMPLIANT)
        if (nonCompliantEndIndex == -1) {
            throw InvalidCodeExampleDocumentationException(
                    "Rule $name contains an incorrect noncompliant code definition")
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
                        "Rule $name contains an incorrect compliant code definition")
            }
            compliant = comment.substring(compliantIndex + TAG_COMPLIANT.length, compliantEndIndex)
                    .trimStartingLineBreaks()
                    .trimEnd()
        }
    }

    private fun extractAliases(klass: KtClass) {
        val initializer = klass.getProperties()
                .singleOrNull { it.name == "defaultRuleIdAliases" }
                ?.initializer
        if (initializer != null) {
            aliases = (initializer as? KtCallExpression
                ?: throw InvalidAliasesDeclaration())
                    .valueArguments
                    .joinToString(", ") { it.text.replace("\"", "") }
        }
    }

    private fun extractIssueSeverityAndDebt(klass: KtClass) {
        val arguments = (klass.getProperties()
                .singleOrNull { it.name == "issue" }
                ?.initializer as? KtCallExpression)
                ?.valueArguments ?: emptyList()

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

    private fun getArgument(argument: KtValueArgument, name: String): String {
        val text = argument.text
        val type = text.split('.')
        if (text.startsWith(name, true) && type.size == 2) {
            return type[1]
        }
        throw InvalidIssueDeclaration(name)
    }

    companion object {
        private val ruleClasses = listOf(
                io.gitlab.arturbosch.detekt.api.Rule::class.simpleName,
                FormattingRule::class.simpleName,
                ThresholdRule::class.simpleName,
                EmptyRule::class.simpleName
        )

        private const val TAG_ACTIVE = "active"
        private const val TAG_AUTO_CORRECT = "autoCorrect"
        private const val TAG_REQUIRES_TYPE_RESOLUTION = "requiresTypeResolution"
        private const val TAG_NONCOMPLIANT = "<noncompliant>"
        private const val ENDTAG_NONCOMPLIANT = "</noncompliant>"
        private const val TAG_COMPLIANT = "<compliant>"
        private const val ENDTAG_COMPLIANT = "</compliant>"

        private const val ISSUE_ARGUMENT_SIZE = 4
        private const val DEBT_ARGUMENT_INDEX = 3
    }
}

private fun String.trimStartingLineBreaks(): String {
    var i = 0
    while (i < this.length && (this[i] == '\n' || this[i] == '\r')) {
        i++
    }
    return this.substring(i)
}
