package io.gitlab.arturbosch.detekt.rules.complexity

import io.gitlab.arturbosch.detekt.test.TestConfig
import io.gitlab.arturbosch.detekt.test.assertThat
import io.gitlab.arturbosch.detekt.test.compileAndLint
import org.junit.jupiter.api.Test

class NestedBlockDepthSpec {

    private val defaultAllowedDepth = 3
    private val defaultConfig = TestConfig("allowedDepth" to defaultAllowedDepth)
    private val subject = NestedBlockDepth(defaultConfig)

    @Test
    fun `should ignore class nesting levels`() {
        val code = """
            class NestedClasses {
            
                class InnerClass {
            
                    class NestedInnerClass {
            
                        fun nestedLongMethod() {
                            if (true) {
                                if (true) {
                                    if (true) {
                                        5.run {
                                            this.let {
                                                listOf(1, 2, 3).map { it * 2 }
                                                    .groupBy(Int::toString, Int::toString)
                                            }
                                        }
                                    }
                                }
                            }
            
                            try {
                                for (i in 1..5) {
                                    when (i) {
                                        1 -> print(1)
                                    }
                                }
                            } finally {
            
                            }
            
                            fun nestedLocalMethod() {
                                println()
                            }
                            nestedLocalMethod()
                        }
                    }
                }
            }
        """.trimIndent()
        val findings = subject.compileAndLint(code)

        assertThat(findings).hasSize(1)
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
        """.trimIndent()
        val findings = subject.compileAndLint(code)

        assertThat(findings)
            .hasSize(1)
            .hasTextLocations(4 to 5)
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
        """.trimIndent()
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
        """.trimIndent()
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
        """.trimIndent()
        assertThat(subject.compileAndLint(code)).hasSize(1)
    }
}
