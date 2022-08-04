package io.gitlab.arturbosch.detekt.authors

import io.gitlab.arturbosch.detekt.rules.KotlinCoreEnvironmentTest
import io.gitlab.arturbosch.detekt.test.compileAndLintWithContext
import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.kotlin.cli.jvm.compiler.KotlinCoreEnvironment
import org.junit.jupiter.api.Test

@KotlinCoreEnvironmentTest
internal class RequiresTypeResolutionSpec(private val env: KotlinCoreEnvironment) {

    private val rule = RequiresTypeResolution()

    @Test
    fun `should not report classes that doesn't extend from BaseRule`() {
        val code = """
            class A {
                val issue: Int = error("bindingContext")
            }
        """.trimIndent()
        val findings = rule.compileAndLintWithContext(env, code)
        assertThat(findings).isEmpty()
    }

    @Test
    fun `should report Rules that use bindingContext and are not annotated`() {
        val code = """
            import io.gitlab.arturbosch.detekt.api.Config
            import io.gitlab.arturbosch.detekt.api.Rule
            
            class A(config: Config) : Rule(config) {
                override val issue = error("I don't care")

                private fun asdf() {
                    bindingContext
                }
            }
        """.trimIndent()
        val findings = rule.compileAndLintWithContext(env, code)
        assertThat(findings).hasSize(1)
    }

    @Test
    fun `should not report Rules that doesn't use bindingContext and are not annotated`() {
        val code = """
            import io.gitlab.arturbosch.detekt.api.Config
            import io.gitlab.arturbosch.detekt.api.Rule
            
            class A(config: Config) : Rule(config) {
                override val issue = error("I don't care")
            }
        """.trimIndent()
        val findings = rule.compileAndLintWithContext(env, code)
        assertThat(findings).isEmpty()
    }

    @Test
    fun `should not report Rules that use bindingContext and are annotated`() {
        val code = """
            import io.gitlab.arturbosch.detekt.api.Config
            import io.gitlab.arturbosch.detekt.api.Rule
            import io.gitlab.arturbosch.detekt.api.internal.RequiresTypeResolution

            @RequiresTypeResolution
            class A(config: Config) : Rule(config) {
                override val issue = error("I don't care")

                private fun asdf() {
                    bindingContext
                }
            }
        """.trimIndent()
        val findings = rule.compileAndLintWithContext(env, code)
        assertThat(findings).isEmpty()
    }

    @Test
    fun `should not report Rules that doesn't use bindingContext and are annotated`() {
        val code = """
            import io.gitlab.arturbosch.detekt.api.Config
            import io.gitlab.arturbosch.detekt.api.Rule
            import io.gitlab.arturbosch.detekt.api.internal.RequiresTypeResolution

            @RequiresTypeResolution
            class A(config: Config) : Rule(config) {
                override val issue = error("I don't care")
            }
        """.trimIndent()
        val findings = rule.compileAndLintWithContext(env, code)
        assertThat(findings).hasSize(1)
    }
}
