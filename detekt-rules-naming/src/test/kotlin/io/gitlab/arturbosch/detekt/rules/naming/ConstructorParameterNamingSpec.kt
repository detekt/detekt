package io.gitlab.arturbosch.detekt.rules.naming

import io.github.detekt.test.utils.KotlinEnvironmentContainer
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.rules.KotlinCoreEnvironmentTest
import io.gitlab.arturbosch.detekt.test.assertThat
import io.gitlab.arturbosch.detekt.test.lint
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

@KotlinCoreEnvironmentTest
class ConstructorParameterNamingSpec(val env: KotlinEnvironmentContainer) {

    @Test
    fun `should detect no violations`() {
        val code = """
            class C(val param: String, private val privateParam: String)
            
            class D {
                constructor(param: String) {}
                constructor(param: String, privateParam: String) {}
            }
        """.trimIndent()
        assertThat(ConstructorParameterNaming(Config.empty).lint(code)).isEmpty()
    }

    @Test
    fun `should find some violations`() {
        val code = """
            class C(val PARAM: String, private val PRIVATE_PARAM: String)
            
            class D {
                constructor(PARAM: String) {}
                constructor(PARAM: String, PRIVATE_PARAM: String) {}
            }
        """.trimIndent()
        assertThat(ConstructorParameterNaming(Config.empty).lint(code)).hasSize(5)
    }

    @Test
    fun `should find a violation in the correct text location`() {
        val code = """
            class C(val PARAM: String)
        """.trimIndent()
        assertThat(ConstructorParameterNaming(Config.empty).lint(code)).hasTextLocations(8 to 25)
    }

    @Test
    fun `should not complain about override`() {
        val code = """
            class C(override val PARAM: String) : I
            
            interface I { val PARAM: String }
        """.trimIndent()
        assertThat(ConstructorParameterNaming(Config.empty).lint(code)).isEmpty()
    }

    @Nested
    inner class `with backticks` {
        @Test
        fun `should not complain about public param name - #5531`() {
            val code = """
                class Foo(val `is`: Boolean)
            """.trimIndent()
            assertThat(ConstructorParameterNaming(Config.empty).lint(code)).isEmpty()
        }

        @Test
        fun `should not complain about private param name`() {
            val code = """
                class Foo(private val `is`: Boolean)
            """.trimIndent()
            assertThat(ConstructorParameterNaming(Config.empty).lint(code)).isEmpty()
        }

        @Test
        fun `should complain about param name with violation`() {
            val code = """
                class Foo(private val `PARAM_NAME`: Boolean)
            """.trimIndent()
            assertThat(ConstructorParameterNaming(Config.empty).lint(code))
                .hasSize(1)
                .hasStartSourceLocation(1, 11)
        }

        @Test
        fun `should not complain about param in secondary constructor`() {
            val code = """
                @JvmInline
                value class A1 constructor(val `is`: Boolean) {
                    constructor(`is`: Boolean, `when`: Boolean): this(`is`)
                }
            """.trimIndent()
            assertThat(ConstructorParameterNaming(Config.empty).lint(code)).isEmpty()
        }
    }
}
