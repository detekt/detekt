package io.gitlab.arturbosch.detekt.rules.bugs

import io.gitlab.arturbosch.detekt.test.TestConfig
import io.gitlab.arturbosch.detekt.test.lint
import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.given
import org.jetbrains.spek.api.dsl.it

class LateinitUsageSpec : Spek({

	given("a kt file with lateinit usages") {
		val code = """
			package foo

			import kotlin.jvm.JvmField

			class SomeRandomTest {
				@JvmField lateinit var test: Int
			}
		"""

		it("should report lateinit usages") {
			val findings = LateinitUsage().lint(code)
			assertThat(findings).hasSize(1)
		}

		it("should not report lateinit properties annotated @JvmField") {
			val findings = LateinitUsage(TestConfig(mapOf(LateinitUsage.EXCLUDE_ANNOTATED_PROPERTIES to "JvmField"))).lint(code)
			assertThat(findings).hasSize(0)
		}

		it("should not report lateinit properties annotated @JvmField with trailing whitespace") {
			val findings = LateinitUsage(TestConfig(mapOf(LateinitUsage.EXCLUDE_ANNOTATED_PROPERTIES to " JvmField "))).lint(code)
			assertThat(findings).hasSize(0)
		}

		it("should not report lateinit properties matching kotlin.jvm.") {
			val findings = LateinitUsage(TestConfig(mapOf(LateinitUsage.EXCLUDE_ANNOTATED_PROPERTIES to "kotlin.jvm."))).lint(code)
			assertThat(findings).hasSize(0)
		}

		it("should not report lateinit properties matching kotlin.jvm.*") {
			val findings = LateinitUsage(TestConfig(mapOf(LateinitUsage.EXCLUDE_ANNOTATED_PROPERTIES to "kotlin.jvm.*"))).lint(code)
			assertThat(findings).hasSize(0)
		}

		it("should not exclude lateinit properties not matching the exclude pattern") {
			val findings = LateinitUsage(TestConfig(mapOf(LateinitUsage.EXCLUDE_ANNOTATED_PROPERTIES to "IgnoreThis"))).lint(code)
			assertThat(findings).hasSize(1)
		}

		it("should report lateinit properties when ignoreOnClassesPattern does not match") {
			val findings = LateinitUsage(TestConfig(mapOf(LateinitUsage.IGNORE_ON_CLASSES_PATTERN to "[\\w]+Test1234"))).lint(code)
			assertThat(findings).hasSize(1)
		}

		it("should not report lateinit properties when ignoreOnClassesPattern does match") {
			val findings = LateinitUsage(TestConfig(mapOf(LateinitUsage.IGNORE_ON_CLASSES_PATTERN to "[\\w]+Test"))).lint(code)
			assertThat(findings).hasSize(0)
		}
	}
})
