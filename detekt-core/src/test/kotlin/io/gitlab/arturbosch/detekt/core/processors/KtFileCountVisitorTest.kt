package io.gitlab.arturbosch.detekt.core.processors

import io.gitlab.arturbosch.detekt.core.path
import io.gitlab.arturbosch.detekt.test.compileForTest
import org.assertj.core.api.Assertions
import org.jetbrains.kotlin.psi.KtFile
import org.junit.jupiter.api.Test

class KtFileCountVisitorTest {

	@Test
	fun twoFiles() {
		val files = arrayOf(
				compileForTest(path.resolve("Default.kt")),
				compileForTest(path.resolve("Test.kt"))
		)
		val count = files
				.map { getData(it) }
				.sum()
		Assertions.assertThat(count).isEqualTo(2)
	}

	private fun getData(file: KtFile): Int {
		return with(file) {
			accept(KtFileCountVisitor())
			getUserData(numberOfFilesKey)!!
		}
	}
}
