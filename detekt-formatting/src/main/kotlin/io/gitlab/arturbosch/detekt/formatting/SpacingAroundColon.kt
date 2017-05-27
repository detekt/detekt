package io.gitlab.arturbosch.detekt.formatting

import com.intellij.psi.impl.source.tree.LeafPsiElement
import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Rule
import org.jetbrains.kotlin.psi.KtAnnotatedExpression
import org.jetbrains.kotlin.psi.KtClass
import org.jetbrains.kotlin.psi.KtNamedFunction
import org.jetbrains.kotlin.psi.KtObjectDeclaration
import org.jetbrains.kotlin.psi.KtParameter
import org.jetbrains.kotlin.psi.KtVariableDeclaration

/**
 * @author Artur Bosch
 */
class SpacingAroundColon(config: Config) : Rule("SpacingAroundColon", Severity.Style, config) {

	override fun visitColon(element: LeafPsiElement) {
		val parent = element.parent
		val modified = when {
			parent is KtAnnotatedExpression -> false
			parent is KtVariableDeclaration -> element.trimSpacesAfter(autoCorrect)
			parent is KtParameter -> element.trimSpacesAfter(autoCorrect)
			parent is KtNamedFunction -> element.trimSpacesAfter(autoCorrect)
			parent is KtObjectDeclaration && parent.name == null -> element.trimSpacesAround(autoCorrect)
			parent is KtClass -> element.trimSpacesAround(autoCorrect)
			else -> false
		}
		if (modified) {
			addFindings(CodeSmell(id, Entity.from(element)))
		}
	}

}