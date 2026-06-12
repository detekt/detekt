package dev.detekt.rules.style

import dev.detekt.api.Config
import dev.detekt.test.assertj.assertThat
import dev.detekt.test.lint
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class OneTopLevelTypePerFileSpec {

    private val subject = OneTopLevelTypePerFile(Config.empty)

    @Test
    fun `does not report one class in a file`() {
        val code = "class User"

        assertThat(subject.lint(code)).isEmpty()
    }

    @Test
    fun `does not report one interface in a file`() {
        val code = "interface UserRepository"

        assertThat(subject.lint(code)).isEmpty()
    }

    @Test
    fun `reports class plus interface in same file`() {
        val code = """
            class User

            interface UserRepository
        """.trimIndent()

        assertThat(subject.lint(code))
            .singleElement()
            .hasMessage(
                "The file contains 2 top-level type declarations. " +
                    "Each file may contain only one top-level type declaration."
            )
            .hasStartSourceLocation(3, 11)
    }

    @Test
    fun `reports two data classes in same file`() {
        val code = """
            data class User(val name: String)

            data class Address(val city: String)
        """.trimIndent()

        assertThat(subject.lint(code)).hasSize(1)
    }

    @Test
    fun `does not report class with nested class`() {
        val code = """
            class User {
                class Address
            }
        """.trimIndent()

        assertThat(subject.lint(code)).isEmpty()
    }

    @Test
    fun `does not report class with companion object`() {
        val code = """
            class User {
                companion object
            }
        """.trimIndent()

        assertThat(subject.lint(code)).isEmpty()
    }

    @Test
    fun `does not report file with only top-level functions and properties`() {
        val code = """
            const val answer = 42

            fun answer() = answer
        """.trimIndent()

        assertThat(subject.lint(code)).isEmpty()
    }

    @Test
    fun `reports enum and object in same file`() {
        val code = """
            enum class Direction {
                North, South
            }

            object DirectionParser
        """.trimIndent()

        assertThat(subject.lint(code)).hasSize(1)
    }

    @Test
    fun `reports sealed class and sealed interface in same file`() {
        val code = """
            sealed class Result

            sealed interface ResultHandler
        """.trimIndent()

        assertThat(subject.lint(code)).hasSize(1)
    }

    @Test
    fun `reports value class and annotation class in same file`() {
        val code = """
            @JvmInline
            value class UserId(val value: String)

            annotation class Marker
        """.trimIndent()

        assertThat(subject.lint(code)).hasSize(1)
    }

    @Test
    fun `does not report local class inside function`() {
        val code = """
            fun createUser() {
                class User
            }
        """.trimIndent()

        assertThat(subject.lint(code)).isEmpty()
    }
}
