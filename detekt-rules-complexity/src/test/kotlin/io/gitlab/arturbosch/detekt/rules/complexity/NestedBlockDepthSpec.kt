package io.gitlab.arturbosch.detekt.rules.complexity

import io.github.detekt.test.utils.resourceAsPath
import io.gitlab.arturbosch.detekt.api.ThresholdedCodeSmell
import io.gitlab.arturbosch.detekt.test.TestConfig
import io.gitlab.arturbosch.detekt.test.assertThat
import io.gitlab.arturbosch.detekt.test.compileAndLint
import io.gitlab.arturbosch.detekt.test.lint
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class NestedBlockDepthSpec {

    val defaultThreshold = 4
    val defaultConfig = TestConfig(mapOf("threshold" to defaultThreshold))
    val subject = NestedBlockDepth(defaultConfig)

    @Nested
    inner class `nested classes are also considered` {
        @Test
        fun `should detect only the nested large class`() {
            subject.lint(resourceAsPath("NestedClasses.kt"))
            assertThat(subject.findings).hasSize(1)
            assertThat((subject.findings[0] as ThresholdedCodeSmell).value).isEqualTo(5)
        }

        @Test
        fun `should detect too nested block depth`() {
            val code = """
                fun f() {
                    if (true) {
                        if (true) {
                            if (true) {
                                if (true) {
                                }
                            }
                        }
                    }
                }
            """
            val findings = subject.compileAndLint(code)

            assertThat(findings).hasSize(1)
            assertThat(findings).hasTextLocations(4 to 5)
        }

        @Test
        fun `should not detect valid nested block depth`() {
            val code = """
                fun f() {
                    if (true) {
                        if (true) {
                            if (true) {
                            }
                        }
                    }
                }
            """
            assertThat(subject.compileAndLint(code)).isEmpty()
        }

        @Test
        fun `does not report valid nested if else branches`() {
            val code = """
                fun f() {
                    if (true) {
                        if (true) {
                        } else if (true) {
                            if (true) {
                            }
                        }
                    }
                }
            """
            assertThat(subject.compileAndLint(code)).isEmpty()
        }

        @Test
        fun `reports deeply nested if else branches`() {
            val code = """
                fun f() {
                    if (true) {
                        if (true) {
                        } else if (true) {
                            if (true) {
                                if (true) {
                                }
                            }
                        }
                    }
                }
            """
            assertThat(subject.compileAndLint(code)).hasSize(1)
        }
    }
}
