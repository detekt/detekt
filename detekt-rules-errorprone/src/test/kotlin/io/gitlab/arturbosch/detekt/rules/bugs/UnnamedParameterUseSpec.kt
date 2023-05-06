package io.gitlab.arturbosch.detekt.rules.bugs

import io.gitlab.arturbosch.detekt.rules.KotlinCoreEnvironmentTest
import io.gitlab.arturbosch.detekt.test.TestConfig
import io.gitlab.arturbosch.detekt.test.assertThat
import io.gitlab.arturbosch.detekt.test.compileAndLintWithContext
import org.jetbrains.kotlin.cli.jvm.compiler.KotlinCoreEnvironment
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

@KotlinCoreEnvironmentTest
class UnnamedParameterUseSpec(private val env: KotlinCoreEnvironment) {
    private val subject = UnnamedParameterUse()

    @Test
    fun `does not report for no param function call`() {
        val code = """
            fun f() {
                println("f called")
            }

            fun test() {
                f()
            }
        """.trimIndent()
        assertThat(subject.compileAndLintWithContext(env, code)).isEmpty()
    }

    @Test
    fun `does not report single unnamed param by default`() {
        val code = """
            fun f(enabled: Boolean) {
                println(enabled)
            }

            fun test() {
                f(true)
            }
        """.trimIndent()
        assertThat(subject.compileAndLintWithContext(env, code)).isEmpty()
    }

    @Test
    fun `does not report single unnamed param when ignoreSingleParamUse is true`() {
        val code = """
            fun f(enabled: Boolean) {
                println(enabled)
            }

            fun test() {
                f(true)
            }
        """.trimIndent()
        assertThat(getSubject(ignoreSingleParamUse = true).compileAndLintWithContext(env, code)).isEmpty()
    }

    @Test
    fun `report unnamed param when ignoreSingleParamUse is true and allowAdjacentDifferentTypeParams is false`() {
        val code = """
            fun f(enabled: Boolean) {
                println(enabled)
            }

            fun test() {
                f(true)
            }
        """.trimIndent()
        assertThat(
            getSubject(
                ignoreSingleParamUse = true,
                allowAdjacentDifferentTypeParams = false
            ).compileAndLintWithContext(env, code)
        ).isEmpty()
    }

    @Test
    fun `does report two unnamed param by default`() {
        val code = """
            fun f(enabled: Boolean, shouldLog: Boolean) {
                if (shouldLog) println(enabled)
            }

            fun test() {
                f(false, true)
            }
        """.trimIndent()
        assertThat(
            subject.compileAndLintWithContext(env, code)
        ).hasSize(1)
    }

    @Test
    fun `does not report two unnamed param when both are same`() {
        val code = """
            fun f(enabled: Boolean, shouldLog: Boolean) {
                if (shouldLog) println(enabled)
            }

            fun test() {
                f(true, true)
            }
        """.trimIndent()
        assertThat(
            subject.compileAndLintWithContext(env, code)
        ).isEmpty()
    }

    @Test
    fun `does not report two non adjacent unnamed param by default `() {
        val code = """
            fun f(enabled: Boolean, tag: String, shouldLog: Boolean) {
                if (shouldLog) println(tag + enabled.toString())
            }

            fun test() {
                f(true, "", true)
            }
        """.trimIndent()
        assertThat(
            subject.compileAndLintWithContext(env, code)
        ).isEmpty()
    }

    @Test
    fun `does report two non adjacent unnamed param when allowAdjacentDifferentTypeParams is false`() {
        val code = """
            fun f(enabled: Boolean, tag: String, shouldLog: Boolean) {
                if (shouldLog) println(tag + enabled.toString())
            }

            fun test() {
                f(true, "", true)
            }
        """.trimIndent()
        assertThat(
            getSubject(allowAdjacentDifferentTypeParams = false).compileAndLintWithContext(env, code)
        ).hasSize(1)
    }

    @Test
    fun `does report two adjacent param with different type with one being supertype`() {
        val code = """
            fun f(enabled: Boolean, tag1: String, tag2: CharSequence) {
                /* no-op */
            }


            fun test() {
                f(enabled = true, "tag1", "tag2")
            }
        """.trimIndent()
        assertThat(
            getSubject(allowAdjacentDifferentTypeParams = true).compileAndLintWithContext(env, code)
        ).hasSize(1)
    }

    @Test
    fun `does not report two adjacent same type param with one named param with adjacent allowed`() {
        val code = """
            fun f(tag: String, enabled: Boolean, shouldLog: Boolean) {
                if (shouldLog) println(tag + enabled.toString())
            }

            fun test() {
                f("", enabled = true, true)
            }
        """.trimIndent()
        assertThat(
            getSubject(allowAdjacentDifferentTypeParams = true).compileAndLintWithContext(env, code)
        ).isEmpty()
    }

    @Test
    fun `does not report two adjacent same type param with one named last param with adjacent allowed`() {
        val code = """
            fun f(tag: String, enabled: Boolean, shouldLog: Boolean) {
                if (shouldLog) println(tag + enabled.toString())
            }

            fun test() {
                f("", enabled = true, true)
            }
        """.trimIndent()
        assertThat(
            getSubject(allowAdjacentDifferentTypeParams = true).compileAndLintWithContext(env, code)
        ).isEmpty()
    }

    @Test
    fun `does not report non kotlin function`() {
        val code = """
            fun test() {
                System.out.write(byteArrayOf(), 9, 9)
            }
        """.trimIndent()
        assertThat(
            getSubject(allowAdjacentDifferentTypeParams = true).compileAndLintWithContext(env, code)
        ).isEmpty()
    }

    @Test
    fun `does not report single trailing lambda param`() {
        val code = """
            fun useLambda(lambda: () -> Unit) {
                /* no-op */
            }

            fun test() {
                useLambda { 
                    /* no-op */
                }
            }
        """.trimIndent()
        assertThat(
            getSubject(
                ignoreSingleParamUse = false,
                allowAdjacentDifferentTypeParams = false
            ).compileAndLintWithContext(env, code)
        ).isEmpty()
    }

    @Test
    fun `does report single lambda is used inside braces`() {
        val code = """
            fun useLambda(lambda: () -> Unit) {
                /* no-op */
            }

            fun test() {
                useLambda({ 
                    /* no-op */
                })
            }
        """.trimIndent()
        assertThat(
            getSubject(
                ignoreSingleParamUse = false,
                allowAdjacentDifferentTypeParams = false
            ).compileAndLintWithContext(env, code)
        ).hasSize(1)
    }

    @Test
    fun `does report when two lambda is used inside braces`() {
        val code = """
            fun useLambda(preCondition: () -> Boolean, lambda: () -> Boolean) {
                /* no-op */
            }

            fun test() {
                useLambda(
                    { true },
                    { false }
                )
            }
        """.trimIndent()
        assertThat(
            subject.compileAndLintWithContext(env, code)
        ).hasSize(1)
    }

    @Test
    fun `does report when two lambda is used inside braces with adjacent allowed`() {
        val code = """
            fun useLambda(preCondition: () -> Boolean, lambda: () -> Boolean) {
                /* no-op */
            }

            fun test() {
                useLambda(
                    { true },
                    { false }
                )
            }
        """.trimIndent()
        assertThat(
            getSubject(allowAdjacentDifferentTypeParams = true).compileAndLintWithContext(env, code)
        ).hasSize(1)
    }

    @Test
    fun `does report class constructor is called without name`() {
        val code = """
            class A(x: Int, y: Int)

            fun test() {
                A(1, 2)
            }
        """.trimIndent()
        assertThat(
            getSubject(allowAdjacentDifferentTypeParams = true).compileAndLintWithContext(env, code)
        ).hasSize(1)
    }

    @Test
    fun `does report class constructor is called in delegation`() {
        val code = """
            interface Behaviour

            class B(i: Int) : Behaviour

            class A(i: Int, j: Int): Behaviour by B(i)

            fun test() {
                A(1, 2)
            }
        """.trimIndent()
        assertThat(
            getSubject(allowAdjacentDifferentTypeParams = true).compileAndLintWithContext(env, code)
        ).hasSize(1)
    }

    @Nested
    inner class WithVararg {
        @Test
        fun `does not report vararg call without name`() {
            val code = """
                fun a(a: Int, vararg b: Int) {
                    /* no-op */
                }

                fun test() {
                    a(9, 0)
                }
            """.trimIndent()
            assertThat(subject.compileAndLintWithContext(env, code)).isEmpty()
        }

        @Test
        fun `does not report vararg call with first param with name`() {
            val code = """
                fun a(a: Int, vararg b: Int) {
                    /* no-op */
                }

                fun test() {
                    a(a = 9, 0)
                }
            """.trimIndent()
            assertThat(subject.compileAndLintWithContext(env, code)).isEmpty()
        }

        @Test
        fun `does report vararg call with first param with name with ignoreSingleParamUse = false`() {
            val code = """
                fun a(a: Int, vararg b: Int) {
                    /* no-op */
                }

                fun test() {
                    a(9, 0)
                }
            """.trimIndent()
            assertThat(
                getSubject(
                    ignoreSingleParamUse = false,
                    allowAdjacentDifferentTypeParams = false
                ).compileAndLintWithContext(env, code)
            ).hasSize(1)
        }

        @Test
        fun `does not report one param and one vararg param with multiple values`() {
            val code = """
                fun a(a: Int, vararg b: Int) {
                    /* no-op */
                }

                fun test() {
                    a(9, 0, 0, 0)
                }
            """.trimIndent()
            assertThat(subject.compileAndLintWithContext(env, code)).isEmpty()
        }

        @Test
        fun `does report one param and one vararg param with multiple values`() {
            val code = """
                fun a(a: Int, vararg b: Int) {
                    /* no-op */
                }

                fun test() {
                    a(9, 0, 0, 0)
                }
            """.trimIndent()
            assertThat(
                getSubject(
                    ignoreSingleParamUse = false,
                    allowAdjacentDifferentTypeParams = false
                ).compileAndLintWithContext(env, code)
            ).hasSize(1)
        }

        @Test
        fun `does not report vararg call with multiple values corresponding to same vararg`() {
            val code = """
                fun a(vararg a: String) {
                    /* no-op */
                }

                fun test() {
                    a("", "", "")
                }
            """.trimIndent()
            assertThat(subject.compileAndLintWithContext(env, code)).isEmpty()
        }

        @Test
        fun `does not report vararg param with multiple with ignoreSingleParamUse = true`() {
            val code = """
                fun a(vararg a: String) {
                    /* no-op */
                }

                fun test() {
                    a("", "", "")
                }
            """.trimIndent()
            assertThat(getSubject(ignoreSingleParamUse = false).compileAndLintWithContext(env, code)).isEmpty()
        }

        @Test
        fun `does report vararg call used with spread with arrayOf type`() {
            val code = """
                fun a(vararg a: String) {
                    /* no-op */
                }

                fun test(arr: Array<String>) {
                    a(*arr)
                }
            """.trimIndent()
            assertThat(getSubject(ignoreSingleParamUse = false).compileAndLintWithContext(env, code)).isEmpty()
        }
    }

    private fun getSubject(
        ignoreSingleParamUse: Boolean = true,
        allowAdjacentDifferentTypeParams: Boolean = true,
    ): UnnamedParameterUse {
        return UnnamedParameterUse(
            TestConfig(
                ALLOW_SINGLE_PARAM_USE to ignoreSingleParamUse,
                ALLOW_NON_ADJACENT_PARAM to allowAdjacentDifferentTypeParams,
            )
        )
    }

    companion object {
        private const val ALLOW_SINGLE_PARAM_USE = "allowSingleParamUse"
        private const val ALLOW_NON_ADJACENT_PARAM = "allowAdjacentDifferentTypeParams"
    }
}
