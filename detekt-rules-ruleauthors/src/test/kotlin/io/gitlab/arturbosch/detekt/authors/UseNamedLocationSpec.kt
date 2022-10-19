package io.gitlab.arturbosch.detekt.authors

import io.gitlab.arturbosch.detekt.rules.KotlinCoreEnvironmentTest
import io.gitlab.arturbosch.detekt.test.compileAndLintWithContext
import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.kotlin.cli.jvm.compiler.KotlinCoreEnvironment
import org.junit.jupiter.api.Test

@KotlinCoreEnvironmentTest
internal class UseNamedLocationSpec(private val env: KotlinCoreEnvironment) {

    private val rule = UseNamedLocation()

    @Test
    fun `should not report calls when there's no name involved`() {
        val code = """
        """.trimIndent()
        val findings = rule.compileAndLintWithContext(env, code)
        assertThat(findings).isEmpty()
    }
}
