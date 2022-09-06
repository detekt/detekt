package io.gitlab.arturbosch.detekt.rules.empty

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.test.compileAndLint
import io.gitlab.arturbosch.detekt.test.lint
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

internal class EmptyDefaultConstructorSpec {

    @Test
    fun `EmptyConstructor`() {
        val code = """
            class EmptyConstructor()
        """.trimIndent()
        assertThat(EmptyDefaultConstructor(Config.empty).compileAndLint(code)).hasSize(1)
    }

    @Test
    fun `EmptyPrimaryConstructor`() {
        val code = """
            class EmptyPrimaryConstructor constructor()
        """.trimIndent()
        assertThat(EmptyDefaultConstructor(Config.empty).compileAndLint(code)).hasSize(1)
    }

    @Test
    fun `EmptyPublicPrimaryConstructor`() {
        val code = """
            class EmptyPublicPrimaryConstructor public constructor()
        """.trimIndent()
        assertThat(EmptyDefaultConstructor(Config.empty).compileAndLint(code)).hasSize(1)
    }

    @Test
    fun `PrimaryConstructorWithParameter`() {
        val code = """
            class PrimaryConstructorWithParameter constructor(x: Int)
        """.trimIndent()
        assertThat(EmptyDefaultConstructor(Config.empty).compileAndLint(code)).isEmpty()
    }

    @Test
    fun `PrimaryConstructorWithAnnotation`() {
        val code = """
            class PrimaryConstructorWithAnnotation @SafeVarargs constructor()
        """.trimIndent()
        assertThat(EmptyDefaultConstructor(Config.empty).compileAndLint(code)).isEmpty()
    }

    @Test
    fun `PrivatePrimaryConstructor`() {
        val code = """
            class PrivatePrimaryConstructor private constructor()
        """.trimIndent()
        assertThat(EmptyDefaultConstructor(Config.empty).compileAndLint(code)).isEmpty()
    }

    @Test
    fun `EmptyConstructorIsCalled`() {
        val code = """
            class EmptyConstructorIsCalled() {

                constructor(i: Int) : this()
            }
        """.trimIndent()
        assertThat(EmptyDefaultConstructor(Config.empty).compileAndLint(code)).isEmpty()
    }

    @Test
    fun `should not report empty constructors for classes with expect keyword - #1362`() {
        val code = """
            expect class NeedsConstructor()
        """.trimIndent()
        assertThat(EmptyDefaultConstructor(Config.empty).lint(code)).isEmpty()
    }

    @Test
    fun `should not report empty constructors for classes with actual - #1362`() {
        val code = """
            actual class NeedsConstructor actual constructor()
        """.trimIndent()
        assertThat(EmptyDefaultConstructor(Config.empty).lint(code)).isEmpty()
    }

    @Test
    fun `should not report empty constructors for annotation classes with expect keyword - #1362`() {
        val code = """
            expect annotation class NeedsConstructor()
        """.trimIndent()
        assertThat(EmptyDefaultConstructor(Config.empty).lint(code)).isEmpty()
    }

    @Test
    fun `should not report empty constructors for annotation classes with actual - #1362`() {
        val code = """
            actual annotation class NeedsConstructor actual constructor()
        """.trimIndent()
        assertThat(EmptyDefaultConstructor(Config.empty).lint(code)).isEmpty()
    }
}
