package dev.detekt.rules.performance

import dev.detekt.api.Config
import dev.detekt.test.lint
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource

class UnnecessaryInitOnArraySpec {

    val subject = UnnecessaryInitOnArray(Config.empty)

    @ParameterizedTest(name = "reports {0} with lambda returning {1}")
    @MethodSource("defaultValueTestCases")
    fun casesWithDefaultValues(arrayType: String, defaultValue: String) {
        val code = "val a = $arrayType(10) { $defaultValue }"
        assertThat(subject.lint(code)).hasSize(1)
    }

    @ParameterizedTest(name = "does not report {0} without lambda")
    @MethodSource("arrayTypes")
    fun casesWithoutInit(arrayType: String) {
        val code = "val a = $arrayType(10)"
        assertThat(subject.lint(code)).isEmpty()
    }

    @ParameterizedTest(name = "does not report {0} with lambda returning non-default value")
    @MethodSource("nonDefaultValueTestCases")
    fun casesWithoutDefaultValues(arrayType: String, nonDefaultValue: String) {
        val code = "val a = $arrayType(10) { $nonDefaultValue }"
        assertThat(subject.lint(code)).isEmpty()
    }

    @Nested
    inner class `Side effects detection` {
        @Test
        fun `does not report when lambda has side effects with println`() {
            val code = """
                val a = IntArray(10) {
                    println("side effect")
                    0
                }
            """.trimIndent()
            assertThat(subject.lint(code)).isEmpty()
        }

        @Test
        fun `does not report when lambda has side effects with multiple statements`() {
            val code = """
                val a = IntArray(10) {
                    val x = 5
                    println(x)
                    0
                }
            """.trimIndent()
            assertThat(subject.lint(code)).isEmpty()
        }

        @Test
        fun `does not report when multiple statements are in same line`() {
            val code = """
                val a = IntArray(10) {
                    println(""); 0
                }
            """.trimIndent()
            assertThat(subject.lint(code)).isEmpty()
        }

        @Test
        fun `does not report when lambda has side effects with function call`() {
            val code = """
                fun sideEffect() = println("test")
                val a = IntArray(10) {
                    sideEffect()
                    0
                }
            """.trimIndent()
            assertThat(subject.lint(code)).isEmpty()
        }
    }

    @Nested
    inner class `Edge cases` {
        @Test
        fun `does not report non-array constructor calls`() {
            val code = """
                val a = ArrayList<Int>(10)
            """.trimIndent()
            assertThat(subject.lint(code)).isEmpty()
        }

        @Test
        fun `does not report when lambda returns different value`() {
            val code = """
                val a = IntArray(10) { it }
            """.trimIndent()
            assertThat(subject.lint(code)).isEmpty()
        }

        @Test
        fun `does not report when lambda returns calculated value`() {
            val code = """
                val a = IntArray(10) { it * 2 }
            """.trimIndent()
            assertThat(subject.lint(code)).isEmpty()
        }

        @Test
        fun `does not report when lambda returns variable`() {
            val code = """
                val defaultValue = 0
                val a = IntArray(10) { defaultValue }
            """.trimIndent()
            assertThat(subject.lint(code)).isEmpty()
        }
    }

    @Test
    fun `reports multiple violations in same file`() {
        val code = """
            val a = IntArray(10) { 0 }
            val b = FloatArray(10) { 0F }
            val c = BooleanArray(10) { false }
        """.trimIndent()
        assertThat(subject.lint(code)).hasSize(3)
    }

    @Test
    fun `reports violations in function calls`() {
        val code = """
            fun createArray(): IntArray {
                return IntArray(10) { 0 }
            }
        """.trimIndent()
        assertThat(subject.lint(code)).hasSize(1)
    }

    @Test
    fun `reports violations in class properties`() {
        val code = """
            class Test {
                val array = IntArray(10) { 0 }
            }
        """.trimIndent()
        assertThat(subject.lint(code)).hasSize(1)
    }

    companion object {
        @JvmStatic
        fun defaultValueTestCases() =
            listOf(
                Arguments.of("IntArray", "0"),
                Arguments.of("IntArray", "0.toInt()"),
                Arguments.of("UIntArray", "0u"),
                Arguments.of("UIntArray", "0.toUInt()"),
                Arguments.of("FloatArray", "0F"),
                Arguments.of("FloatArray", "0.0F"),
                Arguments.of("FloatArray", "0f"),
                Arguments.of("FloatArray", "0.0f"),
                Arguments.of("FloatArray", "0.toFloat()"),
                Arguments.of("LongArray", "0L"),
                Arguments.of("LongArray", "0.toLong()"),
                Arguments.of("ULongArray", "0uL"),
                Arguments.of("ULongArray", "0.toULong()"),
                Arguments.of("BooleanArray", "false"),
                Arguments.of("ByteArray", "0.toByte()"),
                Arguments.of("ByteArray", "0"),
                Arguments.of("UByteArray", "0.toUByte()"),
                Arguments.of("UByteArray", "0u"),
                Arguments.of("CharArray", "'\\u0000'"),
                Arguments.of("CharArray", "'\u0000'"),
                Arguments.of("CharArray", "0.toChar()")
            )

        @JvmStatic
        fun arrayTypes(): List<Arguments> = defaultValueTestCases().map { Arguments.of(it.get().first()) }

        @JvmStatic
        fun nonDefaultValueTestCases() =
            listOf(
                Arguments.of("IntArray", "1"),
                Arguments.of("UIntArray", "1u"),
                Arguments.of("LongArray", "1L"),
                Arguments.of("ULongArray", "1uL"),
                Arguments.of("BooleanArray", "true"),
                Arguments.of("ByteArray", "1.toByte()"),
                Arguments.of("UByteArray", "1.toUByte()"),
                Arguments.of("DoubleArray", "1.0"),
                Arguments.of("CharArray", "'A'")
            )
    }
}
