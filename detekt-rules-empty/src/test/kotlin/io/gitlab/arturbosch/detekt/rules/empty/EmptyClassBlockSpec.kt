package io.gitlab.arturbosch.detekt.rules.empty

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.test.assertThat
import io.gitlab.arturbosch.detekt.test.lint
import org.junit.jupiter.api.Test

class EmptyClassBlockSpec {

    private val subject = EmptyClassBlock(Config.empty)

    @Test
    fun `reports the empty class body`() {
        val code = "class SomeClass {}"
        assertThat(subject.lint(code)).hasSize(1)
    }

    @Test
    fun `does not report class with comments in the body`() {
        val code = """
            class SomeClass {
                // Some comment to explain what this class is supposed to do
            }
        """.trimIndent()
        assertThat(subject.lint(code)).isEmpty()
    }

    @Test
    fun `does not report class with multiline comments in the body`() {
        val code = """
            class SomeClass {
                /*
                Some comment to explain what this class is supposed to do
                */
            }
        """.trimIndent()
        assertThat(subject.lint(code)).isEmpty()
    }

    @Test
    fun `reports the empty nested class body`() {
        val code = """
            class SomeClass {
                class EmptyClass {}
            }
        """.trimIndent()
        assertThat(subject.lint(code)).hasSize(1)
    }

    @Test
    fun `reports the empty object body`() {
        val code = "object SomeObject {}"
        val findings = subject.lint(code)
        assertThat(findings).hasSize(1)
        assertThat(findings).hasTextLocations(18 to 20)
    }

    @Test
    fun `does not report the object if it is of an anonymous class`() {
        val code = """
            open class Open
            
            fun f() {
                 object : Open() {}
            }
        """.trimIndent()
        assertThat(subject.lint(code)).isEmpty()
    }
}
