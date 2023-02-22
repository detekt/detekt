package io.gitlab.arturbosch.detekt.rules.complexity

import io.gitlab.arturbosch.detekt.test.TestConfig
import io.gitlab.arturbosch.detekt.test.compileAndLint
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class LongParameterListSpec {

    val defaultThreshold = 2
    val defaultConfig =
        TestConfig(
            mapOf(
                "functionThreshold" to defaultThreshold,
                "constructorThreshold" to defaultThreshold
            )
        )

    val subject = LongParameterList(defaultConfig)

    val reportMessageForFunction = "The function long(a: Int, b: Int) has too many parameters. " +
        "The current threshold is set to $defaultThreshold."
    val reportMessageForConstructor = "The constructor(a: Int, b: Int) has too many parameters. " +
        "The current threshold is set to $defaultThreshold."

    @Test
    fun `reports too long parameter list`() {
        val code = "fun long(a: Int, b: Int) {}"
        val findings = subject.compileAndLint(code)
        assertThat(findings).hasSize(1)
        assertThat(findings.first().message).isEqualTo(reportMessageForFunction)
    }

    @Test
    fun `does not report short parameter list`() {
        val code = "fun long(a: Int) {}"
        assertThat(subject.compileAndLint(code)).isEmpty()
    }

    @Test
    fun `reports too long parameter list event for parameters with defaults`() {
        val code = "fun long(a: Int, b: Int = 1) {}"
        assertThat(subject.compileAndLint(code)).hasSize(1)
    }

    @Test
    fun `does not report long parameter list if parameters with defaults should be ignored`() {
        val config = TestConfig(mapOf("ignoreDefaultParameters" to "true"))
        val rule = LongParameterList(config)
        val code = "fun long(a: Int, b: Int, c: Int = 2) {}"
        assertThat(rule.compileAndLint(code)).isEmpty()
    }

    @Test
    fun `reports too long parameter list for primary constructors`() {
        val code = "class LongCtor(a: Int, b: Int)"
        val findings = subject.compileAndLint(code)
        assertThat(findings).hasSize(1)
        assertThat(findings.first().message).isEqualTo(reportMessageForConstructor)
    }

    @Test
    fun `does not report short parameter list for primary constructors`() {
        val code = "class LongCtor(a: Int)"
        assertThat(subject.compileAndLint(code)).isEmpty()
    }

    @Test
    fun `reports too long parameter list for secondary constructors`() {
        val code = "class LongCtor() { constructor(a: Int, b: Int) : this() }"
        val findings = subject.compileAndLint(code)
        assertThat(findings).hasSize(1)
        assertThat(findings.first().message).isEqualTo(reportMessageForConstructor)
    }

    @Test
    fun `does not report short parameter list for secondary constructors`() {
        val code = "class LongCtor() { constructor(a: Int) : this() }"
        assertThat(subject.compileAndLint(code)).isEmpty()
    }

    @Test
    fun `reports long parameter list if custom threshold is set`() {
        val config = TestConfig(mapOf("constructorThreshold" to "1"))
        val rule = LongParameterList(config)
        val code = "class LongCtor(a: Int)"
        assertThat(rule.compileAndLint(code)).hasSize(1)
    }

    @Test
    fun `does not report long parameter list for constructors of data classes if asked`() {
        val config = TestConfig(
            mapOf(
                "ignoreDataClasses" to "true",
                "constructorThreshold" to "1"
            )
        )
        val rule = LongParameterList(config)
        val code = "data class Data(val a: Int)"
        assertThat(rule.compileAndLint(code)).isEmpty()
    }

    @Nested
    inner class `constructors and functions with ignored annotations` {

        val config =
            TestConfig(
                mapOf(
                    "ignoreAnnotatedParameter" to listOf(
                        "Generated",
                        "kotlin.Deprecated",
                        "kotlin.jvm.JvmName",
                        "kotlin.Suppress"
                    ),
                    "functionThreshold" to 1,
                    "constructorThreshold" to 1
                )
            )

        val rule = LongParameterList(config)

        @Test
        fun `reports long parameter list for constructors if constructor parameters are annotated with annotation that is not ignored`() {
            val code = """
                @Target(AnnotationTarget.VALUE_PARAMETER)
                annotation class CustomAnnotation
                
                class Data constructor(@CustomAnnotation val a: Int)
            """.trimIndent()
            assertThat(rule.compileAndLint(code)).hasSize(1)
        }

        @Test
        fun `reports long parameter list for functions if enough function parameters are annotated with annotation that is not ignored`() {
            val code = """
                @Target(AnnotationTarget.VALUE_PARAMETER)
                annotation class CustomAnnotation
                
                class Data { fun foo(@CustomAnnotation a: Int) {} }
            """.trimIndent()
            assertThat(rule.compileAndLint(code)).hasSize(1)
        }

        @Test
        fun `does not report long parameter list for constructors if enough constructor parameters are annotated with ignored annotation`() {
            val code = "class Data constructor(@kotlin.Suppress(\"\") val a: Int)"
            assertThat(rule.compileAndLint(code)).isEmpty()
        }

        @Test
        fun `does not report long parameter list for functions if enough function parameters are annotated with ignored annotation`() {
            val code = """
                class Data {
                    fun foo(@kotlin.Suppress("") a: Int) {}
                }
            """.trimIndent()
            assertThat(rule.compileAndLint(code)).isEmpty()
        }
    }
}
