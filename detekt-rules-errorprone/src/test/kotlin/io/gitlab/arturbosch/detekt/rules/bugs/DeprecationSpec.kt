package io.gitlab.arturbosch.detekt.rules.bugs

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.rules.KotlinCoreEnvironmentTest
import io.gitlab.arturbosch.detekt.test.compileAndLintWithContext
import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.kotlin.cli.jvm.compiler.KotlinCoreEnvironment
import org.junit.jupiter.api.Test

@KotlinCoreEnvironmentTest
class DeprecationSpec(private val env: KotlinCoreEnvironment) {
    private val subject = Deprecation(Config.empty)

    @Test
    fun `reports when supertype is deprecated`() {
        val code = """
            @Deprecated("deprecation message")
            abstract class Foo {
                abstract fun bar() : Int
            
                fun baz() {
                }
            }
            
            abstract class Oof : Foo() {
                fun spam() {
                }
            }
        """.trimIndent()
        val findings = subject.compileAndLintWithContext(env, code)
        assertThat(findings).hasSize(1)
        assertThat(findings.first().message).isEqualTo("""Foo is deprecated with message "deprecation message"""")
    }

    @Test
    fun `does not report when supertype is not deprecated`() {
        val code = """
            abstract class Oof : Foo() {
                fun spam() {
                }
            }
            abstract class Foo {
                abstract fun bar() : Int
            
                fun baz() {
                }
            }
        """.trimIndent()
        assertThat(subject.compileAndLintWithContext(env, code)).isEmpty()
    }
}
