package io.gitlab.arturbosch.detekt.rules.empty

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.test.assertThat
import io.gitlab.arturbosch.detekt.test.compileAndLint
import org.junit.jupiter.api.Test

class EmptyKotlinFileSpec {
    private val subject = EmptyKotlinFile(Config.empty)

    private val codeWithPackageStatement = """
            package my.packagee
    """.trimIndent()

    private val codeWithFunction = """
            package my.packagee
            
            fun myFunction() {}
    """.trimIndent()

    @Test
    fun `reports empty if file is blank`() {
        val code = ""
        assertThat(subject.lint(code)).hasSize(1)
    }

    @Test
    fun `does report file with package statement`() {
        assertThat(subject.compileAndLint(codeWithPackageStatement)).hasSize(1)
    }

    @Test
    fun `does not report file with code`() {
        assertThat(subject.compileAndLint(codeWithFunction)).hasSize(0)
    }
}
