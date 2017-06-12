package io.gitlab.arturbosch.detekt.formatting

import io.gitlab.arturbosch.detekt.api.*
import org.jetbrains.kotlin.com.intellij.psi.PsiElement
import org.jetbrains.kotlin.com.intellij.psi.PsiWhiteSpace
import org.jetbrains.kotlin.com.intellij.psi.impl.source.tree.LeafPsiElement
import org.jetbrains.kotlin.psi.KtNamedFunction
import org.jetbrains.kotlin.psi.KtTypeReference

/**
 * @author Artur Bosch
 */
class OptionalUnit(config: Config = Config.empty) : Rule("OptionalUnit", config) {

	override fun visitNamedFunction(context: Context, function: KtNamedFunction) {
		if (function.funKeyword == null) return
		val colon = function.colon
		if (function.hasDeclaredReturnType() && colon != null) {
			val typeReference = function.typeReference
			typeReference?.typeElement?.text?.let {
				if (it == "Unit") {
					context.report(CodeSmell(ISSUE, Entity.from(typeReference)))
					withAutoCorrect {
						deleteUnitReturnType(colon, typeReference)
					}
				}
			}
		}
		super.visitNamedFunction(context, function)
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

	companion object {
		val ISSUE = Issue("OptionalUnit", Issue.Severity.Style)
	}
}