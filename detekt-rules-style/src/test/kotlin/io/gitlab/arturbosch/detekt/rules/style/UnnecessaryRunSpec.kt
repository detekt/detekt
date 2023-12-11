package io.gitlab.arturbosch.detekt.rules.style

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.rules.KotlinCoreEnvironmentTest
import io.gitlab.arturbosch.detekt.test.compileAndLintWithContext
import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.kotlin.cli.jvm.compiler.KotlinCoreEnvironment
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

@KotlinCoreEnvironmentTest
class UnnecessaryRunSpec(val env: KotlinCoreEnvironment) {

    val subject = UnnecessaryRun(Config.empty)

    @Nested
    inner class `Unnecessary run expressions that can be changed to ordinary method call` {

        @Test
        fun `reports run on non-nullable type`() {
            val findings = subject.compileAndLintWithContext(
                env,
                """
                    fun f() {
                        val a = 0
                        val result = a.run {
                            plus(1)
                        }
                    }
                """.trimIndent()
            )
            assertThat(findings).hasSize(1)
            assertThat(findings.first().message).isEqualTo(MSG)
        }

        @Test
        fun `reports run on elvis single statement expression`() {
            val findings = subject.compileAndLintWithContext(
                env,
                """
                    import java.lang.System

                    fun f() {
                        val a: Int? = 0
                        val result1 = a ?: run { java.lang.System.currentTimeMillis() }
                        val result2 = a ?: run { System.currentTimeMillis() }
                    }
                """.trimIndent()
            )
            assertThat(findings).hasSize(2)
            assertThat(findings.first().message).isEqualTo(MSG)
        }

        @Test
        fun `does not reports run on elvis multi statement expression`() {
            val findings = subject.compileAndLintWithContext(
                env,
                """
                    import java.lang.System

                    fun f() {
                        val a: Int? = 0
                        val result = a ?: run { 
                            println("a is null")
                            java.lang.System.currentTimeMillis()
                        }
                    }
                """.trimIndent()
            )
            assertThat(findings).isEmpty()
        }

        @Test
        fun `reports run side effect statement`() {
            val findings = subject.compileAndLintWithContext(
                env,
                """
                    import java.lang.System

                    fun f() {
                        val a = 0
                        val result = a.run { 
                            println("a is null")
                        }
                    }
                """.trimIndent()
            )
            assertThat(findings).hasSize(1)
            assertThat(findings.first().message).isEqualTo(MSG)
        }

        @Test
        fun `reports run on nullable type`() {
            val findings = subject.compileAndLintWithContext(
                env,
                """
                    import kotlin.random.Random
                    fun f() {
                        val a: Int? = if (Random.nextBoolean()) null else 0
                        val result = a?.run {
                            plus(1)
                        }
                    }
                """.trimIndent()
            )
            assertThat(findings).hasSize(1)
            assertThat(findings.first().message).isEqualTo(IF_LET_MSG)
        }

        @Test
        fun `reports run on nullable type with extension fun`() {
            assertThat(
                subject.compileAndLintWithContext(
                    env,
                    """
                        val a: Any? = Any()
                        fun Any.b() = Unit

                        fun foo() {
                            a?.run {
                                b()
                            }
                        }
                    """.trimIndent()
                )
            ).hasSize(1)
        }

        @Test
        fun `reports an run with lambda block`() {
            assertThat(
                subject.compileAndLintWithContext(
                    env,
                    """
                        fun f() {
                            val a: Int? = null
                            a?.run({
                                plus(1)
                            })

                            a?.run(foo@{
                                plus(1)
                            })
                        }
                    """.trimIndent()
                )
            ).hasSize(2)
        }

        @Test
        fun `reports single statement in run used as function argument`() {
            assertThat(
                subject.compileAndLintWithContext(
                    env,
                    """
                        fun b(i: String?) {}

                        fun foo() {
                            val a: Int? = null
                            b(a.run {
                                toString()
                            })
                        }
                    """.trimIndent()
                )
            ).hasSize(1)
        }

        @Test
        fun `does not report double statements in run used as function argument as if else`() {
            assertThat(
                subject.compileAndLintWithContext(
                    env,
                    """
                        class C {
                            var prop = 0
                        }

                        fun foo() {
                            val list = ArrayList<C>()
                            list.add(
                                if (true) {
                                    C().run {
                                        prop = 1
                                        this
                                    }
                                } else {
                                    C()
                                }
                            )
                        }
                    """.trimIndent()
                )
            ).isEmpty()
        }

        @Test
        fun `reports double statements in run used as function argument as if else`() {
            assertThat(
                subject.compileAndLintWithContext(
                    env,
                    """
                        data class C(var prop: Int = 1)

                        fun foo() {
                            val list = ArrayList<C>()
                            list.add(
                                if (true) {
                                    C().run { copy() }
                                } else {
                                    C()
                                }
                            )
                        }
                    """.trimIndent()
                )
            ).hasSize(1)
        }

        @Test
        fun `does not report run with lambda body containing more than one statement`() {
            assertThat(
                subject.compileAndLintWithContext(
                    env,
                    """
                        fun b(i: Int?) {}

                        fun foo() {
                            val a: Int? = null
                            a?.run {
                                plus(1)
                                plus(2)
                            }
                            a?.run {
                                plus(1)
                                plus(2)
                            }
                            b(1.run {
                                plus(1)
                                plus(2)
                            })
                        }
                    """.trimIndent()
                )
            ).isEmpty()
        }

        @Test
        fun `reports when lambda has a dot qualified expression`() {
            val findings = subject.compileAndLintWithContext(
                env,
                """
                    class Foo(val bar: Bar)

                    class Bar {
                        fun bar() {}
                    }
                    fun test(foo: Foo) {
                        foo.run {
                            bar.bar()
                        }
                    }
                """.trimIndent()
            )
            assertThat(findings).hasSize(1)
        }

        @Test
        fun `reports when lambda has a dot qualified expression which has 'this' receiver`() {
            val findings = subject.compileAndLintWithContext(
                env,
                """
                    class Foo(val bar: Bar)

                    class Bar {
                        fun bar() {}
                    }

                    fun test(foo: Foo) {
                        foo.run {
                            this.bar.bar()
                        }
                    }
                """.trimIndent()
            )
            assertThat(findings).hasSize(1)
        }

        @Test
        fun `reports when lambda has a 'this' expression`() {
            val findings = subject.compileAndLintWithContext(
                env,
                """
                    fun test() {
                        "foo".run {
                            this
                        }
                    }
                """.trimIndent()
            )
            assertThat(findings).hasSize(1)
        }
    }

    @Nested
    inner class `run with assignment expressions` {
        @Test
        fun `reports when result is unused`() {
            assertThat(
                subject.compileAndLintWithContext(
                    env,
                    """
                        class C {
                            var prop = 0
                        }
                        
                        fun foo() {
                            val c = C()
                            c.run {
                                prop = 1
                            }

                            c.run {
                                prop += 1
                            }

                            c.run {
                                prop /= 1
                            }
                        }
                    """.trimIndent()
                )
            ).hasSize(3)
        }

        @Test
        fun `does not report when result is used`() {
            assertThat(
                subject.compileAndLintWithContext(
                    env,
                    """
                        class C {
                            var prop = 0
                        }
                        
                        fun foo() {
                            val c = C()
                            val a1 = C().run {
                                prop = 1
                            }
                            val a2 = c.run {
                                prop = 1
                            }

                            val a3 = c.run {
                                prop += 1
                            }

                            val a4 = c.run {
                                prop /= 1
                            }
                        }
                    """.trimIndent()
                )
            ).isEmpty()
        }
    }

    @Test
    fun `reports run usage inside safe chained expressions`() {
        assertThat(
            subject.compileAndLintWithContext(
                env,
                """
                    fun f() {
                        val arguments = listOf(1,2,3)
                            ?.map { it * 2 }
                            ?.toMutableList()
                            ?.run { this.add(0) }
                    }
                """.trimIndent()
            )
        ).hasSize(1)
    }

    @Test
    fun `reports run usage inside safe chained expressions with equality op`() {
        assertThat(
            subject.compileAndLintWithContext(
                env,
                """
                    fun f() {
                        val arguments = listOf(1,2,3)
                            ?.map { it * 2 }
                            ?.toMutableList()
                            ?.run { this.size == 0 }
                    }
                """.trimIndent()
            )
        ).hasSize(1)
    }

    @Test
    fun `should not report the if expression`() {
        assertThat(
            subject.compileAndLintWithContext(
                env,
                """
                    class C {
                        fun has() = true
                    }

                    fun f() {
                        C().run {
                            if (has()) {
                                // actions
                            }
                        }

                        C().run {
                            if (has()) {
                                1
                            } else {
                                2
                            }
                        }

                        C().run {
                            if (has()) { 1 } else { 2 }
                        }
                    }
                """.trimIndent()
            )
        ).isEmpty()
    }

    @Test
    fun `do not report when nested lambda is using this by run`() {
        assertThat(
            subject.compileAndLintWithContext(
                env,
                """
                    fun test(foo: Foo) {
                        foo.run {
                            bar {
                                baz1()
                            }
                        }
                        foo.run {
                            bar {
                                baz1()
                                baz2()
                            }
                        }
                    }

                    class Foo {
                        fun bar(f: () -> Unit) {
                        }

                        fun baz1() = 1
                        fun baz2() = 2
                    }
                """.trimIndent()
            )
        ).isEmpty()
    }

    companion object {
        private const val MSG = "`run` expression can be omitted"
        private const val IF_LET_MSG = "`run` can be replaced with `let` or an `if`"
    }
}
