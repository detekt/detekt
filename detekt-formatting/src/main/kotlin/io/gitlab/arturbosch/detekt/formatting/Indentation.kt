package io.gitlab.arturbosch.detekt.formatting

import io.gitlab.arturbosch.detekt.api.*
import org.jetbrains.kotlin.com.intellij.lang.ASTNode
import org.jetbrains.kotlin.com.intellij.openapi.util.TextRange
import org.jetbrains.kotlin.com.intellij.psi.PsiComment
import org.jetbrains.kotlin.com.intellij.psi.PsiWhiteSpace
import org.jetbrains.kotlin.com.intellij.psi.util.PsiTreeUtil
import org.jetbrains.kotlin.diagnostics.DiagnosticUtils
import org.jetbrains.kotlin.psi.KtParameter
import org.jetbrains.kotlin.psi.KtParameterList
import org.jetbrains.kotlin.psi.psiUtil.getNonStrictParentOfType
import org.jetbrains.kotlin.psi.psiUtil.startOffset

/**
 * Adapted from KtLint.
 *
 * @author Artur Bosch
 */
class Indentation(config: Config) : TokenRule("Indentation", config) {

	companion object {
		private const val DEFAULT_INDENT = 4
		private const val INDENT_SIZE = "indentSize"
		val ISSUE = Issue("Indentation", Issue.Severity.Style)
	}

	private var indent = withConfig { valueOrDefault(INDENT_SIZE, DEFAULT_INDENT) }

	override fun procedure(context: Context, node: ASTNode) {
		if (node is PsiWhiteSpace && !node.isPartOf(PsiComment::class)) {
			val split = node.getText().split("\n")
			if (split.size > 1) {
				var offset = node.startOffset + split.first().length + 1
				val firstParameterColumn = lazy {
					val firstParameter = PsiTreeUtil.findChildOfType(
							node.getNonStrictParentOfType(KtParameterList::class.java),
							KtParameter::class.java
					)
					firstParameter?.run {
						DiagnosticUtils.getLineAndColumnInPsiFile(node.containingFile,
								TextRange(startOffset, startOffset)).column
					} ?: 0
				}
				split.dropFirst().forEach {
					if (it.length % indent != 0) {
						if (node.isPartOf(KtParameterList::class) && firstParameterColumn.value != 0) {
							if (firstParameterColumn.value - 1 != it.length) {
								context.report(CodeSmell(ISSUE, Entity.from(node, offset = 1),
										"Unexpected indentation (${it.length}) (" +
												"parameters should be either vertically aligned or indented by the multiple of 4)"))
							}
						} else {
							context.report(CodeSmell(ISSUE, Entity.from(node, offset = 1),
									"Unexpected indentation (${it.length}) (it should be multiple of $indent)"))
						}
					}
					offset += it.length + 1
				}
			}
		}
	}
}
