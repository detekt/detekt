package io.gitlab.arturbosch.detekt.rules.style

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.Severity
import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.psi.KtQualifiedExpression
import org.jetbrains.kotlin.psi.KtSafeQualifiedExpression
import org.jetbrains.kotlin.resolve.BindingContext
import org.jetbrains.kotlin.resolve.calls.callUtil.getResolvedCall
import org.jetbrains.kotlin.resolve.calls.callUtil.getType
import org.jetbrains.kotlin.resolve.descriptorUtil.fqNameOrNull
import org.jetbrains.kotlin.types.TypeUtils

class UselessCallOnNotNull(config: Config = Config.empty) : Rule(config) {
	override val issue: Issue = Issue("UselessCallOnNotNull",
			Severity.Performance,
			"This call on not-null type may be reduced or removed.")

	private val uselessFqNames = mapOf("kotlin.collections.orEmpty" to deleteConversion,
			"kotlin.text.orEmpty" to deleteConversion,
			"kotlin.text.isNullOrEmpty" to Conversion("isEmpty"),
			"kotlin.text.isNullOrBlank" to Conversion("isBlank"))

	private val uselessNames = toShortFqNames(uselessFqNames.keys)

	@Suppress("ReturnCount")
	override fun visitQualifiedExpression(expression: KtQualifiedExpression) {
		if (bindingContext == BindingContext.EMPTY) return
		val selector = expression.selectorExpression as? KtCallExpression ?: return
		val calleeExpression = selector.calleeExpression ?: return
		if (calleeExpression.text !in uselessNames) return

		val resolvedCall = expression.getResolvedCall(bindingContext) ?: return
		if (uselessFqNames.contains(resolvedCall.resultingDescriptor.fqNameOrNull()?.asString()))
			suggestConversionIfNeeded(expression, bindingContext)
	}

	private fun suggestConversionIfNeeded(
			expression: KtQualifiedExpression,
			context: BindingContext
	) {
		val safeExpression = expression as? KtSafeQualifiedExpression
		val notNullType = expression.receiverExpression.getType(context)?.let { TypeUtils.isNullableType(it) } == false
		if (notNullType || safeExpression != null) {
			report(CodeSmell(issue, Entity.from(expression), ""))
		}
	}

	private companion object {
		data class Conversion(val replacementName: String? = null)

		val deleteConversion = Conversion()

		private fun toShortFqNames(longNames: Set<String>): Set<String> {
			return longNames.mapTo(mutableSetOf()) { fqName -> fqName.takeLastWhile { it != '.' } }
		}
	}
}
