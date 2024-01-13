package io.gitlab.arturbosch.detekt.rules.naming

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.test.TestConfig
import io.gitlab.arturbosch.detekt.test.assertThat
import io.gitlab.arturbosch.detekt.test.compileAndLint
import io.gitlab.arturbosch.detekt.test.lint
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class TopLevelPropertyNamingSpec {

    val subject = TopLevelPropertyNaming(Config.empty)

    @Test
    fun `should use custom name top level properties`() {
        val code = """
            const val lowerCaseConst = ""
        """.trimIndent()
        val subject = TopLevelPropertyNaming(TestConfig(TopLevelPropertyNaming.CONSTANT_PATTERN to "^lowerCaseConst$"))
        assertThat(subject.lint(code)).isEmpty()
    }

    @Nested
    inner class `constants on top level` {

        @Test
        fun `should not detect any constants not complying to the naming rules`() {
            val code = """
                const val MY_NAME_8 = "Artur"
                const val MYNAME = "Artur"
            """.trimIndent()
            assertThat(subject.lint(code)).isEmpty()
        }

        @Test
        fun `should detect five constants not complying to the naming rules`() {
            val code = """
                const val MyNAME = "Artur"
                const val name = "Artur"
                const val nAme = "Artur"
                private const val _nAme = "Artur"
                const val serialVersionUID = 42L
            """.trimIndent()
            assertThat(subject.lint(code)).hasSize(5)
        }
    }

    @Nested
    inner class `variables on top level` {

        @Test
        fun `should not report any`() {
            val code = """
                val name = "Artur"
                val nAme8 = "Artur"
                private val _name = "Artur"
                val serialVersionUID = 42L
                val MY_NAME = "Artur"
                val MYNAME = "Artur"
                val MyNAME = "Artur"
                private val NAME = "Artur"
                val s_d_d_1 = listOf("")
                private val INTERNAL_VERSION = "1.0.0"
            """.trimIndent()
            assertThat(subject.lint(code)).isEmpty()
        }

        @Test
        fun `should report non private top level property using underscore`() {
            val code = """
                val _nAme = "Artur"
            """.trimIndent()
            assertThat(subject.lint(code)).hasSize(1)
        }

        @Test
        fun `should report private top level property using two underscores`() {
            val code = """
                private val __NAME = "Artur"
            """.trimIndent()
            io.gitlab.arturbosch.detekt.test.assertThat(subject.lint(code)).hasSize(1)
        }
    }

    @Test
    fun `should not care about no top level properties`() {
        val code = """
            class Foo{
                val __baz = ""
                companion object {
                  const val __bar = ""
                  val __foo = ""
                }
            }
        """.trimIndent()

        assertThat(subject.compileAndLint(code)).isEmpty()
    }
}
