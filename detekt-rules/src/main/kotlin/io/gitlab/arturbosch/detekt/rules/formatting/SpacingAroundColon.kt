package io.gitlab.arturbosch.detekt.rules.formatting

import com.intellij.lang.ASTNode
import com.intellij.psi.PsiWhiteSpace
import com.intellij.psi.impl.source.tree.LeafPsiElement
import com.intellij.psi.impl.source.tree.PsiWhiteSpaceImpl
import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.TokenRule
import io.gitlab.arturbosch.detekt.rules.isPartOf
import io.gitlab.arturbosch.detekt.rules.isPartOfString
import org.jetbrains.kotlin.psi.KtAnnotation
import org.jetbrains.kotlin.psi.KtAnnotationEntry
import org.jetbrains.kotlin.psi.KtClassOrObject

/**
 * Based on KtLint.
 *
 * @author Shyiko
 */
class SpacingAroundColon(config: Config) : TokenRule("SpacingAroundColon", Severity.Style, config) {

	override fun procedure(node: ASTNode) {
		if (node is LeafPsiElement && node.textMatches(":") && !node.isPartOfString()) {
			if (node.isPartOf(KtAnnotation::class) || node.isPartOf(KtAnnotationEntry::class)) {
				// todo: enforce "no spacing"
				return
			}
			val missingSpacingBefore = node.prevSibling !is PsiWhiteSpace && node.parent is KtClassOrObject
			val missingSpacingAfter = node.nextSibling !is PsiWhiteSpace
			when {
				missingSpacingBefore && missingSpacingAfter -> {
					addFindings(CodeSmell(id, Entity.from(node), "Missing spacing around \":\""))
					withAutoCorrect {
						node.rawInsertBeforeMe(PsiWhiteSpaceImpl(" "))
						node.rawInsertAfterMe(PsiWhiteSpaceImpl(" "))
					}
				}
				missingSpacingBefore -> {
					addFindings(CodeSmell(id, Entity.from(node), "Missing spacing before \":\""))
					withAutoCorrect {
						node.rawInsertBeforeMe(PsiWhiteSpaceImpl(" "))
					}
				}
				missingSpacingAfter -> {
					addFindings(CodeSmell(id, Entity.from(node), "Missing spacing after \":\""))
					withAutoCorrect {
						node.rawInsertAfterMe(PsiWhiteSpaceImpl(" "))
					}
				}
			}
		}
	}

}