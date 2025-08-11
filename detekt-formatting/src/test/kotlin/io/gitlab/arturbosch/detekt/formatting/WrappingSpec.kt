package io.gitlab.arturbosch.detekt.formatting

import dev.detekt.api.Config
import dev.detekt.test.assertThat
import dev.detekt.test.lint
import io.gitlab.arturbosch.detekt.formatting.wrappers.Wrapping
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

        assertThat(subject.lint(code)).singleElement()
            .hasStartSourceLocation(1, 12)
            .hasTextLocation(11 to 12)
    }
}
