package dev.detekt.api.internal

import io.github.detekt.test.utils.compileContentForTest
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.jetbrains.kotlin.psi.KtNamedFunction
import org.jetbrains.kotlin.psi.psiUtil.findDescendantOfType
import org.junit.jupiter.api.Test

class SignaturesSpec {
    @Test
    fun `function with type reference`() {
        val result = compileContentForTest("fun data(): Int = 0")
            .findDescendantOfType<KtNamedFunction>()!!
            .buildFullSignature()

        assertThat(result).isEqualTo("fun data(): Int")
    }

    @Test
    fun `function and parent`() {
        val result = compileContentForTest("class A { fun data(): Int = 0 }")
            .findDescendantOfType<KtNamedFunction>()!!
            .buildFullSignature()

        assertThat(result).isEqualTo("A\$fun data(): Int")
    }

    @Test
    fun `function without type reference`() {
        val result = compileContentForTest("fun data() = 0")
            .findDescendantOfType<KtNamedFunction>()!!
            .buildFullSignature()

        assertThat(result).isEqualTo("fun data()")
    }

    @Test
    fun `function with comments`() {
        val result = compileContentForTest("/* comments */ fun data() = 0")
            .findDescendantOfType<KtNamedFunction>()!!
            .buildFullSignature()

        assertThat(result).isEqualTo("fun data()")
    }

    @Test
    fun `function throws exception`() {
        assertThatThrownBy {
            compileContentForTest("{ fun data() = 0 }")
                .findDescendantOfType<KtNamedFunction>()!!
                .buildFullSignature()
        }
            .isInstanceOf(IllegalArgumentException::class.java)
            .hasMessageContaining("Error building function signature")
    }
}
