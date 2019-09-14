package io.gitlab.arturbosch.detekt.generator.collection

import io.gitlab.arturbosch.detekt.api.DetektVisitor
import io.gitlab.arturbosch.detekt.generator.collection.exception.InvalidDocumentationException
import io.gitlab.arturbosch.detekt.rules.isOverride
import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.psi.KtClassOrObject
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.psi.KtProperty
import org.jetbrains.kotlin.psi.KtReferenceExpression
import org.jetbrains.kotlin.psi.KtSuperTypeList
import org.jetbrains.kotlin.psi.KtValueArgumentList
import org.jetbrains.kotlin.psi.psiUtil.containingClass
import org.jetbrains.kotlin.psi.psiUtil.referenceExpression

data class MultiRule(
    val name: String,
    val rules: List<String> = listOf()
) {

    operator fun contains(ruleName: String) = ruleName in this.rules
}

private val multiRule = io.gitlab.arturbosch.detekt.api.MultiRule::class.simpleName ?: ""

class MultiRuleCollector : Collector<MultiRule> {
    override val items = mutableListOf<MultiRule>()

    override fun visit(file: KtFile) {
        val visitor = MultiRuleVisitor()
        file.accept(visitor)

        if (visitor.containsMultiRule) {
            items.add(visitor.getMultiRule())
        }
    }
}

class MultiRuleVisitor : DetektVisitor() {
    val containsMultiRule
        get() = classesMap.any { it.value }
    private var classesMap = mutableMapOf<String, Boolean>()
    private var name = ""
    private val rulesVisitor = RuleListVisitor()
    private val properties: MutableMap<String, String> = mutableMapOf()

    fun getMultiRule(): MultiRule {
        val rules = mutableListOf<String>()

        val ruleProperties = rulesVisitor.ruleProperties
                .mapNotNull { properties[it] }
        rules.addAll(ruleProperties)
        rules.addAll(rulesVisitor.ruleNames)

        if (name.isEmpty()) {
            throw InvalidDocumentationException("MultiRule without name found.")
        }
        if (rules.isEmpty()) {
            throw InvalidDocumentationException("MultiRule $name contains no rules.")
        }
        return MultiRule(name, rules)
    }

    override fun visitSuperTypeList(list: KtSuperTypeList) {
        val isMultiRule = list.entries
                ?.mapNotNull { it.typeAsUserType?.referencedName }
                ?.any { it == multiRule } ?: false

        val containingClass = list.containingClass()
        val className = containingClass?.name
        if (containingClass != null && className != null && !classesMap.containsKey(className)) {
            classesMap[className] = isMultiRule
        }
        super.visitSuperTypeList(list)
    }

    override fun visitClassOrObject(classOrObject: KtClassOrObject) {
        super.visitClassOrObject(classOrObject)
        if (classesMap[classOrObject.name] != true) {
            return
        }

        name = classOrObject.name?.trim() ?: ""
    }

    override fun visitProperty(property: KtProperty) {
        super.visitProperty(property)
        if (classesMap[property.containingClass()?.name] != true) {
            return
        }

        if (property.isOverride() && property.name != null && property.name == "rules") {
            property.accept(rulesVisitor)
        } else {
            val name = property.name
            val initializer = property.initializer?.referenceExpression()?.text
            if (name != null && initializer != null) {
                properties[name] = initializer
            }
        }
    }
}

class RuleListVisitor : DetektVisitor() {
    var ruleNames: MutableSet<String> = mutableSetOf()
        private set
    var ruleProperties: MutableSet<String> = mutableSetOf()
        private set

    override fun visitValueArgumentList(list: KtValueArgumentList) {
        super.visitValueArgumentList(list)
        val argumentExpressions = list.arguments.map { it.getArgumentExpression() }

        // Call Expression = Constructor of rule
        ruleNames.addAll(argumentExpressions
                .filterIsInstance<KtCallExpression>()
                .map { it.calleeExpression?.text ?: "" })

        // Reference Expression = variable we need to search for
        ruleProperties.addAll(argumentExpressions
                .filterIsInstance<KtReferenceExpression>()
                .map { it.text ?: "" })
    }
}
