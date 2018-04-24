package io.gitlab.arturbosch.detekt.formatting

import io.gitlab.arturbosch.detekt.test.TestConfig
import io.gitlab.arturbosch.detekt.test.compileForTest
import io.gitlab.arturbosch.detekt.test.resource
import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.kotlin.com.intellij.openapi.util.text.StringUtilRt
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it
import java.io.File
import java.nio.file.Paths

/**
 * @author Artur Bosch
 */
class KtLintIntegrationSpec : Spek({

	describe("tests integration of formatting") {

		it("should work like KtLint") {
			val fileBefore = compileForTest(Paths.get(resource("before.kt")))
			val expected = StringUtilRt.convertLineSeparators(
					File(resource("after.kt")).readText())

			val ruleSet = FormattingProvider()
					.instance(TestConfig(mapOf("autoCorrect" to "true")))
			val findings = ruleSet.accept(fileBefore)

			assertThat(findings).isNotEmpty
			assertThat(fileBefore.text).isEqualTo(expected)
		}
	}
})
