package io.gitlab.arturbosch.detekt.rules.bugs

import dev.detekt.api.Config
import io.github.detekt.test.utils.KotlinEnvironmentContainer
import io.gitlab.arturbosch.detekt.rules.KotlinCoreEnvironmentTest
import io.gitlab.arturbosch.detekt.test.assertThat
import io.gitlab.arturbosch.detekt.test.lintWithContext
import org.junit.jupiter.api.Test

@KotlinCoreEnvironmentTest
class PropertyUsedBeforeDeclarationSpec(private val env: KotlinEnvironmentContainer) {
    private val subject = PropertyUsedBeforeDeclaration(Config.empty)

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
        val findings = subject.lintWithContext(env, code)
        assertThat(findings).hasSize(1)
        assertThat(findings).hasTextLocations(45 to 52)
        assertThat(findings.first()).hasMessage("'isValid' is used before declaration.")
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
        val findings = subject.lintWithContext(env, code)
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
        val findings = subject.lintWithContext(env, code)
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
        val findings = subject.lintWithContext(env, code)
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
        val findings = subject.lintWithContext(env, code)
        assertThat(findings).isEmpty()
    }

    @Test
    fun `used before declaration in nested object`() {
        val code = """
            object Outer {
                object O {
                    val inner = outer1
                }
            
                class C {
                    val inner = outer2
                }
            
                annotation class Ann(val value: String)
                interface I {
                    fun f(@Ann(outer3) namedProp: String)
                }
            
                val outer1 = "value1"
                val outer2 = "value2"
                const val outer3 = "value3"
            }
        """.trimIndent()
        val findings = subject.lintWithContext(env, code)
        assertThat(findings).isEmpty()
    }

    @Test
    fun `used before declaration in inner class`() {
        val code = """
            class A {
                inner class B {
                    val inner = outer
                }
                val outer = "value"
            }
        """.trimIndent()
        val findings = subject.lintWithContext(env, code)
        assertThat(findings).isEmpty()
    }
}
