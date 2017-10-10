package io.gitlab.arturbosch.detekt.sample.extensions.rules

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.Severity
import org.jetbrains.kotlin.com.intellij.psi.PsiFile
import org.jetbrains.kotlin.psi.KtNamedFunction

/**
 * @author Artur Bosch
 */
class TooManyFunctions : Rule() {

	override val issue = Issue(javaClass.simpleName, Severity.CodeSmell, "")

	private var amount: Int = 0

	override fun visitFile(file: PsiFile) {
		super.visitFile(file)
		if (amount > THRESHOLD) {
			report(CodeSmell(issue, Entity.from(file), message = ""))
		}
	}

	override fun visitNamedFunction(function: KtNamedFunction) {
		amount++
	}

}

const val THRESHOLD = 10
