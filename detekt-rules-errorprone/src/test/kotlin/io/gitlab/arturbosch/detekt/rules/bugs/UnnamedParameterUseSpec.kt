package io.gitlab.arturbosch.detekt.rules.bugs

import io.github.detekt.test.utils.KotlinEnvironmentContainer
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.rules.KotlinCoreEnvironmentTest
import io.gitlab.arturbosch.detekt.test.TestConfig
import io.gitlab.arturbosch.detekt.test.assertThat
import io.gitlab.arturbosch.detekt.test.lintWithContext
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

@KotlinCoreEnvironmentTest
class UnnamedParameterUseSpec(private val env: KotlinEnvironmentContainer) {
    private val subject = UnnamedParameterUse(Config.empty)

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
        assertThat(subject.lintWithContext(env, code)).isEmpty()
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
        assertThat(subject.lintWithContext(env, code)).isEmpty()
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
        assertThat(getSubject(ignoreSingleParamUse = true).lintWithContext(env, code)).isEmpty()
    }

    @Test
    fun `does not report single unnamed param when ignoreSingleParamUse is true and ignoreArgumentsMatchingNames is false`() {
        val code = """
            fun f(enabled: Boolean) {
                println(enabled)
            }

            fun test(active: Boolean) {
                f(active)
            }
        """.trimIndent()
        assertThat(
            getSubject(
                ignoreSingleParamUse = true,
                ignoreArgumentsMatchingNames = false
            ).lintWithContext(env, code)
        ).isEmpty()
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
            ).lintWithContext(env, code)
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
            subject.lintWithContext(env, code)
        ).hasSize(1)
    }

    @Test
    fun `does not report two same named params use by default`() {
        val code = """
            fun f(enabled: Boolean, active: Boolean) {
                if (enabled) println(active)
            }

            fun test(enabled: Boolean, active: Boolean) {
                f(enabled, active)
            }
        """.trimIndent()
        assertThat(subject.lintWithContext(env, code)).isEmpty()
    }

    @Test
    fun `does not report two same named params use by default when allowAdjacentDifferentTypeParams is false`() {
        val code = """
            fun f(enabled: Boolean, active: Boolean) {
                if (enabled) println(active)
            }

            fun test(enabled: Boolean, active: Boolean) {
                f(enabled, active)
            }
        """.trimIndent()
        assertThat(
            getSubject(allowAdjacentDifferentTypeParams = false)
                .lintWithContext(env, code)
        ).isEmpty()
    }

    @Test
    fun `does report two same named params use in wrong order by default`() {
        val code = """
            fun f(enabled: Boolean, active: Boolean) {
                if (enabled) println(active)
            }

            fun test(enabled: Boolean, active: Boolean) {
                f(active, enabled)
            }
        """.trimIndent()
        assertThat(subject.lintWithContext(env, code)).hasSize(1)
    }

    @Test
    fun `does report two same named params use in wrong order by default when allowAdjacentDifferentTypeParams is false`() {
        val code = """
            fun f(enabled: Boolean, active: Boolean) {
                if (enabled) println(active)
            }

            fun test(enabled: Boolean, active: Boolean) {
                f(active, enabled)
            }
        """.trimIndent()
        assertThat(
            getSubject(allowAdjacentDifferentTypeParams = false)
                .lintWithContext(env, code)
        ).hasSize(1)
    }

    @Test
    fun `does not report one same named param with one different named param when allowAdjacentDifferentTypeParams is true`() {
        val code = """
            fun f(enabled: Boolean, active: Boolean) {
                if (enabled) println(active)
            }

            fun test(allowed: Boolean, active: Boolean) {
                f(allowed, active)
            }
        """.trimIndent()
        assertThat(
            getSubject(allowAdjacentDifferentTypeParams = true).lintWithContext(
                env,
                code
            )
        ).isEmpty()
    }

    @Test
    fun `does report one same named param with one different named param when allowAdjacentDifferentTypeParams is false`() {
        val code = """
            fun f(enabled: Boolean, active: Boolean) {
                if (enabled) println(active)
            }

            fun test(allowed: Boolean, active: Boolean) {
                f(allowed, active)
            }
        """.trimIndent()
        assertThat(
            getSubject(allowAdjacentDifferentTypeParams = false).lintWithContext(
                env,
                code
            )
        ).hasSize(1)
    }

    @Test
    fun `report uses the function name as location and message`() {
        val code = """
            fun f(enabled: Boolean, shouldLog: Boolean) {
                if (shouldLog) println(enabled)
            }

            fun test() {
                f(false, true)
            }
        """.trimIndent()
        assertThat(subject.lintWithContext(env, code))
            .hasTextLocations(102 to 103)
            .singleElement()
            .hasMessage("Consider using named parameters in f as they make usage of the function more safe.")
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
            subject.lintWithContext(env, code)
        ).isEmpty()
    }

    @Test
    fun `does not report two non adjacent unnamed param by default`() {
        val code = """
            fun f(enabled: Boolean, tag: String, shouldLog: Boolean) {
                if (shouldLog) println(tag + enabled.toString())
            }

            fun test() {
                f(true, "", false)
            }
        """.trimIndent()
        assertThat(
            subject.lintWithContext(env, code)
        ).isEmpty()
    }

    @Test
    fun `does not report two same type separated by correctly named same type with allowAdjacentDifferentTypeParams true`() {
        val code = """
            fun f(enabled: Boolean, isFatal: Boolean, shouldLog: Boolean) {
                if (shouldLog) println(isFatal.toString() + enabled.toString())
            }

            fun test(isFatal: Boolean) {
                f(true, isFatal, false)
            }
        """.trimIndent()
        assertThat(
            getSubject().lintWithContext(
                env,
                code
            )
        ).isEmpty()
    }

    @Test
    fun `does not report two same type separated by correctly named same type with allowAdjacentDifferentTypeParams false`() {
        val code = """
            fun f(enabled: Boolean, isFatal: Boolean, shouldLog: Boolean) {
                if (shouldLog) println(isFatal.toString() + enabled.toString())
            }

            fun test(isFatal: Boolean) {
                f(true, isFatal, false)
            }
        """.trimIndent()
        assertThat(
            getSubject(allowAdjacentDifferentTypeParams = false).lintWithContext(
                env,
                code
            )
        ).hasSize(1)
    }

    @Test
    fun `does not report two same type separated by correctly named different type with allowAdjacentDifferentTypeParams true`() {
        val code = """
            fun f(enabled: Boolean, level: Int, shouldLog: Boolean) {
                if (shouldLog) println(level.toString() + enabled.toString())
            }

            fun test(level: Int) {
                f(true, level, false)
            }
        """.trimIndent()
        assertThat(
            getSubject().lintWithContext(
                env,
                code
            )
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
            subject.lintWithContext(env, code)
        ).isEmpty()
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
            getSubject(allowAdjacentDifferentTypeParams = true).lintWithContext(env, code)
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
            getSubject(allowAdjacentDifferentTypeParams = true).lintWithContext(env, code)
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
            getSubject(allowAdjacentDifferentTypeParams = true).lintWithContext(env, code)
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
            getSubject(allowAdjacentDifferentTypeParams = true).lintWithContext(env, code)
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
            ).lintWithContext(env, code)
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
            ).lintWithContext(env, code)
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
            subject.lintWithContext(env, code)
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
            getSubject(allowAdjacentDifferentTypeParams = true).lintWithContext(env, code)
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
            getSubject(allowAdjacentDifferentTypeParams = true).lintWithContext(env, code)
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
            getSubject(allowAdjacentDifferentTypeParams = true).lintWithContext(env, code)
        ).hasSize(1)
    }

    @Test
    fun `does not report for a Java method call`() {
        val code = """
            import java.time.LocalDate
            fun main() {
                LocalDate.of(2025, 1, 2)
            }
        """.trimIndent()
        assertThat(
            getSubject(allowAdjacentDifferentTypeParams = true).lintWithContext(env, code)
        ).isEmpty()
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
            assertThat(subject.lintWithContext(env, code)).isEmpty()
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
            assertThat(subject.lintWithContext(env, code)).isEmpty()
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
                ).lintWithContext(env, code)
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
            assertThat(subject.lintWithContext(env, code)).isEmpty()
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
                ).lintWithContext(env, code)
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
            assertThat(subject.lintWithContext(env, code)).isEmpty()
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
            assertThat(
                getSubject(ignoreSingleParamUse = false).lintWithContext(
                    env,
                    code
                )
            ).isEmpty()
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
            assertThat(
                getSubject(ignoreSingleParamUse = false).lintWithContext(
                    env,
                    code
                )
            ).isEmpty()
        }
    }

    @Nested
    inner class FunctionMatcher {
        @Test
        fun `does not report when a function is in ignoreFunctionCall`() {
            val code = """
                package foo
                
                fun listOfChecked(a: String, b: String) = listOf(a, b)
                
                fun foo() {
                    listOfChecked("hello", "world")
                }
            """.trimIndent()
            val findings = getSubject(
                ignoredFunctionCalls = listOf("foo.listOfChecked")
            ).lintWithContext(env, code)
            assertThat(findings).isEmpty()
        }

        @Test
        fun `does not report when a function is in ignoreFunctionCall for specific overload`() {
            val code = """
                fun foo() {
                    maxOf(1, 2, 3)
                }
            """.trimIndent()
            val findings = getSubject(
                ignoredFunctionCalls = listOf("kotlin.comparisons.maxOf(kotlin.Int, kotlin.Int, kotlin.Int)")
            ).lintWithContext(env, code)
            assertThat(findings).isEmpty()
        }
    }

    private fun getSubject(
        ignoreSingleParamUse: Boolean = true,
        allowAdjacentDifferentTypeParams: Boolean = true,
        ignoreArgumentsMatchingNames: Boolean = true,
        ignoredFunctionCalls: List<String> = emptyList(),
    ): UnnamedParameterUse =
        UnnamedParameterUse(
            TestConfig(
                ALLOW_SINGLE_PARAM_USE to ignoreSingleParamUse,
                ALLOW_NON_ADJACENT_PARAM to allowAdjacentDifferentTypeParams,
                IGNORE_ARGUMENTS_MATCHING_NAMES to ignoreArgumentsMatchingNames,
                IGNORE_FUNCTION_CALL to ignoredFunctionCalls,
            )
        )

    companion object {
        private const val ALLOW_SINGLE_PARAM_USE = "allowSingleParamUse"
        private const val ALLOW_NON_ADJACENT_PARAM = "allowAdjacentDifferentTypeParams"
        private const val IGNORE_ARGUMENTS_MATCHING_NAMES = "ignoreArgumentsMatchingNames"
        private const val IGNORE_FUNCTION_CALL = "ignoreFunctionCall"
    }
}
