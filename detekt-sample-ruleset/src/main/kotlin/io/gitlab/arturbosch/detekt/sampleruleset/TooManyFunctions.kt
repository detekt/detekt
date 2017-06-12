package io.gitlab.arturbosch.detekt.sampleruleset

import io.gitlab.arturbosch.detekt.api.*
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.psi.KtNamedFunction

/**
 * @author Artur Bosch
 */
class TooManyFunctions : Rule("TooManyFunctions") {

	private var amount: Int = 0

	override fun visitNamedFunction(context: Context, function: KtNamedFunction) {
		amount++
	}

	override fun postVisit(context: Context, root: KtFile) {
		super.postVisit(context, root)
		if (amount > 10) {
			context.report(CodeSmell(ISSUE, Entity.from(root)))
		}
	}

	companion object {
		val ISSUE = Issue("TooManyFunctions")
	}
}