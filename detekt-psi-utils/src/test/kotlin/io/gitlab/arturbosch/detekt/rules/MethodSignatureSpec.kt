package io.gitlab.arturbosch.detekt.rules

import io.github.detekt.test.utils.compileContentForTest
import org.assertj.core.api.Assertions.assertThat
import org.intellij.lang.annotations.Language
import org.jetbrains.kotlin.psi.KtNamedFunction
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource

@Suppress("DEPRECATION")
class MethodSignatureSpec {

    @Test
    fun `should return method name and null params list in case of simplifies signature`() {
        val methodSignature = "java.time.LocalDate.now"

        val (methodName, params) = extractMethodNameAndParams(methodSignature)

        assertThat(methodName).isEqualTo("java.time.LocalDate.now")
        assertThat(params).isNull()
    }

    @Test
    fun `should return method name and empty params list for full signature parameterless method`() {
        val methodSignature = "java.time.LocalDate.now()"

        val (methodName, params) = extractMethodNameAndParams(methodSignature)

        assertThat(methodName).isEqualTo("java.time.LocalDate.now")
        assertThat(params).isEmpty()
    }

    @Test
    fun `should return method name and params list for full signature method with single param`() {
        val methodSignature = "java.time.LocalDate.now(java.time.Clock)"

        val (methodName, params) = extractMethodNameAndParams(methodSignature)

        assertThat(methodName).isEqualTo("java.time.LocalDate.now")
        assertThat(params).containsExactly("java.time.Clock")
    }

    @Test
    fun `should return method name and params list for full signature method with multiple params`() {
        val methodSignature = "java.time.LocalDate.of(kotlin.Int, kotlin.Int, kotlin.Int)"

        val (methodName, params) = extractMethodNameAndParams(methodSignature)

        assertThat(methodName).isEqualTo("java.time.LocalDate.of")
        assertThat(params).containsExactly("kotlin.Int", "kotlin.Int", "kotlin.Int")
    }

    @Test
    fun `should return method name and params list for full signature method with multiple params where method name has spaces and special characters`() {
        val methodSignature = "io.gitlab.arturbosch.detekt.SomeClass.`some , method`(kotlin.String)"

        val (methodName, params) = extractMethodNameAndParams(methodSignature)

        assertThat(methodName).isEqualTo("io.gitlab.arturbosch.detekt.SomeClass.some , method")
        assertThat(params).containsExactly("kotlin.String")
    }

    @Nested
    inner class isJvmFinalizeFunction {

        @ParameterizedTest(name = "should return {1} for finalize function with {0} access modifier")
        @CsvSource(
            "'',true",
            "'public',true",
            "'internal',true",
            "'protected',true",
            "'private',false"
        )
        fun `should return x for finalize function with y access modifier`(accessModifier: String, isFinalize: Boolean) {
            val namedFunction = makeFunction("$accessModifier fun finalize() {}")
            assertThat(namedFunction.isJvmFinalizeFunction()).isEqualTo(isFinalize)
        }

        @Test
        fun `should return false for finalize function with arguments`() {
            val namedFunction = makeFunction("fun finalize(text: String)")
            assertThat(namedFunction.isJvmFinalizeFunction()).isFalse()
        }

        @Test
        fun `should return false for overriden finalize function`() {
            val namedFunction = makeFunction("override fun finalize()")
            assertThat(namedFunction.isJvmFinalizeFunction()).isFalse()
        }

        @Test
        fun `should return false for non-finalize function`() {
            val namedFunction = makeFunction("fun foo()")
            assertThat(namedFunction.isJvmFinalizeFunction()).isFalse()
        }

        private fun makeFunction(@Language("kotlin") code: String): KtNamedFunction {
            val ktFile = compileContentForTest(code)
            return ktFile.findChildByClass(KtNamedFunction::class.java)!!
        }
    }
}
