package io.gitlab.arturbosch.detekt.formatting

import io.gitlab.arturbosch.detekt.formatting.wrappers.MaximumLineLength
import io.gitlab.arturbosch.detekt.test.TestConfig
import org.assertj.core.api.Assertions.assertThat
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe
import java.nio.file.Paths

class MaximumLineLengthSpec : Spek({

    val subject by memoized {
        val config = TestConfig(MaximumLineLength.MAX_LINE_LENGTH to "30")
        MaximumLineLength(config)
    }

    describe("MaximumLineLength rule") {

        describe("a single function") {

            val code = """
                package home.test
                fun f() { /* 123456789012345678901234567890 */ }
            """.trimIndent()

            it("reports line which exceeds the threshold") {
                assertThat(subject.lint(code)).hasSize(1)
            }

            it("reports issues with the filename and package as signature") {
                val finding = subject.lint(
                    code,
                    Paths.get("home", "test", "Test.kt").toString()
                ).first()

                assertThat(finding.entity.signature).isEqualTo("home.test.Test.kt:2")
            }

            it("does not report line which does not exceed the threshold") {
                val config = TestConfig(MaximumLineLength.MAX_LINE_LENGTH to code.length)
                assertThat(MaximumLineLength(config).lint(code)).isEmpty()
            }
        }

        it("does not report line which does not exceed the threshold") {
            val code = "val a = 1"
            assertThat(subject.lint(code)).isEmpty()
        }

        it("reports correct line numbers") {
            val findings = subject.lint(longLines)

            // Note that KtLint's MaximumLineLength rule, in contrast to detekt's MaxLineLength rule, does not report
            // exceeded lines in block comments.
            assertThat(findings).hasSize(2)

            assertThat(findings[0].entity.location.source.line).isEqualTo(8)
            assertThat(findings[1].entity.location.source.line).isEqualTo(14)
        }
    }
})
