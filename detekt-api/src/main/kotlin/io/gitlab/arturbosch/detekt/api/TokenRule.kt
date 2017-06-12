package io.gitlab.arturbosch.detekt.api

import org.jetbrains.kotlin.com.intellij.lang.ASTNode
import org.jetbrains.kotlin.com.intellij.psi.PsiWhiteSpace
import org.jetbrains.kotlin.com.intellij.psi.impl.source.tree.LeafPsiElement
import org.jetbrains.kotlin.psi.KtFile

/**
 * A token rule overrides the visiting process and prohibits the use of standard visit functions.
 * As every ASTNode is considered as single object, this abstract rule can be used to define
 * rules which operate on spaces, tabs, semicolons etc.
 *
 * Token rules must define a procedure function which specifies what to do with each ASTNode.
 *
 * @author Artur Bosch
 */
@Suppress("EmptyFunctionBlock")
abstract class TokenRule(id: String,
						 config: Config = Config.empty) : Rule(id, config) {

	override fun visit(context: Context, root: KtFile) {
		ifRuleActive {
			preVisit(context, root)
			root.node.visitTokens { procedure(context, it) }
			postVisit(context, root)
		}
	}

	/**
	 * Every ASTNode is considered in isolation. Use 'is' operator to search for wished elements.
	 */
	open fun procedure(context: Context, node: ASTNode) {
		when (node) {
			is PsiWhiteSpace -> visitSpaces(context, node)
			is LeafPsiElement -> visitLeaf(context, node)

		}
	}

	open fun visitLeaf(context: Context, leaf: LeafPsiElement) {
		if (!leaf.isPartOfString()) {
			when (leaf.text) {
				"}" -> visitRightBrace(context, leaf)
				"{" -> visitLeftBrace(context, leaf)
				":" -> visitColon(context, leaf)
				";" -> visitSemicolon(context, leaf)
				";;" -> visitDoubleSemicolon(context, leaf)
				"," -> visitComma(context, leaf)
			}
		}
	}

	protected open fun visitSemicolon(context: Context, leaf: LeafPsiElement) {
	}

	protected open fun visitDoubleSemicolon(context: Context, leaf: LeafPsiElement) {
	}

	protected open fun visitComma(context: Context, leaf: LeafPsiElement) {
	}

	protected open fun visitColon(context: Context, colon: LeafPsiElement) {
	}

	protected open fun visitLeftBrace(context: Context, brace: LeafPsiElement) {
	}

	protected open fun visitRightBrace(context: Context, brace: LeafPsiElement) {
	}

	protected open fun visitSpaces(context: Context, space: PsiWhiteSpace) {
	}
}
