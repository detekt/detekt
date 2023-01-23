package io.gitlab.arturbosch.detekt.rules.style

import io.gitlab.arturbosch.detekt.rules.KotlinCoreEnvironmentTest
import io.gitlab.arturbosch.detekt.test.assertThat
import io.gitlab.arturbosch.detekt.test.compileAndLintWithContext
import io.gitlab.arturbosch.detekt.test.lint
import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.kotlin.cli.jvm.compiler.KotlinCoreEnvironment
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

@KotlinCoreEnvironmentTest
class UnusedPrivateParameterSpec(val env: KotlinCoreEnvironment) {
    val subject = UnusedParameter()

    @Nested
    inner class `function parameters` {
        @Test
        fun `reports single parameters if they are unused in public function`() {
            val code = """
            fun function(unusedParameter: Int): Int {
                return 5
            }
            """.trimIndent()

            assertThat(subject.lint(code)).hasSize(1)
        }

        @Test
        fun `does not report single parameters if they used in return statement in public function`() {
            val code = """
            fun function(used: Int): Int {
                return used
            }
            """.trimIndent()

            assertThat(subject.lint(code)).isEmpty()
        }

        @Test
        fun `does not report single parameters if they used in public function`() {
            val code = """
            fun function(used: Int) {
                println(used)
            }
            """.trimIndent()

            assertThat(subject.lint(code)).isEmpty()
        }

        @Test
        fun `reports parameters that are unused in return statement in public function`() {
            val code = """
            fun function(unusedParameter: Int, usedParameter: Int): Int {
                return usedParameter
            }
            """.trimIndent()

            assertThat(subject.lint(code)).hasSize(1)
        }

        @Test
        fun `reports parameters that are unused in public function`() {
            val code = """
            fun function(unusedParameter: Int, usedParameter: Int) {
                println(usedParameter)
            }
            """.trimIndent()

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
            """.trimIndent()

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
            """.trimIndent()

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
            """.trimIndent()

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
            """.trimIndent()

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
            """.trimIndent()

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
            """.trimIndent()

            assertThat(subject.lint(code)).hasSize(1)
        }
    }

    @Nested
    inner class `suppress unused parameter warning annotations` {
        @Test
        fun `does not report annotated parameters`() {
            val code = """
                fun foo(@Suppress("UNUSED_PARAMETER") unused: String){}
            """.trimIndent()

            assertThat(subject.lint(code)).isEmpty()
        }

        @Test
        fun `reports parameters without annotation`() {
            val code = """
                fun foo(@Suppress("UNUSED_PARAMETER") unused: String, unusedWithoutAnnotation: String){}
            """.trimIndent()

            val lint = subject.lint(code)

            assertThat(lint).hasSize(1)
            assertThat(lint[0].entity.signature).isEqualTo("Test.kt\$unusedWithoutAnnotation: String")
        }

        @Test
        fun `does not report parameters in annotated function`() {
            val code = """
                @Suppress("UNUSED_PARAMETER")
                fun foo(unused: String, otherUnused: String){}
            """.trimIndent()

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
            """.trimIndent()

            assertThat(subject.lint(code)).isEmpty()
        }

        @Test
        fun `does not report parameters in annotated object`() {
            val code = """
                @Suppress("UNUSED_PARAMETER")
                object Test {
                    fun foo(unused: String){}
                }
            """.trimIndent()

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
            """.trimIndent()

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
            """.trimIndent()

            assertThat(subject.lint(code)).isEmpty()
        }
    }

    @Nested
    inner class `error messages` {
        @Test
        fun `are specific for function parameters`() {
            val code = """
                fun foo(unused: Int){}
            """.trimIndent()

            val lint = subject.lint(code)

            assertThat(lint.first().message).startsWith("Function parameter")
        }
    }

    @Nested
    inner class `main methods` {

        @Test
        fun `does not report the args parameter of the main function inside an object`() {
            val code = """
                object O {

                    @JvmStatic
                    fun main(args: Array<String>) {
                        println("b")
                    }
                }
            """.trimIndent()
            assertThat(subject.lint(code)).isEmpty()
        }

        @Test
        fun `does not report the args parameter of the main function as top level function`() {
            val code = """
                fun main(args: Array<String>) {
                    println("b")
                }
            """.trimIndent()
            assertThat(subject.lint(code)).isEmpty()
        }
    }

    @Nested
    inner class `backtick identifiers - #5251` {
        @Test
        fun `does not report used backtick parameters`() {
            val code = """
                fun test(`foo bar`: Int) = `foo bar`
            """.trimIndent()
            assertThat(subject.compileAndLintWithContext(env, code)).isEmpty()
        }
    }

    @Nested
    inner class `highlights declaration name - #4916` {

        @Test
        fun parameter() {
            val code = """
                class Test {
                    fun test(
                        /**
                         * kdoc
                         */
                        x: Int
                    ) = 1
                }
            """.trimIndent()
            assertThat(subject.lint(code)).hasSize(1).hasStartSourceLocation(6, 9)
        }
    }

    @Nested
    inner class `parameter with the same name as a named argument #5373` {
        @Test
        fun `unused parameter`() {
            val code = """
                fun foo(modifier: Int) {
                    bar(modifier = 1)
                }
                
                fun bar(modifier: Int) {
                    println(modifier)
                }
            """.trimIndent()
            assertThat(subject.compileAndLintWithContext(env, code)).hasSize(1).hasStartSourceLocation(1, 9)
        }

        @Test
        fun `used parameter`() {
            val code = """
                fun foo(modifier: Int) {
                    bar(modifier = modifier)
                }
                
                fun bar(modifier: Int) {
                    println(modifier)
                }
            """.trimIndent()
            assertThat(subject.compileAndLintWithContext(env, code)).isEmpty()
        }
    }

    @Nested
    inner class `actual functions and classes` {

        @Test
        fun `should not report unused parameters in actual functions`() {
            val code = """
                actual class Foo {
                    actual fun bar(i: Int) {}
                    actual fun baz(i: Int, s: String) {}
                }
            """.trimIndent()
            assertThat(subject.lint(code)).isEmpty()
        }

        @Test
        fun `should not report unused parameters in actual constructors`() {
            val code = """
                actual class Foo actual constructor(bar: String) {}
            """.trimIndent()
            assertThat(subject.lint(code)).isEmpty()
        }
    }
}
