package io.gitlab.arturbosch.detekt.rules.bugs

import io.gitlab.arturbosch.detekt.rules.KotlinCoreEnvironmentTest
import io.gitlab.arturbosch.detekt.test.assertThat
import io.gitlab.arturbosch.detekt.test.compileAndLintWithContext
import org.jetbrains.kotlin.cli.jvm.compiler.KotlinCoreEnvironment
import org.junit.jupiter.api.Test

@KotlinCoreEnvironmentTest
class PropertyUsedBeforeDeclarationSpec(private val env: KotlinCoreEnvironment) {
    private val subject = PropertyUsedBeforeDeclaration()

    @Test
    fun `used before declaration in getter`() {
        val code = """
            class C {
                private val number get() = if (isValid) 1 else 0
                val list = listOf(number)
                private val isValid = true
            }
            fun main() {
                println(C().list) // [0]
            }
        """.trimIndent()
        val findings = subject.compileAndLintWithContext(env, code)
        assertThat(findings).hasSize(1)
        assertThat(findings).hasTextLocations(45 to 52)
        assertThat(findings.first()).hasMessage("'isValid' is before declaration.")
    }

    @Test
    fun `used before declaration in init`() {
        val code = """
            class C {
                init {
                    run {
                        println(isValid) // false
                    }
                }
                private val isValid = true
            }
            fun main() {
                C()
            }
        """.trimIndent()
        val findings = subject.compileAndLintWithContext(env, code)
        assertThat(findings).hasSize(1)
    }

    @Test
    fun `used before declaration in function`() {
        val code = """
            class C {
                fun f() = isValid
                private val isValid = true
            }
        """.trimIndent()
        val findings = subject.compileAndLintWithContext(env, code)
        assertThat(findings).hasSize(1)
    }

    @Test
    fun `used after declaration in getter`() {
        val code = """
            class C {
                private val isValid = true
                private val number get() = if (isValid) 1 else 0
                val list = listOf(number)
            }
        """.trimIndent()
        val findings = subject.compileAndLintWithContext(env, code)
        assertThat(findings).isEmpty()
    }

    @Test
    fun `variable shadowing`() {
        val code = """
            class C {
                fun f(): Boolean  {
                    val isValid = true 
                    return isValid
                }
                private val isValid = true
            }
        """.trimIndent()
        val findings = subject.compileAndLintWithContext(env, code)
        assertThat(findings).isEmpty()
    }
}
