package dev.detekt.rules.emptyblocks

import dev.detekt.api.Config
import dev.detekt.test.lint
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

internal class EmptyDefaultConstructorSpec {

    @Test
    fun emptyConstructor() {
        val code = """
            class EmptyConstructor()
        """.trimIndent()
        assertThat(EmptyDefaultConstructor(Config.Empty).lint(code)).hasSize(1)
    }

    @Test
    fun EmptyPrimaryConstructor() {
        val code = """
            class EmptyPrimaryConstructor constructor()
        """.trimIndent()
        assertThat(EmptyDefaultConstructor(Config.Empty).lint(code)).hasSize(1)
    }

    @Test
    fun emptyPublicPrimaryConstructor() {
        val code = """
            class EmptyPublicPrimaryConstructor public constructor()
        """.trimIndent()
        assertThat(EmptyDefaultConstructor(Config.Empty).lint(code)).hasSize(1)
    }

    @Test
    fun primaryConstructorWithParameter() {
        val code = """
            class PrimaryConstructorWithParameter constructor(x: Int)
        """.trimIndent()
        assertThat(EmptyDefaultConstructor(Config.Empty).lint(code)).isEmpty()
    }

    @Test
    fun primaryConstructorWithAnnotation() {
        val code = """
            class PrimaryConstructorWithAnnotation @SafeVarargs constructor()
        """.trimIndent()
        assertThat(EmptyDefaultConstructor(Config.Empty).lint(code)).isEmpty()
    }

    @Test
    fun privatePrimaryConstructor() {
        val code = """
            class PrivatePrimaryConstructor private constructor()
        """.trimIndent()
        assertThat(EmptyDefaultConstructor(Config.Empty).lint(code)).isEmpty()
    }

    @Test
    fun emptyConstructorIsCalled() {
        val code = """
            class EmptyConstructorIsCalled() {
            
                constructor(i: Int) : this()
            }
        """.trimIndent()
        assertThat(EmptyDefaultConstructor(Config.Empty).lint(code)).isEmpty()
    }

    @Test
    fun `should not report empty constructors for classes with expect keyword - #1362`() {
        val code = """
            expect class NeedsConstructor()
        """.trimIndent()
        assertThat(EmptyDefaultConstructor(Config.Empty).lint(code, compile = false)).isEmpty()
    }

    @Test
    fun `should not report empty constructors for classes with actual - #1362`() {
        val code = """
            actual class NeedsConstructor actual constructor()
        """.trimIndent()
        assertThat(EmptyDefaultConstructor(Config.Empty).lint(code, compile = false)).isEmpty()
    }

    @Test
    fun `should not report empty constructors for annotation classes with expect keyword - #1362`() {
        val code = """
            expect annotation class NeedsConstructor()
        """.trimIndent()
        assertThat(EmptyDefaultConstructor(Config.Empty).lint(code, compile = false)).isEmpty()
    }

    @Test
    fun `should not report empty constructors for annotation classes with actual - #1362`() {
        val code = """
            actual annotation class NeedsConstructor actual constructor()
        """.trimIndent()
        assertThat(EmptyDefaultConstructor(Config.Empty).lint(code, compile = false)).isEmpty()
    }
}
