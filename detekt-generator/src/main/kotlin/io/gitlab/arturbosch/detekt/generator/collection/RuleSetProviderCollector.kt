package io.gitlab.arturbosch.detekt.generator.collection

import io.gitlab.arturbosch.detekt.api.DetektVisitor
import io.gitlab.arturbosch.detekt.api.internal.ActiveByDefault
import io.gitlab.arturbosch.detekt.api.internal.DefaultRuleSetProvider
import io.gitlab.arturbosch.detekt.generator.collection.exception.InvalidDocumentationException
import io.gitlab.arturbosch.detekt.rules.isOverride
import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.psi.KtClassOrObject
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.psi.KtProperty
import org.jetbrains.kotlin.psi.KtStringTemplateExpression
import org.jetbrains.kotlin.psi.KtSuperTypeList
import org.jetbrains.kotlin.psi.psiUtil.containingClass
import org.jetbrains.kotlin.psi.psiUtil.referenceExpression

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
    private val configuration = mutableListOf<Configuration>()

    fun getRuleSetProvider(): RuleSetProvider {
        if (name.isEmpty()) {
            throw InvalidDocumentationException("RuleSetProvider without name found.")
        }

        if (description.isEmpty()) {
            throw InvalidDocumentationException("Missing description for RuleSet $name.")
        }

        return RuleSetProvider(name, description, defaultActivationStatus, ruleNames, configuration)
    }

    override fun visitSuperTypeList(list: KtSuperTypeList) {
        val superTypes = list.entries
            ?.map { it.typeAsUserType?.referencedName }
            ?.toSet()
            ?: emptySet()
        containsRuleSetProvider = SUPPORTED_PROVIDERS.any { it in superTypes }
        super.visitSuperTypeList(list)
    }

    override fun visitClassOrObject(classOrObject: KtClassOrObject) {
        description = classOrObject.docComment?.getDefaultSection()?.getContent()?.trim() ?: ""
        if (classOrObject.isAnnotatedWith(ActiveByDefault::class)) {
            defaultActivationStatus = Active(since = classOrObject.firstAnnotationParameter(ActiveByDefault::class))
        }
        configuration.addAll(classOrObject.parseConfigurationTags())
        super.visitClassOrObject(classOrObject)
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
}
