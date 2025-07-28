package io.gitlab.arturbosch.detekt.rules.style

import dev.detekt.test.utils.KotlinEnvironmentContainer
import dev.detekt.api.Config
import dev.detekt.test.utils.KotlinCoreEnvironmentTest
import dev.detekt.test.lintWithContext
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

@KotlinCoreEnvironmentTest
class UnnecessaryAnySpec(val env: KotlinEnvironmentContainer) {
    val subject = UnnecessaryAny(Config.empty)

    @Test
    fun `reports any which is used for checking presence of a element`() {
        val code = """
            fun test(list: List<Int>, value: Int) {
                list.any { it == value }
            }
        """.trimIndent()
        val actual = subject.lintWithContext(env, code)
        assertThat(actual)
            .hasSize(1)
            .allMatch {
                it.message == "Use `contains` instead of `any {  }` call to check the presence of the element"
            }
    }

    @Test
    fun `does not report when contains is used`() {
        val code = """
            fun test(list: List<Int>, value: Int) {
                list.contains(value)
            }
        """.trimIndent()
        val actual = subject.lintWithContext(env, code)
        assertThat(actual).isEmpty()
    }

    @Test
    fun `does not report when any is used with multiline lambda body to find a element`() {
        val code = """
            fun test(list: List<Int>, value: Int) {
                list.any {
                    println(it)
                    it == value
                }
            }
        """.trimIndent()
        val actual = subject.lintWithContext(env, code)
        assertThat(actual).isEmpty()
    }

    @Test
    fun `reports any with modified value which is used for checking presence of a element`() {
        val code = """
            fun test(list: List<Int>, value: Int) {
                list.any { it == value * value }
            }
        """.trimIndent()
        val actual = subject.lintWithContext(env, code)
        assertThat(actual).hasSize(1)
    }

    @Test
    fun `does not report any with modified it which is used for checking presence of a element`() {
        val code = """
            fun test(list: List<Int>, value: Int) {
                list.any { it * it == value }
            }
        """.trimIndent()
        val actual = subject.lintWithContext(env, code)
        assertThat(actual).isEmpty()
    }

    @Test
    fun `does not report any when it is printed with chain`() {
        val code = """
            fun test(list: List<Int>, value: Int) {
                list.any { it.also { println(it) } == value }
            }
        """.trimIndent()
        val actual = subject.lintWithContext(env, code)
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
        val actual = subject.lintWithContext(env, code)
        assertThat(actual).isEmpty()
    }

    @Test
    fun `reports any with explicit return which is used for checking presence of a element`() {
        val code = """
            fun test(list: List<Int>, value: Int) {
                list.any { return@any it == value }
            }
        """.trimIndent()
        val actual = subject.lintWithContext(env, code)
        assertThat(actual).hasSize(1)
    }

    @Test
    fun `reports any with equals call which is used for checking presence of a element`() {
        val code = """
            fun test(list: List<Int>, value: Int) {
                list.any { it.equals(value) }
            }
        """.trimIndent()
        val actual = subject.lintWithContext(env, code)
        assertThat(actual).hasSize(1)
    }

    @Test
    fun `reports any with reverse equals call which is used for checking presence of a element`() {
        val code = """
            fun test(list: List<Int>, value: Int) {
                list.any { value.equals(it) }
            }
        """.trimIndent()
        val actual = subject.lintWithContext(env, code)
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
        val actual = subject.lintWithContext(env, code)
        assertThat(actual).isEmpty()
    }

    @Test
    fun `does not report any with safe call which is used for checking presence of a element`() {
        val code = """
            fun test(list: List<Int?>, value: Int?) {
                list.any { value?.equals(it) == true }
            }
        """.trimIndent()
        val actual = subject.lintWithContext(env, code)
        assertThat(actual).isEmpty()
    }

    @Test
    fun `does not report any when it is modified`() {
        val code = """
            fun test(list: List<Int>, value: Int) {
                list.any { value.equals(2 * it) }
            }
        """.trimIndent()
        val actual = subject.lintWithContext(env, code)
        assertThat(actual).isEmpty()
    }

    @Test
    fun `does report any when it is not used`() {
        val code = """
            fun test(list: List<Int>, value: Int) {
                list.any { value == 2 }
                list.any { 2 == value }
            }
        """.trimIndent()
        val actual = subject.lintWithContext(env, code)
        assertThat(actual).hasSize(2)
            .allMatch { it.message == "`any {  }` expression can be omitted" }
    }

    @Test
    fun `does report any when it is not used for multiline lambda`() {
        val code = """
            fun test(list: List<Int>, value: Int) {
                list.any { 
                    println(value)
                    value == 2
                }
            }
        """.trimIndent()
        val actual = subject.lintWithContext(env, code)
        assertThat(actual).hasSize(1)
            .allMatch { it.message == "`any {  }` expression can be omitted" }
    }

    @Test
    fun `does not report any when one of destructed value of it is used`() {
        val code = """
            fun test(list: List<Pair<Int, Int>>, value: Int) {
                list.any { (a, b) ->
                    value == b
                }

                list.any { (_, b) ->
                    value == b
                }
            }
        """.trimIndent()
        val actual = subject.lintWithContext(env, code)
        assertThat(actual).isEmpty()
    }

    @Test
    fun `does report any when none of destructed value of it is used`() {
        val code = """
            fun test(list: List<Pair<Int, Int>>, value: Int) {
                list.any { (a, b) ->
                    false
                }

                list.any { (_, b) ->
                    true
                }
            }
        """.trimIndent()
        val actual = subject.lintWithContext(env, code)
        assertThat(actual)
            .hasSize(2)
            .allMatch { it.message == "`any {  }` expression can be omitted" }
    }

    @Test
    fun `does report any when it is not used in presence of nested it with different value`() {
        val code = """
            fun test(list: List<Int>, value: Int) {
                list.any { value.also { println(it) } == 2 }
            }
        """.trimIndent()
        val actual = subject.lintWithContext(env, code)
        assertThat(actual).hasSize(1)
    }

    @Test
    fun `does report any when it is not used returns boolean directly`() {
        val code = """
            fun test(list: List<Int>, value: Boolean) {
                list.any { true }
                list.any { value }
                list.any { value.also { println(it) } }
            }
        """.trimIndent()
        val actual = subject.lintWithContext(env, code)
        assertThat(actual).hasSize(3)
    }

    @Test
    fun `does report nested violations`() {
        val code = """
            fun test(list: List<Boolean>, values: List<Int>, value: Int) {
                list.any { it == values.any { it == value } }
            }
        """.trimIndent()
        val actual = subject.lintWithContext(env, code)
        assertThat(actual).hasSize(2)
    }

    @Test
    fun `does report nested violations with named it`() {
        val code = """
            fun test(list: List<Boolean>, values: List<Int>, value: Int) {
            list.any { outerIt -> outerIt == values.any { it == value } }
        }
        """.trimIndent()
        val actual = subject.lintWithContext(env, code)
        assertThat(actual).hasSize(2)
    }

    @Test
    fun `does report any when value is modified`() {
        val code = """
            fun test(list: List<Int>, value: Int) {
                list.any { it.equals(2 * value) }
            }
        """.trimIndent()
        val actual = subject.lintWithContext(env, code)
        assertThat(actual).hasSize(1)
    }

    @Test
    fun `reports any with reverse condition which is used for checking presence of a element`() {
        val code = """
            fun test(list: List<Int>, value: Int) {
                list.any { value == it }
            }
        """.trimIndent()
        val actual = subject.lintWithContext(env, code)
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
        val actual = subject.lintWithContext(env, code)
        assertThat(actual).hasSize(1)
    }

    @Test
    fun `reports any with explicit lambda signature which is used for checking presence of a element`() {
        val code = """
            fun test(list: List<Int>, value: Int) {
                list.any { it: Int -> it == value }
            }
        """.trimIndent()
        val actual = subject.lintWithContext(env, code)
        assertThat(actual).hasSize(1)
    }

    @Test
    fun `reports any with name argument lambda which is used for checking presence of a element`() {
        val code = """
            fun test(list: List<Int>, value: Int) {
                list.any(predicate = { it == value })
            }
        """.trimIndent()
        val actual = subject.lintWithContext(env, code)
        assertThat(actual).hasSize(1)
    }

    @Test
    fun `reports any with lambda inside parenthesis signature which is used for checking presence of a element`() {
        val code = """
            fun test(list: List<Int>, value: Int) {
                list.any({ it == value })
            }
        """.trimIndent()
        val actual = subject.lintWithContext(env, code)
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
        val actual = subject.lintWithContext(env, code)
        assertThat(actual).hasSize(1)
    }

    @Test
    fun `reports any with anonymous function with expression body which is used for checking presence of a element`() {
        val code = """
            fun test(list: List<Int>, value: Int) {
                list.any(fun(it: Int) = it == value)
            }
        """.trimIndent()
        val actual = subject.lintWithContext(env, code)
        assertThat(actual).hasSize(1)
    }

    @Test
    fun `reports any with anonymous function with expression body when it is not used`() {
        val code = """
            fun test(list: List<Int>, value: Int) {
                list.any(fun(value: Int) = false)
            }
        """.trimIndent()
        val actual = subject.lintWithContext(env, code)
        assertThat(actual).hasSize(1)
            .allMatch { it.message == "`any {  }` expression can be omitted" }
    }

    @Test
    fun `does not report any with no parameter`() {
        val code = """
            fun test(list: List<Int>, value: Int) {
                list.any()
            }
        """.trimIndent()
        val actual = subject.lintWithContext(env, code)
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
        val actual = subject.lintWithContext(env, code)
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
        val actual = subject.lintWithContext(env, code)
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
        val actual = subject.lintWithContext(env, code)
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
        val actual = subject.lintWithContext(env, code)
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
        val actual = subject.lintWithContext(env, code)
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
        val actual = subject.lintWithContext(env, code)
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
        val actual = subject.lintWithContext(env, code)
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
            val actual = subject.lintWithContext(env, code)
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
            val actual = subject.lintWithContext(env, code)
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
            val actual = subject.lintWithContext(env, code)
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
            val actual = subject.lintWithContext(env, code)
            assertThat(actual).isEmpty()
        }

        @Test
        fun `does not report any with param which is used for checking presence of a element`() {
            val code = """
                fun test(list: List<Int>, value: Int, predicate: (Int) -> Boolean) {
                    list.any(predicate)
                }
            """.trimIndent()
            val actual = subject.lintWithContext(env, code)
            assertThat(actual).isEmpty()
        }

        @Test
        fun `does not report any with param with default which is used for checking presence of a element`() {
            val code = """
                fun test(list: List<Int>, value: Int, predicate: (Int) -> Boolean = { it == value }) {
                    list.any(predicate)
                }
            """.trimIndent()
            val actual = subject.lintWithContext(env, code)
            assertThat(actual).isEmpty()
        }
    }
}
