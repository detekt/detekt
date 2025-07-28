package io.gitlab.arturbosch.detekt.rules.performance

import dev.detekt.api.Config
import dev.detekt.test.lint
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class UnnecessaryTypeCastingSpec {
    private val subject = UnnecessaryTypeCasting(Config.empty)

    @Test
    fun `reports unnecessary type casting instead of type checking`() {
        val code = """
            fun foo() {
                val objList: List<Any> = emptyList()
                objList.any { it as? String != null }
            }
        """.trimIndent()

        assertThat(subject.lint(code)).hasSize(1)
    }

    @Test
    fun `reports unnecessary type casting with null checking on left side`() {
        val code = """
            fun foo() {
                val objList: List<Any> = emptyList()
                objList.any { null != it as? String }
            }
        """.trimIndent()

        assertThat(subject.lint(code)).hasSize(1)
    }

    @Test
    fun `does not report type checking`() {
        val code = """
            fun foo() {
                val objList: List<Any> = emptyList()
                objList.any { it is String }
            }
        """.trimIndent()

        assertThat(subject.lint(code)).isEmpty()
    }

    @Test
    fun `does not report used type casting`() {
        val code = """
            fun foo() {
                val objList: List<Any> = emptyList()
                objList.any { it as? String != "foo" }
            }
        """.trimIndent()

        assertThat(subject.lint(code)).isEmpty()
    }

    @Test
    fun `does not report used type casting with equal op`() {
        val code = """
            fun foo() {
                val objList: List<Any> = emptyList()
                objList.any { "foo" == it as? String }
            }
        """.trimIndent()

        assertThat(subject.lint(code)).isEmpty()
    }

    @Test
    fun `does not report type casting when stored in variable`() {
        val code = """
            fun foo(any: Any) {
                val castResult = any as? String
                print(castResult)
            }
        """.trimIndent()

        assertThat(subject.lint(code)).isEmpty()
    }

    @Test
    fun `does not report unsafe type casting`() {
        val code = """
            fun foo() {
                val objList: List<Any> = emptyList()
                objList.any { it as String != null }
            }
        """.trimIndent()

        assertThat(subject.lint(code)).isEmpty()
    }
}
