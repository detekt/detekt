package io.gitlab.arturbosch.detekt.core.processors

import io.gitlab.arturbosch.detekt.core.path
import io.gitlab.arturbosch.detekt.test.compileForTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class CLOCVisitorTest {

	@Test
	fun commentCases() {
		val file = compileForTest(path.resolve("../comments/CommentsClass.kt"))
		val commentLines = with(file) {
			accept(CLOCVisitor())
			getUserData(NUMBER_OF_COMMENT_LINES_KEY)
		}
		assertThat(commentLines).isEqualTo(10)
	}
}
