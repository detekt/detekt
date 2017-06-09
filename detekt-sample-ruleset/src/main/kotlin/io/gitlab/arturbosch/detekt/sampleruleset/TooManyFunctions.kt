package io.gitlab.arturbosch.detekt.sampleruleset

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Rule
import org.jetbrains.kotlin.com.intellij.psi.PsiFile
import org.jetbrains.kotlin.psi.KtNamedFunction

/**
 * @author Artur Bosch
 */
class TooManyFunctions : Rule("TooManyFunctions") {

	private var amount: Int = 0

	override fun visitFile(file: PsiFile) {
		super.visitFile(file)
		if (amount > 10) {
			addFindings(CodeSmell(id, severity, Entity.from(file)))
		}
	}

	override fun visitNamedFunction(function: KtNamedFunction) {
		amount++
	}

}