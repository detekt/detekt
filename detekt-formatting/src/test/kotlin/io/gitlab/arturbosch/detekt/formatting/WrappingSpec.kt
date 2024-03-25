package io.gitlab.arturbosch.detekt.formatting

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.formatting.wrappers.Wrapping
import io.gitlab.arturbosch.detekt.test.assertThat
import io.gitlab.arturbosch.detekt.test.compileAndLint
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
            interface B
            
            interface C
            
            class A() : B,
                C {
            }
        """.trimIndent()

        assertThat(subject.compileAndLint(code))
            .hasSize(1)
            .hasStartSourceLocation(5, 12)
            .hasTextLocations(37 to 38)
    }
}
