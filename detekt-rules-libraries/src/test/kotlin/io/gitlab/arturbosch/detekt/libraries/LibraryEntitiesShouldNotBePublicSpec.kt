package io.gitlab.arturbosch.detekt.libraries

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.test.assertThat
import io.gitlab.arturbosch.detekt.test.lint
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class LibraryEntitiesShouldNotBePublicSpec {

    @Nested
    inner class `positive cases` {
        val subject = LibraryEntitiesShouldNotBePublic(Config.empty)

        @Test
        fun `should report a class`() {
            assertThat(
                subject.lint(
                    """
                        class A
                    """.trimIndent()
                )
            ).hasSize(1)
        }

        @Test
        fun `should report a class with function`() {
            assertThat(
                subject.lint(
                    """
                        class A {
                            fun foo(): Int{
                                return 1
                            }
                        }
                    """.trimIndent()
                )
            ).hasSize(1)
        }

        @Test
        fun `should report a typealias`() {
            assertThat(
                subject.lint(
                    """
                        typealias A = List<String>
                    """.trimIndent()
                )
            ).hasSize(1)
        }

        @Test
        fun `should report a typealias and a function`() {
            assertThat(
                subject.lint(
                    """
                        typealias A = List<String>
                        fun foo() = Unit
                    """.trimIndent()
                )
            ).hasSize(2)
        }

        @Test
        fun `should report a function`() {
            assertThat(
                subject.lint(
                    """
                        fun foo() = Unit
                    """.trimIndent()
                )
            ).hasSize(1)
        }
    }

    @Nested
    inner class `negative cases` {
        val subject = LibraryEntitiesShouldNotBePublic(Config.empty)

        @Test
        fun `should not report a class`() {
            assertThat(
                subject.lint(
                    """
                        internal class A {
                            fun foo(): Int{
                                return 1
                            }
                        }
                    """.trimIndent()
                )
            ).isEmpty()
        }

        @Test
        fun `should not report a class with function`() {
            assertThat(
                subject.lint(
                    """
                        internal class A
                    """.trimIndent()
                )
            ).isEmpty()
        }

        @Test
        fun `should not report a typealias`() {
            assertThat(
                subject.lint(
                    """
                        internal typealias A = List<String>
                    """.trimIndent()
                )
            ).isEmpty()
        }
    }
}
