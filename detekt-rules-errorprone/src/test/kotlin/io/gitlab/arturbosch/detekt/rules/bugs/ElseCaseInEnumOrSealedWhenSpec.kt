package io.gitlab.arturbosch.detekt.rules.bugs

import io.gitlab.arturbosch.detekt.rules.KotlinCoreEnvironmentTest
import io.gitlab.arturbosch.detekt.test.compileAndLintWithContext
import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.kotlin.cli.jvm.compiler.KotlinCoreEnvironment
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

@KotlinCoreEnvironmentTest
class ElseCaseInEnumOrSealedWhenSpec(private val env: KotlinCoreEnvironment) {
    private val subject = ElseCaseInEnumOrSealedWhen()

    @Nested
    inner class `ElseCaseInEnumOrSealedWhen rule` {
        @Nested
        inner class `enum` {
            @Test
            fun `reports when enum or sealed _when_ expression used as statement contains _else_ case`() {
                val code = """
                enum class Color {
                    RED,
                    GREEN,
                    BLUE
                }

                fun whenOnEnumFail(c: Color) {
                    when (c) {
                        Color.BLUE -> {}
                        Color.GREEN -> {}
                        else -> {}
                    }
                }
                """
                val actual = subject.compileAndLintWithContext(env, code)
                assertThat(actual).hasSize(1)
            }

            @Test
            fun `reports when enum or sealed _when_ expression contains _else_ case`() {
                val code = """
                enum class Color {
                    RED,
                    GREEN,
                    BLUE
                }

                fun whenOnEnumFail(c: Color) {
                    val x = when (c) {
                        Color.BLUE -> 1
                        Color.GREEN -> 2
                        else -> 100
                    }
                }
                """
                val actual = subject.compileAndLintWithContext(env, code)
                assertThat(actual).hasSize(1)
            }

            @Test
            fun `does not report when enum or sealed _when_ expression does not contain _else_ case`() {
                val code = """
                enum class Color {
                    RED,
                    GREEN,
                    BLUE
                }

                fun whenOnEnumPassA(c: Color) {
                    when (c) {
                        Color.BLUE -> {}
                        Color.GREEN -> {}
                        Color.RED -> {}
                    }

                    when (c) {
                        Color.BLUE -> {}
                        Color.GREEN -> {}
                        Color.RED -> {}
                    }

                    val x = when (c) {
                        Color.BLUE -> 1
                        Color.GREEN -> 2
                        Color.RED -> 3
                    }
                }

                fun whenOnEnumPassB(c: Color) {
                    when (c) {
                        Color.BLUE -> {}
                        Color.GREEN -> {}
                    }
                }
                """
                assertThat(subject.compileAndLintWithContext(env, code)).isEmpty()
            }
        }

        @Nested
        inner class `sealed classes` {
            @Test
            fun `reports when enum or sealed _when_ expression used as statement contains _else_ case`() {
                val code = """
                    sealed class Variant {
                        object VariantA : Variant()
                        class VariantB : Variant()
                        object VariantC : Variant()
                    }

                    fun whenOnEnumFail(v: Variant) {
                        when (v) {
                            is Variant.VariantA -> {}
                            is Variant.VariantB -> {}
                            else -> {}
                        }
                    }
                """
                assertThat(subject.compileAndLintWithContext(env, code)).hasSize(1)
            }

            @Test
            fun `reports when enum or sealed _when_ expression contains _else_ case`() {
                val code = """
                    sealed class Variant {
                        object VariantA : Variant()
                        class VariantB : Variant()
                        object VariantC : Variant()
                    }

                    fun whenOnEnumFail(v: Variant) {
                        val x = when (v) {
                            is Variant.VariantA -> "a"
                            is Variant.VariantB -> "b"
                            else -> "other"
                        }
                    }
                """
                assertThat(subject.compileAndLintWithContext(env, code)).hasSize(1)
            }

            @Test
            fun `does not report when enum or sealed _when_ expression does not contain _else_ case`() {
                val code = """
                    sealed class Variant {
                        object VariantA : Variant()
                        class VariantB : Variant()
                        object VariantC : Variant()
                    }

                    fun whenOnEnumPass(v: Variant) {
                        when (v) {
                            is Variant.VariantA -> {}
                            is Variant.VariantB -> {}
                        }
                    }
                """
                assertThat(subject.compileAndLintWithContext(env, code)).isEmpty()
            }
        }

        @Nested
        inner class `standard when` {
            @Test
            fun `does not report when _else_ case is used for non enum or sealed _when_ expression`() {
                val code = """
                    fun whenChecks() {
                        val x = 3
                        val s = "3"

                        when (x) {
                            0, 1 -> print("x == 0 or x == 1")
                            else -> print("otherwise")
                        }

                        when (x) {
                            Integer.parseInt(s) -> print("s encodes x")
                            else -> print("s does not encode x")
                        }

                        when (x) {
                            in 1..10 -> print("x is in the range")
                            !in 10..20 -> print("x is outside the range")
                            else -> print("none of the above")
                        }

                        val y = when(s) {
                            is String -> s.startsWith("prefix")
                            else -> false
                        }

                        when {
                            x.equals(s) -> print("x equals s")
                            x.plus(3) == 4 -> print("x is 1")
                            else -> print("x is funny")
                        }
                    }
                """
                assertThat(subject.compileAndLintWithContext(env, code)).isEmpty()
            }
        }
    }
}
