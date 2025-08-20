package dev.detekt.rules.empty

import dev.detekt.api.Config
import dev.detekt.test.assertThat
import dev.detekt.test.lint
import org.junit.jupiter.api.Test

class EmptyKotlinFileSpec {
    private val subject = EmptyKotlinFile(Config.empty)

    @Test
    fun `reports empty if file is blank`() {
        val code = ""
        assertThat(subject.lint(code)).singleElement()
            .hasStartSourceLocation(1, 1)
    }

    @Test
    fun `does report file with package statement`() {
        val codeWithPackageStatement = """
            package my.packagee
        """.trimIndent()
        assertThat(subject.lint(codeWithPackageStatement)).singleElement()
            .hasStartSourceLocation(1, 1)
    }

    @Test
    fun `does not report file with code`() {
        val code = """
            package my.packagee

            fun myFunction() {}
        """.trimIndent()
        assertThat(subject.lint(code)).isEmpty()
    }
}
