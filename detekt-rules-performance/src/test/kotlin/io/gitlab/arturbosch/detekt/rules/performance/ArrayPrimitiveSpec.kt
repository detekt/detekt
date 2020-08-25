package io.gitlab.arturbosch.detekt.rules.performance

import io.gitlab.arturbosch.detekt.rules.setupKotlinEnvironment
import io.gitlab.arturbosch.detekt.test.compileAndLint
import io.gitlab.arturbosch.detekt.test.compileAndLintWithContext
import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.kotlin.cli.jvm.compiler.KotlinCoreEnvironment
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

class ArrayPrimitiveSpec : Spek({
    setupKotlinEnvironment()

    val env: KotlinCoreEnvironment by memoized()
    val subject by memoized { ArrayPrimitive() }

    describe("one function parameter") {
        it("is an array of primitive type") {
            val code = "fun function(array: Array<Int>) {}"
            assertThat(subject.compileAndLint(code)).hasSize(1)
        }

        it("is not an array") {
            val code = "fun function(i: Int) {}"
            assertThat(subject.compileAndLint(code)).isEmpty()
        }

        it("is a specialized array") {
            val code = "fun function(array: ByteArray) {}"
            assertThat(subject.compileAndLint(code)).isEmpty()
        }

        it("is a star-projected array") {
            val code = "fun function(array: Array<*>) {}"
            assertThat(subject.compileAndLint(code)).isEmpty()
        }

        it("is not present") {
            val code = "fun function() {}"
            assertThat(subject.compileAndLint(code)).isEmpty()
        }

        it("is an array of a non-primitive type") {
            val code = "fun function(array: Array<String>) {}"
            assertThat(subject.compileAndLint(code)).isEmpty()
        }

        it("is an array of an array of a primitive type") {
            val code = "fun function(array: Array<Array<Int>>) {}"
            assertThat(subject.compileAndLint(code)).hasSize(1)
        }

        it("is a dictionary with an array of a primitive type as key") {
            val code = "fun function(dict: java.util.Dictionary<Int, Array<Int>>) {}"
            assertThat(subject.compileAndLint(code)).hasSize(1)
        }
    }

    describe("multiple function parameters") {
        it("one is Array<Primitive> and the other is not") {
            val code = "fun function(array: Array<Int>, array2: IntArray) {}"
            assertThat(subject.compileAndLint(code)).hasSize(1)
        }

        it("both are arrays of primitive types") {
            val code = "fun function(array: Array<Int>, array2: Array<Double>) {}"
            assertThat(subject.compileAndLint(code)).hasSize(2)
        }
    }

    describe("return type") {
        it("is Array<Primitive>") {
            val code = "fun returningFunction(): Array<Float> { return emptyArray() }"
            assertThat(subject.compileAndLint(code)).hasSize(1)
        }

        it("is not an array") {
            val code = "fun returningFunction(): Int { return 1 }"
            assertThat(subject.compileAndLint(code)).isEmpty()
        }

        it("is a specialized array") {
            val code = "fun returningFunction(): CharArray { return CharArray(0) }"
            assertThat(subject.compileAndLint(code)).isEmpty()
        }

        it("is a star-projected array") {
            val code = "fun returningFunction(): Array<*> { return emptyArray<Any>() }"
            assertThat(subject.compileAndLint(code)).isEmpty()
        }

        it("is not explicitly set") {
            val code = "fun returningFunction() {}"
            assertThat(subject.compileAndLint(code)).isEmpty()
        }
    }

    describe("variable type") {
        it("is Array<Primitive>") {
            val code = "val foo: Array<Int>? = null"
            assertThat(subject.compileAndLintWithContext(env, code)).hasSize(1)
        }
    }

    describe("receiver type") {
        it("is Array<Primitive>") {
            val code = "fun Array<Boolean>.foo() { println(this) }"
            assertThat(subject.compileAndLintWithContext(env, code)).hasSize(1)
        }
    }

    describe("arrayOf") {
        it("is arrayOf(Char)") {
            val code = "fun foo(x: Char) = arrayOf(x)"
            assertThat(subject.compileAndLintWithContext(env, code)).hasSize(1)
        }

        it("is arrayOf(Byte)") {
            val code = "fun foo(x: Byte) = arrayOf(x)"
            assertThat(subject.compileAndLintWithContext(env, code)).hasSize(1)
        }

        it("is arrayOf(Short)") {
            val code = "fun foo(x: Short) = arrayOf(x)"
            assertThat(subject.compileAndLintWithContext(env, code)).hasSize(1)
        }

        it("is arrayOf(Int)") {
            val code = "fun foo(x: Int) = arrayOf(x)"
            assertThat(subject.compileAndLintWithContext(env, code)).hasSize(1)
        }

        it("is arrayOf(Long)") {
            val code = "fun foo(x: Long) = arrayOf(x)"
            assertThat(subject.compileAndLintWithContext(env, code)).hasSize(1)
        }

        it("is arrayOf(Float)") {
            val code = "fun foo(x: Float) = arrayOf(x)"
            assertThat(subject.compileAndLintWithContext(env, code)).hasSize(1)
        }

        it("is arrayOf(Double)") {
            val code = "fun foo(x: Double) = arrayOf(x)"
            assertThat(subject.compileAndLintWithContext(env, code)).hasSize(1)
        }

        it("is arrayOf(Boolean)") {
            val code = "fun foo(x: Boolean) = arrayOf(x)"
            assertThat(subject.compileAndLintWithContext(env, code)).hasSize(1)
        }

        it("is arrayOf(String)") {
            val code = "fun foo(x: String) = arrayOf(x)"
            assertThat(subject.compileAndLintWithContext(env, code)).isEmpty()
        }

        it("is intArrayOf()") {
            val code = "fun test(x: Int) = intArrayOf(x)"
            assertThat(subject.compileAndLintWithContext(env, code)).isEmpty()
        }
    }

    describe("emptyArray") {
        it("is emptyArray<Char>()") {
            val code = "val a = emptyArray<Char>()"
            assertThat(subject.compileAndLintWithContext(env, code)).hasSize(1)
        }

        it("is emptyArray<Byte>()") {
            val code = "val a = emptyArray<Byte>()"
            assertThat(subject.compileAndLintWithContext(env, code)).hasSize(1)
        }

        it("is emptyArray<Short>()") {
            val code = "val a = emptyArray<Short>()"
            assertThat(subject.compileAndLintWithContext(env, code)).hasSize(1)
        }

        it("is emptyArray<Int>()") {
            val code = "val a = emptyArray<Int>()"
            assertThat(subject.compileAndLintWithContext(env, code)).hasSize(1)
        }

        it("is emptyArray<Long>()") {
            val code = "val a = emptyArray<Long>()"
            assertThat(subject.compileAndLintWithContext(env, code)).hasSize(1)
        }

        it("is emptyArray<Float>()") {
            val code = "val a = emptyArray<Float>()"
            assertThat(subject.compileAndLintWithContext(env, code)).hasSize(1)
        }

        it("is emptyArray<Double>()") {
            val code = "val a = emptyArray<Double>()"
            assertThat(subject.compileAndLintWithContext(env, code)).hasSize(1)
        }

        it("is emptyArray<Boolean>()") {
            val code = "val a = emptyArray<Boolean>()"
            assertThat(subject.compileAndLintWithContext(env, code)).hasSize(1)
        }

        it("is emptyArray<String>()") {
            val code = "val a = emptyArray<String>()"
            assertThat(subject.compileAndLintWithContext(env, code)).isEmpty()
        }
    }
})
