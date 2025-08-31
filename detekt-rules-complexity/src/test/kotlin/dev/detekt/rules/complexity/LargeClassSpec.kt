package dev.detekt.rules.complexity

import dev.detekt.api.SourceLocation
import dev.detekt.test.TestConfig
import dev.detekt.test.assertThat
import dev.detekt.test.lint
import org.junit.jupiter.api.Test

private fun subject(allowedLines: Int) = LargeClass(TestConfig("allowedLines" to allowedLines))

class LargeClassSpec {

    @Test
    fun `should detect only the nested large class which exceeds the allowed lines`() {
        val code = """
            class NestedClasses {
            
                private val i = 0
            
                class InnerClass {
            
                    class NestedInnerClass {
            
                        fun nestedMethod() {
                            fun nestedLocalMethod() {
                                println()
                            }
                            nestedLocalMethod()
                        }
                    }
                }
            }
            
            /**
             * Top level members must be skipped for LargeClass rule
             */
            val aTopLevelPropertyOfNestedClasses = 0
        """.trimIndent()
        val findings = subject(allowedLines = 4).lint(code)
        assertThat(findings).singleElement()
            .hasStartSourceLocation(SourceLocation(7, 15))
    }

    @Test
    fun `should not report anything in files without classes`() {
        val code = """
            val i = 0
            
            fun f() {
                println()
                println()
            }
        """.trimIndent()
        val rule = subject(allowedLines = 2)
        assertThat(rule.lint(code)).isEmpty()
    }

    @Test
    fun `should not report a class that has exactly the allowed lines`() {
        val code = """
            class MyClass {
                fun f() {
                    println()
                    println()
                }
            }
        """.trimIndent()

        val rule = subject(allowedLines = 6)

        assertThat(rule.lint(code)).isEmpty()
    }

    @Test
    fun `should not report a class that has less than the allowed lines`() {
        val code = """
            class MyClass {
                fun f() {
                    println()
                }
            }
        """.trimIndent()

        val rule = subject(allowedLines = 6)

        assertThat(rule.lint(code)).isEmpty()
    }
}
