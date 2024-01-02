package io.gitlab.arturbosch.detekt.rules.empty

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.test.TestConfig
import io.gitlab.arturbosch.detekt.test.compileAndLint
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test


class EmptyKotlinFileBlockSpec {
    private val subject = EmptyKotlinFile(Config.empty)
    private val ignorePackageDirectiveSubject = EmptyKotlinFile(TestConfig("ignorePackageDeclaration" to true))


    @Test
    fun `reports empty if file is blank`() {
        val code = ""
        Assertions.assertThat(subject.compileAndLint(code)).hasSize(1)
    }


    private val codeWithPackageStatement = """
            package my.package
        """.trimIndent()

    @Test
    fun `does not reports file with package statement`() {
        Assertions.assertThat(subject.compileAndLint(codeWithPackageStatement)).hasSize(0)
    }

    @Test
    fun `reports file with package statement if configured to`() {
        Assertions.assertThat(ignorePackageDirectiveSubject.compileAndLint(codeWithPackageStatement)).hasSize(1)
    }



}