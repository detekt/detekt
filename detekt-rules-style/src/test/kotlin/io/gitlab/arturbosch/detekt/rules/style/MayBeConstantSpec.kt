package io.gitlab.arturbosch.detekt.rules.style

import dev.detekt.api.Config
import dev.detekt.api.SourceLocation
import dev.detekt.test.assertThat
import dev.detekt.test.lint
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class MayBeConstantSpec {

    val subject = MayBeConstant(Config.empty)

    @Nested
    inner class `some valid constants` {
        @Test
        fun `is a valid constant`() {
            val code = """
                object Something {
                    const val X = 42
                }
            """.trimIndent()
            val findings = subject.lint(code)
            assertThat(findings).isEmpty()
        }

        @Test
        fun `is const vals in object`() {
            val code = """
                object Test {
                    const val TEST = "Test"
                }
            """.trimIndent()
            val findings = subject.lint(code)
            assertThat(findings).isEmpty()
        }

        @Test
        fun `isconst vals in companion objects`() {
            val code = """
                class Test {
                    companion object {
                        const val B = 1
                    }
                }
            """.trimIndent()
            val findings = subject.lint(code)
            assertThat(findings).isEmpty()
        }

        @Test
        fun `does not report const vals that use other const vals`() {
            val code = """
                object Something {
                    const val A = 0
                }
                
                class Test {
                    companion object {
                        const val B = Something.A + 1
                    }
                }
            """.trimIndent()
            val findings = subject.lint(code)
            assertThat(findings).isEmpty()
        }

        @Test
        fun `does not report none const val candidates`() {
            val code = """
                object Something {
                    const val a = 0
                }
                val p = Pair(Something.a, Something.a + Something.a)
                val p2 = emptyList<Int>().plus(Something.a)
            """.trimIndent()
            val findings = subject.lint(code)
            assertThat(findings).isEmpty()
        }
    }

    @Nested
    inner class `some vals that could be constants` {
        @Test
        fun `is a simple val`() {
            val code = """
                val x = 1
            """.trimIndent()
            val findings = subject.lint(code)
            assertThat(findings).singleElement().hasStartSourceLocation(
                SourceLocation(1, 5)
            )
        }

        @Test
        fun `is a simple JvmField val`() {
            val code = """
                @JvmField val x = 1
            """.trimIndent()
            val findings = subject.lint(code)
            assertThat(findings).singleElement().hasStartSourceLocation(
                SourceLocation(1, 15)
            )
        }

        @Test
        fun `is a field in an object`() {
            val code = """
                object Test {
                    @JvmField val test = "Test"
                }
            """.trimIndent()
            val findings = subject.lint(code)
            assertThat(findings).singleElement().hasStartSourceLocation(
                SourceLocation(2, 19)
            )
        }

        @Test
        fun `reports vals in companion objects`() {
            val code = """
                class Test {
                    companion object {
                        val b = 1
                    }
                }
            """.trimIndent()
            val findings = subject.lint(code)
            assertThat(findings).singleElement().hasStartSourceLocation(
                SourceLocation(3, 13)
            )
        }
    }

    @Nested
    inner class `vals that can be constants but detekt doesn't handle yet` {
        @Test
        fun `is a constant binary expression`() {
            val code = """
                object Something {
                    const val one = 1
                    val two = one * 2
                }
            """.trimIndent()
            val findings = subject.lint(code)
            assertThat(findings).singleElement().hasStartSourceLocation(
                SourceLocation(3, 9)
            )
        }

        @Test
        fun `is a constant binary expression in a companion object`() {
            val code = """
                class Test {
                    companion object {
                        const val one = 1
                        val two = one * 2
                    }
                }
            """.trimIndent()
            val findings = subject.lint(code)
            assertThat(findings).singleElement().hasStartSourceLocation(
                SourceLocation(4, 13)
            )
        }

        @Test
        fun `is a nested constant binary expression`() {
            val code = """
                object Something {
                    const val one = 1
                    val two = one * 2 + 1
                }
            """.trimIndent()
            val findings = subject.lint(code)
            assertThat(findings).singleElement().hasStartSourceLocation(
                SourceLocation(3, 9)
            )
        }

        @Test
        fun `is a nested constant parenthesised expression`() {
            val code = """
                object Something {
                    const val one = 1
                    val two = one * (2 + 1)
                }
            """.trimIndent()
            val findings = subject.lint(code)
            assertThat(findings).singleElement().hasStartSourceLocation(
                SourceLocation(3, 9)
            )
        }

        @Test
        fun `reports vals that use other const vals`() {
            val code = """
                object Something {
                    const val a = 0
                
                    @JvmField
                    val b = a + 1
                }
            """.trimIndent()
            val findings = subject.lint(code)
            assertThat(findings).singleElement().hasStartSourceLocation(
                SourceLocation(5, 9)
            )
        }

        @Test
        fun `reports concatenated string vals`() {
            val code = """
                object Something {
                    private const val A = "a"
                    private val B = A + "b"
                }
            """.trimIndent()
            val findings = subject.lint(code)
            assertThat(findings).singleElement().hasStartSourceLocation(
                SourceLocation(3, 17)
            )
        }
    }

    @Nested
    inner class `vals that cannot be constants` {
        @Test
        fun `does not report arrays`() {
            val code = "val arr = arrayOf(\"a\", \"b\")"
            val findings = subject.lint(code)
            assertThat(findings).isEmpty()
        }

        @Test
        fun `is a var`() {
            val code = "var test = 1"
            val findings = subject.lint(code)
            assertThat(findings).isEmpty()
        }

        @Test
        fun `has a getter`() {
            val code = "val withGetter get() = 42"
            val findings = subject.lint(code)
            assertThat(findings).isEmpty()
        }

        @Test
        fun `is initialized to null`() {
            val code = "val test = null"
            val findings = subject.lint(code)
            assertThat(findings).isEmpty()
        }

        @Test
        fun `is a JvmField in a class`() {
            val code = """
                class Test {
                    @JvmField val a = 3
                }
            """.trimIndent()
            val findings = subject.lint(code)
            assertThat(findings).isEmpty()
        }

        @Test
        fun `has some annotation`() {
            val code = """
                annotation class A
                
                @A val a = 55
            """.trimIndent()
            val findings = subject.lint(code)
            assertThat(findings).isEmpty()
        }

        @Test
        fun `overrides something`() {
            val code = """
                interface Base {
                    val property: Int
                }
                
                object Derived : Base {
                    override val property = 1
                }
            """.trimIndent()
            val findings = subject.lint(code)
            assertThat(findings).isEmpty()
        }

        @Test
        fun `does not detect just a dollar as interpolation`() {
            val code = """ val hasDollar = "$" """
            val findings = subject.lint(code)
            assertThat(findings).hasSize(1)
        }

        @Test
        fun `does not report interpolated strings`() {
            val innerCode = "\"\"\"object Test { val TEST = \"Test \$test_var\"}\"\"\""
            val classReference = "\${AnotherClass::class.java.name}"
            val staticReference = "\${AnotherClass.staticVariable}"
            val code = """
                class AnotherClass {
                    companion object {
                        const val staticVariable = ""
                    }
                }
                object Something {
                    val const = "$classReference.EXTRA_DETAILS"
                    private val A = "asdf=$staticReference"
                }
                var test_var = "test"
                var code = $innerCode
            """.trimIndent()
            val findings = subject.lint(code)
            assertThat(findings).isEmpty()
        }

        @Test
        fun `does not report vals inside anonymous object declaration`() {
            val code = """
                fun main() {
                    val versions = object {
                        val prop = ""
                    }
                }
            """.trimIndent()
            val findings = subject.lint(code)
            assertThat(findings).isEmpty()
        }

        @Test
        fun `does not report actual vals`() {
            // Until the [KotlinScriptEngine] can compile KMP, we will only lint.
            val findings = subject.lint("""actual val abc123 = "abc123" """, compile = false)
            assertThat(findings).isEmpty()
        }
    }

    @Nested
    inner class `some const val candidates in nested objects` {

        @Test
        fun `reports the const val candidates`() {
            val code = """
                object Root {
                    const val ROOT_CONST = 1
                    object A1 {
                        val ACONST = ROOT_CONST + 1
                        const val ACONST_1 = 1
                        object B1 {
                            val BCONST = ACONST_1 + 1
                        }
                    }
                    object A2 {
                        val ACONST = ROOT_CONST + 1
                    }
                }
            """.trimIndent()
            val findings = subject.lint(code)
            assertThat(findings).hasSize(3).hasStartSourceLocations(
                SourceLocation(4, 13),
                SourceLocation(7, 17),
                SourceLocation(11, 13)
            )
        }
    }
}
