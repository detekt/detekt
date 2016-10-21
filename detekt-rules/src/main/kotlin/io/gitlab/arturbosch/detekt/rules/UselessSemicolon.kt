package io.gitlab.arturbosch.detekt.rules

import com.intellij.lang.ASTNode
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiErrorElement
import com.intellij.psi.PsiWhiteSpace
import com.intellij.psi.impl.source.tree.LeafPsiElement
import com.intellij.psi.util.PsiTreeUtil
import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Location
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.TokenRule
import org.jetbrains.kotlin.psi.KtEnumEntry
import org.jetbrains.kotlin.psi.KtStringTemplateEntry
import org.jetbrains.kotlin.psi.psiUtil.getNonStrictParentOfType

/**
 * @author Artur Bosch
 */
class UselessSemicolon(config: Config = Config.EMPTY) : TokenRule("UselessSemicolon", Rule.Severity.Style, config) {

	override fun procedure(node: ASTNode) {
		val psi = node.psi
		if (psi.isNoErrorElement() && psi.isNotPartOfEnum() && psi.isNotPartOfString()) {
			if (psi.isDoubleSemicolon()) {
				addFindings(CodeSmell(id, Location.of(psi)))
			} else if (psi.isSemicolon()) {
				val nextLeaf = PsiTreeUtil.nextLeaf(psi)
				if (isSemicolonOrEOF(nextLeaf) || nextTokenHasSpaces(nextLeaf)) {
					addFindings(CodeSmell(id, Location.of(psi)))
				}
			}
		}
	}

	private fun PsiElement.isNotPartOfString() = this.getNonStrictParentOfType(KtStringTemplateEntry::class.java) == null
	private fun PsiElement.isNotPartOfEnum() = this.getNonStrictParentOfType(KtEnumEntry::class.java) == null
	private fun PsiElement.isNoErrorElement() = this is LeafPsiElement && this !is PsiErrorElement
	private fun PsiElement.isSemicolon() = this.textMatches(";")
	private fun PsiElement.isDoubleSemicolon() = this.textMatches(";;")

	private fun nextTokenHasSpaces(nextLeaf: PsiElement?) = nextLeaf is PsiWhiteSpace &&
			(nextLeaf.text.contains("\n") || isSemicolonOrEOF(PsiTreeUtil.nextLeaf(nextLeaf)))

	private fun isSemicolonOrEOF(nextLeaf: PsiElement?) = nextLeaf == null || nextLeaf.isSemicolon()

}

