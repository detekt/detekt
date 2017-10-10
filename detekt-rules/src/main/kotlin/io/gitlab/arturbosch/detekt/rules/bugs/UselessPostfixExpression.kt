package io.gitlab.arturbosch.detekt.rules.bugs

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.Severity
import io.gitlab.arturbosch.detekt.rules.collectByType
import org.jetbrains.kotlin.lexer.KtTokens.MINUSMINUS
import org.jetbrains.kotlin.lexer.KtTokens.PLUSPLUS
import org.jetbrains.kotlin.psi.KtBinaryExpression
import org.jetbrains.kotlin.psi.KtClass
import org.jetbrains.kotlin.psi.KtExpression
import org.jetbrains.kotlin.psi.KtNamedFunction
import org.jetbrains.kotlin.psi.KtPostfixExpression
import org.jetbrains.kotlin.psi.KtProperty
import org.jetbrains.kotlin.psi.KtReturnExpression
import org.jetbrains.kotlin.psi.psiUtil.getNonStrictParentOfType

class UselessPostfixExpression(config: Config = Config.empty) : Rule(config) {

	override val issue: Issue = Issue("UselessPostfixExpression", Severity.Defect,
			"The incremented or decremented value is unused. This value is replaced with the original value.")

	var properties = setOf<String?>()

	override fun visitClass(klass: KtClass) {
		properties = klass.getProperties()
				.map { it.name }
				.toSet()
		super.visitClass(klass)
	}

	override fun visitReturnExpression(expression: KtReturnExpression) {
		val postfixExpression = expression.returnedExpression?.asPostFixExpression()

		if (postfixExpression != null && postfixExpression.shouldBeReported()) {
			report(postfixExpression)
		}

		getPostfixExpressionChilds(expression.returnedExpression)
				?.forEach { report(it) }
	}

	override fun visitBinaryExpression(expression: KtBinaryExpression) {
		val postfixExpression = expression.right?.asPostFixExpression()
		val leftIdentifierText = expression.left?.text
		checkPostfixExpression(postfixExpression, leftIdentifierText)
		getPostfixExpressionChilds(expression.right)
				?.forEach { checkPostfixExpression(it, leftIdentifierText) }
	}

	private fun KtExpression.asPostFixExpression() = if (this is KtPostfixExpression &&
			(operationToken === PLUSPLUS || operationToken === MINUSMINUS)) this else null

	private fun checkPostfixExpression(postfixExpression: KtPostfixExpression?, leftIdentifierText: String?) {
		if (postfixExpression != null && leftIdentifierText == postfixExpression.firstChild?.text) {
			report(postfixExpression)
		}
	}

	private fun KtPostfixExpression.shouldBeReported(): Boolean {
		val functionProperties = this.getNonStrictParentOfType(KtNamedFunction::class.java)
				?.collectByType<KtProperty>()
				?.map { it.name }
				?.toSet()
		val postfixReceiverName = this.baseExpression?.text

		if (functionProperties != null && functionProperties.contains(postfixReceiverName)) {
			return true
		}
		return !properties.contains(postfixReceiverName)

	}

	private fun report(postfixExpression: KtPostfixExpression) {
		report(CodeSmell(issue, Entity.from(postfixExpression), message = ""))
	}

	private fun getPostfixExpressionChilds(expression: KtExpression?) =
			expression?.children?.filterIsInstance<KtPostfixExpression>()
}
