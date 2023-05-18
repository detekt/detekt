package io.gitlab.arturbosch.detekt.rules.complexity

import io.gitlab.arturbosch.detekt.api.SourceLocation
import io.gitlab.arturbosch.detekt.test.TestConfig
import io.gitlab.arturbosch.detekt.test.assertThat
import io.gitlab.arturbosch.detekt.test.compileAndLint
import io.gitlab.arturbosch.detekt.test.lint
import org.junit.jupiter.api.Test

private fun subject(threshold: Int) = LargeClass(TestConfig("threshold" to threshold))

class LargeClassSpec {

    @Test
    fun `should detect only the nested large class which exceeds the threshold`() {
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
        val findings = subject(threshold = 4).lint(code)
        assertThat(findings).hasSize(1)
        assertThat(findings).hasStartSourceLocations(SourceLocation(7, 15))
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
        val rule = subject(threshold = 2)
        assertThat(rule.compileAndLint(code)).isEmpty()
    }
}
