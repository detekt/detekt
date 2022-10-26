package io.gitlab.arturbosch.detekt.rules.style

import io.gitlab.arturbosch.detekt.test.assertThat
import io.gitlab.arturbosch.detekt.test.compileAndLint
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

class UseArrayLiteralsInAnnotationsSpec {

    val subject = UseArrayLiteralsInAnnotations()

    @Test
    fun `finds an arrayOf usage`() {
        val findings = subject.compileAndLint(
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
        subject.compileAndLint(code)
        assertThat(subject.findings).hasSize(1)
    }

    @Test
    fun `finds longArrayOf usage`() {
        val code = """
            annotation class Test(val values: LongArray)
            
            @Test(longArrayOf(1, 2))
            fun test() {}
        """.trimIndent()
        subject.compileAndLint(code)
        assertThat(subject.findings).hasSize(1)
    }

    @Test
    fun `finds arrayOf usage as default value`() {
        val code = """
            annotation class Test(val s: Array<String> = arrayOf("a", "b"))
        """.trimIndent()
        subject.compileAndLint(code)
        assertThat(subject.findings)
            .hasSize(1)
            .hasTextLocations(45 to 62)
    }

    @Test
    @DisplayName("expects [] syntax")
    fun expectsBracketSyntax() {
        val findings = subject.compileAndLint(
            """
        annotation class Test(val values: Array<String>)
        @Test(["value"])
        fun test() = Unit
            """.trimIndent()
        )

        assertThat(findings).isEmpty()
    }
}
