package dev.detekt.api.internal

import dev.detekt.test.utils.compileContentForTest
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.jetbrains.kotlin.psi.KtConstructor
import org.jetbrains.kotlin.psi.KtNamedFunction
import org.jetbrains.kotlin.psi.psiUtil.findDescendantOfType
import org.junit.jupiter.api.Test

class SignaturesSpec {
    @Test
    fun `function with type reference`() {
        val result = compileContentForTest("fun data(): Int = 0")
            .findDescendantOfType<KtNamedFunction>()!!
            .buildFullSignature()

        assertThat(result).isEqualTo("fun data: Int")
    }

    @Test
    fun `function and parent`() {
        val result = compileContentForTest("class A { fun data(): Int = 0 }")
            .findDescendantOfType<KtNamedFunction>()!!
            .buildFullSignature()

        assertThat(result).isEqualTo($$"A$fun data: Int")
    }

    @Test
    fun `function without type reference`() {
        val result = compileContentForTest("fun data() = 0")
            .findDescendantOfType<KtNamedFunction>()!!
            .buildFullSignature()

        assertThat(result).isEqualTo("fun data")
    }

    @Test
    fun `function with comments`() {
        val result = compileContentForTest("/* comments */ fun data() = 0")
            .findDescendantOfType<KtNamedFunction>()!!
            .buildFullSignature()

        assertThat(result).isEqualTo("fun data")
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

    @Test
    fun `function with params`() {
        assertThat(
            compileContentForTest("class A { fun data(a: Int, b: Int): Int = a + b }")
                .findDescendantOfType<KtNamedFunction>()!!
                .buildFullSignature(),
        ).isEqualTo($$"A$fun data: Int")
    }

    @Test
    fun `primary constructor with params`() {
        assertThat(
            compileContentForTest("class A(private val a: String)")
                .findDescendantOfType<KtConstructor<*>>()!!
                .buildFullSignature(),
        ).isEqualTo("A")
    }

    @Test
    fun `secondary constructor without params`() {
        assertThat(
            compileContentForTest(
                """
                    class A {
                        constructor()
                    }
                """.trimIndent(),
            )
                .findDescendantOfType<KtConstructor<*>>()!!
                .buildFullSignature(),
        ).isEqualTo($$"A$constructor")
    }

    @Test
    fun `secondary constructor with params`() {
        assertThat(
            compileContentForTest(
                """
                    class A {
                        constructor(a: String)
                    }
                """.trimIndent(),
            )
                .findDescendantOfType<KtConstructor<*>>()!!
                .buildFullSignature(),
        ).isEqualTo($$"A$constructor")
    }
}
