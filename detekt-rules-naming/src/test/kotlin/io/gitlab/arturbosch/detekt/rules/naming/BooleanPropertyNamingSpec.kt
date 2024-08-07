package io.gitlab.arturbosch.detekt.rules.naming

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.rules.KotlinCoreEnvironmentTest
import io.gitlab.arturbosch.detekt.test.assertThat
import io.gitlab.arturbosch.detekt.test.compileAndLintWithContext
import org.jetbrains.kotlin.cli.jvm.compiler.KotlinCoreEnvironment
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

@KotlinCoreEnvironmentTest
class BooleanPropertyNamingSpec(val env: KotlinCoreEnvironment) {
    val subject = BooleanPropertyNaming(Config.empty)

    @Nested
    inner class `argument declarations` {
        @Test
        fun `should warn about Kotlin Boolean`() {
            val code = """data class Test (var default: Boolean)"""
            val findings = subject.compileAndLintWithContext(env, code)

            assertThat(findings).hasSize(1)
        }

        @Test
        fun `should not warn about Kotlin Boolean override`() {
            val code = """
                interface Test {
                    val default: Boolean
                }
                
                data class TestImpl (override var default: Boolean) : Test
            """.trimIndent()
            val findings = subject.compileAndLintWithContext(env, code)

            assertThat(findings).hasSize(1)
        }

        @Test
        fun `should warn about Kotlin Boolean nullable`() {
            val code = """data class Test (var default: Boolean?)"""
            val findings = subject.compileAndLintWithContext(env, code)

            assertThat(findings).hasSize(1)
        }

        @Test
        fun `should not warn about Kotlin Boolean nullable override`() {
            val code = """
                interface Test {
                    val default: Boolean?
                }
                
                data class TestImpl (override var default: Boolean?) : Test
            """.trimIndent()
            val findings = subject.compileAndLintWithContext(env, code)

            assertThat(findings).hasSize(1)
        }

        @Test
        fun `should warn about Kotlin Boolean initialized`() {
            val code = """data class Test (var default: Boolean = false)"""
            val findings = subject.compileAndLintWithContext(env, code)

            assertThat(findings).hasSize(1)
        }

        @Test
        fun `should not warn about Kotlin Boolean initialized override`() {
            val code = """
                interface Test {
                    val default: Boolean
                }
                
                data class TestImpl (override var default: Boolean = false) : Test
            """.trimIndent()
            val findings = subject.compileAndLintWithContext(env, code)

            assertThat(findings).hasSize(1)
        }

        @Test
        fun `should warn about Java Boolean`() {
            val code = """data class Test (var default: java.lang.Boolean)"""
            val findings = subject.compileAndLintWithContext(env, code)

            assertThat(findings).hasSize(1)
        }

        @Test
        fun `should not warn about Java Boolean override in data class`() {
            val code = """
                interface Test {
                    val default: java.lang.Boolean
                }
                
                data class TestImpl (override var default: java.lang.Boolean) : Test
            """.trimIndent()
            val findings = subject.compileAndLintWithContext(env, code)

            assertThat(findings).hasSize(1)
        }

        @Test
        fun `should not warn about Java Boolean override`() {
            val code = """
                interface Test {
                    val default: java.lang.Boolean
                }
                
                class TestImpl : Test {
                    override val default: java.lang.Boolean = java.lang.Boolean(true)
                }
            """.trimIndent()
            val findings = subject.compileAndLintWithContext(env, code)

            assertThat(findings).hasSize(1)
        }

        @Test
        fun `should not detect primitive types`() {
            val code = """data class Test (var count: Int)"""
            val findings = subject.compileAndLintWithContext(env, code)

            assertThat(findings).isEmpty()
        }

        @Test
        fun `should not detect names that match an allowed pattern`() {
            val code = """data class Test (var isEnabled: Boolean, var hasDefault: Boolean)"""
            val findings = subject.compileAndLintWithContext(env, code)

            assertThat(findings).isEmpty()
        }

        @Test
        fun `should highlight only the name`() {
            val code = """
                data class Test(
                    /**
                     * True if the user's e-mail address has been verified; otherwise false.
                     */
                    @Deprecated("Don't use this", replaceWith = ReplaceWith("email_verified"))
                    val emailVerified: Boolean?,
                )
            """.trimIndent()
            val findings = subject.compileAndLintWithContext(env, code)

            assertThat(findings)
                .hasSize(1)
                .hasTextLocations("emailVerified")
        }
    }

    @Nested
    inner class `property declarations` {
        @Test
        fun `should warn about Kotlin Boolean`() {
            val code = """
                class Test {
                    var default: Boolean = true
                }
            """.trimIndent()
            val findings = subject.compileAndLintWithContext(env, code)

            assertThat(findings).hasSize(1)
        }

        @Test
        fun `should not warn about Kotlin Boolean override`() {
            val code = """
                interface Test {
                    val default: Boolean
                }
                
                class TestImpl : Test {
                    override var default: Boolean = true
                }
            """.trimIndent()
            val findings = subject.compileAndLintWithContext(env, code)

            assertThat(findings).hasSize(1)
        }

        @Test
        fun `should not warn about Kotlin Boolean if it is a constant val`() {
            val code = """
                object Test {
                    const val CONSTANT_VAL_BOOLEAN = true
                }
            """.trimIndent()
            val findings = subject.compileAndLintWithContext(env, code)

            assertThat(findings).isEmpty()
        }

        @Test
        fun `should warn about Kotlin Boolean nullable`() {
            val code = """
                class Test {
                    var default: Boolean? = null
                }
            """.trimIndent()
            val findings = subject.compileAndLintWithContext(env, code)

            assertThat(findings).hasSize(1)
        }

        @Test
        fun `should not warn about Kotlin Boolean nullable override`() {
            val code = """
                interface Test {
                    val default: Boolean?
                }
                
                class TestImpl : Test {
                    override var default: Boolean? = null
                }
            """.trimIndent()
            val findings = subject.compileAndLintWithContext(env, code)

            assertThat(findings).hasSize(1)
        }

        @Test
        fun `should warn about Kotlin Boolean initialized`() {
            val code = """
                class Test {
                    var default: Boolean = false
                }
            """.trimIndent()
            val findings = subject.compileAndLintWithContext(env, code)

            assertThat(findings).hasSize(1)
        }

        @Test
        fun `should not warn about Kotlin Boolean initialized override`() {
            val code = """
                interface Test {
                    val default: Boolean
                }
                
                class TestImpl : Test {
                    override var default: Boolean = false
                }
            """.trimIndent()
            val findings = subject.compileAndLintWithContext(env, code)

            assertThat(findings).hasSize(1)
        }

        @Test
        fun `should warn about inferred boolean type`() {
            val code = """
                class Test {
                    var default = true
                }
            """.trimIndent()
            val findings = subject.compileAndLintWithContext(env, code)

            assertThat(findings).hasSize(1)
        }

        @Test
        fun `should not warn about inferred boolean type override`() {
            val code = """
                interface Test {
                    val default: Boolean
                }
                
                class TestImpl : Test {
                    override var default = true
                }
            """.trimIndent()
            val findings = subject.compileAndLintWithContext(env, code)

            assertThat(findings).hasSize(1)
        }

        @Test
        fun `should warn about Java Boolean`() {
            val code = """
                class Test {
                    var default: java.lang.Boolean = java.lang.Boolean(true)
                }
            """.trimIndent()
            val findings = subject.compileAndLintWithContext(env, code)

            assertThat(findings).hasSize(1)
        }

        @Test
        fun `should not warn about Java Boolean override`() {
            val code = """
                interface Test {
                    val default: java.lang.Boolean
                }
                
                class TestImpl : Test {
                    override var default: java.lang.Boolean = java.lang.Boolean(true)
                }
            """.trimIndent()
            val findings = subject.compileAndLintWithContext(env, code)

            assertThat(findings).hasSize(1)
        }

        @Test
        fun `should not detect primitive types`() {
            val code = """
                class Test {
                    var count: Int = 0
                }
            """.trimIndent()
            val findings = subject.compileAndLintWithContext(env, code)

            assertThat(findings).isEmpty()
        }

        @Test
        fun `should not detect names that match an allowed pattern`() {
            val code = """
                class Test {
                    var isEnabled: Boolean = true
                    var hasDefault: Boolean = true
                }
            """.trimIndent()
            val findings = subject.compileAndLintWithContext(env, code)

            assertThat(findings).isEmpty()
        }

        @Test
        fun `should highlight only the name`() {
            val code = """
                class Test {
                    /**
                     * True if the user's e-mail address has been verified; otherwise false.
                     */
                    @Deprecated("Don't use this", replaceWith = ReplaceWith("email_verified"))
                    var emailVerified: Boolean? = false
                }
            """.trimIndent()
            val findings = subject.compileAndLintWithContext(env, code)

            assertThat(findings)
                .hasSize(1)
                .hasTextLocations("emailVerified")
        }
    }
}
