package io.gitlab.arturbosch.detekt.generator.collection

import io.gitlab.arturbosch.detekt.api.DetektVisitor
import io.gitlab.arturbosch.detekt.api.internal.ActiveByDefault
import io.gitlab.arturbosch.detekt.api.internal.DefaultRuleSetProvider
import io.gitlab.arturbosch.detekt.generator.collection.exception.InvalidDocumentationException
import io.gitlab.arturbosch.detekt.rules.isOverride
import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.psi.KtClass
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.psi.KtProperty
import org.jetbrains.kotlin.psi.KtStringTemplateExpression
import org.jetbrains.kotlin.psi.KtSuperTypeList
import org.jetbrains.kotlin.psi.psiUtil.containingClass
import org.jetbrains.kotlin.psi.psiUtil.referenceExpression
import io.gitlab.arturbosch.detekt.api.internal.Configuration as ConfigAnnotation

data class RuleSetProvider(
    val name: String,
    val description: String,
    val defaultActivationStatus: DefaultActivationStatus,
    val rules: List<String> = listOf(),
    val configuration: List<Configuration> = listOf()
)

class RuleSetProviderCollector : Collector<RuleSetProvider> {
    override val items = mutableListOf<RuleSetProvider>()

    override fun visit(file: KtFile) {
        val visitor = RuleSetProviderVisitor()
        file.accept(visitor)

        if (visitor.containsRuleSetProvider) {
            items.add(visitor.getRuleSetProvider())
        }
    }
}

private const val PROPERTY_RULE_SET_ID = "ruleSetId"

private val SUPPORTED_PROVIDERS =
    setOf(RuleSetProvider::class.simpleName, DefaultRuleSetProvider::class.simpleName)

class RuleSetProviderVisitor : DetektVisitor() {
    var containsRuleSetProvider = false
    private var name: String = ""
    private var description: String = ""
    private var defaultActivationStatus: DefaultActivationStatus = Inactive
    private val ruleNames: MutableList<String> = mutableListOf()
    private val configurations = mutableListOf<Configuration>()

    fun getRuleSetProvider(): RuleSetProvider {
        if (name.isEmpty()) {
            throw InvalidDocumentationException("RuleSetProvider without name found.")
        }

        if (description.isEmpty()) {
            throw InvalidDocumentationException("Missing description for RuleSet $name.")
        }

        return RuleSetProvider(name, description, defaultActivationStatus, ruleNames, configurations)
    }

    override fun visitSuperTypeList(list: KtSuperTypeList) {
        val superTypes = list.entries
            ?.map { it.typeAsUserType?.referencedName }
            ?.toSet()
            ?: emptySet()
        containsRuleSetProvider = SUPPORTED_PROVIDERS.any { it in superTypes }
        super.visitSuperTypeList(list)
    }

    override fun visitClass(ktClass: KtClass) {
        description = ktClass.docComment?.getDefaultSection()?.getContent()?.trim() ?: ""
        if (ktClass.isAnnotatedWith(ActiveByDefault::class)) {
            defaultActivationStatus = Active(since = ktClass.firstAnnotationParameter(ActiveByDefault::class))
        }
        if (ktClass.hasConfigurationKDocTag()) {
            throw InvalidDocumentationException(
                "Configuration of rule set ${ktClass.name} is invalid. Rule configuration via KDoc tag is no longer supported. " +
                    "Use rule set config delegate instead."
            )
        }
        super.visitClass(ktClass)
    }

    override fun visitProperty(property: KtProperty) {
        super.visitProperty(property)
        if (property.isOverride() && property.name != null && property.name == PROPERTY_RULE_SET_ID) {
            name = (property.initializer as? KtStringTemplateExpression)?.entries?.get(0)?.text
                ?: throw InvalidDocumentationException(
                    "RuleSetProvider class " +
                        "${property.containingClass()?.name ?: ""} doesn't provide list of rules."
                )
        }
        if (property.isAnnotatedWith(ConfigAnnotation::class)) {
            val defaultValue = checkNotNull(property.delegate?.expression as? KtCallExpression)
                .valueArguments.first().text.let(::formatDefaultValue)
            configurations.add(
                Configuration(
                    name = checkNotNull(property.name),
                    description = property.firstAnnotationParameter(ConfigAnnotation::class),
                    defaultValue = defaultValue,
                    defaultAndroidValue = defaultValue,
                    deprecated = property.firstAnnotationParameterOrNull(Deprecated::class),
                )
            )
        }
    }

    override fun visitCallExpression(expression: KtCallExpression) {
        super.visitCallExpression(expression)

        if (expression.calleeExpression?.text == "RuleSet") {
            val ruleListExpression = expression.valueArguments
                .map { it.getArgumentExpression() }
                .firstOrNull { it?.referenceExpression()?.text == "listOf" }
                ?: throw InvalidDocumentationException("RuleSetProvider $name doesn't provide list of rules.")

            val ruleArgumentNames = (ruleListExpression as? KtCallExpression)
                ?.valueArguments
                ?.mapNotNull { it.getArgumentExpression() }
                ?.mapNotNull { it.referenceExpression()?.text }
                ?: emptyList()

            ruleNames.addAll(ruleArgumentNames)
        }
    }

    companion object {

        private const val DOUBLE_QUOTE = '"'

        private fun formatDefaultValue(defaultValueText: String): String =
            if (defaultValueText.startsWith(DOUBLE_QUOTE) && defaultValueText.endsWith(DOUBLE_QUOTE)) {
                "'${defaultValueText.removeSurrounding("$DOUBLE_QUOTE")}'"
            } else {
                defaultValueText
            }
    }
}
