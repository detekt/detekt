package io.gitlab.arturbosch.detekt.core

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

/**
 * @author Artur Bosch
 */
internal class KtCompilerTest {

	@Test
	fun ktFileHasExtraUserData() {
		val ktCompiler = KtCompiler(path)

		val ktFile = ktCompiler.compile(path.resolve("Default.kt"))

		assertThat(ktFile.getUserData(KtCompiler.LINE_SEPARATOR)).isEqualTo("\n")
		assertThat(ktFile.getUserData(KtCompiler.RELATIVE_PATH)).isEqualTo("Default.kt")
	}

}