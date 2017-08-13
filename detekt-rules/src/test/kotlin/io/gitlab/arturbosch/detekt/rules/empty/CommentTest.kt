package io.gitlab.arturbosch.detekt.rules.empty

import io.gitlab.arturbosch.detekt.api.DetektVisitor
import io.gitlab.arturbosch.detekt.test.compileContentForTest
import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.kotlin.psi.KtBlockExpression
import org.jetbrains.kotlin.psi.KtFile
import org.junit.jupiter.api.Test

/**
 * @author Karol Wrótniak
 */
class CommentTest {

	val blockWithComment = "{/*comment*/}"
	val blockWithoutComment = "{val x = 0}"

	@Test
	fun findsCommentInsideBlock() {
		val ktFile = compileContentForTest(blockWithComment)
		assertThat(ktFile.hasCommentInside()).isTrue()
	}

	@Test
	fun doesNotFindComment() {
		val ktFile = compileContentForTest(blockWithoutComment)
		assertThat(ktFile.hasCommentInside()).isFalse()
	}

	private fun KtFile.hasCommentInside(): Boolean {
		var hasCommentInside = false
		this.acceptChildren(object : DetektVisitor() {
			override fun visitBlockExpression(expression: KtBlockExpression) {
				if (expression.hasCommentInside()) hasCommentInside = true
			}
		})
		return hasCommentInside
	}

}
