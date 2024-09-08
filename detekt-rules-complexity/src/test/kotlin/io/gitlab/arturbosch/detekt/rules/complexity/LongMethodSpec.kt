package io.gitlab.arturbosch.detekt.rules.complexity

import io.gitlab.arturbosch.detekt.test.TestConfig
import io.gitlab.arturbosch.detekt.test.assertThat
import io.gitlab.arturbosch.detekt.test.compileAndLint
import org.junit.jupiter.api.Test

class LongMethodSpec {

    val subject = LongMethod(TestConfig("allowedLines" to 5))

    @Test
    fun `should find two long methods`() {
        val code = """
            fun longMethod() { // 6 lines
                println()
                println()
                println()
                println()
            
                fun nestedLongMethod() { // 6 lines
                    println()
                    println()
                    println()
                    println()
                }
            }
        """.trimIndent()
        val findings = subject.compileAndLint(code)

        assertThat(findings).hasSize(2)
        assertThat(findings).hasTextLocations("longMethod", "nestedLongMethod")
    }

    @Test
    fun `should not find too long methods`() {
        val code = """
            fun methodOk() { // 3 lines
                println()
                fun localMethodOk() { // 4 lines
                    println()
                    println()
                }
            }
        """.trimIndent()

        assertThat(subject.compileAndLint(code)).isEmpty()
    }

    @Test
    fun `should not find too long method with params on newlines`() {
        val code = """
            fun methodWithParams(
                param1: String
            ) { // 4 lines
                println()
                println()
            }
        """.trimIndent()

        assertThat(subject.compileAndLint(code)).isEmpty()
    }

    @Test
    fun `should find too long method with params on newlines`() {
        val code = """
            fun longMethodWithParams(
                param1: String
            ) { // 6 lines
                println()
                println()
                println()
                println()
            }
        """.trimIndent()

        val findings = subject.compileAndLint(code)

        assertThat(findings).hasSize(1)
    }

    @Test
    fun `should find long method with method call with params on separate lines`() {
        val code = """
            fun longMethod(
                x1: Int,
                x2: Int,
                y1: Int,
                y2: Int
            ) { // 8 lines
                listOf(
                    x1,
                    y1,
                    x2,
                    y2
                )
            }
        """.trimIndent()

        val findings = subject.compileAndLint(code)

        assertThat(findings).hasSize(1)
    }

    @Test
    fun `should find two long methods with params on separate lines`() {
        val code = """
            fun longMethod(
                param1: String
            ) { // 6 lines
                println()
                println()
                println()
                println()
            
                fun nestedLongMethod(
                    param1: String
                ) { // 6 lines
                    println()
                    println()
                    println()
                    println()
                }
            }
        """.trimIndent()

        val findings = subject.compileAndLint(code)

        assertThat(findings)
            .hasSize(2)
            .hasTextLocations("longMethod", "nestedLongMethod")
    }

    @Test
    fun `should find nested long methods with params on separate lines`() {
        val code = """
            fun longMethod(
                param1: String
            ) { // 5 lines
                println()
                println()
                println()
            
                fun nestedLongMethod(
                    param1: String
                ) { // 6 lines
                    println()
                    println()
                    println()
                    println()
                }
            }
        """.trimIndent()

        val findings = subject.compileAndLint(code)

        assertThat(findings)
            .hasSize(1)
            .hasTextLocations("nestedLongMethod")
    }
}
