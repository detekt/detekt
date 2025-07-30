package io.gitlab.arturbosch.detekt.rules.style

import dev.detekt.api.Config
import dev.detekt.test.assertThat
import dev.detekt.test.lint
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

class UseArrayLiteralsInAnnotationsSpec {

    val subject = UseArrayLiteralsInAnnotations(Config.empty)

    @Test
    fun `finds an arrayOf usage`() {
        val findings = subject.lint(
            """
                annotation class Test(val values: Array<String>)
                @Test(arrayOf("value"))
                fun test() = Unit
            """.trimIndent()
        )

        assertThat(findings).hasSize(1)
    }

    @Test
    fun `finds intArrayOf usage`() {
        val code = """
            annotation class Test(val values: IntArray)
            
            @Test(intArrayOf(1, 2))
            fun test() {}
        """.trimIndent()
        val findings = subject.lint(code)
        assertThat(findings).hasSize(1)
    }

    @Test
    fun `finds longArrayOf usage`() {
        val code = """
            annotation class Test(val values: LongArray)
            
            @Test(longArrayOf(1, 2))
            fun test() {}
        """.trimIndent()
        val findings = subject.lint(code)
        assertThat(findings).hasSize(1)
    }

    @Test
    fun `finds arrayOf usage as default value`() {
        val code = """
            annotation class Test(val s: Array<String> = arrayOf("a", "b"))
        """.trimIndent()
        val findings = subject.lint(code)
        assertThat(findings)
            .hasSize(1)
            .hasTextLocations(45 to 62)
    }

    @Test
    @DisplayName("expects [] syntax")
    fun expectsBracketSyntax() {
        val findings = subject.lint(
            """
                annotation class Test(val values: Array<String>)
                @Test(["value"])
                fun test() = Unit
            """.trimIndent()
        )

        assertThat(findings).isEmpty()
    }
}
