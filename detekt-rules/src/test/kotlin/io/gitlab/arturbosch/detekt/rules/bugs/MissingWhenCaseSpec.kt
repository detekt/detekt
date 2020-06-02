package io.gitlab.arturbosch.detekt.rules.bugs

import io.gitlab.arturbosch.detekt.rules.setupKotlinEnvironment
import io.gitlab.arturbosch.detekt.test.compileAndLintWithContext
import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.kotlin.cli.jvm.compiler.KotlinCoreEnvironment
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

object MissingWhenCaseSpec : Spek({
    setupKotlinEnvironment()

    val env: KotlinCoreEnvironment by memoized()
    val subject by memoized { MissingWhenCase() }

    describe("MissingWhenCase rule") {
        context("enum") {
            it("reports when `when` expression used as statement and not all cases covered") {
                val code = """
                enum class Color {
                    RED,
                    GREEN,
                    BLUE
                }

                fun whenOnEnumFail(c: Color) {
                    when(c) {
                        Color.BLUE -> {}
                        Color.GREEN -> {}
                    }
                }
                """
                val actual = subject.compileAndLintWithContext(env, code)
                assertThat(actual).hasSize(1)
                assertThat(actual.first().issue.id).isEqualTo("MissingWhenCase")
                assertThat(actual.first().message).isEqualTo("When expression is missing cases: RED. Either add missing cases or a default `else` case.")
            }

            it("does not report when `when` expression used as statement and all cases covered") {
                val code = """
                enum class Color {
                    RED,
                    GREEN,
                    BLUE
                }

                fun whenOnEnumPass(c: Color) {
                    when(c) {
                        Color.BLUE -> {}
                        Color.GREEN -> {}
                        Color.RED -> {}
                    }
                }

                fun whenOnEnumPass2(c: Color) {
                    when(c) {
                        Color.BLUE -> {}
                        else -> {}
                    }
                }
                """
                assertThat(subject.compileAndLintWithContext(env, code)).isEmpty()
            }
        }
        context("sealed classes") {
            it("reports when `when` expression used as statement and not all cases covered") {
                val code = """
                    sealed class Variant {
                        object VariantA : Variant()
                        class VariantB : Variant()
                        object VariantC : Variant()
                    }

                    fun whenOnEnumFail(v: Variant) {
                        when(v) {
                            is Variant.VariantA -> {}
                            is Variant.VariantB -> {}
                        }
                    }
                """
                val actual = subject.compileAndLintWithContext(env, code)
                assertThat(actual).hasSize(1)
                assertThat(actual.first().issue.id).isEqualTo("MissingWhenCase")
                assertThat(actual.first().message).isEqualTo("When expression is missing cases: VariantC. Either add missing cases or a default `else` case.")
            }

            it("does not report when `when` expression used as statement and all cases covered") {
                val code = """
                    sealed class Variant {
                        object VariantA : Variant()
                        class VariantB : Variant()
                        object VariantC : Variant()
                    }

                    fun whenOnEnumPassA(v: Variant) {
                        when(v) {
                            is Variant.VariantA -> {}
                            is Variant.VariantB -> {}
                            else -> {}
                        }
                    }

                    fun whenOnEnumPassB(v: Variant) {
                        when(v) {
                            is Variant.VariantA -> {}
                            is Variant.VariantB -> {}
                            is Variant.VariantC -> {}
                        }
                    }
                """
                assertThat(subject.compileAndLintWithContext(env, code)).isEmpty()
            }
        }
        context("standard when") {
            it("does not report when `when` not checking for missing cases") {
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
})
