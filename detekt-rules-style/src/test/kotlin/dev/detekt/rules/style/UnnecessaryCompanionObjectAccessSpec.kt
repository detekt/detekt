package dev.detekt.rules.style

import dev.detekt.api.Config
import dev.detekt.test.assertj.assertThat
import dev.detekt.test.junit.KotlinCoreEnvironmentTest
import dev.detekt.test.lintWithContext
import dev.detekt.test.utils.KotlinEnvironmentContainer
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

@KotlinCoreEnvironmentTest
class UnnecessaryCompanionObjectAccessSpec(val env: KotlinEnvironmentContainer) {

    private val subject = UnnecessaryCompanionObjectAccess(Config.empty)

    @Nested
    inner class `default companion object` {
        @Test
        fun `reports explicit Companion for function call`() {
            val code = """
                class A {
                    companion object {
                        fun foo() = 1
                    }
                }

                fun test() {
                    A.Companion.foo()
                }
            """.trimIndent()

            assertThat(subject.lintWithContext(env, code)).hasSize(1)
        }

        @Test
        fun `reports explicit Companion for property`() {
            val code = """
                class A {
                    companion object {
                        val BAZ = 2
                    }
                }

                fun test() {
                    println(A.Companion.BAZ)
                }
            """.trimIndent()

            assertThat(subject.lintWithContext(env, code)).hasSize(1)
        }

        @Test
        fun `does not report shortened companion access`() {
            val code = """
                class A {
                    companion object {
                        fun foo() = 1
                        val BAZ = 2
                    }
                }

                fun test() {
                    A.foo()
                    println(A.BAZ)
                }
            """.trimIndent()

            assertThat(subject.lintWithContext(env, code)).isEmpty()
        }

        @Test
        fun `reports explicit Companion on interface companion`() {
            val code = """
                interface I {
                    companion object {
                        fun foo() = 1
                    }
                }

                fun test() {
                    I.Companion.foo()
                }
            """.trimIndent()

            assertThat(subject.lintWithContext(env, code)).hasSize(1)
        }
    }

    @Test
    fun `doesn't report object with the name of companion`() {
        val code = """
            class A {
                object Companion {
                    fun foo() = 1
                    const val BAZ = 2
                }
            }

            fun test() {
                val ref = A.Companion.foo()
                val baz = A.Companion.BAZ
            }
        """.trimIndent()

        assertThat(subject.lintWithContext(env, code)).isEmpty()
    }

    @Nested
    inner class `named companion object` {

        @Test
        fun `reports explicit named companion for function call`() {
            val code = """
                class AFactory {
                    companion object Factory {
                        fun foo() = 1
                    }
                }

                fun test() {
                    AFactory.Factory.foo()
                }
            """.trimIndent()

            assertThat(subject.lintWithContext(env, code)).hasSize(1)
        }

        @Test
        fun `does not report shortened access for named companion`() {
            val code = """
                class AFactory {
                    companion object Factory {
                        fun foo() = 1
                    }
                }

                fun test() {
                    AFactory.foo()
                }
            """.trimIndent()

            assertThat(subject.lintWithContext(env, code)).isEmpty()
        }
    }

    @Nested
    inner class `nested class` {

        @Test
        fun `reports explicit Companion on nested class companion`() {
            val code = """
                class Outer {
                    class Inner {
                        companion object {
                            fun foo() = 1
                        }
                    }
                }

                fun test() {
                    Outer.Inner.Companion.foo()
                }
            """.trimIndent()

            assertThat(subject.lintWithContext(env, code)).hasSize(1)
        }

        @Test
        fun `does not report Outer Factory when Factory is a nested class`() {
            val code = """
                class Outer {
                    class Factory {
                        fun foo() = 1
                    }
                }

                fun test() {
                    Outer.Factory().foo()
                }
            """.trimIndent()

            assertThat(subject.lintWithContext(env, code)).isEmpty()
        }

        @Test
        fun `does not report when accessing nested class declared inside companion object`() {
            val code = """
                class Outer {
                    class Inner {
                        companion object {
                            class CompanionOuter {
                                class CompanionInner
                            }
                        }
                    }
                }

                fun test() {
                    // Nested classes in a companion are not reachable without an explicit `Companion` segment.
                    Outer.Inner.Companion.CompanionOuter.CompanionInner()
                }

            """.trimIndent()

            assertThat(subject.lintWithContext(env, code)).isEmpty()
        }
    }

    @Nested
    inner class `callable references` {

        @Test
        fun `reports explicit Companion in callable reference`() {
            val code = """
                class A {
                    companion object {
                        fun foo() = 1
                    }
                }

                fun test() {
                    val ref = A.Companion::foo
                }
            """.trimIndent()

            assertThat(subject.lintWithContext(env, code)).hasSize(1)
        }

        @Test
        fun `does not report shortened callable reference`() {
            val code = """
                class A {
                    companion object {
                        fun foo() = 1
                    }
                }

                fun test() {
                    val ref = A::foo
                }
            """.trimIndent()

            assertThat(subject.lintWithContext(env, code)).isEmpty()
        }
    }

    @Test
    fun `reports explicit Companion inside string template interpolation`() {
        val stringTemplateRhs = "\"\${A.Companion.foo()}\""
        val code = """
            class A {
                companion object {
                    fun foo() = 1
                }
            }

            fun test() {
                val msg = $stringTemplateRhs
            }
        """.trimIndent()

        assertThat(subject.lintWithContext(env, code)).hasSize(1)
    }

    @Test
    fun `does not report when Companion is present in import statement`() {
        val code = """
            import kotlin.Int.Companion.MIN_VALUE
            fun main() {
                println(MIN_VALUE)
            }
        """.trimIndent()
        assertThat(subject.lintWithContext(env, code)).isEmpty()
    }

    @Test
    fun `does report when Companion is present in usage area`() {
        val code = """
            fun main() {
                println(Int.Companion.MIN_VALUE)
            }
        """.trimIndent()
        assertThat(subject.lintWithContext(env, code)).hasSize(1)
    }
}
