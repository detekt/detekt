package dev.detekt.rules.ktlintwrapper

import dev.detekt.api.Config
import dev.detekt.rules.ktlintwrapper.wrappers.Wrapping
import dev.detekt.test.assertThat
import dev.detekt.test.lint
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
