package io.gitlab.arturbosch.detekt.formatting

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.formatting.wrappers.Wrapping
import io.gitlab.arturbosch.detekt.test.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class WrappingSpec {

    private lateinit var subject: Wrapping

    @BeforeEach
    fun createSubject() {
        subject = Wrapping(Config.empty)
    }

    @Test
    fun `should report a wrong wrapping in the extends clause of the class definition`() {
        val code =
            """
            class A() : B, 
                C {
            }
            """.trimIndent()

        assertThat(subject.lint(code))
            .hasSize(1)
            .hasTextLocations(11 to 11)
    }
}
