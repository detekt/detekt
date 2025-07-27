package io.gitlab.arturbosch.detekt.rules.style

import dev.detekt.api.Config
import io.github.detekt.test.utils.KotlinEnvironmentContainer
import io.gitlab.arturbosch.detekt.rules.KotlinCoreEnvironmentTest
import io.gitlab.arturbosch.detekt.test.assertThat
import io.gitlab.arturbosch.detekt.test.lintWithContext
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

@KotlinCoreEnvironmentTest
class UseOrEmptySpec(val env: KotlinEnvironmentContainer) {
    val subject = UseOrEmpty(Config.empty)

    @Nested
    inner class `report UseOrEmptySpec rule` {
        @Test
        fun emptyList() {
            val code = """
                fun test(x: List<Int>?) {
                    val a = x ?: emptyList()
                }
            """.trimIndent()
            val findings = subject.lintWithContext(env, code)
            assertThat(findings).singleElement()
                .hasMessage("This '?: emptyList()' can be replaced with 'orEmpty()' call")
            assertThat(findings).hasStartSourceLocation(2, 13)
        }

        @Test
        fun emptySet() {
            val code = """
                fun test(x: Set<Int>?) {
                    val a = x ?: emptySet()
                }
            """.trimIndent()
            val findings = subject.lintWithContext(env, code)
            assertThat(findings).hasSize(1)
        }

        @Test
        fun emptyMap() {
            val code = """
                fun test(x: Map<Int, String>?) {
                    val a = x ?: emptyMap()
                }
            """.trimIndent()
            val findings = subject.lintWithContext(env, code)
            assertThat(findings).hasSize(1)
        }

        @Test
        fun emptySequence() {
            val code = """
                fun test(x: Sequence<Int>?) {
                    val a = x ?: emptySequence()
                }
            """.trimIndent()
            val findings = subject.lintWithContext(env, code)
            assertThat(findings).hasSize(1)
        }

        @Test
        fun emptyArray() {
            val code = """
                fun test(x: Array<Int>?) {
                    val a = x ?: emptyArray()
                }
            """.trimIndent()
            val findings = subject.lintWithContext(env, code)
            assertThat(findings).hasSize(1)
        }

        @Test
        fun listOf() {
            val code = """
                fun test(x: List<Int>?) {
                    val a = x ?: listOf()
                }
            """.trimIndent()
            val findings = subject.lintWithContext(env, code)
            assertThat(findings).hasSize(1)
        }

        @Test
        fun setOf() {
            val code = """
                fun test(x: Set<Int>?) {
                    val a = x ?: setOf()
                }
            """.trimIndent()
            val findings = subject.lintWithContext(env, code)
            assertThat(findings).hasSize(1)
        }

        @Test
        fun mapOf() {
            val code = """
                fun test(x: Map<Int, String>?) {
                    val a = x ?: mapOf()
                }
            """.trimIndent()
            val findings = subject.lintWithContext(env, code)
            assertThat(findings).hasSize(1)
        }

        @Test
        fun sequenceOf() {
            val code = """
                fun test(x: Sequence<Int>?) {
                    val a = x ?: sequenceOf()
                }
            """.trimIndent()
            val findings = subject.lintWithContext(env, code)
            assertThat(findings).hasSize(1)
        }

        @Test
        fun arrayOf() {
            val code = """
                fun test(x: Array<Int>?) {
                    val a = x ?: arrayOf()
                }
            """.trimIndent()
            val findings = subject.lintWithContext(env, code)
            assertThat(findings).hasSize(1)
        }

        @Test
        fun `empty string`() {
            val code = """
                fun test(x: String?) {
                    val a = x ?: ""
                }
            """.trimIndent()
            val findings = subject.lintWithContext(env, code)
            assertThat(findings).hasSize(1)
        }

        @Test
        fun `mutable list`() {
            val code = """
                fun test(x: MutableList<Int>?) {
                    val a = x ?: emptyList()
                }
            """.trimIndent()
            val findings = subject.lintWithContext(env, code)
            assertThat(findings).hasSize(1)
        }

        @Test
        fun `indexing operator call`() {
            val code = """
                class C {
                    operator fun get(key: String): List<Int>? = null
                }
                fun test(c: C) {
                    c["key"] ?: emptyList()
                }
            """.trimIndent()
            val findings = subject.lintWithContext(env, code)
            assertThat(findings).hasSize(1)
        }
    }

    @Nested
    inner class `does not report UseOrEmptySpec rule` {
        @Test
        fun `not null`() {
            val code = """
                fun test(x: List<Int>) {
                    val a = x ?: emptyList()
                }
            """.trimIndent()
            val findings = subject.lintWithContext(env, code)
            assertThat(findings).isEmpty()
        }

        @Test
        fun `not empty`() {
            val code = """
                fun test(x: List<Int>?) {
                    val a = x ?: listOf(1)
                }
            """.trimIndent()
            val findings = subject.lintWithContext(env, code)
            assertThat(findings).isEmpty()
        }

        @Test
        fun `different types`() {
            val code = """
                fun test(x: List<Int>?) {
                    val a = x ?: emptySet()
                }
                fun test(c: Any?) {
                    val x = c ?: emptyList<Int>()
                }
            """.trimIndent()
            val findings = subject.lintWithContext(env, code)
            assertThat(findings).isEmpty()
        }

        @Test
        fun mutableListOf() {
            val code = """
                fun test(x: MutableList<Int>?) {
                    val a = x ?: mutableListOf()
                }
            """.trimIndent()
            val findings = subject.lintWithContext(env, code)
            assertThat(findings).isEmpty()
        }

        @Test
        fun intArrayOf() {
            val code = """
                fun test(x: IntArray?) {
                    val a = x ?: intArrayOf()
                }
            """.trimIndent()
            val findings = subject.lintWithContext(env, code)
            assertThat(findings).isEmpty()
        }

        @Test
        fun `indexing operator call with type parameter`() {
            val code = """
                class C {
                    operator fun <T> get(key: String): List<T>? = null
                }
                fun test(c: C) {
                    val x = c["key"] ?: emptyList<Int>()
                    val y: List<Int> = c["key"] ?: emptyList()
                    val z = (c["key"]) ?: emptyList<Int>()
                }
            """.trimIndent()
            val findings = subject.lintWithContext(env, code)
            assertThat(findings).isEmpty()
        }
    }
}
