package io.gitlab.arturbosch.detekt.api

import com.intellij.psi.PsiElement
import com.intellij.psi.impl.source.tree.LeafPsiElement
import org.jetbrains.kotlin.psi.KtTreeVisitorVoid

/**
 * Base visitor for detekt rules.
 * Adds additional rules for psi leaf elements.
 *
 * @author Artur Bosch
 */
@Suppress("EmptyFunctionBlock")
open class DetektVisitor : KtTreeVisitorVoid() {

	override fun visitElement(element: PsiElement) {
		if (element is LeafPsiElement) visitLeaf(element)
		super.visitElement(element)
	}

	open fun visitLeaf(element: LeafPsiElement) {
		when (element.text) {
			"}" -> visitLeftBrace(element)
			"{" -> visitRightBrace(element)
			":" -> visitColon(element)
		}
	}

	open fun visitColon(element: LeafPsiElement) {
	}

	open fun visitLeftBrace(element: LeafPsiElement) {
	}

	open fun visitRightBrace(element: LeafPsiElement) {
	}
}