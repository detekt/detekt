package io.gitlab.arturbosch.detekt.rules.bugs

import io.gitlab.arturbosch.detekt.rules.setupKotlinEnvironment
import io.gitlab.arturbosch.detekt.test.TestConfig
import io.gitlab.arturbosch.detekt.test.compileAndLintWithContext
import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.kotlin.cli.jvm.compiler.KotlinCoreEnvironment
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

object MissingWhenCaseSpec : Spek({
    setupKotlinEnvironment()

    val env: KotlinCoreEnvironment by memoized()

    describe("MissingWhenCase rule") {
        val subject by memoized { MissingWhenCase() }

        context("enum") {
            it("reports when `when` expression used as statement and not all cases are covered") {
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

            it("reports when `when` expression used as statement and not all cases including null are covered") {
                val code = """
                enum class Color {
                    RED,
                    GREEN,
                    BLUE
                }

                fun whenOnEnumFail(c: Color?) {
                    when(c) {
                        Color.BLUE -> {}
                        Color.GREEN -> {}
                    }
                }
                """
                val actual = subject.compileAndLintWithContext(env, code)
                assertThat(actual).hasSize(1)
                assertThat(actual.first().issue.id).isEqualTo("MissingWhenCase")
                assertThat(actual.first().message).isEqualTo("When expression is missing cases: RED, null. Either add missing cases or a default `else` case.")
            }

            it("reports when `when` expression used as statement and null case is not covered") {
                val code = """
                enum class Color {
                    RED,
                    GREEN,
                    BLUE
                }

                fun whenOnEnumFail(c: Color?) {
                    when(c) {
                        Color.BLUE -> {}
                        Color.GREEN -> {}
                        Color.RED -> {}
                    }
                }
                """
                val actual = subject.compileAndLintWithContext(env, code)
                assertThat(actual).hasSize(1)
                assertThat(actual.first().issue.id).isEqualTo("MissingWhenCase")
                assertThat(actual.first().message).isEqualTo("When expression is missing cases: null. Either add missing cases or a default `else` case.")
            }

            it("does not report missing null case in `when` expression when it is handled outside of `when`") {
                val code = """
                enum class Color {
                    RED,
                    GREEN,
                    BLUE
                }

                fun whenNulLCheckEnum(c: Color?) {
                    if(c == null) return
                    when(c) {
                        Color.BLUE -> {}
                        Color.GREEN -> {}
                        Color.RED -> {}
                    }
                }
                """
                assertThat(subject.compileAndLintWithContext(env, code)).isEmpty()
            }

            it("does not report when `when` expression used as statement and all cases are covered") {
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
            it("reports when `when` expression used as statement and not all cases are covered") {
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

            it("reports when `when` expression used as statement and null case is not covered") {
                val code = """
                    sealed class Variant {
                        object VariantA : Variant()
                        class VariantB : Variant()
                        object VariantC : Variant()
                    }

                    fun whenOnEnumFail(v: Variant?) {
                        when(v) {
                            is Variant.VariantA -> {}
                            is Variant.VariantB -> {}
                            is Variant.VariantC -> {}
                        }
                    }
                """
                val actual = subject.compileAndLintWithContext(env, code)
                assertThat(actual).hasSize(1)
                assertThat(actual.first().issue.id).isEqualTo("MissingWhenCase")
                assertThat(actual.first().message).isEqualTo("When expression is missing cases: null. Either add missing cases or a default `else` case.")
            }

            it("reports when `when` expression used as statement and not all cases including null are covered") {
                val code = """
                    sealed class Variant {
                        object VariantA : Variant()
                        class VariantB : Variant()
                        object VariantC : Variant()
                    }

                    fun whenOnEnumFail(v: Variant?) {
                        when(v) {
                            is Variant.VariantA -> {}
                            is Variant.VariantB -> {}
                        }
                    }
                """
                val actual = subject.compileAndLintWithContext(env, code)
                assertThat(actual).hasSize(1)
                assertThat(actual.first().issue.id).isEqualTo("MissingWhenCase")
                assertThat(actual.first().message).isEqualTo("When expression is missing cases: VariantC, null. Either add missing cases or a default `else` case.")
            }

            it("does not report missing null case in `when` expression when it is handled outside of `when`") {
                val code = """
                sealed class Variant {
                        object VariantA : Variant()
                        class VariantB : Variant()
                        object VariantC : Variant()
                    }

                    fun whenOnEnumFail(v: Variant?) {
                        if(v == null) return
                        when(v) {
                            is Variant.VariantA -> {}
                            is Variant.VariantB -> {}
                            is Variant.VariantC -> {}
                        }
                    }
                """
                assertThat(subject.compileAndLintWithContext(env, code)).isEmpty()
            }

            it("does not report when `when` expression used as statement and all cases are covered") {
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

    describe("MissingWhenCase rule when else expression is not considered") {
        val subject by memoized {
            MissingWhenCase(
                TestConfig(mapOf(MissingWhenCase.ALLOW_ELSE_EXPRESSION to false))
            )
        }

        context("enum") {
            it("reports when `when` expression used as statement and not all cases are covered") {
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
                        else -> {}
                    }
                }
                """
                val actual = subject.compileAndLintWithContext(env, code)
                assertThat(actual).hasSize(1)
                assertThat(actual.first().issue.id).isEqualTo("MissingWhenCase")
                assertThat(actual.first().message).isEqualTo("When expression is missing cases: RED.")
            }

            it("reports when `when` expression used as statement and null case is not covered") {
                val code = """
                enum class Color {
                    RED,
                    GREEN,
                    BLUE
                }

                fun whenOnEnumFail(c: Color?) {
                    when(c) {
                        Color.BLUE -> {}
                        Color.GREEN -> {}
                        Color.RED -> {}
                        else -> {}
                    }
                }
                """
                val actual = subject.compileAndLintWithContext(env, code)
                assertThat(actual).hasSize(1)
                assertThat(actual.first().issue.id).isEqualTo("MissingWhenCase")
                assertThat(actual.first().message).isEqualTo("When expression is missing cases: null.")
            }

            it("does not reports when `when` expression used as statement and all cases are covered") {
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
                        Color.RED -> {}
                    }
                }
                """
                val actual = subject.compileAndLintWithContext(env, code)
                assertThat(actual).isEmpty()
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
                            else -> {}
                        }
                    }
                """
                val actual = subject.compileAndLintWithContext(env, code)
                assertThat(actual).hasSize(1)
                assertThat(actual.first().issue.id).isEqualTo("MissingWhenCase")
                assertThat(actual.first().message).isEqualTo("When expression is missing cases: VariantC.")
            }

            it("does not report when `when` expression used as statement and all cases are covered") {
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
                            is Variant.VariantC -> {}
                        }
                    }
                """
                assertThat(subject.compileAndLintWithContext(env, code)).isEmpty()
            }
        }

        context("standard when") {
            it("does not report when else is used for non enum or sealed `when` expression") {
                val code = """
                     fun whenChecks() {
                        val x = 3

                        when (x) {
                            0, 1 -> print("x == 0 or x == 1")
                            else -> print("otherwise")
                        }
                    }
                """
                assertThat(subject.compileAndLintWithContext(env, code)).isEmpty()
            }
        }
    }
})
