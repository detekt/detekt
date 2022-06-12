package io.gitlab.arturbosch.detekt.rules.style

import io.gitlab.arturbosch.detekt.rules.KotlinCoreEnvironmentTest
import io.gitlab.arturbosch.detekt.test.assertThat
import io.gitlab.arturbosch.detekt.test.lint
import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.kotlin.cli.jvm.compiler.KotlinCoreEnvironment
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

@KotlinCoreEnvironmentTest
class UnusedPrivateParameterSpec(val env: KotlinCoreEnvironment) {
    val subject = UnusedPrivateMember()

    @Nested
    inner class `function parameters` {
        @Test
        fun `reports single parameters if they are unused in public function`() {
            val code = """
            fun function(unusedParameter: Int): Int {
                return 5
            }
            """

            assertThat(subject.lint(code)).hasSize(1)
        }

        @Test
        fun `does not report single parameters if they used in return statement in public function`() {
            val code = """
            fun function(used: Int): Int {
                return used
            }
            """

            assertThat(subject.lint(code)).isEmpty()
        }

        @Test
        fun `does not report single parameters if they used in public function`() {
            val code = """
            fun function(used: Int) {
                println(used)
            }
            """

            assertThat(subject.lint(code)).isEmpty()
        }

        @Test
        fun `reports parameters that are unused in return statement in public function`() {
            val code = """
            fun function(unusedParameter: Int, usedParameter: Int): Int {
                return usedParameter
            }
            """

            assertThat(subject.lint(code)).hasSize(1)
        }

        @Test
        fun `reports parameters that are unused in public function`() {
            val code = """
            fun function(unusedParameter: Int, usedParameter: Int) {
                println(usedParameter)
            }
            """

            assertThat(subject.lint(code)).hasSize(1)
        }

        @Test
        fun `reports single parameters if they are unused in private function`() {
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

        @Test
        fun `reports two parameters if they are unused and called the same in different methods`() {
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

        @Test
        fun `does not report single parameters if they used in return statement in private function`() {
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

        @Test
        fun `does not report single parameters if they used in private function`() {
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

        @Test
        fun `reports parameters that are unused in return statement in private function`() {
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

        @Test
        fun `reports parameters that are unused in private function`() {
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

    @Nested
    inner class `parameters in primary constructors` {
        @Test
        fun `reports unused parameter`() {
            val code = """
                class Test(unused: Any)
            """
            assertThat(subject.lint(code)).hasSize(1)
        }

        @Test
        fun `does not report used parameter for calling super`() {
            val code = """
                class Parent(val ignored: Any)
                class Test(used: Any) : Parent(used)
            """
            assertThat(subject.lint(code)).isEmpty()
        }

        @Test
        fun `does not report used parameter in init block`() {
            val code = """
                class Test(used: Any) {
                    init {
                        used.toString()
                    }
                }
            """
            assertThat(subject.lint(code)).isEmpty()
        }

        @Test
        fun `does not report used parameter to initialize property`() {
            val code = """
                class Test(used: Any) {
                    val usedString = used.toString()
                }
            """
            assertThat(subject.lint(code)).isEmpty()
        }
    }

    @Nested
    inner class `secondary parameters` {
        @Test
        fun `report unused parameters in secondary constructors`() {
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

    @Nested
    inner class `suppress unused parameter warning annotations` {
        @Test
        fun `does not report annotated parameters`() {
            val code = """
                fun foo(@Suppress("UNUSED_PARAMETER") unused: String){}
            """

            assertThat(subject.lint(code)).isEmpty()
        }

        @Test
        fun `reports parameters without annotation`() {
            val code = """
                fun foo(@Suppress("UNUSED_PARAMETER") unused: String, unusedWithoutAnnotation: String){}
            """

            val lint = subject.lint(code)

            assertThat(lint).hasSize(1)
            assertThat(lint[0].entity.signature).isEqualTo("Test.kt\$unusedWithoutAnnotation: String")
        }

        @Test
        fun `does not report parameters in annotated function`() {
            val code = """
                @Suppress("UNUSED_PARAMETER")
                fun foo(unused: String, otherUnused: String){}
            """

            assertThat(subject.lint(code)).isEmpty()
        }

        @Test
        fun `does not report parameters in annotated class`() {
            val code = """
                @Suppress("UNUSED_PARAMETER")
                class Test {
                    fun foo(unused: String, otherUnused: String){}
                    fun bar(unused: String){}
                }
            """

            assertThat(subject.lint(code)).isEmpty()
        }

        @Test
        fun `does not report parameters in annotated object`() {
            val code = """
                @Suppress("UNUSED_PARAMETER")
                object Test {
                    fun foo(unused: String){}
                }
            """

            assertThat(subject.lint(code)).isEmpty()
        }

        @Test
        fun `does not report parameters in class with annotated outer class`() {
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

        @Test
        fun `does not report parameters in annotated file`() {
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
}
