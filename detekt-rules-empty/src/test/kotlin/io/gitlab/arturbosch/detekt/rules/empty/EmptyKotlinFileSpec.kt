package io.gitlab.arturbosch.detekt.rules.empty

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.test.TestConfig
import io.gitlab.arturbosch.detekt.test.assertThat
import io.gitlab.arturbosch.detekt.test.compileAndLint
import org.junit.jupiter.api.Test

class EmptyKotlinFileSpec {
    private val subject = EmptyKotlinFile(Config.empty)
    private val ignorePackageDirectiveSubject = EmptyKotlinFile(TestConfig("ignorePackageDeclaration" to true))

    private val codeWithPackageStatement = """
            package my.package
    """.trimIndent()

    @Test
    fun `reports empty if file is blank`() {
        val code = ""
        assertThat(subject.compileAndLint(code)).hasSize(1)
    }

    @Test
    fun `does not reports file with package statement`() {
        assertThat(subject.compileAndLint(codeWithPackageStatement)).hasSize(0)
    }

    @Test
    fun `reports file with package statement if configured to`() {
        assertThat(ignorePackageDirectiveSubject.compileAndLint(codeWithPackageStatement)).hasSize(1)
    }
}
