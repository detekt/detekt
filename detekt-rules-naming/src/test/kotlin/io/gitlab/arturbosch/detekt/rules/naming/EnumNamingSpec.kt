package io.gitlab.arturbosch.detekt.rules.naming

import dev.detekt.api.Config
import dev.detekt.test.TestConfig
import dev.detekt.test.assertThat
import dev.detekt.test.lint
import org.junit.jupiter.api.Test

class EnumNamingSpec {

    @Test
    fun `should use custom name for enum`() {
        val rule = EnumNaming(TestConfig(EnumNaming.ENUM_PATTERN to "^(enum1)|(enum2)$"))
        assertThat(
            rule.lint(
                """
                    enum class aBbD {
                        enum1, enum2
                    }
                """.trimIndent()
            )
        ).isEmpty()
    }

    @Test
    fun `should detect no violation`() {
        val findings = EnumNaming(Config.empty).lint(
            """
                enum class WorkFlow {
                    ACTIVE, NOT_ACTIVE, Unknown, Number1
                }
            """.trimIndent()
        )
        assertThat(findings).isEmpty()
    }

    @Test
    fun `enum name that start with lowercase`() {
        val code = """
            enum class WorkFlow {
                default
            }
        """.trimIndent()
        assertThat(EnumNaming(Config.empty).lint(code)).hasSize(1)
    }

    @Test
    fun `reports an underscore in enum name`() {
        val code = """
            enum class WorkFlow {
                _Default
            }
        """.trimIndent()
        assertThat(EnumNaming(Config.empty).lint(code)).hasSize(1)
    }

    @Test
    fun `no reports an underscore in enum name because it's suppressed`() {
        val code = """
            enum class WorkFlow {
                @Suppress("EnumNaming") _Default
            }
        """.trimIndent()
        assertThat(EnumNaming(Config.empty).lint(code)).isEmpty()
    }

    @Test
    fun `reports the correct text location in enum name`() {
        val code = """
            enum class WorkFlow {
                _Default,
            }
        """.trimIndent()
        val findings = EnumNaming(Config.empty).lint(code)
        assertThat(findings).hasTextLocations(26 to 34)
    }
}
