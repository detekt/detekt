package io.gitlab.arturbosch.detekt.generator.collection

import io.gitlab.arturbosch.detekt.api.ActiveByDefault
import io.gitlab.arturbosch.detekt.api.Alias
import io.gitlab.arturbosch.detekt.api.DetektVisitor
import io.gitlab.arturbosch.detekt.api.RequiresTypeResolution
import io.gitlab.arturbosch.detekt.api.internal.AutoCorrectable
import io.gitlab.arturbosch.detekt.generator.collection.exception.InvalidDocumentationException
import org.jetbrains.kotlin.psi.KtClass
import org.jetbrains.kotlin.psi.KtClassOrObject
import org.jetbrains.kotlin.psi.KtProperty
import org.jetbrains.kotlin.psi.KtSuperTypeList
import org.jetbrains.kotlin.psi.psiUtil.containingClass
import org.jetbrains.kotlin.psi.psiUtil.getSuperNames

internal class RuleVisitor(textReplacements: Map<String, String>) : DetektVisitor() {

    val containsRule
        get() = classesMap.any { it.value }
    private var name = ""
    private val documentationCollector = DocumentationCollector(textReplacements)
    private var defaultActivationStatus: DefaultActivationStatus = Inactive
    private var autoCorrect = false
    private var requiresTypeResolution = false
    private var aliases: List<String> = emptyList()
    private var parent = ""
    private val configurationCollector = ConfigurationCollector()
    private val classesMap = mutableMapOf<String, Boolean>()
    private var deprecationMessage: String? = null

    fun getRule(): Rule {
        if (documentationCollector.description.isEmpty()) {
            throw InvalidDocumentationException("Rule $name is missing a description in its KDoc.")
        }

        val configurationsByAnnotation = configurationCollector.getConfigurations()

        return Rule(
            name = name,
            description = documentationCollector.description,
            nonCompliantCodeExample = documentationCollector.nonCompliant,
            compliantCodeExample = documentationCollector.compliant,
            defaultActivationStatus = defaultActivationStatus,
            aliases = aliases,
            parent = parent,
            configurations = configurationsByAnnotation,
            autoCorrect = autoCorrect,
            deprecationMessage = deprecationMessage,
            requiresTypeResolution = requiresTypeResolution
        )
    }

    override fun visitSuperTypeList(list: KtSuperTypeList) {
        val isRule = list.entries
            ?.asSequence()
            ?.map { it.typeAsUserType?.referencedName }
            ?.any { ruleClasses.contains(it) }
            ?: false

        val containingClass = list.containingClass()
        val className = containingClass?.name
        if (containingClass != null && className != null && !classesMap.containsKey(className)) {
            classesMap[className] = isRule
            parent = containingClass.getSuperNames().firstOrNull { ruleClasses.contains(it) }.orEmpty()
            extractAliases(containingClass)
        }
        super.visitSuperTypeList(list)
    }

    override fun visitClassOrObject(classOrObject: KtClassOrObject) {
        super.visitClassOrObject(classOrObject)
        if (classesMap[classOrObject.name] != true) {
            return
        }

        name = checkNotNull(classOrObject.name?.trim()) { "Unable to determine rule name." }

        // Use unparsed KDoc text here to check for tabs
        // Parsed [KDocSection] element contains no tabs
        if (classOrObject.docComment?.text?.contains('\t') == true) {
            throw InvalidDocumentationException("KDoc for rule $name must not contain tabs")
        }

        if (classOrObject.hasConfigurationKDocTag()) {
            throw InvalidDocumentationException(
                "Configuration of rule $name is invalid. Rule configuration via KDoc tag is no longer supported. " +
                    "Use config delegate instead."
            )
        }

        if (classOrObject.isAnnotatedWith(ActiveByDefault::class)) {
            val activeByDefaultSinceValue = classOrObject.firstAnnotationParameter(ActiveByDefault::class)
            defaultActivationStatus = Active(since = activeByDefaultSinceValue)
        }

        autoCorrect = classOrObject.isAnnotatedWith(AutoCorrectable::class)
        requiresTypeResolution = classOrObject.superTypeListEntries
            .any { it.text.substringAfterLast(".") == RequiresTypeResolution::class.simpleName }
        deprecationMessage = classOrObject.firstAnnotationParameterOrNull(Deprecated::class)

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
        val annotation = klass.getAnnotation(Alias::class) ?: return
        aliases = annotation.valueArguments.mapNotNull {
            it.getArgumentExpression()
                ?.text
                ?.withoutQuotes()
        }
    }

    companion object {
        private val ruleClasses = listOf(
            // These references are stringly-typed to prevent dependency cycle:
            // This class requires FormattingRule,
            // which needs detekt-formatting.jar,
            // which needs :detekt-formatting:processResources task output,
            // which needs output of this class.
            "Rule", // io.gitlab.arturbosch.detekt.api.Rule
            "FormattingRule", // io.gitlab.arturbosch.detekt.formatting.FormattingRule
            "EmptyRule", // io.gitlab.arturbosch.detekt.rules.empty.EmptyRule
        )
    }
}
