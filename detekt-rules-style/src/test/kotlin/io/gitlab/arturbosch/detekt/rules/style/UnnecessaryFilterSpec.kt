package io.gitlab.arturbosch.detekt.rules.style

import io.gitlab.arturbosch.detekt.rules.KotlinCoreEnvironmentTest
import io.gitlab.arturbosch.detekt.test.assertThat
import io.gitlab.arturbosch.detekt.test.compileAndLintWithContext
import org.jetbrains.kotlin.cli.jvm.compiler.KotlinCoreEnvironment
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

@KotlinCoreEnvironmentTest
class UnnecessaryFilterSpec(val env: KotlinCoreEnvironment) {
    val subject = UnnecessaryFilter()

    @Nested
    inner class UnnecessaryFilterTest {
        @Test
        fun `Filter with size`() {
            val code = """
                val x = listOf(1, 2, 3)
                    .filter { it > 1 }
                    .size
            """

            val findings = subject.compileAndLintWithContext(env, code)
            assertThat(findings).hasSize(1)
            assertThat(findings[0]).hasMessage("'filter { it > 1 }' can be replaced by 'size { it > 1 }'")
        }

        @Test
        fun `Filter with count`() {
            val code = """
                val x = listOf(1, 2, 3)
                    .filter { it > 1 }
                    .count()
            """

            val findings = subject.compileAndLintWithContext(env, code)
            assertThat(findings).hasSize(1)
        }

        @Test
        fun `Sequence with count`() {
            val code = """
                val x = listOf(1, 2, 3)
                    .asSequence()
                    .map { it * 2 }
                    .filter { it > 1 }
                    .count()
            """

            val findings = subject.compileAndLintWithContext(env, code)
            assertThat(findings).hasSize(1)
        }

        @Test
        fun `None item`() {
            val code = """
                val x = listOf(1, 2, 3)
                    .filter { it > 2 }
                    .isEmpty()
            """

            val findings = subject.compileAndLintWithContext(env, code)
            assertThat(findings).hasSize(1)
        }

        @Test
        fun `Any item`() {
            val code = """
                val x = listOf(1, 2, 3)
                    .filter { it > 2 }
                    .isNotEmpty()
            """

            val findings = subject.compileAndLintWithContext(env, code)
            assertThat(findings).hasSize(1)
        }
    }

    @Nested
    inner class `Correct filter` {
        @Test
        fun `Not stdlib count list function`() {
            val code = """
                fun <T> List<T>.count() : Any{
                    return Any()
                }
                
                val x = listOf<Int>().count()
                val y = listOf<Int>().filter { it > 0 }.count()
            """

            val findings = subject.compileAndLintWithContext(env, code)
            assertThat(findings).isEmpty()
        }

        @Test
        fun `Not stdlib count sequences function`() {
            val code = """
                fun <T> Sequence<T>.count() : Any{
                    return Any()
                }
                
                val x = listOf<Int>().asSequence().count()
                val y = listOf<Int>().asSequence().filter { it > 0 }.count()
            """

            val findings = subject.compileAndLintWithContext(env, code)
            assertThat(findings).isEmpty()
        }

        @Test
        fun `Not stdlib filter function`() {
            val code = """
                fun filter() : List<Any>{
                    return emptyList()
                }
                
                val x = filter().size
            """

            val findings = subject.compileAndLintWithContext(env, code)
            assertThat(findings).isEmpty()
        }

        @Test
        fun `Filter with count`() {
            val code = """
                val x = listOf(1, 2, 3)
                    .count { it > 2 }
            """

            val findings = subject.compileAndLintWithContext(env, code)
            assertThat(findings).isEmpty()
        }

        @Test
        fun `None item`() {
            val code = """
                val x = listOf(1, 2, 3)
                    .none { it > 2 }
            """

            val findings = subject.compileAndLintWithContext(env, code)
            assertThat(findings).isEmpty()
        }

        @Test
        fun `Any item`() {
            val code = """
                val x = listOf(1, 2, 3)
                    .any { it > 2 }
            """

            val findings = subject.compileAndLintWithContext(env, code)
            assertThat(findings).isEmpty()
        }

        @Test
        fun `Sequence with count`() {
            val code = """
                val x = listOf(1, 2, 3)
                    .asSequence()
                    .map { it * 2 }
                    .count { it > 1 }
            """

            val findings = subject.compileAndLintWithContext(env, code)
            assertThat(findings).isEmpty()
        }

        // https://github.com/detekt/detekt/issues/3541#issuecomment-815136831
        @Test
        fun `Size in another statement`() {
            val code = """
                fun foo() {
                    val strings = listOf("abc", "cde", "ader")
                    val filteredStrings = strings.filter { "a" in it }
                    filteredStrings.forEach { println(it) }
                    if (filteredStrings.size > 2) {
                        println("more than two")
                    }
                }
            """
            val findings = subject.compileAndLintWithContext(env, code)
            assertThat(findings).isEmpty()
        }

        // https://github.com/detekt/detekt/issues/3541
        @Test
        @DisplayName("Size/isEmpty()/isNotEmpty() in another statement")
        fun filterUsedInOtherStatement() {
            val code = """
                fun test(queryParts: List<String>, a: List<String>, b: List<String>, c: List<String>) {
                    val dbQueryParts = queryParts.filter { it.length > 1 }.take(3)
                    a.size
                    b.isEmpty()
                    c.isNotEmpty()
                }
            """
            val findings = subject.compileAndLintWithContext(env, code)
            assertThat(findings).isEmpty()
        }
    }
}
