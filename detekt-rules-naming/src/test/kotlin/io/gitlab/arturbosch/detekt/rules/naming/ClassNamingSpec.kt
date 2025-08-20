package io.gitlab.arturbosch.detekt.rules.naming

import dev.detekt.api.Config
import dev.detekt.test.TestConfig
import dev.detekt.test.assertThat
import dev.detekt.test.lint
import org.junit.jupiter.api.Test

class ClassNamingSpec {

    @Test
    fun `should detekt no violations for abstract class implementation`() {
        val code = """
            abstract class AbstractClass {
                abstract fun foo()
            }
            val foo = object : AbstractClass() {
                override fun foo() {}
            }
        """.trimIndent()

        assertThat(ClassNaming(Config.empty).lint(code)).isEmpty()
    }

    @Test
    fun `should use custom name for method and class`() {
        val config = TestConfig(ClassNaming.CLASS_PATTERN to "^aBbD$")
        assertThat(
            ClassNaming(config).lint(
                """
                    class aBbD{}
                """.trimIndent()
            )
        ).isEmpty()
    }

    @Test
    fun `should detect no violations class with numbers`() {
        val code = """
            class MyClassWithNumbers5
        """.trimIndent()

        assertThat(ClassNaming(Config.empty).lint(code)).isEmpty()
    }

    @Test
    fun `should detect no violations`() {
        val code = """
            class NamingConventions {
            }
        """.trimIndent()

        assertThat(ClassNaming(Config.empty).lint(code)).isEmpty()
    }

    @Test
    fun `should detect no violations with class using backticks`() {
        val code = """
            class `NamingConventions`
        """.trimIndent()

        assertThat(ClassNaming(Config.empty).lint(code)).isEmpty()
    }

    @Test
    fun `should detect because it have a _`() {
        val code = """
            class _NamingConventions
        """.trimIndent()

        assertThat(ClassNaming(Config.empty).lint(code)).singleElement()
            .hasTextLocation(6 to 24)
    }

    @Test
    fun `should detect because it have starts with lowercase`() {
        val code = """
            class namingConventions {}
        """.trimIndent()

        assertThat(ClassNaming(Config.empty).lint(code)).singleElement()
            .hasTextLocation(6 to 23)
    }

    @Test
    fun `should ignore the issue suppression`() {
        val code = """
            @Suppress("ClassNaming")
            class namingConventions {}
        """.trimIndent()
        assertThat(ClassNaming(Config.empty).lint(code)).isEmpty()
    }

    @Test
    fun `should not detect any`() {
        val code = """
            data class D(val i: Int, val j: Int)
            fun doStuff() {
                val (_, HOLY_GRAIL) = D(5, 4)
            }
        """.trimIndent()

        assertThat(ClassNaming(Config.empty).lint(code)).isEmpty()
    }
}
