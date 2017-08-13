package io.gitlab.arturbosch.detekt.rules.empty

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Debt
import io.gitlab.arturbosch.detekt.api.DetektVisitor
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.Severity
import io.gitlab.arturbosch.detekt.rules.asBlockExpression
import org.jetbrains.kotlin.com.intellij.openapi.util.Key
import org.jetbrains.kotlin.com.intellij.psi.PsiComment
import org.jetbrains.kotlin.psi.KtBlockExpression
import org.jetbrains.kotlin.psi.KtExpression

/**
 * @author Artur Bosch
 */
abstract class EmptyRule(config: Config) : Rule(config) {

	override val issue = Issue(javaClass.simpleName,
			Severity.Minor,
			"Empty block of code detected. As they serve no purpose they should be removed.",
			Debt.FIVE_MINS)

	fun KtExpression.addFindingIfBlockExprIsEmpty() {
		val blockExpression = this.asBlockExpression()
		blockExpression?.statements?.let {
			if (it.isEmpty() && !blockExpression.hasCommentInside()) report(CodeSmell(issue, Entity.from(this)))
		}
	}
}

internal fun KtBlockExpression.hasCommentInside(): Boolean {
	val commentKey = Key<Boolean>("comment")
	this.acceptChildren(object : DetektVisitor() {
		override fun visitComment(comment: PsiComment?) {
			if (comment != null) putUserData(commentKey, true)
		}
	})
	return getUserData(commentKey) ?: false
}
