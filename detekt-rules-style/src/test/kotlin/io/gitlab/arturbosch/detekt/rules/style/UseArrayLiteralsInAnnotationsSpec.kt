package io.gitlab.arturbosch.detekt.rules.style

import io.gitlab.arturbosch.detekt.test.assertThat
import io.gitlab.arturbosch.detekt.test.compileAndLint
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class UseArrayLiteralsInAnnotationsSpec {

    val subject = UseArrayLiteralsInAnnotations()

    @Nested
    @DisplayName("suggests replacing arrayOf with [] syntax`")
    inner class ReplaceArrayOfWithSquareBrackets {

        @Test
        fun `finds an arrayOf usage`() {
            val findings = subject.compileAndLint(
                """
            annotation class Test(val values: Array<String>)
            @Test(arrayOf("value"))
            fun test() = Unit
                """
            )

            assertThat(findings).hasSize(1)
        }

        @Test
        @DisplayName("expects [] syntax")
        fun expectsBracketSyntax() {
            val findings = subject.compileAndLint(
                """
            annotation class Test(val values: Array<String>)
            @Test(["value"])
            fun test() = Unit
                """
            )

            assertThat(findings).isEmpty()
        }
    }
}
