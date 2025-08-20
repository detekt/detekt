package dev.detekt.rules.documentation

import dev.detekt.api.Config
import dev.detekt.test.assertThat
import dev.detekt.test.lint
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class EndOfSentenceFormatSpec {

    val subject = EndOfSentenceFormat(Config.empty)

    @Test
    fun `reports invalid KDoc endings on classes`() {
        val code = """
            /** Some doc */
            class Test {
            }
        """.trimIndent()
        assertThat(subject.lint(code)).hasSize(1)
    }

    @Test
    fun `reports invalid KDoc endings on function with expression body`() {
        val code = """
            /** Some doc */
            fun f(x: Int, y: Int, z: Int) =
                if (x == 0) y + z else x + y
        """.trimIndent()
        assertThat(subject.lint(code)).hasSize(1)
    }

    @Test
    fun `reports invalid KDoc endings on properties`() {
        val code = """
            class Test {
                /** Some doc */
                val test = 3
            }
        """.trimIndent()
        assertThat(subject.lint(code)).hasSize(1)
    }

    @Test
    fun `reports invalid KDoc endings on top-level functions`() {
        val code = """
            /** Some doc */
            fun test() = 3
        """.trimIndent()
        assertThat(subject.lint(code)).hasSize(1)
    }

    @Test
    fun `reports invalid KDoc endings on functions`() {
        val code = """
            class Test {
                /** Some doc */
                fun test() = 3
            }
        """.trimIndent()
        assertThat(subject.lint(code)).hasSize(1)
    }

    @Test
    fun `reports invalid KDoc endings`() {
        val code = """
            class Test {
                /** Some doc-- */
                fun test() = 3
            }
        """.trimIndent()
        assertThat(subject.lint(code)).hasSize(1)
    }

    @Test
    fun `reports invalid KDoc endings in block`() {
        val code = """
            /**
             * Something off abc@@
             */
            class Test {
            }
        """.trimIndent()
        assertThat(subject.lint(code)).hasSize(1)
    }

    @Test
    fun `does not validate first sentence KDoc endings in a multi sentence comment`() {
        val code = """
            /**
             * This sentence is correct.
             *
             * This sentence doesn't matter
             */
            class Test {
            }
        """.trimIndent()
        assertThat(subject.lint(code)).isEmpty()
    }

    @Test
    fun `does not report KDoc which doesn't contain any real sentence`() {
        val code = """
            /**
             */
            class Test {
            }
        """.trimIndent()
        assertThat(subject.lint(code)).isEmpty()
    }

    @Test
    fun `does not report KDoc which doesn't contain any real sentence but many tags`() {
        val code = """
            /**
             * @configuration this - just an example (default: `150`)
             *
             * @active since v1.0.0
             */
            class Test {
            }
        """.trimIndent()
        assertThat(subject.lint(code)).isEmpty()
    }

    @Test
    fun `does not report KDoc which doesn't contain any real sentence but html tags`() {
        val code = """
            /**
             *
             * <noncompliant>
             * fun foo(): Unit { }
             * </noncompliant>
             *
             * <compliant>
             * fun foo() { }
             * </compliant>
             *
             */
            class Test {
            }
        """.trimIndent()
        assertThat(subject.lint(code)).isEmpty()
    }

    @Test
    fun `reports invalid KDoc even when it looks like it contains html tags`() {
        val code = """
            /**
             * < is the less-than sign --
             * ```
             * <code>this contains HTML, but doesn't start with a tag</code>
             * ```
             */
            class Test {
            }
        """.trimIndent()
        assertThat(subject.lint(code)).hasSize(1)
    }

    @Test
    fun `does not report KDoc ending with periods`() {
        val code = """
            /**
             * Something correct.
             */
            class Test {
            }
        """.trimIndent()
        assertThat(subject.lint(code)).isEmpty()
    }

    @Test
    fun `does not report KDoc ending with questionmarks`() {
        val code = """
            /**
             * Something correct?
             */
            class Test {
            }
        """.trimIndent()
        assertThat(subject.lint(code)).isEmpty()
    }

    @Test
    fun `does not report KDoc ending with exclamation marks`() {
        val code = """
            /**
             * Something correct!
             */
            class Test {
            }
        """.trimIndent()
        assertThat(subject.lint(code)).isEmpty()
    }

    @Test
    fun `does not report KDoc ending with colon`() {
        val code = """
            /**
             * Something correct:
             */
            class Test {
            }
        """.trimIndent()
        assertThat(subject.lint(code)).isEmpty()
    }

    @Test
    fun `does not report URLs in comments`() {
        val code = """
            /** http://www.google.com */
            class Test1 {
            }
            
            /** Look here
            http://google.com */
            class Test2 {
            }
        """.trimIndent()
        assertThat(subject.lint(code)).isEmpty()
    }

    @Nested
    inner class `highlights only the relevant part of the comment - #5310` {

        @Test
        fun function() {
            val code = """
                /**
                 * This sentence is correct invalid
                 *
                 * This sentence counts too, because it doesn't know where the other ends */
                fun test() = 3
            """.trimIndent()
            assertThat(subject.lint(code)).singleElement()
                .hasStartSourceLocation(2, 2)
                .hasEndSourceLocation(4, 75)
        }

        @Test
        fun property() {
            val code = """
                class Test {
                    /** This sentence is correct invalid
                        This sentence counts too, because it doesn't know where the other ends */
                    val test = 3
                }
            """.trimIndent()
            assertThat(subject.lint(code)).singleElement()
                .hasStartSourceLocation(2, 8)
                .hasEndSourceLocation(3, 80)
        }

        @Test
        fun `class`() {
            val code = """
                /**
                 * This sentence is correct invalid
                 *
                 * This sentence counts too, because it doesn't know where the other ends
                 */
                class Test
            """.trimIndent()
            assertThat(subject.lint(code)).singleElement()
                .hasStartSourceLocation(2, 2)
                .hasEndSourceLocation(4, 74)
        }
    }
}
