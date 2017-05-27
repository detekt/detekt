package io.gitlab.arturbosch.detekt.formatting

import com.intellij.psi.PsiElement
import com.intellij.psi.impl.source.tree.LeafPsiElement
import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Rule
import org.jetbrains.kotlin.lexer.KtTokens
import org.jetbrains.kotlin.psi.KtAnnotatedExpression
import org.jetbrains.kotlin.psi.KtClass
import org.jetbrains.kotlin.psi.KtObjectDeclaration
import org.jetbrains.kotlin.psi.KtVariableDeclaration

/**
 * @author Artur Bosch
 */
class SpacingAroundColon(config: Config) : Rule("SpacingAroundColon", Severity.Style, config) {

	override fun visitElement(element: PsiElement) {
		val type = element.node.elementType
		if (type == KtTokens.COLON && element is LeafPsiElement) {
			val parent = element.parent
			val modified = when {
				parent is KtAnnotatedExpression -> false
				parent is KtVariableDeclaration -> element.trimSpacesAfter(autoCorrect)
				parent is KtObjectDeclaration && parent.name == null -> element.trimSpacesAfter(autoCorrect)
				parent is KtClass -> element.trimSpacesAround(autoCorrect)
				else -> false
			}
			if (modified) {
				addFindings(CodeSmell(id, Entity.from(element)))
			}
		}

		super.visitElement(element)
	}

}