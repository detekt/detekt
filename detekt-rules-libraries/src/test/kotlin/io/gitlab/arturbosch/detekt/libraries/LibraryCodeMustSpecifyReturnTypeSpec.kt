package io.gitlab.arturbosch.detekt.libraries

import io.github.detekt.test.utils.KotlinEnvironmentContainer
import dev.detekt.api.Config
import io.gitlab.arturbosch.detekt.rules.KotlinCoreEnvironmentTest
import dev.detekt.test.TestConfig
import dev.detekt.test.assertThat
import dev.detekt.test.lintWithContext
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource

@KotlinCoreEnvironmentTest
class LibraryCodeMustSpecifyReturnTypeSpec(val env: KotlinEnvironmentContainer) {
    @Nested
    inner class `positive cases` {
        val subject = LibraryCodeMustSpecifyReturnType(Config.empty)

        @Test
        fun `should report a top level function`() {
            assertThat(
                subject.lintWithContext(
                    env,
                    """
                        fun foo() = 5
                    """.trimIndent()
                )
            ).hasSize(1)
        }

        @Test
        fun `should report a top level function returning Unit with default allowOmitUnit value of false`() {
            assertThat(
                subject.lintWithContext(
                    env,
                    """
                        fun foo() = println("")
                    """.trimIndent()
                )
            ).hasSize(1)
        }

        @Test
        fun `should report a top level function returning Unit when allowOmitUnit is false`() {
            val subject = LibraryCodeMustSpecifyReturnType(TestConfig(ALLOW_OMIT_UNIT to false))
            assertThat(
                subject.lintWithContext(
                    env,
                    """
                        fun foo() = println("")
                    """.trimIndent()
                )
            ).hasSize(1)
        }

        @Test
        fun `should report a top level property`() {
            assertThat(
                subject.lintWithContext(
                    env,
                    """
                        val foo = 5
                    """.trimIndent()
                )
            ).hasSize(1)
        }

        @Test
        fun `should report a public class with public members`() {
            assertThat(
                subject.lintWithContext(
                    env,
                    """
                        class A {
                            val foo = 5
                            fun bar() = 5
                        }
                    """.trimIndent()
                )
            ).hasSize(2)
        }

        @Test
        fun `should report a public class with protected members`() {
            assertThat(
                subject.lintWithContext(
                    env,
                    """
                        open class A {
                            protected val foo = 5
                            protected fun bar() = 5
                        }
                    """.trimIndent()
                )
            ).hasSize(2)
        }
    }

    @Nested
    inner class `negative cases with public scope` {
        val subject = LibraryCodeMustSpecifyReturnType(Config.empty)

        @Test
        fun `should not report a top level function`() {
            assertThat(
                subject.lintWithContext(
                    env,
                    """
                        fun foo(): Int = 5
                    """.trimIndent()
                )
            ).isEmpty()
        }

        @Test
        fun `should not report a non expression function`() {
            assertThat(
                subject.lintWithContext(
                    env,
                    """
                        fun foo() {}
                    """.trimIndent()
                )
            ).isEmpty()
        }

        @Test
        fun `should not report a top level function returning Unit when allowOmitUnit is true`() {
            val subject = LibraryCodeMustSpecifyReturnType(TestConfig(ALLOW_OMIT_UNIT to true))
            assertThat(
                subject.lintWithContext(
                    env,
                    """
                        fun foo() = println("")
                    """.trimIndent()
                )
            ).isEmpty()
        }

        @Test
        fun `should not report a top level property`() {
            assertThat(
                subject.lintWithContext(
                    env,
                    """
                        val foo: Int = 5
                    """.trimIndent()
                )
            ).isEmpty()
        }

        @Test
        fun `should not report a public class with public members`() {
            assertThat(
                subject.lintWithContext(
                    env,
                    """
                        class A {
                            val foo: Int = 5
                            fun bar(): Int = 5
                        }
                    """.trimIndent()
                )
            ).isEmpty()
        }

        @ParameterizedTest(
            name = "should not report for implicit type Unit expression when allowOmitUnit is {0}",
        )
        @ValueSource(booleans = [true, false])
        fun `does not report for Unit expression`(allowOmitUnit: Boolean) {
            val code = """
                fun foo() = Unit
            """.trimIndent()
            val subject = LibraryCodeMustSpecifyReturnType(
                TestConfig(
                    ALLOW_OMIT_UNIT to allowOmitUnit
                )
            )
            val findings = subject.lintWithContext(env, code)
            assertThat(findings).isEmpty()
        }
    }

    @Nested
    inner class `negative cases with no public scope` {
        val subject = LibraryCodeMustSpecifyReturnType(Config.empty)

        @Test
        fun `should not report a private top level function`() {
            assertThat(
                subject.lintWithContext(
                    env,
                    """
                        internal fun bar() = 5
                        private fun foo() = 5
                    """.trimIndent()
                )
            ).isEmpty()
        }

        @Test
        fun `should not report a internal top level property`() {
            assertThat(
                subject.lintWithContext(
                    env,
                    """
                        internal val foo = 5
                    """.trimIndent()
                )
            ).isEmpty()
        }

        @Test
        fun `should not report members and local variables`() {
            assertThat(
                subject.lintWithContext(
                    env,
                    """
                        internal class A {
                            internal val foo = 5
                            private fun bar() {
                                fun stuff() = Unit
                                val a = 5
                            }
                        }
                    """.trimIndent()
                )
            ).isEmpty()
        }

        @Test
        fun `should not report effectively private properties and functions`() {
            assertThat(
                subject.lintWithContext(
                    env,
                    """
                        internal class A {
                            fun baz() = 5
                            val qux = 5
                        }
                    """.trimIndent()
                )
            ).isEmpty()
        }
    }

    companion object {
        private const val ALLOW_OMIT_UNIT = "allowOmitUnit"
    }
}
