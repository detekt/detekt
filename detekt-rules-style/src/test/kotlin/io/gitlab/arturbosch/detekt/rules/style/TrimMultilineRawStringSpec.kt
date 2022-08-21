@file:Suppress("StringTemplate")

package io.gitlab.arturbosch.detekt.rules.style

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.test.compileAndLint
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class TrimMultilineRawStringSpec {
    val subject = TrimMultilineRawString(Config.empty)

    @Test
    fun `raises multiline raw strings without trim`() {
        val code = """
                val a = ${TQ}
                Hello world!
                ${TQ}
        """
        subject.compileAndLint(code)
        assertThat(subject.findings).hasSize(1)
    }

    @Test
    fun `raises multiline raw strings with lenght`() {
        val code = """
                val a = ${TQ}
                Hello world!
                ${TQ}.length
        """
        subject.compileAndLint(code)
        assertThat(subject.findings).hasSize(1)
    }

    @Test
    fun `doesn't raise multiline raw strings without trimIndent`() {
        val code = """
                val a = ${TQ}
                Hello world!
                ${TQ}.trimIndent()
        """
        subject.compileAndLint(code)
        assertThat(subject.findings).isEmpty()
    }

    @Test
    fun `doesn't raise multiline raw strings without trimMargin`() {
        val code = """
                val a = ${TQ}
                |Hello world!
                ${TQ}.trimMargin()
        """
        subject.compileAndLint(code)
        assertThat(subject.findings).isEmpty()
    }

    @Test
    fun `doesn't raise multiline raw strings without trimMargin with parameter`() {
        val code = """
                val a = ${TQ}
                >Hello world!
                ${TQ}.trimMargin(">")
        """
        subject.compileAndLint(code)
        assertThat(subject.findings).isEmpty()
    }

    @Test
    fun `don't raise one line raw strings`() {
        val code = """
                val a = ${TQ}Hello world!${TQ}
        """
        subject.compileAndLint(code)
        assertThat(subject.findings).isEmpty()
    }

    @Test
    fun `doesn't raise if it is not a raw string`() {
        val code = """
                val a = "Hello world!"
        """
        subject.compileAndLint(code)
        assertThat(subject.findings).isEmpty()
    }
}

private const val TQ = "\"\"\""
