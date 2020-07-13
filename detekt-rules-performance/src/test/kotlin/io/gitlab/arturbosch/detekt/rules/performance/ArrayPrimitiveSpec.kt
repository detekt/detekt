package io.gitlab.arturbosch.detekt.rules.performance

import io.gitlab.arturbosch.detekt.test.compileAndLint
import org.assertj.core.api.Assertions.assertThat
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

class ArrayPrimitiveSpec : Spek({
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
})
