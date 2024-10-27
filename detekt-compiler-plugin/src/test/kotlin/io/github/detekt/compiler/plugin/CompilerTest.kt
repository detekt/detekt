package io.github.detekt.compiler.plugin

import io.github.detekt.compiler.plugin.util.CompilerTestUtils.compile
import io.github.detekt.compiler.plugin.util.assertThat
import org.junit.jupiter.api.Test

class CompilerTest {

    @Test
    fun `with a source file that contains violations`() {
        val result = compile(
            """
                class KClass0 {
                    fun foo() {
                        var x = 3
                        println(x)
                        var y = 4
                        println(y)
                    }
                }
            """.trimIndent(),
            """
                class KClass1 {
                   fun foo() {
                      val x = "test"
                   }
                }
            """.trimIndent()
        )

        assertThat(result)
            .passCompilation(true)
            .passDetekt(false)
            .withViolations(7)
            .withRuleViolationInOrder(
                listOf(
                    "VarCouldBeVal",
                    "MagicNumber",
                    "VarCouldBeVal",
                    "MagicNumber",
                    "NewLineAtEndOfFile",
                    "UnusedVariable",
                    "NewLineAtEndOfFile"
                )
            )
    }

    @Test
    fun `with a source file that contains local suppression`() {
        val result = compile(
            """
                class KClass0 {
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
                class KClass0 {
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
