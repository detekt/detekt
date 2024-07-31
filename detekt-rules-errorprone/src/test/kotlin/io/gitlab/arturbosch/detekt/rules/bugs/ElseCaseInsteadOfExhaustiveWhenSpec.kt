package io.gitlab.arturbosch.detekt.rules.bugs

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.rules.KotlinCoreEnvironmentTest
import io.gitlab.arturbosch.detekt.test.TestConfig
import io.gitlab.arturbosch.detekt.test.compileAndLintWithContext
import io.gitlab.arturbosch.detekt.test.lintWithContext
import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.kotlin.cli.jvm.compiler.KotlinCoreEnvironment
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

@KotlinCoreEnvironmentTest
class ElseCaseInsteadOfExhaustiveWhenSpec(private val env: KotlinCoreEnvironment) {
    private val subject = ElseCaseInsteadOfExhaustiveWhen(Config.empty)

    @Nested
    inner class Enum {
        @Test
        fun `reports when enum _when_ expression used as statement contains _else_ case`() {
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
            """.trimIndent()
            val actual = subject.compileAndLintWithContext(env, code)
            assertThat(actual).hasSize(1)
        }

        @Test
        fun `reports when enum _when_ expression contains _else_ case`() {
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
            """.trimIndent()
            val actual = subject.compileAndLintWithContext(env, code)
            assertThat(actual).hasSize(1)
        }

        @Test
        fun `does not report when enum _when_ expression does not contain _else_ case`() {
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
            """.trimIndent()
            assertThat(subject.lintWithContext(env, code)).isEmpty()
        }
    }

    @Nested
    inner class `Sealed class` {
        @Test
        fun `reports when sealed _when_ expression used as statement contains _else_ case`() {
            val code = """
                sealed class Variant {
                    object VariantA : Variant()
                    class VariantB : Variant()
                    object VariantC : Variant()
                }
                
                fun whenOnSealedFail(v: Variant) {
                    when (v) {
                        is Variant.VariantA -> {}
                        is Variant.VariantB -> {}
                        else -> {}
                    }
                }
            """.trimIndent()
            assertThat(subject.compileAndLintWithContext(env, code)).hasSize(1)
        }

        @Test
        fun `reports when sealed _when_ expression contains _else_ case`() {
            val code = """
                sealed class Variant {
                    object VariantA : Variant()
                    class VariantB : Variant()
                    object VariantC : Variant()
                }
                
                fun whenOnSealedFail(v: Variant) {
                    val x = when (v) {
                        is Variant.VariantA -> "a"
                        is Variant.VariantB -> "b"
                        else -> "other"
                    }
                }
            """.trimIndent()
            assertThat(subject.compileAndLintWithContext(env, code)).hasSize(1)
        }

        @Test
        fun `does not report when sealed _when_ expression does not contain _else_ case`() {
            val code = """
                sealed class Variant {
                    object VariantA : Variant()
                    class VariantB : Variant()
                    object VariantC : Variant()
                }
                
                fun whenOnSealedPass(v: Variant) {
                    when (v) {
                        is Variant.VariantA -> {}
                        is Variant.VariantB -> {}
                    }
                }
            """.trimIndent()
            assertThat(subject.compileAndLintWithContext(env, code)).isEmpty()
        }
    }

    @Nested
    inner class `Various ignoreSubjectTypes configurations` {

        @Test
        fun `does not report if _when_ contains _else_ case for ignored _enum_ subject type`() {
            val code = """
                package com.example
                
                enum class Color {
                    RED,
                    GREEN,
                    BLUE
                }
                
                fun whenOnEnumPasses(c: Color) {
                    when (c) {
                        Color.BLUE -> {}
                        Color.GREEN -> {}
                        else -> {}
                    }
                }
            """.trimIndent()
            assertThat(
                ElseCaseInsteadOfExhaustiveWhen(
                    TestConfig("ignoredSubjectTypes" to listOf("com.example.Color"))
                ).compileAndLintWithContext(env, code)
            ).isEmpty()
        }

        @Test
        fun `does not report if _when_ contains _else_ case for ignored _sealed_ subject type`() {
            val code = """
                package com.example
                
                sealed class Variant {
                    object VariantA : Variant()
                    class VariantB : Variant()
                    object VariantC : Variant()
                }
                
                fun whenOnSealedPasses(v: Variant) {
                    when (v) {
                        is Variant.VariantA -> {}
                        is Variant.VariantB -> {}
                        else -> {}
                    }
                }
            """.trimIndent()
            assertThat(
                ElseCaseInsteadOfExhaustiveWhen(
                    TestConfig("ignoredSubjectTypes" to listOf("com.example.Variant"))
                ).compileAndLintWithContext(env, code)
            ).isEmpty()
        }

        @Test
        fun `reports if _when_ contains _else_ case for non-ignored _enum_ subject type`() {
            val code = """
                package com.example
                
                enum class Color {
                    RED,
                    GREEN,
                    BLUE
                }
                
                fun whenOnEnumFails(c: Color) {
                    when (c) {
                        Color.BLUE -> {}
                        Color.GREEN -> {}
                        else -> {}
                    }
                }
            """.trimIndent()
            assertThat(
                ElseCaseInsteadOfExhaustiveWhen(
                    TestConfig("ignoredSubjectTypes" to listOf("com.example.Class"))
                ).compileAndLintWithContext(env, code)
            ).hasSize(1)
        }

        @Test
        fun `reports if _when_ contains _else_ case for non-ignored _sealed_ subject type`() {
            val code = """
                package com.example
                
                sealed class Variant {
                    object VariantA : Variant()
                    class VariantB : Variant()
                    object VariantC : Variant()
                }
                
                fun whenOnSealedPasses(v: Variant) {
                    when (v) {
                        is Variant.VariantA -> {}
                        is Variant.VariantB -> {}
                        else -> {}
                    }
                }
            """.trimIndent()
            assertThat(
                ElseCaseInsteadOfExhaustiveWhen(
                    TestConfig("ignoredSubjectTypes" to listOf("com.example.Class"))
                ).compileAndLintWithContext(env, code)
            ).hasSize(1)
        }
    }

    @Nested
    inner class `Expected sealed class` {
        @Test
        fun `does not report when _expect_ sealed _when_ expression used as statement contains _else_ case`() {
            val code = """
                expect sealed class Variant {
                    class VariantA : Variant
                    class VariantB : Variant
                    class VariantC : Variant
                }
                
                fun whenOnSealedFail(v: Variant) {
                    when (v) {
                        is Variant.VariantA -> {}
                        is Variant.VariantB -> {}
                        else -> {}
                    }
                }
            """.trimIndent()
            assertThat(subject.lintWithContext(env, code)).isEmpty()
        }

        @Test
        fun `does not report when _expect_ sealed _when_ expression contains _else_ case`() {
            val code = """
                expect sealed class Variant {
                    class VariantA : Variant
                    class VariantB : Variant
                    class VariantC : Variant
                }
                
                fun whenOnSealedFail(v: Variant) {
                    val x = when (v) {
                        is Variant.VariantA -> "a"
                        is Variant.VariantB -> "b"
                        else -> "other"
                    }
                }
            """.trimIndent()
            assertThat(subject.lintWithContext(env, code)).isEmpty()
        }
    }

    @Nested
    inner class Boolean {
        @Test
        fun `reports when boolean _when_ expression used as statement contains _else_ case`() {
            val code = """
                fun whenOnBooleanFail(b: Boolean) {
                    when (b) {
                        true -> {}
                        else -> {}
                    }
                }
            """.trimIndent()
            val actual = subject.compileAndLintWithContext(env, code)
            assertThat(actual).hasSize(1)
        }

        @Test
        fun `reports when nullable boolean _when_ expression contains _else_ case`() {
            val code = """
                fun whenOnNullableBooleanFail(b: Boolean?) {
                    val x = when (b) {
                        true -> 1
                        false -> 2
                        else -> 100
                    }
                }
            """.trimIndent()
            val actual = subject.compileAndLintWithContext(env, code)
            assertThat(actual).hasSize(1)
        }

        @Test
        fun `does not report when boolean _when_ expression does not contain _else_ case`() {
            val code = """
                fun whenOnBooleanPassA(b: Boolean) {
                    when (b) {
                        true -> {}
                        false -> {}
                    }
                
                    val x = when (b) {
                        true -> 1
                        false -> 2
                    }
                }
            """.trimIndent()
            assertThat(subject.compileAndLintWithContext(env, code)).isEmpty()
        }

        @Test
        fun `does not report when nullable boolean _when_ expression does not contain _else_ case`() {
            val code = """
                fun whenOnNullableBooleanPassA(b: Boolean?) {
                    when (b) {
                        true -> {}
                        false -> {}
                        null -> {}
                    }
                
                    val x = when (b) {
                        true -> 1
                        false -> 2
                        null -> 100
                    }
                }
            """.trimIndent()
            assertThat(subject.compileAndLintWithContext(env, code)).isEmpty()
        }
    }

    @Nested
    inner class `Standard when` {
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
            """.trimIndent()
            assertThat(subject.compileAndLintWithContext(env, code)).isEmpty()
        }
    }
}
