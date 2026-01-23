package dev.detekt.rules.style

import dev.detekt.api.Config
import dev.detekt.test.assertj.assertThat
import dev.detekt.test.junit.KotlinCoreEnvironmentTest
import dev.detekt.test.lintWithContext
import dev.detekt.test.utils.KotlinEnvironmentContainer
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

@KotlinCoreEnvironmentTest
class UnnecessaryFilterSpec(val env: KotlinEnvironmentContainer) {
    val subject = UnnecessaryFilter(Config.Empty)

    @Nested
    inner class UnnecessaryFilterTest {
        @Test
        fun `Filter with size`() {
            val code = """
                val x = listOf(1, 2, 3)
                    .filter { it > 1 }
                    .size
            """.trimIndent()

            val findings = subject.lintWithContext(env, code)
            assertThat(findings).singleElement()
                .hasMessage("'filter { it > 1 }' can be replaced by 'count { it > 1 }'")
        }

        @Test
        fun `Filter with count`() {
            val code = """
                val x = listOf(1, 2, 3).filter { it > 1 }.count()
                val y = sequenceOf(1, 2, 3).filter { it > 2 }.count()
                val z = "abc".filter { it > 'a' }.count()
            """.trimIndent()

            val findings = subject.lintWithContext(env, code)
            assertThat(findings).hasSize(3)
        }

        @Test
        fun `Filter with return and count`() {
            val code = """
                fun test(list: List<Int>): Int {
                    val x = list.map { it + 1 }.filter { it > 2 }
                    return x.count()
                }
            """.trimIndent()

            val findings = subject.lintWithContext(env, code)
            assertThat(findings).singleElement()
                .hasMessage("'filter { it > 2 }' can be replaced by 'count { it > 2 }'")
        }

        @Test
        fun `Filter with assignment and count`() {
            val code = """
                fun test(list: List<Int>): Int {
                    val x = list.map { it + 1 }.filter { it > 2 }
                    val count = x.count()
                    return count + 3
                }
            """.trimIndent()

            val findings = subject.lintWithContext(env, code)
            assertThat(findings).singleElement()
                .hasMessage("'filter { it > 2 }' can be replaced by 'count { it > 2 }'")
        }

        @Test
        fun `Filter with isEmpty`() {
            val code = """
                val x = listOf(1, 2, 3).filter { it > 2 }.isEmpty()
                val y = "abc".filter { it > 'a' }.isEmpty()
            """.trimIndent()

            val findings = subject.lintWithContext(env, code)
            assertThat(findings).satisfiesExactlyInAnyOrder(
                { assertThat(it).hasMessage("'filter { it > 2 }' can be replaced by 'none { it > 2 }'") },
                { assertThat(it).hasMessage("'filter { it > 'a' }' can be replaced by 'none { it > 'a' }'") },
            )
        }

        @Test
        fun `Filter with isNotEmpty`() {
            val code = """
                val x = listOf(1, 2, 3).filter { it > 2 }.isNotEmpty()
                val y = "abc".filter { it > 'a' }.isNotEmpty()
            """.trimIndent()

            val findings = subject.lintWithContext(env, code)
            assertThat(findings).satisfiesExactlyInAnyOrder(
                { assertThat(it).hasMessage("'filter { it > 2 }' can be replaced by 'any { it > 2 }'") },
                { assertThat(it).hasMessage("'filter { it > 'a' }' can be replaced by 'any { it > 'a' }'") },
            )
        }

        @Test
        fun `Filter with any`() {
            val code = """
                val x = listOf(1, 2, 3).filter { it > 1 }.any()
                val y = sequenceOf(1, 2, 3).filter { it > 2 }.any()
                val z = "abc".filter { it > 'a' }.any()
            """.trimIndent()
            val findings = subject.lintWithContext(env, code)
            assertThat(findings).hasSize(3)
        }

        @Test
        fun `Filter with none`() {
            val code = """
                val x = listOf(1, 2, 3).filter { it > 1 }.none()
                val y = sequenceOf(1, 2, 3).filter { it > 2 }.none()
                val z = "abc".filter { it > 'a' }.none()
            """.trimIndent()
            val findings = subject.lintWithContext(env, code)
            assertThat(findings).hasSize(3)
        }

        @Test
        fun `Filter with first`() {
            val code = """
                val x = listOf(1, 2, 3).filter { it > 1 }.first()
                val y = sequenceOf(1, 2, 3).filter { it > 2 }.first()
                val z = "abc".filter { it > 'a' }.first()
            """.trimIndent()
            val findings = subject.lintWithContext(env, code)
            assertThat(findings).hasSize(3)
        }

        @Test
        fun `Filter with firstOrNull`() {
            val code = """
                val x = listOf(1, 2, 3).filter { it > 1 }.firstOrNull()
                val y = sequenceOf(1, 2, 3).filter { it > 2 }.firstOrNull()
                val z = "abc".filter { it > 'a' }.firstOrNull()
            """.trimIndent()
            val findings = subject.lintWithContext(env, code)
            assertThat(findings).hasSize(3)
        }

        @Test
        fun `Filter with last`() {
            val code = """
                val x = listOf(1, 2, 3).filter { it > 1 }.last()
                val y = sequenceOf(1, 2, 3).filter { it > 2 }.last()
                val z = "abc".filter { it > 'a' }.last()
            """.trimIndent()
            val findings = subject.lintWithContext(env, code)
            assertThat(findings).hasSize(3)
        }

        @Test
        fun `Filter with lastOrNull`() {
            val code = """
                val x = listOf(1, 2, 3).filter { it > 1 }.lastOrNull()
                val y = sequenceOf(1, 2, 3).filter { it > 2 }.lastOrNull()
                val z = "abc".filter { it > 'a' }.lastOrNull()
            """.trimIndent()
            val findings = subject.lintWithContext(env, code)
            assertThat(findings).hasSize(3)
        }

        @Test
        fun `Filter with single`() {
            val code = """
                val x = listOf(1, 2, 3).filter { it > 1 }.single()
                val y = sequenceOf(1, 2, 3).filter { it > 2 }.single()
                val z = "abc".filter { it > 'a' }.single()
            """.trimIndent()
            val findings = subject.lintWithContext(env, code)
            assertThat(findings).hasSize(3)
        }

        @Test
        fun `Filter with singleOrNull`() {
            val code = """
                val x = listOf(1, 2, 3).filter { it > 1 }.singleOrNull()
                val y = sequenceOf(1, 2, 3).filter { it > 2 }.singleOrNull()
                val z = "abc".filter { it > 'a' }.singleOrNull()
            """.trimIndent()
            val findings = subject.lintWithContext(env, code)
            assertThat(findings).hasSize(3)
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
            """.trimIndent()

            val findings = subject.lintWithContext(env, code)
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
            """.trimIndent()

            val findings = subject.lintWithContext(env, code)
            assertThat(findings).isEmpty()
        }

        @Test
        fun `Not stdlib filter function`() {
            val code = """
                fun filter() : List<Any>{
                    return emptyList()
                }
                
                val x = filter().size
            """.trimIndent()

            val findings = subject.lintWithContext(env, code)
            assertThat(findings).isEmpty()
        }

        @Test
        fun `Filter with count`() {
            val code = """
                val x = listOf(1, 2, 3)
                    .count { it > 2 }
            """.trimIndent()

            val findings = subject.lintWithContext(env, code)
            assertThat(findings).isEmpty()
        }

        @Test
        fun `Filter with assignment and count`() {
            val code = """
                fun test(list: List<Int>): Int {
                    val x = list.map { it + 1 }.filter { it > 2 }
                    foo(x)
                    val count = x.count()
                    return count + 3
                }
                fun foo(list: List<Int>) {}
            """.trimIndent()

            val findings = subject.lintWithContext(env, code)
            assertThat(findings).isEmpty()
        }

        @Test
        fun `None item`() {
            val code = """
                val x = listOf(1, 2, 3)
                    .none { it > 2 }
            """.trimIndent()

            val findings = subject.lintWithContext(env, code)
            assertThat(findings).isEmpty()
        }

        @Test
        fun `Any item`() {
            val code = """
                val x = listOf(1, 2, 3)
                    .any { it > 2 }
            """.trimIndent()

            val findings = subject.lintWithContext(env, code)
            assertThat(findings).isEmpty()
        }

        @Test
        fun `Sequence with count`() {
            val code = """
                val x = listOf(1, 2, 3)
                    .asSequence()
                    .map { it * 2 }
                    .count { it > 1 }
            """.trimIndent()

            val findings = subject.lintWithContext(env, code)
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
            """.trimIndent()
            val findings = subject.lintWithContext(env, code)
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
            """.trimIndent()
            val findings = subject.lintWithContext(env, code)
            assertThat(findings).isEmpty()
        }

        @Test
        fun `Terminal function has an argument`() {
            val code = """
                val x = listOf(1, 2, 3).filter { it > 1 }.count { it > 2 }
                val y = listOf(1, 2, 3).filter { it > 1 }.singleOrNull { it > 2 }
            """.trimIndent()

            val findings = subject.lintWithContext(env, code)
            assertThat(findings).isEmpty()
        }
    }
}
