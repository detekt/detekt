package io.gitlab.arturbosch.detekt.rules.style

import io.gitlab.arturbosch.detekt.rules.KotlinCoreEnvironmentTest
import io.gitlab.arturbosch.detekt.test.assertThat
import io.gitlab.arturbosch.detekt.test.compileAndLintWithContext
import org.jetbrains.kotlin.cli.jvm.compiler.KotlinCoreEnvironment
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

@KotlinCoreEnvironmentTest
class UseOrEmptySpec(val env: KotlinCoreEnvironment) {
    val subject = UseOrEmpty()

    @Nested
    inner class `report UseOrEmptySpec rule` {
        @Test
        fun `emptyList`() {
            val code = """
                fun test(x: List<Int>?) {
                    val a = x ?: emptyList()
                }
            """
            val findings = subject.compileAndLintWithContext(env, code)
            assertThat(findings).hasSize(1)
            assertThat(findings).hasSourceLocation(2, 13)
            assertThat(findings[0]).hasMessage("This '?: emptyList()' can be replaced with 'orEmpty()' call")
        }

        @Test
        fun `emptySet`() {
            val code = """
                fun test(x: Set<Int>?) {
                    val a = x ?: emptySet()
                }
            """
            val findings = subject.compileAndLintWithContext(env, code)
            assertThat(findings).hasSize(1)
        }

        @Test
        fun `emptyMap`() {
            val code = """
                fun test(x: Map<Int, String>?) {
                    val a = x ?: emptyMap()
                }
            """
            val findings = subject.compileAndLintWithContext(env, code)
            assertThat(findings).hasSize(1)
        }

        @Test
        fun `emptySequence`() {
            val code = """
                fun test(x: Sequence<Int>?) {
                    val a = x ?: emptySequence()
                }
            """
            val findings = subject.compileAndLintWithContext(env, code)
            assertThat(findings).hasSize(1)
        }

        @Test
        fun `emptyArray`() {
            val code = """
                fun test(x: Array<Int>?) {
                    val a = x ?: emptyArray()
                }
            """
            val findings = subject.compileAndLintWithContext(env, code)
            assertThat(findings).hasSize(1)
        }

        @Test
        fun `listOf`() {
            val code = """
                fun test(x: List<Int>?) {
                    val a = x ?: listOf()
                }
            """
            val findings = subject.compileAndLintWithContext(env, code)
            assertThat(findings).hasSize(1)
        }

        @Test
        fun `setOf`() {
            val code = """
                fun test(x: Set<Int>?) {
                    val a = x ?: setOf()
                }
            """
            val findings = subject.compileAndLintWithContext(env, code)
            assertThat(findings).hasSize(1)
        }

        @Test
        fun `mapOf`() {
            val code = """
                fun test(x: Map<Int, String>?) {
                    val a = x ?: mapOf()
                }
            """
            val findings = subject.compileAndLintWithContext(env, code)
            assertThat(findings).hasSize(1)
        }

        @Test
        fun `sequenceOf`() {
            val code = """
                fun test(x: Sequence<Int>?) {
                    val a = x ?: sequenceOf()
                }
            """
            val findings = subject.compileAndLintWithContext(env, code)
            assertThat(findings).hasSize(1)
        }

        @Test
        fun `arrayOf`() {
            val code = """
                fun test(x: Array<Int>?) {
                    val a = x ?: arrayOf()
                }
            """
            val findings = subject.compileAndLintWithContext(env, code)
            assertThat(findings).hasSize(1)
        }

        @Test
        fun `empty string`() {
            val code = """
                fun test(x: String?) {
                    val a = x ?: ""
                }
            """
            val findings = subject.compileAndLintWithContext(env, code)
            assertThat(findings).hasSize(1)
        }

        @Test
        fun `mutable list`() {
            val code = """
                fun test(x: MutableList<Int>?) {
                    val a = x ?: emptyList()
                }
            """
            val findings = subject.compileAndLintWithContext(env, code)
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
            """
            val findings = subject.compileAndLintWithContext(env, code)
            assertThat(findings).isEmpty()
        }

        @Test
        fun `not empty`() {
            val code = """
                fun test(x: List<Int>?) {
                    val a = x ?: listOf(1)
                }
            """
            val findings = subject.compileAndLintWithContext(env, code)
            assertThat(findings).isEmpty()
        }

        @Test
        fun `different types`() {
            val code = """
                fun test(x: List<Int>?) {
                    val a = x ?: emptySet()
                }
            """
            val findings = subject.compileAndLintWithContext(env, code)
            assertThat(findings).isEmpty()
        }

        @Test
        fun `mutableListOf`() {
            val code = """
                fun test(x: MutableList<Int>?) {
                    val a = x ?: mutableListOf()
                }
            """
            val findings = subject.compileAndLintWithContext(env, code)
            assertThat(findings).isEmpty()
        }

        @Test
        fun `intArrayOf`() {
            val code = """
                fun test(x: IntArray?) {
                    val a = x ?: intArrayOf()
                }
            """
            val findings = subject.compileAndLintWithContext(env, code)
            assertThat(findings).isEmpty()
        }
    }
}
