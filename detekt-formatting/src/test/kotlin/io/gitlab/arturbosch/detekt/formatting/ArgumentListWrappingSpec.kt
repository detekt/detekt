package io.gitlab.arturbosch.detekt.formatting

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.formatting.wrappers.ArgumentListWrapping
import io.gitlab.arturbosch.detekt.test.TestConfig
import org.assertj.core.api.Assertions.assertThat
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

class ArgumentListWrappingSpec : Spek({

    describe("ArgumentListWrapping rule") {

        it("reports wrong argument wrapping") {
            val code = """
                val x = f(
                    1,
                    2, 3
                )
            """.trimIndent()
            assertThat(ArgumentListWrapping(Config.empty).lint(code)).hasSize(1)
        }

        it("does not report correct argument list wrapping") {
            val code = """
                val x = f(
                    1,
                    2,
                    3
                )
            """.trimIndent()
            assertThat(ArgumentListWrapping(Config.empty).lint(code)).isEmpty()
        }

        it("does not report when overriding an indentation level config of 1") {
            val code = """
                val x = f(
                 1,
                 2,
                 3
                )
            """.trimIndent()
            val config = TestConfig("indentSize" to "1")
            assertThat(ArgumentListWrapping(config).lint(code)).isEmpty()
        }

        it("reports when max line length is exceeded") {
            val code = """
                val x = f(1111, 2222, 3333)
            """.trimIndent()
            val config = TestConfig("maxLineLength" to "10")
            assertThat(ArgumentListWrapping(config).lint(code)).hasSize(4)
        }
    }
})
