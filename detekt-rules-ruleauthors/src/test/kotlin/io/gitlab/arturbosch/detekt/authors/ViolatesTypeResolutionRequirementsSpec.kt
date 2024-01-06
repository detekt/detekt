package io.gitlab.arturbosch.detekt.authors

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.rules.KotlinCoreEnvironmentTest
import io.gitlab.arturbosch.detekt.test.compileAndLintWithContext
import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.kotlin.cli.jvm.compiler.KotlinCoreEnvironment
import org.junit.jupiter.api.Test

@KotlinCoreEnvironmentTest
internal class ViolatesTypeResolutionRequirementsSpec(private val env: KotlinCoreEnvironment) {

    private val rule = ViolatesTypeResolutionRequirements(Config.empty)

    @Test
    fun `should not report classes that don't extend from BaseRule`() {
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
            import io.gitlab.arturbosch.detekt.api.RequiresTypeResolution
            import io.gitlab.arturbosch.detekt.api.Rule
            
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
    fun `should report Rules that don't use bindingContext and are annotated`() {
        val code = """
            import io.gitlab.arturbosch.detekt.api.Config
            import io.gitlab.arturbosch.detekt.api.RequiresTypeResolution
            import io.gitlab.arturbosch.detekt.api.Rule
            
            @RequiresTypeResolution
            class A(config: Config) : Rule(config) {
                override val issue = error("I don't care")
            }
        """.trimIndent()
        val findings = rule.compileAndLintWithContext(env, code)
        assertThat(findings).hasSize(1)
    }

    @Test
    fun `should report Rules that use bindingContext outside class and are not annotated`() {
        val code = """
            import io.gitlab.arturbosch.detekt.api.Config
            import io.gitlab.arturbosch.detekt.api.Rule
            
            class A(config: Config) : Rule(config) {
                override val issue = error("I don't care")
            
                private fun asdf() {
                    extension()
                }
            }
            
            inline fun Rule.extension(): Boolean {
                bindingContext
                return true
            }
        """.trimIndent()
        val findings = rule.compileAndLintWithContext(env, code)
        assertThat(findings).hasSize(1)
    }
}
