package io.gitlab.arturbosch.detekt.formatting

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.formatting.wrappers.Wrapping
import io.gitlab.arturbosch.detekt.test.assert
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class WrappingSpec {

    private lateinit var subject: Wrapping

    @BeforeEach
    fun createSubject() {
        subject = Wrapping(Config.empty)
    }

    @Test
    fun `Given a wrong wrapping in the class definition`() {
        val code = """
            class A() : B, 
                C {
            }

            interface B

            interface C

        """.trimIndent()

        subject.lint(code).assert()
            .hasSize(1)
            .hasStartSourceLocation(1, 12)
            .hasTextLocations(11 to 12)
    }
}
