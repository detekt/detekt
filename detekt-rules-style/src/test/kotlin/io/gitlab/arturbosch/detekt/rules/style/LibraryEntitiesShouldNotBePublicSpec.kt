package io.gitlab.arturbosch.detekt.rules.style

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.test.TestConfig
import io.gitlab.arturbosch.detekt.test.assertThat
import io.gitlab.arturbosch.detekt.test.compileAndLint
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class LibraryEntitiesShouldNotBePublicSpec {

    @Test
    fun `should not report without explicit filters set`() {
        val subject = LibraryEntitiesShouldNotBePublic(TestConfig(Config.EXCLUDES_KEY to "**"))
        assertThat(
            subject.compileAndLint(
                """
                class A 
                """
            )
        ).isEmpty()
    }

    @Nested
    inner class `positive cases` {
        val subject = LibraryEntitiesShouldNotBePublic()

        @Test
        fun `should report a class`() {
            assertThat(
                subject.compileAndLint(
                    """
                class A
                    """
                )
            ).hasSize(1)
        }

        @Test
        fun `should report a class with function`() {
            assertThat(
                subject.compileAndLint(
                    """
                class A {
                    fun foo(): Int{
                        return 1
                    }
                }
                    """
                )
            ).hasSize(1)
        }

        @Test
        fun `should report a typealias`() {
            assertThat(
                subject.compileAndLint(
                    """
                typealias A = List<String>
                    """
                )
            ).hasSize(1)
        }

        @Test
        fun `should report a typealias and a function`() {
            assertThat(
                subject.compileAndLint(
                    """
                typealias A = List<String>
                fun foo() = Unit
                    """
                )
            ).hasSize(2)
        }

        @Test
        fun `should report a function`() {
            assertThat(
                subject.compileAndLint(
                    """
                fun foo() = Unit
                    """
                )
            ).hasSize(1)
        }
    }

    @Nested
    inner class `negative cases` {
        val subject = LibraryEntitiesShouldNotBePublic()

        @Test
        fun `should not report a class`() {
            assertThat(
                subject.compileAndLint(
                    """
                internal class A {
                    fun foo(): Int{
                        return 1
                    }
                }
                    """
                )
            ).isEmpty()
        }

        @Test
        fun `should not report a class with function`() {
            assertThat(
                subject.compileAndLint(
                    """
                internal class A
                    """
                )
            ).isEmpty()
        }

        @Test
        fun `should not report a typealias`() {
            assertThat(
                subject.compileAndLint(
                    """
                internal typealias A = List<String>
                    """
                )
            ).isEmpty()
        }
    }
}
