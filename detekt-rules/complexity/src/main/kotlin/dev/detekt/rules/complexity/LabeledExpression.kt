package dev.detekt.rules.complexity

import dev.detekt.api.Config
import dev.detekt.api.Configuration
import dev.detekt.api.Entity
import dev.detekt.api.Finding
import dev.detekt.api.Rule
import dev.detekt.api.config
import org.jetbrains.kotlin.psi.KtClass
import org.jetbrains.kotlin.psi.KtElement
import org.jetbrains.kotlin.psi.KtExpressionWithLabel
import org.jetbrains.kotlin.psi.KtNamedFunction
import org.jetbrains.kotlin.psi.KtThisExpression
import org.jetbrains.kotlin.psi.psiUtil.containingClass
import org.jetbrains.kotlin.psi.psiUtil.getStrictParentOfType
import org.jetbrains.kotlin.psi.psiUtil.isExtensionDeclaration

/**
 * This rule reports labeled expressions. Expressions with labels generally increase complexity and worsen the
 * maintainability of the code. Refactor the violating code to not use labels instead.
 * Labeled expressions referencing an outer class with a label from an inner class are allowed, because there is no
 * way to get the instance of an outer class from an inner class in Kotlin.
 *
 * <noncompliant>
 * val range = listOf<String>("foo", "bar")
 * loop@ for (r in range) {
 *     if (r == "bar") break@loop
 *     println(r)
 * }
 *
 * class Outer {
 *     inner class Inner {
 *         fun f() {
 *             val i = this@Inner // referencing itself, use `this instead
 *         }
 *     }
 * }
 * </noncompliant>
 *
 * <compliant>
 * val range = listOf<String>("foo", "bar")
 * for (r in range) {
 *     if (r == "bar") break
 *     println(r)
 * }
 *
 * class Outer {
 *     inner class Inner {
 *         fun f() {
 *             val outer = this@Outer
 *         }
 *         fun Int.extend() {
 *             val inner = this@Inner // this would reference Int and not Inner
 *         }
 *     }
 * }
 * </compliant>
 */
class LabeledExpression(config: Config) :
    Rule(config, "Expression with labels increase complexity and affect maintainability.") {

    @Configuration("allows to provide a list of label names which should be ignored by this rule")
    private val ignoredLabels: List<String> by config(emptyList<String>()) { list ->
        list.map { it.removePrefix("*").removeSuffix("*") }
    }

    override fun visitExpressionWithLabel(expression: KtExpressionWithLabel) {
        super.visitExpressionWithLabel(expression)
        if (expression !is KtThisExpression || isNotReferencingOuterClass(expression)) {
            val label = expression.getTargetLabel()
            val labelName = label?.getReferencedName()
            if (labelName != null && ignoredLabels.none { labelName.contains(it, ignoreCase = true) }) {
                report(Finding(Entity.from(label), description))
            }
        }
    }

    private fun isNotReferencingOuterClass(expression: KtExpressionWithLabel): Boolean {
        val containingClasses = mutableListOf<KtClass>()
        val containingClass = expression.containingClass() ?: return false
        if (isAllowedToReferenceContainingClass(containingClass, expression)) {
            containingClasses.add(containingClass)
        }
        getClassHierarchy(containingClass, containingClasses)
        return !containingClasses.any { it.name == expression.getLabelName() }
    }

    private fun isAllowedToReferenceContainingClass(klass: KtClass, expression: KtExpressionWithLabel): Boolean =
        !klass.isInner() ||
            expression.getStrictParentOfType<KtNamedFunction>()?.isExtensionDeclaration() == true

    private fun getClassHierarchy(element: KtElement, classes: MutableList<KtClass>) {
        val containingClass = element.containingClass() ?: return
        classes.add(containingClass)
        getClassHierarchy(containingClass, classes)
    }
}
