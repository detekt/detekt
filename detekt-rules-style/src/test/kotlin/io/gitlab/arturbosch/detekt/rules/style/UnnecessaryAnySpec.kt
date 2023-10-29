package io.gitlab.arturbosch.detekt.rules.style

import io.gitlab.arturbosch.detekt.rules.KotlinCoreEnvironmentTest
import io.gitlab.arturbosch.detekt.test.compileAndLintWithContext
import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.kotlin.cli.jvm.compiler.KotlinCoreEnvironment
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

@KotlinCoreEnvironmentTest
class UnnecessaryAnySpec(val env: KotlinCoreEnvironment) {
    val subject = UnnecessaryAny()

    @Test
    fun `reports any which is used for checking presence of a element`() {
        val code = """
            fun test(list: List<Int>, value: Int) {
                list.any { it == value }
            }
        """.trimIndent()
        val actual = subject.compileAndLintWithContext(env, code)
        assertThat(actual).hasSize(1)
        assertThat(actual[0].message)
            .isEqualTo(
                "Use `contains` instead of `any {  }` call to check the presence of the element"
            )
    }

    @Test
    fun `does not report when contains is used`() {
        val code = """
            fun test(list: List<Int>, value: Int) {
                list.contains(value)
            }
        """.trimIndent()
        val actual = subject.compileAndLintWithContext(env, code)
        assertThat(actual).isEmpty()
    }

    @Test
    fun `reports any with modified value which is used for checking presence of a element`() {
        val code = """
            fun test(list: List<Int>, value: Int) {
                list.any { it == value * value }
            }
        """.trimIndent()
        val actual = subject.compileAndLintWithContext(env, code)
        assertThat(actual).hasSize(1)
    }

    @Test
    fun `does not report any with modified it which is used for checking presence of a element`() {
        val code = """
            fun test(list: List<Int>, value: Int) {
                list.any { it * it == value }
            }
        """.trimIndent()
        val actual = subject.compileAndLintWithContext(env, code)
        assertThat(actual).isEmpty()
    }

    @Test
    fun `does not report any value is used with it`() {
        val code = """
            fun test(list: List<Int>, value: Int) {
                list.any { it == it * value }
                list.any { it * value == it }
            }
        """.trimIndent()
        val actual = subject.compileAndLintWithContext(env, code)
        assertThat(actual).isEmpty()
    }

    @Test
    fun `reports any with explicit return which is used for checking presence of a element`() {
        val code = """
            fun test(list: List<Int>, value: Int) {
                list.any { return@any it == value }
            }
        """.trimIndent()
        val actual = subject.compileAndLintWithContext(env, code)
        assertThat(actual).hasSize(1)
    }

    @Test
    fun `reports any with equals call which is used for checking presence of a element`() {
        val code = """
            fun test(list: List<Int>, value: Int) {
                list.any { it.equals(value) }
            }
        """.trimIndent()
        val actual = subject.compileAndLintWithContext(env, code)
        assertThat(actual).hasSize(1)
    }

    @Test
    fun `reports any with reverse equals call which is used for checking presence of a element`() {
        val code = """
            fun test(list: List<Int>, value: Int) {
                list.any { value.equals(it) }
            }
        """.trimIndent()
        val actual = subject.compileAndLintWithContext(env, code)
        assertThat(actual).hasSize(1)
    }

    @Test
    fun `does not report any with custom equals method for checking presence of a element`() {
        val code = """
            fun test(list: List<Custom>, value: Custom) {
                list.any { it.equals(value) }
            }
            class Custom {
                fun equals(value: Custom) = false
            }
        """.trimIndent()
        val actual = subject.compileAndLintWithContext(env, code)
        assertThat(actual).isEmpty()
    }

    @Test
    fun `does not report any with safe call which is used for checking presence of a element`() {
        val code = """
            fun test(list: List<Int>, value: Int?) {
                list.any { value?.equals(it) == true }
            }
        """.trimIndent()
        val actual = subject.compileAndLintWithContext(env, code)
        assertThat(actual).isEmpty()
    }

    @Test
    fun `does not report any when it is modified`() {
        val code = """
            fun test(list: List<Int>, value: Int) {
                list.any { value.equals(2 * it) }
            }
        """.trimIndent()
        val actual = subject.compileAndLintWithContext(env, code)
        assertThat(actual).isEmpty()
    }

    @Test
    fun `does not report any when it is not used`() {
        val code = """
            fun test(list: List<Int>, value: Int) {
                list.any { value == 2 }
                list.any { 2 == value }
            }
        """.trimIndent()
        val actual = subject.compileAndLintWithContext(env, code)
        assertThat(actual).isEmpty()
    }

    @Test
    fun `does report any when value is modified`() {
        val code = """
            fun test(list: List<Int>, value: Int) {
                list.any { it.equals(2 * value) }
            }
        """.trimIndent()
        val actual = subject.compileAndLintWithContext(env, code)
        assertThat(actual).hasSize(1)
    }

    @Test
    fun `reports any with reverse condition which is used for checking presence of a element`() {
        val code = """
            fun test(list: List<Int>, value: Int) {
                list.any { value == it }
            }
        """.trimIndent()
        val actual = subject.compileAndLintWithContext(env, code)
        assertThat(actual).hasSize(1)
    }

    @Test
    fun `reports any with multiline binary which is used for checking presence of a element`() {
        val code = """
            fun test(list: List<Int>, veryVeryVeryVeryVeryVeryVeryBigVariableName: Int) {
                list.any { 
                    it == 
                        veryVeryVeryVeryVeryVeryVeryBigVariableName 
                }
            }
        """.trimIndent()
        val actual = subject.compileAndLintWithContext(env, code)
        assertThat(actual).hasSize(1)
    }

    @Test
    fun `reports any with explicit lambda signature which is used for checking presence of a element`() {
        val code = """
            fun test(list: List<Int>, value: Int) {
                list.any { it: Int -> it == value }
            }
        """.trimIndent()
        val actual = subject.compileAndLintWithContext(env, code)
        assertThat(actual).hasSize(1)
    }

    @Test
    fun `reports any with name argument lambda which is used for checking presence of a element`() {
        val code = """
            fun test(list: List<Int>, value: Int) {
                list.any(predicate = { it == value })
            }
        """.trimIndent()
        val actual = subject.compileAndLintWithContext(env, code)
        assertThat(actual).hasSize(1)
    }

    @Test
    fun `reports any with lambda inside parenthesis signature which is used for checking presence of a element`() {
        val code = """
            fun test(list: List<Int>, value: Int) {
                list.any({ it == value })
            }
        """.trimIndent()
        val actual = subject.compileAndLintWithContext(env, code)
        assertThat(actual).hasSize(1)
    }

    @Test
    fun `reports any with anonymous function which is used for checking presence of a element`() {
        val code = """
            fun test(list: List<Int>, value: Int) {
                list.any(fun(it: Int): Boolean {
                    return it == value
                })
            }
        """.trimIndent()
        val actual = subject.compileAndLintWithContext(env, code)
        assertThat(actual).hasSize(1)
    }

    @Test
    fun `reports any with anonymous function with expression body which is used for checking presence of a element`() {
        val code = """
            fun test(list: List<Int>, value: Int) {
                list.any(fun(it: Int) = it == value)
            }
        """.trimIndent()
        val actual = subject.compileAndLintWithContext(env, code)
        assertThat(actual).hasSize(1)
    }

    @Test
    fun `does not report any with no parameter`() {
        val code = """
            fun test(list: List<Int>, value: Int) {
                list.any()
            }
        """.trimIndent()
        val actual = subject.compileAndLintWithContext(env, code)
        assertThat(actual).isEmpty()
    }

    @Test
    fun `does not report any having extra statement`() {
        val code = """
            fun test(list: List<Int>, value: Int) {
                list.any {
                    println(it)
                    it == value
                }
            }
        """.trimIndent()
        val actual = subject.compileAndLintWithContext(env, code)
        assertThat(actual).isEmpty()
    }

    @Test
    fun `does not report any having not eq condition`() {
        val code = """
            fun test(list: List<Int>, value: Int) {
                list.any {
                    it != value
                }
            }
        """.trimIndent()
        val actual = subject.compileAndLintWithContext(env, code)
        assertThat(actual).isEmpty()
    }

    @Test
    fun `does not report any with generic param where value is not generic`() {
        val code = """
            fun <M>test(list: List<M>, value: Any) {
                list.any {
                    it == value
                }
            }
        """.trimIndent()
        val actual = subject.compileAndLintWithContext(env, code)
        assertThat(actual).isEmpty()
    }

    @Test
    fun `does report any when value is nonnull subtype`() {
        val code = """
            fun test(list: List<Int?>, value: Int) {
                list.any {
                    it == value
                }
            }
        """.trimIndent()
        val actual = subject.compileAndLintWithContext(env, code)
        assertThat(actual).hasSize(1)
    }

    @Test
    fun `does report any when value is subtype`() {
        val code = """
            fun test(list: List<Number>, value: Int) {
                list.any {
                    it == value
                }
            }
        """.trimIndent()
        val actual = subject.compileAndLintWithContext(env, code)
        assertThat(actual).hasSize(1)
    }

    @Test
    fun `does not report when it needs to operated before comparison`() {
        val code = """
            fun test() {
                listOf(1F).any {
                    it.toInt() == 2
                }
            }
        """.trimIndent()
        val actual = subject.compileAndLintWithContext(env, code)
        assertThat(actual).isEmpty()
    }

    @Test
    fun `does report when value needs to operated before comparison`() {
        val code = """
            fun test() {
                listOf(1).any {
                    it == 2.0.toInt()
                }
            }
        """.trimIndent()
        val actual = subject.compileAndLintWithContext(env, code)
        assertThat(actual).hasSize(1)
    }

    @Nested
    inner class `Given predicate is a reference` {
        @Test
        fun `does not report any with predicate which is used for checking presence of a element`() {
            val code = """
                fun test(list: List<Int>, value: Int) {
                    val predicate = { it: Int -> it == value }
                    list.any(predicate)
                }
            """.trimIndent()
            val actual = subject.compileAndLintWithContext(env, code)
            assertThat(actual).isEmpty()
        }

        @Test
        fun `does not report any with predicate with type which is used for checking presence of a element`() {
            val code = """
                fun test(list: List<Int>, value: Int) {
                    val predicate: (Int) -> Boolean = { it == value }
                    list.any(predicate)
                }
            """.trimIndent()
            val actual = subject.compileAndLintWithContext(env, code)
            assertThat(actual).isEmpty()
        }

        @Test
        fun `does not report any with predicate with anonymous fun which is used for checking presence of a element`() {
            val code = """
                fun test(list: List<Int>, value: Int) {
                    val predicate: (Int) -> Boolean = fun(it: Int): Boolean {
                        return it == value
                    }
                    list.any(predicate)
                }
            """.trimIndent()
            val actual = subject.compileAndLintWithContext(env, code)
            assertThat(actual).isEmpty()
        }

        @Test
        fun `does not report any with predicate with parenthesis which is used for checking presence of a element`() {
            val code = """
                fun test(list: List<Int>, value: Int) {
                    val predicate = ({ it: Int -> it == value })
                    list.any(predicate)
                }
            """.trimIndent()
            val actual = subject.compileAndLintWithContext(env, code)
            assertThat(actual).isEmpty()
        }

        @Test
        fun `does not report any with param which is used for checking presence of a element`() {
            val code = """
                fun test(list: List<Int>, value: Int, predicate: (Int) -> Boolean) {
                    list.any(predicate)
                }
            """.trimIndent()
            val actual = subject.compileAndLintWithContext(env, code)
            assertThat(actual).isEmpty()
        }

        @Test
        fun `does not report any with param with default which is used for checking presence of a element`() {
            val code = """
                fun test(list: List<Int>, value: Int, predicate: (Int) -> Boolean = { it == value }) {
                    list.any(predicate)
                }
            """.trimIndent()
            val actual = subject.compileAndLintWithContext(env, code)
            assertThat(actual).isEmpty()
        }
    }
}
