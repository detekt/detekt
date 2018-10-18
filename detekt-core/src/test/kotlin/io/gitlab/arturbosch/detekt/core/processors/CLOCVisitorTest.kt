package io.gitlab.arturbosch.detekt.core.processors

import io.gitlab.arturbosch.detekt.core.path
import io.gitlab.arturbosch.detekt.test.compileForTest
import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.it

class CLOCVisitorTest : Spek({

	it("commentCases") {
		val file = compileForTest(path.resolve("../comments/CommentsClass.kt"))
		val commentLines = with(file) {
			accept(CLOCVisitor())
			getUserData(commentLinesKey)
		}
		assertThat(commentLines).isEqualTo(10)
	}
})
