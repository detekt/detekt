package io.github.detekt.compiler.plugin

import io.github.detekt.compiler.plugin.util.CompilerTestUtils.compile
import io.github.detekt.compiler.plugin.util.assertThat
import org.junit.jupiter.api.Test

class CompilerTest {

    @Test
    fun `with a source file that contains violations`() {
        val result = compile(
            """
                class KClass {
                    fun foo() {
                        val x = 3
                        println(x)
                    }
                }
            """.trimIndent()
        )

        assertThat(result)
            .passCompilation(true)
            .passDetekt(false)
            .withViolations(2)
            .withRuleViolation("MagicNumber", "NewLineAtEndOfFile")
    }

    @Test
    fun `with a source file that contains local suppression`() {
        val result = compile(
            """
                @file:Suppress("NewLineAtEndOfFile")
                class KClass {
                    fun foo() {
                        @Suppress("MagicNumber")
                        val x = 3
                        println(x)
                    }
                }
            """.trimIndent()
        )

        assertThat(result)
            .passCompilation()
            .passDetekt()
            .withNoViolations()
    }

    @Test
    fun `with a source file that does not contain violations`() {
        val result = compile(
            """
                class KClass {
                    fun foo() {
                        println("Hello world :)")
                    }
                }
            """.trimIndent()
        )

        assertThat(result)
            .passCompilation()
            .passDetekt()
            .withNoViolations()
    }
}
