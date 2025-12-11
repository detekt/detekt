package dev.detekt.rules.style

import dev.detekt.api.Config
import dev.detekt.test.KotlinEnvironmentContainer
import dev.detekt.test.junit.KotlinCoreEnvironmentTest
import dev.detekt.test.lintWithContext
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

@KotlinCoreEnvironmentTest
class MultilineLambdaItParameterSpec(val env: KotlinEnvironmentContainer) {
    val subject = MultilineLambdaItParameter(Config.empty)

    @Nested
    inner class `single parameter, multiline lambda with multiple statements` {
        @Test
        fun `reports when parameter name is implicit 'it'`() {
            val code = """
                fun f() {
                    val digits = 1234.let {
                        listOf(it)
                        println(it)
                    }
                }
            """.trimIndent()
            val findings = subject.lintWithContext(env, code)
            assertThat(findings).hasSize(1)
        }

        @Test
        fun `reports when parameter name is explicit 'it'`() {
            val code = """
                fun f() {
                    val digits = 1234.let { it ->
                        listOf(it)
                        println(it)
                    }
                }
            """.trimIndent()
            val findings = subject.lintWithContext(env, code)
            assertThat(findings).hasSize(1)
        }

        @Test
        fun `does not report when parameter name is explicit and not 'it'`() {
            val code = """
                fun f() {
                    val digits = 1234.let { param ->
                        listOf(param)
                        println(param)
                    }
                }
            """.trimIndent()
            val findings = subject.lintWithContext(env, code)
            assertThat(findings).isEmpty()
        }

        @Test
        fun `does not report when lambda has no implicit parameter references`() {
            val code = """
                fun foo(f: (Int) -> Unit) {}
                fun main() {
                    foo {
                        println(1)
                        println(2)
                        val it = 3
                        println(it)
                    }
                }
            """.trimIndent()
            val findings = subject.lintWithContext(env, code)
            assertThat(findings).isEmpty()
        }
    }

    @Nested
    inner class `single parameter, multiline lambda with a single statement` {
        @Test
        fun `does not report when parameter name is an implicit 'it'`() {
            val code = """
                fun f() {
                    val digits = 1234.let {
                        listOf(it)
                    }
                }
            """.trimIndent()
            val findings = subject.lintWithContext(env, code)
            assertThat(findings).isEmpty()
        }

        @Test
        fun `does not report when parameter name is an explicit 'it'`() {
            val code = """
                fun f() {
                    val digits = 1234.let { it ->
                        listOf(it)
                    }
                }
            """.trimIndent()
            val findings = subject.lintWithContext(env, code)
            assertThat(findings).isEmpty()
        }

        @Test
        fun `does not report when parameter name is explicit and not 'it'`() {
            val code = """
                fun f() {
                    val digits = 1234.let { param ->
                        listOf(param)
                    }
                }
            """.trimIndent()
            val findings = subject.lintWithContext(env, code)
            assertThat(findings).isEmpty()
        }

        @Test
        fun `reports when statement is spanning multiple lines`() {
            val code = """
                fun f() {
                    val digits = 1234.let {
                        check(it > 0) {
                            println("error")
                        }
                    }
                }
            """.trimIndent()
            val findings = subject.lintWithContext(env, code)
            assertThat(findings).hasSize(1)
        }

        @Test
        fun `reports when statement has multiple line arguments`() {
            val code = """
                data class Foo(val x: Int, val y: Int)
                
                fun f(i: Int?) {
                    val foo = i?.let {
                        Foo(
                            x = it,
                            y = 2
                        )
                    }
                }
            """.trimIndent()
            val findings = subject.lintWithContext(env, code)
            assertThat(findings).hasSize(1)
        }

        @Test
        fun `reports when statement has multiple line calls`() {
            val code = """
                fun test(strings: List<String>): List<String> {
                  return strings
                    .flatMap {
                      it
                        .reader()
                        .readLines()
                    }
                }
            """.trimIndent()
            val findings = subject.lintWithContext(env, code)
            assertThat(findings).hasSize(1)
        }
    }

    @Nested
    inner class `single parameter, single-line lambda` {
        @Test
        fun `does not report when parameter name is an implicit 'it' with type resolution`() {
            val code = """
                fun f() {
                    val digits = 1234.let { listOf(it) }
                }
            """.trimIndent()
            val findings = subject.lintWithContext(env, code)
            assertThat(findings).isEmpty()
        }

        @Test
        fun `does not report when parameter name is an implicit 'it'`() {
            val code = """
                fun f() {
                    val digits = 1234.let { listOf(it) }
                }
            """.trimIndent()
            val findings = subject.lintWithContext(env, code)
            assertThat(findings).isEmpty()
        }

        @Test
        fun `does not report when parameter name is an explicit 'it'`() {
            val code = """
                fun f() {
                    val digits = 1234.let { it -> listOf(it) }
                }
            """.trimIndent()
            val findings = subject.lintWithContext(env, code)
            assertThat(findings).isEmpty()
        }

        @Test
        fun `does not report when parameter name is explicit and not 'it'`() {
            val code = """
                fun f() {
                    val digits = 1234.let { param -> listOf(param) }
                }
            """.trimIndent()
            val findings = subject.lintWithContext(env, code)
            assertThat(findings).isEmpty()
        }

        @Test
        fun `does not report when statement has one-line lambda argument`() {
            val code = """
                fun f() {
                    val digits = 1234.let {
                        check(it > 0) { println("error") }
                    }
                }
            """.trimIndent()
            val findings = subject.lintWithContext(env, code)
            assertThat(findings).isEmpty()
        }
    }

    @Nested
    inner class `multiple parameters, multiline lambda` {
        @Test
        fun `reports when one of the explicit parameters is an 'it'`() {
            val code = """
                fun f() {
                    val flat = listOf(listOf(1), listOf(2)).mapIndexed { index, it ->
                        println(it)
                        it + index
                    }
                }
            """.trimIndent()
            val findings = subject.lintWithContext(env, code)
            assertThat(findings).hasSize(1)
        }

        @Test
        fun `does not report when none of the explicit parameters is an 'it'`() {
            val code = """
                fun f() {
                    val lambda = { item: Int, that: String ->
                        println(item)
                        item.toString() + that
                    }
                }
            """.trimIndent()
            val findings = subject.lintWithContext(env, code)
            assertThat(findings).isEmpty()
        }
    }

    @Nested
    inner class `no parameter, multiline lambda with multiple statements` {
        @Test
        fun `does not report when there is no parameter`() {
            val code = """
                fun f() {
                    val string = StringBuilder().apply {
                        append("a")
                        append("b")
                    }
                }
            """.trimIndent()
            val findings = subject.lintWithContext(env, code)
            assertThat(findings).isEmpty()
        }
    }
}
