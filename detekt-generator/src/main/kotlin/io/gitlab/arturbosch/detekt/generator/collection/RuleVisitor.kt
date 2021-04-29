package io.gitlab.arturbosch.detekt.generator.collection

import io.gitlab.arturbosch.detekt.api.Debt
import io.gitlab.arturbosch.detekt.api.DetektVisitor
import io.gitlab.arturbosch.detekt.api.ThresholdRule
import io.gitlab.arturbosch.detekt.api.internal.ActiveByDefault
import io.gitlab.arturbosch.detekt.api.internal.RequiresTypeResolution
import io.gitlab.arturbosch.detekt.formatting.FormattingRule
import io.gitlab.arturbosch.detekt.generator.collection.exception.InvalidAliasesDeclaration
import io.gitlab.arturbosch.detekt.generator.collection.exception.InvalidDocumentationException
import io.gitlab.arturbosch.detekt.generator.collection.exception.InvalidIssueDeclaration
import io.gitlab.arturbosch.detekt.rules.empty.EmptyRule
import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.psi.KtClass
import org.jetbrains.kotlin.psi.KtClassOrObject
import org.jetbrains.kotlin.psi.KtProperty
import org.jetbrains.kotlin.psi.KtSuperTypeList
import org.jetbrains.kotlin.psi.KtValueArgument
import org.jetbrains.kotlin.psi.psiUtil.containingClass
import org.jetbrains.kotlin.psi.psiUtil.getSuperNames
import java.lang.reflect.Modifier

internal class RuleVisitor : DetektVisitor() {

    val containsRule
        get() = classesMap.any { it.value }
    private var name = ""
    private val documentationCollector = DocumentationCollector()
    private var defaultActivationStatus: DefaultActivationStatus = Inactive
    private var autoCorrect = false
    private var requiresTypeResolution = false
    private var severity = ""
    private var debt = ""
    private var aliases: String? = null
    private var parent = ""
    private var configurationByKdoc = emptyList<Configuration>()
    private val configurationCollector = ConfigurationCollector()
    private val classesMap = mutableMapOf<String, Boolean>()

    fun getRule(): Rule {
        if (documentationCollector.description.isEmpty()) {
            throw InvalidDocumentationException("Rule $name is missing a description in its KDoc.")
        }

        val configurationByAnnotation = configurationCollector.getConfiguration()
        if (configurationByAnnotation.isNotEmpty() && configurationByKdoc.isNotEmpty()) {
            throw InvalidDocumentationException(
                "Rule $name is using both annotations and kdoc to define configuration parameter."
            )
        }

        return Rule(
            name = name,
            description = documentationCollector.description,
            nonCompliantCodeExample = documentationCollector.nonCompliant,
            compliantCodeExample = documentationCollector.compliant,
            defaultActivationStatus = defaultActivationStatus,
            severity = severity,
            debt = debt,
            aliases = aliases,
            parent = parent,
            configuration = configurationByAnnotation + configurationByKdoc,
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

        if (classOrObject.isAnnotatedWith(ActiveByDefault::class)) {
            val activeByDefaultSinceValue = classOrObject.firstAnnotationParameter(ActiveByDefault::class)
            defaultActivationStatus = Active(since = activeByDefaultSinceValue)
        }

        autoCorrect = classOrObject.hasKDocTag(TAG_AUTO_CORRECT)
        requiresTypeResolution = classOrObject.isAnnotatedWith(RequiresTypeResolution::class)
        configurationByKdoc = classOrObject.parseConfigurationTags()

        documentationCollector.setClass(classOrObject)
    }

    override fun visitProperty(property: KtProperty) {
        super.visitProperty(property)
        configurationCollector.addProperty(property)
    }

    override fun visitClass(klass: KtClass) {
        super.visitClass(klass)
        klass.companionObjects.forEach(configurationCollector::addCompanion)
    }

    private fun extractAliases(klass: KtClass) {
        val initializer = klass.getProperties()
            .singleOrNull { it.name == "defaultRuleIdAliases" }
            ?.initializer
        if (initializer != null) {
            aliases = (
                initializer as? KtCallExpression
                    ?: throw InvalidAliasesDeclaration()
                )
                .valueArguments
                .joinToString(", ") { it.text.replace("\"", "") }
        }
    }

    private fun extractIssueSeverityAndDebt(klass: KtClass) {
        val arguments = (
            klass.getProperties()
                .singleOrNull { it.name == "issue" }
                ?.initializer as? KtCallExpression
            )
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

        private const val TAG_AUTO_CORRECT = "autoCorrect"

        private const val ISSUE_ARGUMENT_SIZE = 4
        private const val DEBT_ARGUMENT_INDEX = 3
    }
}
