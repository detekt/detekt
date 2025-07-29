package io.gitlab.arturbosch.detekt.rules.performance

import dev.detekt.api.Config
import dev.detekt.test.utils.KotlinCoreEnvironmentTest
import dev.detekt.test.utils.KotlinEnvironmentContainer
import io.gitlab.arturbosch.detekt.test.lintWithContext
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

@KotlinCoreEnvironmentTest
class ArrayPrimitiveSpec(val env: KotlinEnvironmentContainer) {

    val subject = ArrayPrimitive(Config.empty)

    @Nested
    inner class `one function parameter` {
        @Test
        fun `is an array of primitive type`() {
            val code = "fun function(array: Array<Int>) {}"
            assertThat(subject.lintWithContext(env, code)).hasSize(1)
        }

        @Test
        fun `is not an array`() {
            val code = "fun function(i: Int) {}"
            assertThat(subject.lintWithContext(env, code)).isEmpty()
        }

        @Test
        fun `is a specialized array`() {
            val code = "fun function(array: ByteArray) {}"
            assertThat(subject.lintWithContext(env, code)).isEmpty()
        }

        @Test
        fun `is a star-projected array`() {
            val code = "fun function(array: Array<*>) {}"
            assertThat(subject.lintWithContext(env, code)).isEmpty()
        }

        @Test
        fun `is not present`() {
            val code = "fun function() {}"
            assertThat(subject.lintWithContext(env, code)).isEmpty()
        }

        @Test
        fun `is an array of a non-primitive type`() {
            val code = "fun function(array: Array<String>) {}"
            assertThat(subject.lintWithContext(env, code)).isEmpty()
        }

        @Test
        fun `is an array of an array of a primitive type`() {
            val code = "fun function(array: Array<Array<Int>>) {}"
            assertThat(subject.lintWithContext(env, code)).hasSize(1)
        }

        @Test
        fun `is a dictionary with an array of a primitive type as key`() {
            val code = "fun function(dict: java.util.Dictionary<Int, Array<Int>>) {}"
            assertThat(subject.lintWithContext(env, code)).hasSize(1)
        }
    }

    @Nested
    inner class `multiple function parameters` {
        @Test
        @DisplayName("one is Array<Primitive> and the other is not")
        fun oneArrayPrimitive() {
            val code = "fun function(array: Array<Int>, array2: IntArray) {}"
            assertThat(subject.lintWithContext(env, code)).hasSize(1)
        }

        @Test
        fun `both are arrays of primitive types`() {
            val code = "fun function(array: Array<Int>, array2: Array<Double>) {}"
            assertThat(subject.lintWithContext(env, code)).hasSize(2)
        }
    }

    @Nested
    inner class `return type` {
        @Test
        @DisplayName("is Array<Primitive>")
        fun isArrayPrimitive() {
            val code = "fun returningFunction(): Array<Float> { return emptyArray() }"
            assertThat(subject.lintWithContext(env, code)).hasSize(2)
        }

        @Test
        fun `is not an array`() {
            val code = "fun returningFunction(): Int { return 1 }"
            assertThat(subject.lintWithContext(env, code)).isEmpty()
        }

        @Test
        fun `is a specialized array`() {
            val code = "fun returningFunction(): CharArray { return CharArray(0) }"
            assertThat(subject.lintWithContext(env, code)).isEmpty()
        }

        @Test
        fun `is a star-projected array`() {
            val code = "fun returningFunction(): Array<*> { return emptyArray<Any>() }"
            assertThat(subject.lintWithContext(env, code)).isEmpty()
        }

        @Test
        fun `is not explicitly set`() {
            val code = "fun returningFunction() {}"
            assertThat(subject.lintWithContext(env, code)).isEmpty()
        }
    }

    @Nested
    inner class `variable type` {
        @Test
        @DisplayName("is Array<Primitive>")
        fun isArrayPrimitive() {
            val code = "val foo: Array<Int>? = null"
            assertThat(subject.lintWithContext(env, code)).hasSize(1)
        }
    }

    @Nested
    inner class `receiver type` {
        @Test
        @DisplayName("is Array<Primitive>")
        fun isArrayPrimitive() {
            val code = "fun Array<Boolean>.foo() { println(this) }"
            assertThat(subject.lintWithContext(env, code)).hasSize(1)
        }
    }

    @Nested
    inner class ArrayOf {
        @Test
        fun `is arrayOf(Char)`() {
            val code = "fun foo(x: Char) = arrayOf(x)"
            assertThat(subject.lintWithContext(env, code)).hasSize(1)
        }

        @Test
        fun `is arrayOf(Byte)`() {
            val code = "fun foo(x: Byte) = arrayOf(x)"
            assertThat(subject.lintWithContext(env, code)).hasSize(1)
        }

        @Test
        fun `is arrayOf(Short)`() {
            val code = "fun foo(x: Short) = arrayOf(x)"
            assertThat(subject.lintWithContext(env, code)).hasSize(1)
        }

        @Test
        fun `is arrayOf(Int)`() {
            val code = "fun foo(x: Int) = arrayOf(x)"
            assertThat(subject.lintWithContext(env, code)).hasSize(1)
        }

        @Test
        fun `is arrayOf(Long)`() {
            val code = "fun foo(x: Long) = arrayOf(x)"
            assertThat(subject.lintWithContext(env, code)).hasSize(1)
        }

        @Test
        fun `is arrayOf(Float)`() {
            val code = "fun foo(x: Float) = arrayOf(x)"
            assertThat(subject.lintWithContext(env, code)).hasSize(1)
        }

        @Test
        fun `is arrayOf(Double)`() {
            val code = "fun foo(x: Double) = arrayOf(x)"
            assertThat(subject.lintWithContext(env, code)).hasSize(1)
        }

        @Test
        fun `is arrayOf(Boolean)`() {
            val code = "fun foo(x: Boolean) = arrayOf(x)"
            assertThat(subject.lintWithContext(env, code)).hasSize(1)
        }

        @Test
        fun `is arrayOf(String)`() {
            val code = "fun foo(x: String) = arrayOf(x)"
            assertThat(subject.lintWithContext(env, code)).isEmpty()
        }

        @Test
        fun `is intArrayOf()`() {
            val code = "fun test(x: Int) = intArrayOf(x)"
            assertThat(subject.lintWithContext(env, code)).isEmpty()
        }
    }

    @Nested
    inner class EmptyArray {
        @Test
        @DisplayName("is emptyArray<Char>()")
        fun isEmptyArrayChar() {
            val code = "val a = emptyArray<Char>()"
            assertThat(subject.lintWithContext(env, code)).hasSize(1)
        }

        @Test
        @DisplayName("is emptyArray<Byte>()")
        fun isEmptyArrayByte() {
            val code = "val a = emptyArray<Byte>()"
            assertThat(subject.lintWithContext(env, code)).hasSize(1)
        }

        @Test
        @DisplayName("is emptyArray<Short>()")
        fun isEmptyArrayShort() {
            val code = "val a = emptyArray<Short>()"
            assertThat(subject.lintWithContext(env, code)).hasSize(1)
        }

        @Test
        @DisplayName("is emptyArray<Int>()")
        fun isEmptyArrayInt() {
            val code = "val a = emptyArray<Int>()"
            assertThat(subject.lintWithContext(env, code)).hasSize(1)
        }

        @Test
        @DisplayName("is emptyArray<Long>()")
        fun isEmptyArrayLong() {
            val code = "val a = emptyArray<Long>()"
            assertThat(subject.lintWithContext(env, code)).hasSize(1)
        }

        @Test
        @DisplayName("is emptyArray<Float>()")
        fun isEmptyArrayFloat() {
            val code = "val a = emptyArray<Float>()"
            assertThat(subject.lintWithContext(env, code)).hasSize(1)
        }

        @Test
        @DisplayName("is emptyArray<Double>()")
        fun isEmptyArrayDouble() {
            val code = "val a = emptyArray<Double>()"
            assertThat(subject.lintWithContext(env, code)).hasSize(1)
        }

        @Test
        @DisplayName("is emptyArray<Boolean>()")
        fun isEmptyArrayBoolean() {
            val code = "val a = emptyArray<Boolean>()"
            assertThat(subject.lintWithContext(env, code)).hasSize(1)
        }

        @Test
        @DisplayName("is emptyArray<String>()")
        fun isEmptyArrayString() {
            val code = "val a = emptyArray<String>()"
            assertThat(subject.lintWithContext(env, code)).isEmpty()
        }
    }
}
