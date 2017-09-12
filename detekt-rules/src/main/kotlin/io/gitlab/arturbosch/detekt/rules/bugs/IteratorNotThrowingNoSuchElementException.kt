package io.gitlab.arturbosch.detekt.rules.bugs

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Debt
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.Severity
import io.gitlab.arturbosch.detekt.rules.collectByType
import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.psi.KtClass
import org.jetbrains.kotlin.psi.KtNamedFunction
import org.jetbrains.kotlin.psi.KtThrowExpression
import org.jetbrains.kotlin.psi.psiUtil.isAbstract

class IteratorNotThrowingNoSuchElementException(config: Config = Config.empty) : Rule(config) {

	override val issue = Issue("IteratorNotThrowingNoSuchElementException", Severity.Defect,
			"The next() method of an Iterator implementation should throw a NoSuchElementException " +
					"when there are no more elements to return",
			Debt.TEN_MINS)

	override fun visitClass(klass: KtClass) {
		if (!klass.isInterface() && !klass.isAbstract() && isImplementingIterator(klass)) {
			val functions = klass.declarations.filterIsInstance(KtNamedFunction::class.java)
			val nextMethod = functions.firstOrNull { it.name == "next" && it.valueParameters.isEmpty() }
			if (!isNoSuchElementExceptionThrown(nextMethod)) {
				report(CodeSmell(issue, Entity.from(klass)))
			}
		}
	}

	private fun isImplementingIterator(klass: KtClass): Boolean {
		val typeList = klass.getSuperTypeList()?.entries
		val name = typeList?.firstOrNull()?.typeAsUserType?.referencedName
		return name == "Iterator"
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
