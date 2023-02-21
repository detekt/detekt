package io.gitlab.arturbosch.detekt.rules.complexity

import io.gitlab.arturbosch.detekt.test.TestConfig
import io.gitlab.arturbosch.detekt.test.assertThat
import io.gitlab.arturbosch.detekt.test.compileAndLint
import org.junit.jupiter.api.Test

class LabeledExpressionSpec {

    private val subject = LabeledExpression()

    @Test
    fun `reports break and continue labels`() {
        val code = """
            fun f() {
                loop@ for (i in 1..3) {
                    for (j in 1..3) {
                        if (j == 4) break@loop
                        if (j == 5) continue@loop
                    }
                }
            }
        """.trimIndent()
        assertThat(subject.compileAndLint(code)).hasSize(3)
    }

    @Test
    fun `reports implicit return label`() {
        val code = """
            fun f(range: IntRange) {
                range.forEach {
                    if (it == 5) return@forEach
                    println(it)
                }
            }
        """.trimIndent()

        val findings = subject.compileAndLint(code)

        assertThat(findings).hasSize(1)
        assertThat(findings).hasStartSourceLocation(3, 28)
    }

    @Test
    fun `reports explicit return label`() {
        val code = """
            fun f(range: IntRange) {
                range.forEach label@{
                    if (it == 5) return@label
                    println(it)
                }
            }
        """.trimIndent()
        assertThat(subject.compileAndLint(code)).hasSize(2)
    }

    @Test
    fun `reports labels referencing inner and outer class`() {
        val code = """
            class Outer {
                inner class Inner {
                    fun f() {
                        val i = this@Inner
                        emptyList<Int>().forEach Outer@{
                            // references forEach label and not outer class
                            if (it == 5) return@Outer
                            println(it)
                        }
                    }
                }
            }
        """.trimIndent()
        assertThat(subject.compileAndLint(code)).hasSize(3)
    }

    @Test
    fun `does not report inner class referencing outer class`() {
        val code = """
        class Outer {
            inner class Inner {
                fun f() {
                    print(this@Outer)
                }
            }
        }
        """.trimIndent()
        assertThat(subject.compileAndLint(code)).isEmpty()
    }

    @Test
    fun `does not report inner class referencing outer class in extension function`() {
        val code = """
        class Outer {
            inner class Inner {
                fun Int.f() {
                    print(this@Inner)
                    print(this@Outer)
                }
            }
        }
        """.trimIndent()
        assertThat(subject.compileAndLint(code)).isEmpty()
    }

    @Test
    fun `does not report nested class referencing outer class in extension function`() {
        val code = """
        class Outer {
            class Nested {
                fun Int.f() {
                    print(this@Nested)
                }
            }
        }
        """.trimIndent()
        assertThat(subject.compileAndLint(code)).isEmpty()
    }

    @Test
    fun `does not report inner classes referencing outer class in extension function`() {
        val code = """
        class Outer {
            inner class Inner {
                inner class InnerInner {
                    fun f() {
                        print(this@Outer)
                        print(this@Inner)
                    }
                    fun Int.f() {
                        print(this@Inner)
                        print(this@InnerInner)
                    }
                }
            }
        }
        """.trimIndent()
        assertThat(subject.compileAndLint(code)).isEmpty()
    }

    @Test
    fun `does not report excluded label`() {
        val code = """
            fun f() {
                loop@ for (i in 1..5) {}
            }
        """.trimIndent()
        val config = TestConfig("ignoredLabels" to listOf("loop"))
        val findings = LabeledExpression(config).compileAndLint(code)
        assertThat(findings).isEmpty()
    }

    @Test
    fun `does not report excluded label config with string`() {
        val code = """
            fun f() {
                loop@ for (i in 1..5) {}
            }
        """.trimIndent()
        val config = TestConfig("ignoredLabels" to "loop")
        val findings = LabeledExpression(config).compileAndLint(code)
        assertThat(findings).isEmpty()
    }

    @Test
    fun `does not report excluded label config with leading and trailing wildcard`() {
        val code = """
            fun f() {
                loop@ for (i in 1..5) {}
            }
        """.trimIndent()
        val config = TestConfig("ignoredLabels" to "*loop*,other")
        val findings = LabeledExpression(config).compileAndLint(code)
        assertThat(findings).isEmpty()
    }
}
