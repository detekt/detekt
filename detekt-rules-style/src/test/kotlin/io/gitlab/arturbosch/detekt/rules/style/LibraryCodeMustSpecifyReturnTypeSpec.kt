package io.gitlab.arturbosch.detekt.rules.style

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.rules.KotlinCoreEnvironmentTest
import io.gitlab.arturbosch.detekt.test.TestConfig
import io.gitlab.arturbosch.detekt.test.assertThat
import io.gitlab.arturbosch.detekt.test.compileAndLintWithContext
import io.gitlab.arturbosch.detekt.test.lintWithContext
import org.jetbrains.kotlin.cli.jvm.compiler.KotlinCoreEnvironment
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

@KotlinCoreEnvironmentTest
class LibraryCodeMustSpecifyReturnTypeSpec(val env: KotlinCoreEnvironment) {

    @Test
    fun `should not report without explicit filters set`() {
        val subject = LibraryCodeMustSpecifyReturnType(TestConfig(Config.EXCLUDES_KEY to "**"))
        assertThat(
            subject.compileAndLintWithContext(
                env,
                """
            fun foo() = 5
            val bar = 5
            class A {
                fun b() = 2
                val c = 2
            }
                """
            )
        ).isEmpty()
    }

    @Nested
    inner class `positive cases` {
        val subject = LibraryCodeMustSpecifyReturnType()

        @Test
        fun `should report a top level function`() {
            assertThat(
                subject.compileAndLintWithContext(
                    env,
                    """
                fun foo() = 5
                    """
                )
            ).hasSize(1)
        }

        @Test
        fun `should report a top level property`() {
            assertThat(
                subject.compileAndLintWithContext(
                    env,
                    """
                val foo = 5
                    """
                )
            ).hasSize(1)
        }

        @Test
        fun `should report a public class with public members`() {
            assertThat(
                subject.compileAndLintWithContext(
                    env,
                    """
                class A {
                    val foo = 5
                    fun bar() = 5
                }
                    """
                )
            ).hasSize(2)
        }

        @Test
        fun `should report a public class with protected members`() {
            assertThat(
                subject.compileAndLintWithContext(
                    env,
                    """
                open class A {
                    protected val foo = 5
                    protected fun bar() = 5
                }
                    """
                )
            ).hasSize(2)
        }
    }

    @Nested
    inner class `negative cases with public scope` {
        val subject = LibraryCodeMustSpecifyReturnType()

        @Test
        fun `should not report a top level function`() {
            assertThat(
                subject.compileAndLintWithContext(
                    env,
                    """
                fun foo(): Int = 5
                    """
                )
            ).isEmpty()
        }

        @Test
        fun `should not report a non expression function`() {
            assertThat(
                subject.compileAndLintWithContext(
                    env,
                    """
                fun foo() {}
                    """
                )
            ).isEmpty()
        }

        @Test
        fun `should not report a top level property`() {
            assertThat(
                subject.compileAndLintWithContext(
                    env,
                    """
                val foo: Int = 5
                    """
                )
            ).isEmpty()
        }

        @Test
        fun `should not report a public class with public members`() {
            assertThat(
                subject.compileAndLintWithContext(
                    env,
                    """
                class A {
                    val foo: Int = 5
                    fun bar(): Int = 5
                }
                    """
                )
            ).isEmpty()
        }
    }

    @Nested
    inner class `negative cases with no public scope` {
        val subject = LibraryCodeMustSpecifyReturnType()

        @Test
        fun `should not report a private top level function`() {
            assertThat(
                subject.lintWithContext(
                    env,
                    """
                internal fun bar() = 5
                private fun foo() = 5
                    """
                )
            ).isEmpty()
        }

        @Test
        fun `should not report a internal top level property`() {
            assertThat(
                subject.compileAndLintWithContext(
                    env,
                    """
                internal val foo = 5
                    """
                )
            ).isEmpty()
        }

        @Test
        fun `should not report members and local variables`() {
            assertThat(
                subject.compileAndLintWithContext(
                    env,
                    """
                internal class A {
                    internal val foo = 5
                    private fun bar() {
                        fun stuff() = Unit
                        val a = 5
                    }
                }
                    """
                )
            ).isEmpty()
        }

        @Test
        fun `should not report effectively private properties and functions`() {
            assertThat(
                subject.compileAndLintWithContext(
                    env,
                    """
                internal class A {
                    fun baz() = 5
                    val qux = 5
                }
                    """
                )
            ).isEmpty()
        }
    }
}
