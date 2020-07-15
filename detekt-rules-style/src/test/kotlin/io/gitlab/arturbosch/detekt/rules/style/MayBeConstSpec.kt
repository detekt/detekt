package io.gitlab.arturbosch.detekt.rules.style

import io.gitlab.arturbosch.detekt.api.SourceLocation
import io.gitlab.arturbosch.detekt.test.assertThat
import io.gitlab.arturbosch.detekt.test.compileAndLint
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

class MayBeConstSpec : Spek({

    val subject by memoized { MayBeConst() }

    describe("MayBeConst rule") {

        context("some valid constants") {
            it("is a valid constant") {
                val code = """
                    object Something {
                        const val X = 42
                    }
                """
                subject.compileAndLint(code)
                assertThat(subject.findings).isEmpty()
            }

            it("is const vals in object") {
                val code = """
                object Test {
                    const val TEST = "Test"
                }
                """
                subject.compileAndLint(code)
                assertThat(subject.findings).isEmpty()
            }

            it("isconst vals in companion objects") {
                val code = """
                class Test {
                    companion object {
                        const val B = 1
                    }
                }
                """
                subject.compileAndLint(code)
                assertThat(subject.findings).isEmpty()
            }

            it("does not report const vals that use other const vals") {
                val code = """
                object Something {
                    const val A = 0
                }

                class Test {
                    companion object {
                        const val B = Something.A + 1
                    }
                }
                """
                subject.compileAndLint(code)
                assertThat(subject.findings).isEmpty()
            }

            it("does not report none const val candidates") {
                val code = """
                object Something {
                    const val a = 0
                }
                val p = Pair(Something.a, Something.a + Something.a)
                val p2 = emptyList<Int>().plus(Something.a)
                """
                subject.compileAndLint(code)
                assertThat(subject.findings).isEmpty()
            }
        }

        context("some vals that could be constants") {
            it("is a simple val") {
                val code = """
                val x = 1
                """
                subject.compileAndLint(code)
                assertThat(subject.findings).hasSize(1).hasSourceLocations(
                    SourceLocation(1, 5)
                )
            }

            it("is a simple JvmField val") {
                val code = """
                @JvmField val x = 1
                """
                subject.compileAndLint(code)
                assertThat(subject.findings).hasSize(1).hasSourceLocations(
                    SourceLocation(1, 15)
                )
            }

            it("is a field in an object") {
                val code = """
                object Test {
                    @JvmField val test = "Test"
                }
                """
                subject.compileAndLint(code)
                assertThat(subject.findings).hasSize(1).hasSourceLocations(
                    SourceLocation(2, 19)
                )
            }

            it("reports vals in companion objects") {
                val code = """
                class Test {
                    companion object {
                        val b = 1
                    }
                }
                """
                subject.compileAndLint(code)
                assertThat(subject.findings).hasSize(1).hasSourceLocations(
                    SourceLocation(3, 13)
                )
            }
        }

        context("vals that can be constants but detekt doesn't handle yet") {
            it("is a constant binary expression") {
                val code = """
                object Something {
                    const val one = 1
                    val two = one * 2
                }
                """
                subject.compileAndLint(code)
                assertThat(subject.findings).hasSize(1).hasSourceLocations(
                    SourceLocation(3, 9)
                )
            }

            it("is a constant binary expression in a companion object") {
                val code = """
                class Test {
                    companion object {
                        const val one = 1
                        val two = one * 2
                    }
                }
                """
                subject.compileAndLint(code)
                assertThat(subject.findings).hasSize(1).hasSourceLocations(
                    SourceLocation(4, 13)
                )
            }

            it("is a nested constant binary expression") {
                val code = """
                object Something {
                    const val one = 1
                    val two = one * 2 + 1
                }
                """
                subject.compileAndLint(code)
                assertThat(subject.findings).hasSize(1).hasSourceLocations(
                    SourceLocation(3, 9)
                )
            }

            it("is a nested constant parenthesised expression") {
                val code = """
                object Something {
                    const val one = 1
                    val two = one * (2 + 1)
                }
                """
                subject.compileAndLint(code)
                assertThat(subject.findings).hasSize(1).hasSourceLocations(
                    SourceLocation(3, 9)
                )
            }

            it("reports vals that use other const vals") {
                val code = """
                object Something {
                    const val a = 0
                
                    @JvmField
                    val b = a + 1
                }
                """
                subject.compileAndLint(code)
                assertThat(subject.findings).hasSize(1).hasSourceLocations(
                    SourceLocation(5, 9)
                )
            }

            it("reports concatenated string vals") {
                val code = """
                object Something {
                    private const val A = "a"
                    private val B = A + "b"
                }
                """
                subject.compileAndLint(code)
                assertThat(subject.findings).hasSize(1).hasSourceLocations(
                    SourceLocation(3, 17)
                )
            }
        }

        context("vals that cannot be constants") {
            it("does not report arrays") {
                val code = "val arr = arrayOf(\"a\", \"b\")"
                subject.compileAndLint(code)
                assertThat(subject.findings).isEmpty()
            }

            it("is a var") {
                val code = "var test = 1"
                subject.compileAndLint(code)
                assertThat(subject.findings).isEmpty()
            }

            it("has a getter") {
                val code = "val withGetter get() = 42"
                subject.compileAndLint(code)
                assertThat(subject.findings).isEmpty()
            }

            it("is initialized to null") {
                val code = "val test = null"
                subject.compileAndLint(code)
                assertThat(subject.findings).isEmpty()
            }

            it("is a JvmField in a class") {
                val code = """
                class Test {
                    @JvmField val a = 3
                }
            """
                subject.compileAndLint(code)
                assertThat(subject.findings).isEmpty()
            }

            it("has some annotation") {
                val code = """
                annotation class A

                @A val a = 55
            """
                subject.compileAndLint(code)
                assertThat(subject.findings).isEmpty()
            }

            it("overrides something") {
                val code = """
                interface Base {
                    val property: Int
                }

                object Derived : Base {
                    override val property = 1
                }
            """
                subject.compileAndLint(code)
                assertThat(subject.findings).isEmpty()
            }

            it("does not detect just a dollar as interpolation") {
                val code = """ val hasDollar = "$" """
                subject.compileAndLint(code)
                assertThat(subject.findings).hasSize(1)
            }

            it("does not report interpolated strings") {
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
                """
                subject.compileAndLint(code)
                assertThat(subject.findings).isEmpty()
            }

            it("does not report vals inside anonymous object declaration") {
                subject.compileAndLint(
                    """
                    fun main() {
                        val versions = object {
                            val prop = ""
                        }
                    }
                """
                )

                assertThat(subject.findings).isEmpty()
            }
        }

        context("some const val candidates in nested objects") {

            it("reports the const val candidates") {
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
                """
                subject.compileAndLint(code)
                assertThat(subject.findings).hasSize(3).hasSourceLocations(
                    SourceLocation(4, 13),
                    SourceLocation(7, 17),
                    SourceLocation(11, 13)
                )
            }
        }
    }
})
