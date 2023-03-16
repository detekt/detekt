package io.gitlab.arturbosch.detekt.formatting

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.formatting.wrappers.Wrapping
import io.gitlab.arturbosch.detekt.test.assert
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
            class A() : B,
                C {
            }
            
            interface B
            
            interface C
            
        """.trimIndent()

        subject.compileAndLint(code).assert()
            .hasSize(1)
            .hasStartSourceLocation(1, 13)
            .hasTextLocations(12 to 20)
    }
}
