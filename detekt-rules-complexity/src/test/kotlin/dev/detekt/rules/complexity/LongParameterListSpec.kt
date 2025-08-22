package dev.detekt.rules.complexity

import dev.detekt.test.TestConfig
import dev.detekt.test.assertThat
import dev.detekt.test.lintWithContext
import dev.detekt.test.utils.KotlinCoreEnvironmentTest
import dev.detekt.test.utils.KotlinEnvironmentContainer
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

@KotlinCoreEnvironmentTest
class LongParameterListSpec(private val env: KotlinEnvironmentContainer) {
    private val defaultMaximumParameters = 2
    private val defaultConfig = TestConfig(
        "allowedFunctionParameters" to defaultMaximumParameters,
        "allowedConstructorParameters" to defaultMaximumParameters,
    )

    private val subject = LongParameterList(defaultConfig)

    private val reportMessageForFunction = "The function long(a: Int, b: Int, c: Int) has too many parameters. " +
        "The current maximum allowed parameters are $defaultMaximumParameters."
    private val reportMessageForConstructor = "The constructor(a: Int, b: Int, c: Int) has too many parameters. " +
        "The current maximum allowed parameters are $defaultMaximumParameters."

    @Test
    fun `does not report function parameter list that has exactly the allowed amount`() {
        val code = "fun long(a: Int, b: Int) {}"

        val findings = subject.lintWithContext(env, code)

        assertThat(findings).isEmpty()
    }

    @Test
    fun `reports too long parameter list`() {
        val code = "fun long(a: Int, b: Int, c: Int) {}"
        val findings = subject.lintWithContext(env, code)
        assertThat(findings).singleElement()
            .hasMessage(reportMessageForFunction)
    }

    @Test
    fun `does not report short parameter list`() {
        val code = "fun long(a: Int) {}"
        assertThat(subject.lintWithContext(env, code)).isEmpty()
    }

    @Test
    fun `reports too long parameter list event for parameters with defaults`() {
        val code = "fun long(a: Int, b: Int = 1, c: Int) {}"
        assertThat(subject.lintWithContext(env, code)).hasSize(1)
    }

    @Test
    fun `does not report long parameter list if parameters with defaults should be ignored`() {
        val config = TestConfig("ignoreDefaultParameters" to "true")
        val rule = LongParameterList(config)
        val code = "fun long(a: Int, b: Int, c: Int = 2) {}"
        assertThat(rule.lintWithContext(env, code)).isEmpty()
    }

    @Test
    fun `reports too long parameter list for primary constructors`() {
        val code = "class LongCtor(a: Int, b: Int, c: Int)"
        val findings = subject.lintWithContext(env, code)
        assertThat(findings).singleElement()
            .hasMessage(reportMessageForConstructor)
    }

    @Test
    fun `does not report short parameter list for primary constructors`() {
        val code = "class LongCtor(a: Int)"
        assertThat(subject.lintWithContext(env, code)).isEmpty()
    }

    @Test
    fun `reports too long parameter list for secondary constructors`() {
        val code = "class LongCtor() { constructor(a: Int, b: Int, c: Int) : this() }"
        val findings = subject.lintWithContext(env, code)
        assertThat(findings).singleElement()
            .hasMessage(reportMessageForConstructor)
    }

    @Test
    fun `does not report short parameter list for secondary constructors`() {
        val code = "class LongCtor() { constructor(a: Int) : this() }"
        assertThat(subject.lintWithContext(env, code)).isEmpty()
    }

    @Test
    fun `reports long parameter list if custom threshold is set`() {
        val config = TestConfig("allowedConstructorParameters" to "1")
        val rule = LongParameterList(config)
        val code = "class LongCtor(a: Int, b: Int)"
        assertThat(rule.lintWithContext(env, code)).hasSize(1)
    }

    @Test
    fun `does not report constructor parameter list that has exactly the allowed amount`() {
        val code = "data class Data(val a: Int, val b: Int)"

        val findings = subject.lintWithContext(env, code)

        assertThat(findings).isEmpty()
    }

    @Test
    fun `does not report long parameter list for constructors of data classes if asked`() {
        val config = TestConfig(
            "ignoreDataClasses" to "true",
            "constructorThreshold" to "1",
        )
        val rule = LongParameterList(config)
        val code = "data class Data(val a: Int)"
        assertThat(rule.lintWithContext(env, code)).isEmpty()
    }

    @Nested
    inner class `constructors and functions with ignored annotations` {
        private val config = TestConfig(
            "ignoreAnnotatedParameter" to listOf(
                "Generated",
                "kotlin.Deprecated",
                "kotlin.jvm.JvmName",
                "kotlin.Suppress",
            ),
            "allowedFunctionParameters" to 1,
            "allowedConstructorParameters" to 1,
        )

        private val rule = LongParameterList(config)

        @Test
        fun `reports long parameter list for constructors if constructor parameters are annotated with annotation that is not ignored`() {
            val code = """
                @Target(AnnotationTarget.VALUE_PARAMETER)
                annotation class CustomAnnotation
                
                class Data constructor(@CustomAnnotation val a: Int, @CustomAnnotation val b: Int)
            """.trimIndent()
            assertThat(rule.lintWithContext(env, code)).hasSize(1)
        }

        @Test
        fun `reports long parameter list for functions if enough function parameters are annotated with annotation that is not ignored`() {
            val code = """
                @Target(AnnotationTarget.VALUE_PARAMETER)
                annotation class CustomAnnotation
                
                class Data { fun foo(@CustomAnnotation a: Int, @CustomAnnotation b: Int) {} }
            """.trimIndent()
            assertThat(rule.lintWithContext(env, code)).hasSize(1)
        }

        @Test
        fun `does not report long parameter list for constructors if enough constructor parameters are annotated with ignored annotation`() {
            val code = "class Data constructor(@kotlin.Suppress(\"\") val a: Int)"
            assertThat(rule.lintWithContext(env, code)).isEmpty()
        }

        @Test
        fun `does not report long parameter list for functions if enough function parameters are annotated with ignored annotation`() {
            val code = """
                class Data {
                    fun foo(@kotlin.Suppress("") a: Int) {}
                }
            """.trimIndent()
            assertThat(rule.lintWithContext(env, code)).isEmpty()
        }
    }
}
