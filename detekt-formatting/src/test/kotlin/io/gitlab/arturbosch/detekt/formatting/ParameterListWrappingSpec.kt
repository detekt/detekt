package io.gitlab.arturbosch.detekt.formatting

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.formatting.wrappers.ParameterListWrapping
import io.gitlab.arturbosch.detekt.test.TestConfig
import org.assertj.core.api.Assertions.assertThat
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

class ParameterListWrappingSpec : Spek({

    val subject by memoized { ParameterListWrapping(Config.empty) }

    describe("ParameterListWrapping rule") {

        describe("indent size equals 1") {

            val code = """
                fun f(
                 a: Int
                ) {}
            """.trimIndent()

            it("reports wrong indent size") {
                assertThat(subject.lint(code)).hasSize(1)
            }

            it("does not report when using an indentation level config of 1") {
                val config = TestConfig("indentSize" to "1")
                assertThat(ParameterListWrapping(config).lint(code)).isEmpty()
            }
        }

        it("does not report correct ParameterListWrapping level") {
            val code = """
                fun f(
                    a: Int
                ) {}
            """.trimIndent()
            assertThat(subject.lint(code)).isEmpty()
        }

        it("reports when max line length is exceeded") {
            val code = """
                fun f(a: Int, b: Int, c: Int) {}
            """.trimIndent()
            val config = TestConfig("maxLineLength" to "10")
            assertThat(ParameterListWrapping(config).lint(code)).hasSize(4)
        }
    }
})
