package io.gitlab.arturbosch.detekt.rules.bugs

import io.gitlab.arturbosch.detekt.test.TestConfig
import io.gitlab.arturbosch.detekt.test.lint
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatExceptionOfType
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe
import java.util.regex.PatternSyntaxException

class LateinitUsageSpec : Spek({

    describe("LateinitUsage rule") {
        val code = """
			package foo

			import kotlin.jvm.JvmField
			import kotlin.SinceKotlin

			class SomeRandomTest {
				@JvmField lateinit var test: Int
				@JvmField @SinceKotlin("1.0.0") lateinit var something: Int
			}
		"""

        it("should report lateinit usages") {
            val findings = LateinitUsage().lint(code)
            assertThat(findings).hasSize(2)
        }

        it("should not report lateinit properties annotated @JvmField") {
            val findings = LateinitUsage(TestConfig(mapOf(LateinitUsage.EXCLUDE_ANNOTATED_PROPERTIES to "JvmField"))).lint(code)
            assertThat(findings).isEmpty()
        }

        it("should not report lateinit properties annotated @JvmField with trailing whitespace") {
            val findings = LateinitUsage(TestConfig(mapOf(LateinitUsage.EXCLUDE_ANNOTATED_PROPERTIES to " JvmField "))).lint(code)
            assertThat(findings).isEmpty()
        }

        it("should not report lateinit properties matching kotlin.*") {
            val findings = LateinitUsage(TestConfig(mapOf(LateinitUsage.EXCLUDE_ANNOTATED_PROPERTIES to "kotlin.*"))).lint(code)
            assertThat(findings).isEmpty()
        }

        it("should not report lateinit properties matching kotlin.jvm.") {
            val findings = LateinitUsage(TestConfig(mapOf(LateinitUsage.EXCLUDE_ANNOTATED_PROPERTIES to "kotlin.jvm."))).lint(code)
            assertThat(findings).isEmpty()
        }

        it("should not report lateinit properties matching kotlin.jvm.*") {
            val findings = LateinitUsage(TestConfig(mapOf(LateinitUsage.EXCLUDE_ANNOTATED_PROPERTIES to "kotlin.jvm.*"))).lint(code)
            assertThat(findings).isEmpty()
        }

        it("should not exclude lateinit properties not matching the exclude pattern") {
            val findings = LateinitUsage(TestConfig(mapOf(LateinitUsage.EXCLUDE_ANNOTATED_PROPERTIES to "IgnoreThis"))).lint(code)
            assertThat(findings).hasSize(2)
        }

        it("should report lateinit properties when ignoreOnClassesPattern does not match") {
            val findings = LateinitUsage(TestConfig(mapOf(LateinitUsage.IGNORE_ON_CLASSES_PATTERN to "[\\w]+Test1234"))).lint(code)
            assertThat(findings).hasSize(2)
        }

        it("should not report lateinit properties when ignoreOnClassesPattern does match") {
            val findings = LateinitUsage(TestConfig(mapOf(LateinitUsage.IGNORE_ON_CLASSES_PATTERN to "[\\w]+Test"))).lint(code)
            assertThat(findings).isEmpty()
        }

        it("should fail when enabled with faulty regex pattern") {
            assertThatExceptionOfType(PatternSyntaxException::class.java).isThrownBy {
                LateinitUsage(TestConfig(mapOf(LateinitUsage.IGNORE_ON_CLASSES_PATTERN to "*Test"))).lint(code)
            }
        }

        it("should not fail when disabled with faulty regex pattern") {
            val configValues = mapOf("active" to "false", LateinitUsage.IGNORE_ON_CLASSES_PATTERN to "*Test")
            val findings = LateinitUsage(TestConfig(configValues)).lint(code)
            assertThat(findings).isEmpty()
        }
    }
})
