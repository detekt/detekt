package dev.detekt.rules.comments

import dev.detekt.api.Config
import dev.detekt.test.assertj.assertThat
import dev.detekt.test.junit.KotlinCoreEnvironmentTest
import dev.detekt.test.lintWithContext
import dev.detekt.test.utils.KotlinEnvironmentContainer
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

@KotlinCoreEnvironmentTest
class UndocumentedPublicFunctionReturnSpec(private val env: KotlinEnvironmentContainer) {
    private val subject = UndocumentedPublicFunctionReturn(Config.empty)

    @Nested
    inner class `return value` {
        @Test
        fun `reports documented function with undocumented return value`() {
            val code = """
                /**
                 * Returns a number.
                 */
                fun count(): Int = 42
            """.trimIndent()
            assertThat(subject.lintWithContext(env, code)).singleElement()
                .hasMessage("The function return value is not documented with a `@return` tag.")
        }

        @Test
        fun `reports documented function with undocumented inferred return value`() {
            val code = """
                /**
                 * Returns a number.
                 */
                fun count() = 42
            """.trimIndent()
            assertThat(subject.lintWithContext(env, code)).hasSize(1)
        }

        @Test
        fun `does not report function with documented return value`() {
            val code = """
                /**
                 * Returns a number.
                 *
                 * @return The number.
                 */
                fun count(): Int = 42
            """.trimIndent()
            assertThat(subject.lintWithContext(env, code)).isEmpty()
        }

        @Test
        fun `does not report function returning Unit`() {
            val code = """
                /**
                 * Prints a message.
                 */
                fun printMessage() {
                    println("message")
                }
            """.trimIndent()
            assertThat(subject.lintWithContext(env, code)).isEmpty()
        }

        @Test
        fun `does not report function with explicit Unit return type`() {
            val code = """
                /**
                 * Prints a message.
                 */
                fun printMessage(): Unit = println("message")
            """.trimIndent()
            assertThat(subject.lintWithContext(env, code)).isEmpty()
        }

        @Test
        fun `reports function returning nullable Unit`() {
            val code = """
                /**
                 * Returns nothing or Unit.
                 */
                fun maybePrint(): Unit? = null
            """.trimIndent()
            assertThat(subject.lintWithContext(env, code)).hasSize(1)
        }
    }

    @Nested
    inner class `Nothing return type` {
        @Test
        fun `reports function returning Nothing without throws tag`() {
            val code = """
                /**
                 * Always fails.
                 */
                fun fail(): Nothing = throw IllegalStateException()
            """.trimIndent()
            assertThat(subject.lintWithContext(env, code)).singleElement()
                .hasMessage("The function returns `Nothing` but does not document a `@throws` or `@exception` tag.")
        }

        @Test
        fun `does not report function returning Nothing with throws tag`() {
            val code = """
                /**
                 * Always fails.
                 *
                 * @throws IllegalStateException Always.
                 */
                fun fail(): Nothing = throw IllegalStateException()
            """.trimIndent()
            assertThat(subject.lintWithContext(env, code)).isEmpty()
        }

        @Test
        fun `does not report function returning Nothing with exception tag`() {
            val code = """
                /**
                 * Always fails.
                 *
                 * @exception IllegalStateException Always.
                 */
                fun fail(): Nothing = throw IllegalStateException()
            """.trimIndent()
            assertThat(subject.lintWithContext(env, code)).isEmpty()
        }

        @Test
        fun `reports function returning Nothing documented only with return tag`() {
            val code = """
                /**
                 * Always fails.
                 *
                 * @return Never.
                 */
                fun fail(): Nothing = throw IllegalStateException()
            """.trimIndent()
            assertThat(subject.lintWithContext(env, code)).singleElement()
                .hasMessage("The function returns `Nothing` but does not document a `@throws` or `@exception` tag.")
        }
    }

    @Nested
    inner class `undocumented or excluded functions` {
        @Test
        fun `does not report function without KDoc`() {
            val code = """
                fun count(): Int = 42
            """.trimIndent()
            assertThat(subject.lintWithContext(env, code)).isEmpty()
        }

        @Test
        fun `does not report private and internal functions`() {
            val code = """
                /**
                 * Returns a number.
                 */
                private fun privateCount(): Int = 42

                /**
                 * Returns a number.
                 */
                internal fun internalCount(): Int = 42
            """.trimIndent()
            assertThat(subject.lintWithContext(env, code)).isEmpty()
        }

        @Test
        fun `does not report public function in internal class`() {
            val code = """
                internal class Counter {
                    /**
                     * Returns a number.
                     */
                    fun count(): Int = 42
                }
            """.trimIndent()
            assertThat(subject.lintWithContext(env, code)).isEmpty()
        }

        @Test
        fun `does not report local functions`() {
            val code = """
                fun outer() {
                    /**
                     * Returns a number.
                     */
                    fun inner(): Int = 42
                }
            """.trimIndent()
            assertThat(subject.lintWithContext(env, code)).isEmpty()
        }

        @Test
        fun `does not report overridden functions`() {
            val code = """
                interface Counter {
                    /**
                     * Returns a number.
                     *
                     * @return The number.
                     */
                    fun count(): Int
                }

                class DefaultCounter : Counter {
                    /**
                     * Returns a number.
                     */
                    override fun count(): Int = 42
                }
            """.trimIndent()
            assertThat(subject.lintWithContext(env, code)).isEmpty()
        }

        @Test
        fun `reports public function in public class`() {
            val code = """
                class Counter {
                    /**
                     * Returns a number.
                     */
                    fun count(): Int = 42
                }
            """.trimIndent()
            assertThat(subject.lintWithContext(env, code)).hasSize(1)
        }
    }
}
