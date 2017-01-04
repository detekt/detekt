package io.gitlab.arturbosch.detekt.formatting

import com.intellij.psi.PsiElement
import com.intellij.psi.PsiWhiteSpace
import com.intellij.psi.impl.source.tree.LeafPsiElement
import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Rule
import org.jetbrains.kotlin.psi.KtNamedFunction
import org.jetbrains.kotlin.psi.KtTypeReference

/**
 * @author Artur Bosch
 */
class OptionalUnit(config: Config = Config.empty) : Rule("OptionalUnit", Severity.Style, config) {

	override fun visitNamedFunction(function: KtNamedFunction) {
		if (function.funKeyword == null) return
		val colon = function.colon
		if (function.hasDeclaredReturnType() && colon != null) {
			val typeReference = function.typeReference
			typeReference?.typeElement?.text?.let {
				if (it == "Unit") {
					addFindings(CodeSmell(id, Entity.from(typeReference)))
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