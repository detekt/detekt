package io.gitlab.arturbosch.detekt.rules.style

import dev.detekt.api.Config
import dev.detekt.test.lint
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class RedundantConstructorKeywordSpec {
    val subject = RedundantConstructorKeyword(Config.empty)

    @Test fun `report on data class with redundant constructor keyword`() {
        val code = """
            data class Foo constructor(val foo: Int)
        """.trimIndent()

        val findings = subject.lint(code)
        assertThat(findings).hasSize(1)
        assertThat(findings.first().message).isEqualTo(
            "The `constructor` keyword on Foo is redundant and should be removed."
        )
    }

    @Test fun `report on abstract class with redundant constructor keyword`() {
        val code = """
            abstract class Foo constructor(val foo: Int)
        """.trimIndent()

        val findings = subject.lint(code)
        assertThat(findings).hasSize(1)
        assertThat(findings.first().message).isEqualTo(
            "The `constructor` keyword on Foo is redundant and should be removed."
        )
    }

    @Test fun `report on class with annotated parameter`() {
        val code = """
            annotation class Ann

            class AnnotatedParam constructor(@Ann x: Double) {
                val y = x
            }
        """.trimIndent()

        val findings = subject.lint(code)
        assertThat(findings).hasSize(1)
        assertThat(findings.first().message).isEqualTo(
            "The `constructor` keyword on AnnotatedParam is redundant and should be removed."
        )
    }

    @Test fun `report on annotation class`() {
        val code = """
            annotation class Foo constructor(val foo: Int)
        """.trimIndent()

        val findings = subject.lint(code)
        assertThat(findings).hasSize(1)
        assertThat(findings.first().message).isEqualTo(
            "The `constructor` keyword on Foo is redundant and should be removed."
        )
    }

    @Test fun `does not report class without constructor keyword`() {
        val code = """
            data class Foo(val foo: Int)
        """.trimIndent()
        assertThat(subject.lint(code)).isEmpty()
    }

    @Test fun `does not report on constructor with comments`() {
        val code = """
            class WithComments
            // my comment
            constructor(val foo: Int)
        """.trimIndent()
        assertThat(subject.lint(code)).isEmpty()
    }

    @Test fun `does not report on private constructor`() {
        val code = """
            class Foo private constructor(val foo: Int)
        """.trimIndent()
        assertThat(subject.lint(code)).isEmpty()
    }

    @Test fun `does not report if annotated constructor`() {
        val code = """
            annotation class Ann           

            class Foo @Ann constructor(val foo: Int)
        """.trimIndent()
        assertThat(subject.lint(code)).isEmpty()
    }
}
