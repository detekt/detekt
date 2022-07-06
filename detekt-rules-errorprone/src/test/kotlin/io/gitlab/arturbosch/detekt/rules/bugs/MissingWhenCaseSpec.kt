package io.gitlab.arturbosch.detekt.rules.bugs

import io.gitlab.arturbosch.detekt.rules.KotlinCoreEnvironmentTest
import io.gitlab.arturbosch.detekt.test.TestConfig
import io.gitlab.arturbosch.detekt.test.compileAndLintWithContext
import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.kotlin.cli.jvm.compiler.KotlinCoreEnvironment
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

@KotlinCoreEnvironmentTest
class MissingWhenCaseSpec(private val env: KotlinCoreEnvironment) {

    @Nested
    inner class `MissingWhenCase rule` {
        private val subject = MissingWhenCase()

        @Nested
        inner class `enum` {
            @Test
            fun `reports when _when_ expression used as statement and not all cases are covered`() {
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
                assertThat(actual.first().message).isEqualTo(
                    "When expression is missing cases: RED. Either add missing cases or a default `else` case."
                )
            }

            @Test
            fun `reports when _when_ expression used as statement and not all cases including null are covered`() {
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
                assertThat(actual.first().message).isEqualTo(
                    "When expression is missing cases: RED, null. Either add missing cases or a default `else` case."
                )
            }

            @Test
            fun `reports when _when_ expression used as statement and null case is not covered`() {
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
                assertThat(actual.first().message).isEqualTo(
                    "When expression is missing cases: null. Either add missing cases or a default `else` case."
                )
            }

            @Test
            fun `does not report missing null case in _when_ expression when it is handled outside of _when_`() {
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

            @Test
            fun `does not report when _when_ expression used as statement and all cases are covered`() {
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

        @Nested
        inner class `sealed classes` {
            @Test
            fun `reports when _when_ expression used as statement and not all cases are covered`() {
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
                assertThat(actual.first().message).isEqualTo(
                    "When expression is missing cases: VariantC. Either add missing cases or a default `else` case."
                )
            }

            @Test
            fun `reports when _when_ expression used as statement and null case is not covered`() {
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
                assertThat(actual.first().message).isEqualTo(
                    "When expression is missing cases: null. Either add missing cases or a default `else` case."
                )
            }

            @Test
            fun `reports when _when_ expression used as statement and not all cases including null are covered`() {
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
                assertThat(actual.first().message).isEqualTo(
                    "When expression is missing cases: VariantC, null. Either add missing cases or a default `else` case."
                )
            }

            @Test
            fun `does not report missing null case in _when_ expression when it is handled outside of _when_`() {
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

            @Test
            fun `does not report when _when_ expression used as statement and all cases are covered`() {
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

        @Nested
        inner class `standard when` {
            @Test
            fun `does not report when _when_ not checking for missing cases`() {
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

    @Nested
    inner class `MissingWhenCase rule when else expression is not considered` {
        private val subject = MissingWhenCase(
            TestConfig(mapOf("allowElseExpression" to false))
        )

        @Nested
        inner class `enum` {
            @Test
            fun `reports when _when_ expression used as statement and not all cases are covered`() {
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

            @Test
            fun `reports when _when_ expression used as statement and null case is not covered`() {
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

            @Test
            fun `does not reports when _when_ expression used as statement and all cases are covered`() {
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

        @Nested
        inner class `sealed classes` {
            @Test
            fun `reports when _when_ expression used as statement and not all cases covered`() {
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

            @Test
            fun `does not report when _when_ expression used as statement and all cases are covered`() {
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

        @Nested
        inner class `standard when` {
            @Test
            fun `does not report when else is used for non enum or sealed _when_ expression`() {
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
}
