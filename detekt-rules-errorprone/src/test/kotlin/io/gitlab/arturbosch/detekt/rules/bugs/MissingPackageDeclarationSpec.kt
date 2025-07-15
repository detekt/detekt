package io.gitlab.arturbosch.detekt.rules.bugs

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.test.assertThat
import io.gitlab.arturbosch.detekt.test.lint
import org.junit.jupiter.api.Test

internal class MissingPackageDeclarationSpec {

    @Test
    fun `should pass if package declaration is declared`() {
        val code = """
            package foo.bar
            
            class C
        """.trimIndent()
        val findings = MissingPackageDeclaration(Config.empty).lint(code)

        assertThat(findings).isEmpty()
    }

    @Test
    fun `should report if package declaration is missing`() {
        val code = "class C"

        val findings = MissingPackageDeclaration(Config.empty).lint(code)

        assertThat(findings).hasSize(1)
    }
}
