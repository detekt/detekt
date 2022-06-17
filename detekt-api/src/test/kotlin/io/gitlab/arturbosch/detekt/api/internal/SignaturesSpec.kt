package io.gitlab.arturbosch.detekt.api.internal

import io.github.detekt.test.utils.compileContentForTest
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.jetbrains.kotlin.psi.KtNamedFunction
import org.junit.jupiter.api.Test

class SignaturesSpec {
    @Test
    fun `function with type reference`() {
        val result = compileContentForTest("fun data(): Int = 0")
            .findChildByClass(KtNamedFunction::class.java)!!
            .buildFullSignature()

        assertThat(result).isEqualTo("Test.kt\$fun data(): Int")
    }

    @Test
    fun `function without type reference`() {
        val result = compileContentForTest("fun data() = 0")
            .findChildByClass(KtNamedFunction::class.java)!!
            .buildFullSignature()

        assertThat(result).isEqualTo("Test.kt\$fun data()")
    }

    @Test
    fun `function throws exception`() {
        assertThatThrownBy {
            compileContentForTest("{ fun data() = 0 }")
                .findChildByClass(KtNamedFunction::class.java)!!
                .buildFullSignature()
        }
            .isInstanceOf(IllegalArgumentException::class.java)
            .hasMessageContaining("Error building function signature")
    }
}
