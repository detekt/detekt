package io.gitlab.arturbosch.detekt.rules.style

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.SourceLocation
import io.gitlab.arturbosch.detekt.test.assertThat
import io.gitlab.arturbosch.detekt.test.compileAndLint
import io.gitlab.arturbosch.detekt.test.lint
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
            subject.compileAndLint(code)
            assertThat(subject.findings).isEmpty()
        }

        @Test
        fun `is const vals in object`() {
            val code = """
                object Test {
                    const val TEST = "Test"
                }
            """.trimIndent()
            subject.compileAndLint(code)
            assertThat(subject.findings).isEmpty()
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
            subject.compileAndLint(code)
            assertThat(subject.findings).isEmpty()
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
            subject.compileAndLint(code)
            assertThat(subject.findings).isEmpty()
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
            subject.compileAndLint(code)
            assertThat(subject.findings).isEmpty()
        }
    }

    @Nested
    inner class `some vals that could be constants` {
        @Test
        fun `is a simple val`() {
            val code = """
                val x = 1
            """.trimIndent()
            subject.compileAndLint(code)
            assertThat(subject.findings).hasSize(1).hasStartSourceLocations(
                SourceLocation(1, 5)
            )
        }

        @Test
        fun `is a simple JvmField val`() {
            val code = """
                @JvmField val x = 1
            """.trimIndent()
            subject.compileAndLint(code)
            assertThat(subject.findings).hasSize(1).hasStartSourceLocations(
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
            subject.compileAndLint(code)
            assertThat(subject.findings).hasSize(1).hasStartSourceLocations(
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
            subject.compileAndLint(code)
            assertThat(subject.findings).hasSize(1).hasStartSourceLocations(
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
            subject.compileAndLint(code)
            assertThat(subject.findings).hasSize(1).hasStartSourceLocations(
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
            subject.compileAndLint(code)
            assertThat(subject.findings).hasSize(1).hasStartSourceLocations(
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
            subject.compileAndLint(code)
            assertThat(subject.findings).hasSize(1).hasStartSourceLocations(
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
            subject.compileAndLint(code)
            assertThat(subject.findings).hasSize(1).hasStartSourceLocations(
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
            subject.compileAndLint(code)
            assertThat(subject.findings).hasSize(1).hasStartSourceLocations(
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
            subject.compileAndLint(code)
            assertThat(subject.findings).hasSize(1).hasStartSourceLocations(
                SourceLocation(3, 17)
            )
        }
    }

    @Nested
    inner class `vals that cannot be constants` {
        @Test
        fun `does not report arrays`() {
            val code = "val arr = arrayOf(\"a\", \"b\")"
            subject.compileAndLint(code)
            assertThat(subject.findings).isEmpty()
        }

        @Test
        fun `is a var`() {
            val code = "var test = 1"
            subject.compileAndLint(code)
            assertThat(subject.findings).isEmpty()
        }

        @Test
        fun `has a getter`() {
            val code = "val withGetter get() = 42"
            subject.compileAndLint(code)
            assertThat(subject.findings).isEmpty()
        }

        @Test
        fun `is initialized to null`() {
            val code = "val test = null"
            subject.compileAndLint(code)
            assertThat(subject.findings).isEmpty()
        }

        @Test
        fun `is a JvmField in a class`() {
            val code = """
                class Test {
                    @JvmField val a = 3
                }
            """.trimIndent()
            subject.compileAndLint(code)
            assertThat(subject.findings).isEmpty()
        }

        @Test
        fun `has some annotation`() {
            val code = """
                annotation class A
                
                @A val a = 55
            """.trimIndent()
            subject.compileAndLint(code)
            assertThat(subject.findings).isEmpty()
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
            subject.compileAndLint(code)
            assertThat(subject.findings).isEmpty()
        }

        @Test
        fun `does not detect just a dollar as interpolation`() {
            val code = """ val hasDollar = "$" """
            subject.compileAndLint(code)
            assertThat(subject.findings).hasSize(1)
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
            subject.compileAndLint(code)
            assertThat(subject.findings).isEmpty()
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
            subject.compileAndLint(code)
            assertThat(subject.findings).isEmpty()
        }

        @Test
        fun `does not report actual vals`() {
            // Until the [KotlinScriptEngine] can compile KMP, we will only lint.
            subject.lint("""actual val abc123 = "abc123" """)
            assertThat(subject.findings).isEmpty()
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
            subject.compileAndLint(code)
            assertThat(subject.findings).hasSize(3).hasStartSourceLocations(
                SourceLocation(4, 13),
                SourceLocation(7, 17),
                SourceLocation(11, 13)
            )
        }
    }
}
