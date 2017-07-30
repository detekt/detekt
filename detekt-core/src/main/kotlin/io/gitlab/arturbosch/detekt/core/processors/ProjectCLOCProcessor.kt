package io.gitlab.arturbosch.detekt.core.processors

import io.gitlab.arturbosch.detekt.api.DetektVisitor
import org.jetbrains.kotlin.com.intellij.openapi.util.Key
import org.jetbrains.kotlin.com.intellij.psi.PsiComment
import org.jetbrains.kotlin.psi.KtDeclaration
import org.jetbrains.kotlin.psi.KtFile

class ProjectCLOCProcessor : AbstractProcessor() {

	override val key = NUMBER_OF_COMMENT_LINES_KEY
	override val visitor = CLOCVisitor()
}

val NUMBER_OF_COMMENT_LINES_KEY = Key<Int>("cloc")

class CLOCVisitor : DetektVisitor() {

	private var count = 0

	override fun visitKtFile(file: KtFile) {
		super.visitKtFile(file)
		file.putUserData(NUMBER_OF_COMMENT_LINES_KEY, count)
	}

	override fun visitComment(comment: PsiComment?) {
		if (comment != null) {
			count += comment.text.split('\n').size
		}
	}

	override fun visitDeclaration(dcl: KtDeclaration) {
		val text = dcl.docComment?.text
		if (text != null) {
			count += text.split('\n').size
		}
		super.visitDeclaration(dcl)
	}
}
