package io.gitlab.arturbosch.detekt.rules.empty

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.test.assertThat
import io.gitlab.arturbosch.detekt.test.compileAndLint
import org.junit.jupiter.api.Test

class EmptyKotlinFileSpec {
    private val subject = EmptyKotlinFile(Config.empty)

    @Test
    fun `reports empty if file is blank`() {
        val code = ""
        assertThat(subject.compileAndLint(code))
            .singleElement()
            .hasSourceLocation(1, 1)
    }

    @Test
    fun `does report file with package statement`() {
        val codeWithPackageStatement = """
            package my.packagee
        """.trimIndent()
        assertThat(subject.compileAndLint(codeWithPackageStatement))
            .singleElement()
            .hasSourceLocation(1, 1)
    }

    @Test
    fun `does not report file with code`() {
        val code = """
            package my.packagee

            fun myFunction() {}
        """.trimIndent()
        assertThat(subject.compileAndLint(code)).isEmpty()
    }
}
