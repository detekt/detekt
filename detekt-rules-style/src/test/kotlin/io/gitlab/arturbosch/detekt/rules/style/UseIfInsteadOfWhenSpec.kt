package io.gitlab.arturbosch.detekt.rules.style

import dev.detekt.api.Config
import io.gitlab.arturbosch.detekt.test.TestConfig
import io.gitlab.arturbosch.detekt.test.lint
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class UseIfInsteadOfWhenSpec {

    val subject = UseIfInsteadOfWhen(Config.empty)

    @Test
    fun `reports when using two branches`() {
        val code = """
            fun function(): Boolean? {
                val x = null
                when (x) {
                    null -> return true
                    else -> return false
                }
            }
        """.trimIndent()
        assertThat(subject.lint(code)).hasSize(1)
    }

    @Test
    fun `does not report when using one branch`() {
        val code = """
            fun function(): Boolean? {
                val x = null
                when (x) {
                    else -> return false
                }
            }
        """.trimIndent()
        assertThat(subject.lint(code)).isEmpty()
    }

    @Test
    fun `does not report when using more than two branches`() {
        val code = """
            fun function(): Boolean? {
                val x = null
                when (x) {
                    null -> return true
                    3 -> return null
                    else -> return false
                }
            }
        """.trimIndent()
        assertThat(subject.lint(code)).isEmpty()
    }

    @Test
    fun `does not report when second branch is not 'else'`() {
        val code = """
            fun function(): Boolean? {
                val x = null
                when (x) {
                    null -> return true
                    3 -> return null
                }
                return false
            }
        """.trimIndent()
        assertThat(subject.lint(code)).isEmpty()
    }

    // TC inspired from https://github.com/JetBrains/kotlin/pull/2921/files
    @Test
    fun `does not report 'when' variable declaration is present in subject expression when flag is true`() {
        val code = """
            enum class Type {
                HYDRO,
                PYRO
            }
            
            fun select(t: Type) {
                when (val i = t.ordinal) {
                    0 -> 1
                    else -> 42
                }
            }
        """.trimIndent()
        val subject = UseIfInsteadOfWhen(TestConfig(IGNORE_WHEN_CONTAINING_VARIABLE_DECLARATION to true))
        assertThat(subject.lint(code)).isEmpty()
    }

    // TC inspired from https://github.com/JetBrains/kotlin/pull/2921/files
    @Test
    fun `does report 'when' variable declaration is present in subject expression when flag is false`() {
        val code = """
            enum class Type {
                HYDRO,
                PYRO
            }
            
            fun select(t: Type) {
                when (val i = t.ordinal) {
                    0 -> 1
                    else -> 42
                }
            }
        """.trimIndent()
        val subject = UseIfInsteadOfWhen(TestConfig(IGNORE_WHEN_CONTAINING_VARIABLE_DECLARATION to false))
        assertThat(subject.lint(code)).hasSize(1)
    }

    companion object {
        private const val IGNORE_WHEN_CONTAINING_VARIABLE_DECLARATION = "ignoreWhenContainingVariableDeclaration"
    }
}
