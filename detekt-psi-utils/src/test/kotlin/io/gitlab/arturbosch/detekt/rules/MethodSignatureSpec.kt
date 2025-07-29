package io.gitlab.arturbosch.detekt.rules

import dev.detekt.test.utils.compileContentForTest
import org.assertj.core.api.Assertions.assertThat
import org.intellij.lang.annotations.Language
import org.jetbrains.kotlin.psi.KtNamedFunction
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource

class MethodSignatureSpec {

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
        fun `should return x for finalize function with y access modifier`(
            accessModifier: String,
            isFinalize: Boolean,
        ) {
            val namedFunction = makeFunction("$accessModifier fun finalize() {}")
            assertThat(namedFunction.isJvmFinalizeFunction()).isEqualTo(isFinalize)
        }

        @Test
        fun `should return false for finalize function with arguments`() {
            val namedFunction = makeFunction("fun finalize(text: String)")
            assertThat(namedFunction.isJvmFinalizeFunction()).isFalse()
        }

        @Test
        fun `should return false for overridden finalize function`() {
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
