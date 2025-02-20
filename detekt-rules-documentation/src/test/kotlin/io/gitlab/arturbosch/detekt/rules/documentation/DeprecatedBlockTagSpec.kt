package io.gitlab.arturbosch.detekt.rules.documentation

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.test.assertThat
import io.gitlab.arturbosch.detekt.test.lint
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class DeprecatedBlockTagSpec {
    val subject = DeprecatedBlockTag(Config.empty)

    @Test
    fun `does not report regular kdoc block`() {
        val code = """
            /**
             * This is just a regular kdoc block.
             *
             * Nothing to see here...
             */
            val v = 2
        """.trimIndent()
        assertThat(subject.lint(code)).isEmpty()
    }

    @Nested
    inner class `reporting deprecation tag on kdoc block` {
        val code = """
            /**
             * I am a KDoc block
             *
             * @deprecated oh no, this should not be here
             */
            fun ohNo() { }
        """.trimIndent()

        @Test
        fun `correct message`() {
            assertThat(subject.lint(code)).singleElement().hasMessage(
                "@deprecated tag block does not properly report " +
                    "deprecation in Kotlin, use @Deprecated annotation instead"
            )
        }
    }

    @Nested
    inner class `reporting deprecation tag wherever @Deprecated is available` {

        @Test
        fun `report deprecation tag on class`() {
            val code = """
                /**
                 * Hello there
                 *
                 * @deprecated This thing is deprecated
                 */
                class Thing { }
            """.trimIndent()
            assertThat(subject.lint(code)).hasSize(1)
        }

        @Test
        fun `report deprecation tag on property`() {
            val code = """
                class Thing {
                    /**
                     * A thing you should not use
                     *
                     * @deprecated Do not use that
                     */
                    val doNotUseMe = 0
                }
            """.trimIndent()
            assertThat(subject.lint(code)).hasSize(1)
        }

        @Test
        fun `report deprecation tag on annotation class`() {
            val code = """
                /**
                 * An annotation you should not use
                 *
                 * @deprecated Do not use that
                 */
                annotation class Thing()
            """.trimIndent()
            assertThat(subject.lint(code)).hasSize(1)
        }

        @Test
        fun `report deprecation tag on constructor`() {
            val code = """
                class Thing {
                    /**
                     * A constructor you should not use
                     *
                     * @deprecated Do not use that
                     */
                    constructor(something: String)
                }
            """.trimIndent()
            assertThat(subject.lint(code)).hasSize(1)
        }

        @Test
        fun `report deprecation tag on property setter`() {
            val code = """
                class Thing {
                    var someProperty: Int
                        get() = 10
                        /**
                         * Do not use this setter
                         *
                         * @deprecated Do not use it
                         */
                        set(value) { println(value) }
                }
            """.trimIndent()
            assertThat(subject.lint(code)).hasSize(1)
        }

        @Test
        fun `report deprecation tag on property getter`() {
            val code = """
                class Thing {
                    var someProperty: Int
                        /**
                         * Do not use this getter
                         *
                         * @deprecated Do not use it
                         */
                        get() = 10
                        set(value) { println(value) }
                }
            """.trimIndent()
            assertThat(subject.lint(code)).hasSize(1)
        }

        @Test
        fun `report deprecation tag on typealias`() {
            val code = """
                /**
                 * This alias is pointless, do not use it
                 *
                 * @deprecated Do not use this typealias
                 */
                typealias VeryString = String
            """.trimIndent()
            assertThat(subject.lint(code)).hasSize(1)
        }
    }
}
