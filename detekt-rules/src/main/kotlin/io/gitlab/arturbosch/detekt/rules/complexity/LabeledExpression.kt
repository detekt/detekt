package io.gitlab.arturbosch.detekt.rules.complexity

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Debt
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.Severity
import org.jetbrains.kotlin.psi.KtClass
import org.jetbrains.kotlin.psi.KtElement
import org.jetbrains.kotlin.psi.KtExpressionWithLabel
import org.jetbrains.kotlin.psi.KtThisExpression
import org.jetbrains.kotlin.psi.psiUtil.containingClass

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
 *     }
 * }
 * </compliant>
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

	override fun visitExpressionWithLabel(expression: KtExpressionWithLabel) {
		super.visitExpressionWithLabel(expression)
		if (isNotReferencingOuterClass(expression)) {
			expression.getLabelName()?.let {
				report(CodeSmell(issue, Entity.from(expression), issue.description))
			}
		}
	}

	private fun isNotReferencingOuterClass(expression: KtExpressionWithLabel): Boolean {
		val containingClasses = mutableListOf<KtClass>()
		expression.containingClass()?.let { containingClasses(it, containingClasses) }
		return expression !is KtThisExpression || !containingClasses.any { it.name == expression.getLabelName() }
	}

	private fun containingClasses(element: KtElement, classes: MutableList<KtClass>) {
		val containingClass = element.containingClass() ?: return
		classes.add(containingClass)
		containingClasses(containingClass, classes)
	}
}
