package dev.detekt.rules.style

import dev.detekt.api.Config
import dev.detekt.test.assertj.assertThat
import dev.detekt.test.junit.KotlinCoreEnvironmentTest
import dev.detekt.test.lintWithContext
import dev.detekt.test.utils.KotlinEnvironmentContainer
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

@KotlinCoreEnvironmentTest
class RedundantHigherOrderMapUsageSpec(val env: KotlinEnvironmentContainer) {
    val subject = RedundantHigherOrderMapUsage(Config.empty)

    @Nested
    inner class `report RedundantHigherOrderMapUsage rule` {
        @Test
        fun simple() {
            val code = """
                fun test() {
                    listOf(1, 2, 3)
                        .filter { it > 1 }
                        .map { it }
                }
            """.trimIndent()
            val findings = subject.lintWithContext(env, code)
            assertThat(findings).singleElement()
                .hasMessage("This 'map' call can be removed.")
            assertThat(findings).singleElement()
                .hasStartSourceLocation(4, 10)
        }

        @Test
        fun `lambda body is not single statement`() {
            val code = """
                fun doSomething() {}
                
                fun test() {
                    listOf(1, 2, 3)
                        .map {
                            doSomething()
                            it
                        }
                        .filter { it > 1 }
                }
            """.trimIndent()
            val findings = subject.lintWithContext(env, code)
            assertThat(findings).singleElement()
                .hasMessage("This 'map' call can be replaced with 'onEach' or 'forEach'.")
            assertThat(findings).singleElement()
                .hasStartSourceLocation(5, 10)
        }

        @Test
        fun `explicit lambda parameter`() {
            val code = """
                fun test() {
                    listOf(1, 2, 3).map { foo -> foo }
                }
            """.trimIndent()
            val findings = subject.lintWithContext(env, code)
            assertThat(findings).hasSize(1)
        }

        @Test
        fun `lambda in argument list`() {
            val code = """
                fun test() {
                    listOf(1).map({ it })
                }
            """.trimIndent()
            val findings = subject.lintWithContext(env, code)
            assertThat(findings).hasSize(1)
        }

        @Test
        fun `labeled return`() {
            val code = """
                fun test(list: List<Int>) {
                    list.map {
                        if (it == 1) return@map it
                        if (it == 2) return@map it
                        it
                    }
                }
            """.trimIndent()
            val findings = subject.lintWithContext(env, code)
            assertThat(findings).hasSize(1)
        }

        @Test
        fun `return for outer function`() {
            val code = """
                fun doSomething() {}
                
                fun test(list: List<Int>): List<Int> {
                    return list.map {
                        if (it == 1) return emptyList()
                        doSomething()
                        it
                    }
                }
            """.trimIndent()
            val findings = subject.lintWithContext(env, code)
            assertThat(findings).hasSize(1)
        }

        @Test
        fun `return for outer lambda`() {
            val code = """
                fun test(list: List<Int>): List<String> {
                    return listOf("a", "b", "c").map outer@{ s ->
                        list.map {
                            if (it == 1) return@outer "-"
                            it
                        }.joinToString("") + s
                    }
                }
            """.trimIndent()
            val findings = subject.lintWithContext(env, code)
            assertThat(findings).hasSize(1)
        }

        @Test
        fun `implicit receiver`() {
            val code = """
                fun List<Int>.test() {
                    map { it }
                }
            """.trimIndent()
            val findings = subject.lintWithContext(env, code)
            assertThat(findings).hasSize(1)
        }

        @Test
        fun `this receiver`() {
            val code = """
                fun List<Int>.test() {
                    this.map { it }
                }
            """.trimIndent()
            val findings = subject.lintWithContext(env, code)
            assertThat(findings).hasSize(1)
        }

        @Test
        fun `mutable list receiver`() {
            val code = """
                fun test() {
                    mutableListOf(1).map { it }
                }
            """.trimIndent()
            val findings = subject.lintWithContext(env, code)
            assertThat(findings).hasSize(1)
        }

        @Test
        fun `sequence receiver`() {
            val code = """
                fun test() {
                    val x:Sequence<Int> = sequenceOf(1).map { it }
                }
            """.trimIndent()
            val findings = subject.lintWithContext(env, code)
            assertThat(findings).hasSize(1)
        }

        @Test
        fun `set receiver`() {
            val code = """
                fun test() {
                    setOf(1).map { it }
                }
            """.trimIndent()
            val findings = subject.lintWithContext(env, code)
            assertThat(findings).singleElement()
                .hasMessage("This 'map' call can be replaced with 'toList'.")
        }
    }

    @Nested
    inner class `does not report RedundantHigherOrderMapUsage rule` {
        @Test
        fun `last statement is not lambda parameter`() {
            val code = """
                fun test() {
                    listOf(1, 2, 3)
                        .filter { it > 1 }
                        .map { it + 1 }
                }
            """.trimIndent()
            val findings = subject.lintWithContext(env, code)
            assertThat(findings).isEmpty()
        }

        @Test
        fun `labeled return is not lambda parameter`() {
            val code = """
                fun test(list: List<Int>) {
                    list.map {
                        if (it == 1) return@map 0
                        it
                    }
                }
            """.trimIndent()
            val findings = subject.lintWithContext(env, code)
            assertThat(findings).isEmpty()
        }

        @Test
        fun `destructuring lambda parameter`() {
            val code = """
                fun test() {
                    listOf(1 to 2).map { (a, b) -> a }
                }
            """.trimIndent()
            val findings = subject.lintWithContext(env, code)
            assertThat(findings).isEmpty()
        }

        @Test
        fun `map receiver`() {
            val code = """
                fun test() {
                    val x: List<Map.Entry<Int, String>> = mapOf(1 to "a").map { it }
                }
            """.trimIndent()
            val findings = subject.lintWithContext(env, code)
            assertThat(findings).isEmpty()
        }
    }
}
