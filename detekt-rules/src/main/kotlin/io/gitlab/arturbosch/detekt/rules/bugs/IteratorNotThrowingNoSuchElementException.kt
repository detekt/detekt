package io.gitlab.arturbosch.detekt.rules.bugs

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Debt
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.Severity
import io.gitlab.arturbosch.detekt.rules.bugs.iterator.getMethod
import io.gitlab.arturbosch.detekt.rules.bugs.iterator.isImplementingIterator
import io.gitlab.arturbosch.detekt.rules.collectByType
import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.psi.KtClassOrObject
import org.jetbrains.kotlin.psi.KtNamedFunction
import org.jetbrains.kotlin.psi.KtThrowExpression

class IteratorNotThrowingNoSuchElementException(config: Config = Config.empty) : Rule(config) {

	override val issue = Issue("IteratorNotThrowingNoSuchElementException", Severity.Defect,
			"The next() method of an Iterator implementation should throw a NoSuchElementException " +
					"when there are no more elements to return",
			Debt.TEN_MINS)

	override fun visitClassOrObject(classOrObject: KtClassOrObject) {
		if (classOrObject.isImplementingIterator()) {
			val nextMethod = classOrObject.getMethod("next")
			if (nextMethod != null && !isNoSuchElementExceptionThrown(nextMethod)) {
				report(CodeSmell(issue, Entity.from(classOrObject)))
			}
		}
		super.visitClassOrObject(classOrObject)
	}

	private fun isNoSuchElementExpression(expression: KtThrowExpression): Boolean {
		val calleeExpression = (expression.thrownExpression as? KtCallExpression)?.calleeExpression
		return calleeExpression?.text == "NoSuchElementException"
	}

	private fun isNoSuchElementExceptionThrown(nextMethod: KtNamedFunction?): Boolean {
		return nextMethod?.bodyExpression
				?.collectByType<KtThrowExpression>()
				?.any { isNoSuchElementExpression(it) } ?: false
	}
}
