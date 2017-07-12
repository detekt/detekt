package io.gitlab.arturbosch.detekt.formatting

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Dept
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.Severity
import org.jetbrains.kotlin.com.intellij.psi.PsiElement
import org.jetbrains.kotlin.com.intellij.psi.PsiWhiteSpace
import org.jetbrains.kotlin.com.intellij.psi.impl.source.tree.LeafPsiElement
import org.jetbrains.kotlin.psi.KtNamedFunction
import org.jetbrains.kotlin.psi.KtTypeReference

/**
 * @author Artur Bosch
 */
class OptionalUnit(config: Config = Config.empty) : Rule(config) {

	override val issue = Issue(javaClass.simpleName, Severity.Style, "", Dept.FIVE_MINS)

	override fun visitNamedFunction(function: KtNamedFunction) {
		if (function.funKeyword == null) return
		val colon = function.colon
		if (function.hasDeclaredReturnType() && colon != null) {
			val typeReference = function.typeReference
			typeReference?.typeElement?.text?.let {
				if (it == "Unit") {
					report(CodeSmell(issue, Entity.from(typeReference)))
					withAutoCorrect {
						deleteUnitReturnType(colon, typeReference)
					}
				}
			}
		}
		super.visitNamedFunction(function)
	}

	private fun deleteUnitReturnType(colon: PsiElement, typeReference: KtTypeReference) {
		typeReference.delete()
		val whitespace = colon.nextSibling
		if (whitespace is PsiWhiteSpace) {
			val whitespaces = whitespace.text
			if (whitespaces.length > 1) {
				(whitespace as LeafPsiElement).replaceWithText(" ")
			}
		}
		colon.delete()
	}
}
