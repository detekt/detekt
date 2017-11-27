package io.gitlab.arturbosch.detekt.core.processors

import io.gitlab.arturbosch.detekt.core.path
import io.gitlab.arturbosch.detekt.test.compileForTest
import org.assertj.core.api.Assertions
import org.jetbrains.kotlin.psi.KtFile
import org.junit.jupiter.api.Test

class PackageCountVisitorTest {

	@Test
	fun twoClassesInSeparatePackage() {
		val files = arrayOf(
				compileForTest(path.resolve("Default.kt")),
				compileForTest(path.resolve("../empty/EmptyEnum.kt"))
		)
		val count = files
				.map { getData(it) }
				.distinct()
				.count()
		Assertions.assertThat(count).isEqualTo(2)
	}

	private fun getData(file: KtFile): String {
		return with(file) {
			accept(PackageCountVisitor())
			getUserData(numberOfPackagesKey)!!
		}
	}
}
