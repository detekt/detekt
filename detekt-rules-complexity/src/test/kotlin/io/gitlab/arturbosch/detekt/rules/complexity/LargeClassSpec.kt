package io.gitlab.arturbosch.detekt.rules.complexity

import io.github.detekt.test.utils.resourceAsPath
import io.gitlab.arturbosch.detekt.api.SourceLocation
import io.gitlab.arturbosch.detekt.test.TestConfig
import io.gitlab.arturbosch.detekt.test.assertThat
import io.gitlab.arturbosch.detekt.test.compileAndLint
import io.gitlab.arturbosch.detekt.test.lint
import org.junit.jupiter.api.Test

private fun subject(threshold: Int) = LargeClass(TestConfig(mapOf("threshold" to threshold)))

class LargeClassSpec {

    @Test
    fun `should detect only the nested large class which exceeds threshold 70`() {
        val findings = subject(threshold = 70).lint(resourceAsPath("NestedClasses.kt"))
        assertThat(findings).hasSize(1)
        assertThat(findings).hasStartSourceLocations(SourceLocation(12, 15))
    }

    @Test
    fun `should not report anything in files without classes`() {
        val code = """
            val i = 0 

            fun f() {
                println()
                println()
            }
        """
        val rule = subject(threshold = 2)
        assertThat(rule.compileAndLint(code)).isEmpty()
    }
}
