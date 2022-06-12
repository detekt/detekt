package io.gitlab.arturbosch.detekt.rules.empty

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.test.TestConfig
import io.gitlab.arturbosch.detekt.test.assertThat
import io.gitlab.arturbosch.detekt.test.compileAndLint
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

private const val IGNORE_OVERRIDDEN_FUNCTIONS = "ignoreOverriddenFunctions"
private const val IGNORE_OVERRIDDEN = "ignoreOverridden"

class EmptyFunctionBlockSpec {

    private val subject = EmptyFunctionBlock(Config.empty)

    @Test
    fun `should flag function with protected modifier`() {
        val code = """
            class A {
                protected fun stuff() {}
            }
        """
        assertThat(subject.compileAndLint(code)).hasSourceLocation(2, 27)
    }

    @Test
    fun `should not flag function with open modifier`() {
        val code = """
            open class A {
                open fun stuff() {}
            }
        """
        assertThat(subject.compileAndLint(code)).isEmpty()
    }

    @Test
    fun `should not flag a default function in an interface`() {
        val code = """
            interface I {
                fun stuff() {}
            }
        """
        assertThat(subject.compileAndLint(code)).isEmpty()
    }

    @Test
    fun `should flag the nested empty function`() {
        val code = """
            fun a() {
                fun b() {}
            }
        """
        assertThat(subject.compileAndLint(code)).hasSourceLocation(2, 13)
    }

    @Nested
    inner class `some overridden functions` {

        val code = """
            fun empty() {}

            open class Base {
                open fun stuff() {}
            }

            class A : Base() {
                override fun stuff() {}
            }

            class B : Base() {
                override fun stuff() {
                    TODO("Implement this")
                }
            }

            class C : Base() {
                override fun stuff() {
                    // this is necessary...
                }
            }
        """

        @Test
        fun `should flag empty block in overridden function`() {
            assertThat(subject.compileAndLint(code)).hasSize(2)
        }

        @Test
        fun `should not flag overridden functions`() {
            val config = TestConfig(mapOf(IGNORE_OVERRIDDEN_FUNCTIONS to "true"))
            assertThat(EmptyFunctionBlock(config).compileAndLint(code)).hasSourceLocation(1, 13)
        }
    }

    @Nested
    inner class `some overridden functions when implementing interfaces` {
        val code = """
            private interface Listener {
                fun listenThis()

                fun listenThat()
            }

            private class AnimationEndListener : Listener {
                override fun listenThis() {
                    // no-op
                }

                override fun listenThat() {

                }
            }
        """

        @Test
        fun `should not flag overridden functions with commented body`() {
            assertThat(subject.compileAndLint(code)).hasSourceLocation(12, 31)
        }

        @Test
        fun `should not flag overridden functions with ignoreOverridden`() {
            val config = TestConfig(mapOf(IGNORE_OVERRIDDEN to "true"))
            assertThat(EmptyFunctionBlock(config).compileAndLint(code)).isEmpty()
        }
    }
}
