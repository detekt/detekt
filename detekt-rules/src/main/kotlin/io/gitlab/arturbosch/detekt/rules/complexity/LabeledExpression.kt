package io.gitlab.arturbosch.detekt.rules.complexity

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Debt
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.Severity
import io.gitlab.arturbosch.detekt.api.SplitPattern
import org.jetbrains.kotlin.psi.KtClass
import org.jetbrains.kotlin.psi.KtElement
import org.jetbrains.kotlin.psi.KtExpressionWithLabel
import org.jetbrains.kotlin.psi.KtNamedFunction
import org.jetbrains.kotlin.psi.KtThisExpression
import org.jetbrains.kotlin.psi.psiUtil.containingClass
import org.jetbrains.kotlin.psi.psiUtil.isExtensionDeclaration
import org.jetbrains.kotlin.psi.psiUtil.parents

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
 *
 * @configuration ignoredLabels - allows to provide a list of label names which should be ignored by this rule
 * (default: `""`)
 *
 * @author Ivan Balaksha
 * @author Marvin Ramin
 * @author schalkms
 */
class LabeledExpression(config: Config = Config.empty) : Rule(config) {

    override val issue: Issue = Issue("LabeledExpression",
            Severity.Maintainability,
            "Expression with labels increase complexity and affect maintainability.",
            Debt.TWENTY_MINS)

    private val ignoredLabels = SplitPattern(valueOrDefault(IGNORED_LABELS, ""))

    override fun visitExpressionWithLabel(expression: KtExpressionWithLabel) {
        super.visitExpressionWithLabel(expression)
        if (expression !is KtThisExpression || isNotReferencingOuterClass(expression)) {
            expression.getLabelName()?.let {
                if (!ignoredLabels.contains(it)) {
                    report(CodeSmell(issue, Entity.from(expression), issue.description))
                }
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

    private fun isAllowedToReferenceContainingClass(klass: KtClass, expression: KtExpressionWithLabel): Boolean {
        return !klass.isInner() ||
                expression.parents.filterIsInstance<KtNamedFunction>().any { it.isExtensionDeclaration() }
    }

    private fun getClassHierarchy(element: KtElement, classes: MutableList<KtClass>) {
        val containingClass = element.containingClass() ?: return
        classes.add(containingClass)
        getClassHierarchy(containingClass, classes)
    }

    companion object {
        const val IGNORED_LABELS = "ignoredLabels"
    }
}
