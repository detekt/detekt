package io.gitlab.arturbosch.detekt.rules.style

import io.gitlab.arturbosch.detekt.rules.setupKotlinEnvironment
import io.gitlab.arturbosch.detekt.test.assertThat
import io.gitlab.arturbosch.detekt.test.lint
import org.assertj.core.api.Assertions.assertThat
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

class UnusedPrivateParameterSpec : Spek({
    setupKotlinEnvironment()

    val subject by memoized { UnusedPrivateMember() }

    describe("function parameters") {
        it("reports single parameters if they are unused") {
            val code = """
            fun function(unusedParameter: Int): Int {
                return 5
            }
            """

            assertThat(subject.lint(code)).hasSize(1)
        }

        it("does not report single parameters if they used in return statement") {
            val code = """
            fun function(used: Int): Int {
                return used
            }
            """

            assertThat(subject.lint(code)).isEmpty()
        }

        it("does not report single parameters if they used in function") {
            val code = """
            fun function(used: Int) {
                println(used)
            }
            """

            assertThat(subject.lint(code)).isEmpty()
        }

        it("reports parameters that are unused in return statement") {
            val code = """
            fun function(unusedParameter: Int, usedParameter: Int): Int {
                return usedParameter
            }
            """

            assertThat(subject.lint(code)).hasSize(1)
        }

        it("reports parameters that are unused in function") {
            val code = """
            fun function(unusedParameter: Int, usedParameter: Int) {
                println(usedParameter)
            }
            """

            assertThat(subject.lint(code)).hasSize(1)
        }

        it("reports single parameters if they are unused") {
            val code = """
            class Test {
                val value = usedMethod(1)

                private fun usedMethod(unusedParameter: Int): Int {
                    return 5
                }
            }
            """

            assertThat(subject.lint(code)).hasSize(1)
        }

        it("reports two parameters if they are unused and called the same in different methods") {
            val code = """
            class Test {
                val value = usedMethod(1)
                val value2 = usedMethod2(1)

                private fun usedMethod(unusedParameter: Int): Int {
                    return 5
                }

                private fun usedMethod2(unusedParameter: Int) {
                    return 5
                }
            }
            """

            assertThat(subject.lint(code)).hasSize(2)
        }

        it("does not report single parameters if they used in return statement") {
            val code = """
            class Test {
                val value = usedMethod(1)

                private fun usedMethod(used: Int): Int {
                    return used
                }
            }
            """

            assertThat(subject.lint(code)).isEmpty()
        }

        it("does not report single parameters if they used in function") {
            val code = """
            class Test {
                val value = usedMethod(1)

                private fun usedMethod(used: Int) {
                    println(used)
                }
            }
            """

            assertThat(subject.lint(code)).isEmpty()
        }

        it("reports parameters that are unused in return statement") {
            val code = """
            class Test {
                val value = usedMethod(1, 2)

                private fun usedMethod(unusedParameter: Int, usedParameter: Int): Int {
                    return usedParameter
                }
            }
            """

            assertThat(subject.lint(code)).hasSize(1)
        }

        it("reports parameters that are unused in function") {
            val code = """
            class Test {
                val value = usedMethod(1, 2)

                private fun usedMethod(unusedParameter: Int, usedParameter: Int) {
                    println(usedParameter)
                }
            }
            """

            assertThat(subject.lint(code)).hasSize(1)
        }
    }

    describe("parameters in primary constructors") {
        it("reports unused parameter") {
            val code = """
                class Test(unused: Any)
            """
            assertThat(subject.lint(code)).hasSize(1)
        }

        it("does not report used parameter for calling super") {
            val code = """
                class Parent(val ignored: Any)
                class Test(used: Any) : Parent(used)
            """
            assertThat(subject.lint(code)).isEmpty()
        }

        it("does not report used parameter in init block") {
            val code = """
                class Test(used: Any) {
                    init {
                        used.toString()
                    }
                }
            """
            assertThat(subject.lint(code)).isEmpty()
        }

        it("does not report used parameter to initialize property") {
            val code = """
                class Test(used: Any) {
                    val usedString = used.toString()
                }
            """
            assertThat(subject.lint(code)).isEmpty()
        }
    }

    describe("secondary parameters") {
        it("report unused parameters in secondary constructors") {
            val code = """
                private class ClassWithSecondaryConstructor {
                    constructor(used: Any, unused: Any) {
                        used.toString()
                    }

                    // this is actually unused, but clashes with the other constructor
                    constructor(used: Any)
                }
            """
            assertThat(subject.lint(code)).hasSize(1)
        }
    }

    describe("suppress unused parameter warning annotations") {
        it("does not report annotated parameters") {
            val code = """
                fun foo(@Suppress("UNUSED_PARAMETER") unused: String){}
            """

            assertThat(subject.lint(code)).isEmpty()
        }

        it("reports parameters without annotation") {
            val code = """
                fun foo(@Suppress("UNUSED_PARAMETER") unused: String, unusedWithoutAnnotation: String){}
            """

            val lint = subject.lint(code)

            assertThat(lint).hasSize(1)
            assertThat(lint[0].entity.signature).isEqualTo("Test.kt\$unusedWithoutAnnotation: String")
        }

        it("does not report parameters in annotated function") {
            val code = """
                @Suppress("UNUSED_PARAMETER")
                fun foo(unused: String, otherUnused: String){}
            """

            assertThat(subject.lint(code)).isEmpty()
        }

        it("does not report parameters in annotated class") {
            val code = """
                @Suppress("UNUSED_PARAMETER")
                class Test {
                    fun foo(unused: String, otherUnused: String){}
                    fun bar(unused: String){}
                }
            """

            assertThat(subject.lint(code)).isEmpty()
        }

        it("does not report parameters in annotated object") {
            val code = """
                @Suppress("UNUSED_PARAMETER")
                object Test {
                    fun foo(unused: String){}
                }
            """

            assertThat(subject.lint(code)).isEmpty()
        }

        it("does not report parameters in class with annotated outer class") {
            val code = """
                @Suppress("UNUSED_PARAMETER")
                class Test {
                    fun foo(unused: String){}

                    class InnerTest {
                        fun bar(unused: String){}
                    }
                }
            """

            assertThat(subject.lint(code)).isEmpty()
        }

        it("does not report parameters in annotated file") {
            val code = """
                @file:Suppress("UNUSED_PARAMETER")

                class Test {
                    fun foo(unused: String){}

                    class InnerTest {
                        fun bar(unused: String){}
                    }
                }
            """

            assertThat(subject.lint(code)).isEmpty()
        }
    }
})
