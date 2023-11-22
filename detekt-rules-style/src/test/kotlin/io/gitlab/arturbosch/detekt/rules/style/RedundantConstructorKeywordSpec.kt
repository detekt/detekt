package io.gitlab.arturbosch.detekt.rules.style

import io.gitlab.arturbosch.detekt.test.assertThat
import io.gitlab.arturbosch.detekt.test.compileAndLint
import org.junit.jupiter.api.Test

class RedundantConstructorKeywordSpec {
    val subject = RedundantConstructorKeyword()

    @Test fun `report on data class with redundant constructor keyword`() {
        val code = """
            data class Foo constructor(val foo: Int)
        """.trimIndent()
        assertThat(subject.compileAndLint(code)).hasSize(1)
    }

    @Test fun `report on abstract class with redundant constructor keyword`() {
        val code = """
            abstract class Foo constructor(val foo: Int)
        """.trimIndent()
        assertThat(subject.compileAndLint(code)).hasSize(1)
    }

    @Test fun `report on class with annotated parameter`() {
        val code = """
            annotation class Ann

            class AnnotatedParam constructor(@Ann x: Double) {
                val y = x
            }
        """.trimIndent()
        assertThat(subject.compileAndLint(code)).hasSize(1)
    }

    @Test fun `report on annotation class`() {
        val code = """
            annotation class Foo constructor(val foo: Int)
        """.trimIndent()
        assertThat(subject.compileAndLint(code)).hasSize(1)
    }

    @Test fun `does not report class without constructor keyword`() {
        val code = """
            data class Foo(val foo: Int)
        """.trimIndent()
        assertThat(subject.compileAndLint(code)).isEmpty()
    }

    @Test fun `does not report on constructor with comments`() {
        val code = """
            class WithComments
            // my comment
            constructor(val foo: Int)
        """.trimIndent()
        assertThat(subject.compileAndLint(code)).isEmpty()
    }

    @Test fun `does not report on private constructor`() {
        val code = """
            class Foo private constructor(val foo: Int)
        """.trimIndent()
        assertThat(subject.compileAndLint(code)).isEmpty()
    }

    @Test fun `does not report if annotated constructor`() {
        val code = """
            annotation class Ann           

            class Foo @Ann constructor(val foo: Int)
        """.trimIndent()
        assertThat(subject.compileAndLint(code)).isEmpty()
    }
}
