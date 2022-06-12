package io.gitlab.arturbosch.detekt.rules.style

import io.gitlab.arturbosch.detekt.rules.KotlinCoreEnvironmentTest
import io.gitlab.arturbosch.detekt.test.compileAndLintWithContext
import io.gitlab.arturbosch.detekt.test.lintWithContext
import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.kotlin.cli.jvm.compiler.KotlinCoreEnvironment
import org.junit.jupiter.api.Test

@KotlinCoreEnvironmentTest
class UselessCallOnNotNullSpec(val env: KotlinCoreEnvironment) {
    val subject = UselessCallOnNotNull()

    @Test
    fun `reports when calling orEmpty on a list`() {
        val code = """val testList = listOf("string").orEmpty()"""
        val findings = subject.compileAndLintWithContext(env, code)
        assertThat(findings).hasSize(1)
        assertThat(findings[0].message).isEqualTo("Remove redundant call to orEmpty")
    }

    @Test
    fun `reports when calling orEmpty on a list with a safe call`() {
        val code = """val testList = listOf("string")?.orEmpty()"""
        val findings = subject.compileAndLintWithContext(env, code)
        assertThat(findings).hasSize(1)
        assertThat(findings[0].message).isEqualTo("Remove redundant call to orEmpty")
    }

    @Test
    fun `reports when calling orEmpty on a list in a chain`() {
        val code = """val testList = listOf("string").orEmpty().map { }"""
        val findings = subject.compileAndLintWithContext(env, code)
        assertThat(findings).hasSize(1)
        assertThat(findings[0].message).isEqualTo("Remove redundant call to orEmpty")
    }

    @Test
    fun `reports when calling orEmpty on a list with a platform type`() {
        // System.getenv().keys.toList() will be of type List<String!>.
        val code = """val testSequence = System.getenv().keys.toList().orEmpty()"""
        val findings = subject.compileAndLintWithContext(env, code)
        assertThat(findings).hasSize(1)
        assertThat(findings[0].message).isEqualTo("Remove redundant call to orEmpty")
    }

    @Test
    fun `does not report when calling orEmpty on a nullable list`() {
        val code = """
            val testList: List<String>? = listOf("string")
            val nonNullableTestList = testList.orEmpty()
        """
        assertThat(subject.compileAndLintWithContext(env, code)).isEmpty()
    }

    @Test
    fun `reports when calling isNullOrBlank on a string with a safe call`() {
        val code = """val testString = ""?.isNullOrBlank()"""
        val findings = subject.compileAndLintWithContext(env, code)
        assertThat(findings).hasSize(1)
        assertThat(findings[0].message).isEqualTo("Replace isNullOrBlank with isBlank")
    }

    @Test
    fun `does not report when calling isNullOrBlank on a nullable string`() {
        val code = """
            val testString: String? = ""
            val nonNullableTestString = testString.isNullOrBlank()
        """
        assertThat(subject.compileAndLintWithContext(env, code)).isEmpty()
    }

    @Test
    fun `reports when calling isNullOrEmpty on a string`() {
        val code = """val testString = "".isNullOrEmpty()"""
        val findings = subject.compileAndLintWithContext(env, code)
        assertThat(findings).hasSize(1)
        assertThat(findings[0].message).isEqualTo("Replace isNullOrEmpty with isEmpty")
    }

    @Test
    fun `reports when calling isNullOrEmpty on a string with a safe call`() {
        val code = """val testString = ""?.isNullOrEmpty()"""
        assertThat(subject.compileAndLintWithContext(env, code)).hasSize(1)
    }

    @Test
    fun `does not report when calling isNullOrEmpty on a nullable string`() {
        val code = """
            val testString: String? = ""
            val nonNullableTestString = testString.isNullOrEmpty()
        """
        assertThat(subject.compileAndLintWithContext(env, code)).isEmpty()
    }

    @Test
    fun `reports when calling orEmpty on a string`() {
        val code = """val testString = "".orEmpty()"""
        val findings = subject.compileAndLintWithContext(env, code)
        assertThat(findings).hasSize(1)
        assertThat(findings[0].message).isEqualTo("Remove redundant call to orEmpty")
    }

    @Test
    fun `does not report when calling orEmpty on a nullable string`() {
        val code = """
            val testString: String? = ""
            val nonNullableTestString = testString.orEmpty()
        """
        assertThat(subject.compileAndLintWithContext(env, code)).isEmpty()
    }

    @Test
    fun `reports when calling orEmpty on a sequence`() {
        val code = """val testSequence = listOf(1).asSequence().orEmpty()"""
        val findings = subject.compileAndLintWithContext(env, code)
        assertThat(findings).hasSize(1)
        assertThat(findings[0].message).isEqualTo("Remove redundant call to orEmpty")
    }

    @Test
    fun `does not report when calling orEmpty on a nullable sequence`() {
        val code = """
            val testSequence: Sequence<Int>? = listOf(1).asSequence()
            val nonNullableTestSequence = testSequence.orEmpty()
        """
        assertThat(subject.compileAndLintWithContext(env, code)).isEmpty()
    }

    @Test
    fun `only reports on a Kotlin list`() {
        val code = """
            fun String.orEmpty(): List<Char> = this.toCharArray().asList()

            val noList = "str".orEmpty()
            val list = listOf(1, 2, 3).orEmpty()
        """
        val findings = subject.compileAndLintWithContext(env, code)
        assertThat(findings).hasSize(1)
        assertThat(findings[0].message).isEqualTo("Remove redundant call to orEmpty")
    }

    @Test
    fun `reports when calling listOfNotNull on all non-nullable arguments`() {
        val code = """
            val strings = listOfNotNull("string")
        """
        val findings = subject.compileAndLintWithContext(env, code)
        assertThat(findings).hasSize(1)
        assertThat(findings[0].message).isEqualTo("Replace listOfNotNull with listOf")
    }

    @Test
    fun `reports when calling listOfNotNull with no arguments`() {
        val code = """
            val strings = listOfNotNull<String>()
        """
        val findings = subject.compileAndLintWithContext(env, code)
        assertThat(findings).hasSize(1)
        assertThat(findings[0].message).isEqualTo("Replace listOfNotNull with listOf")
    }

    @Test
    fun `does not report when calling listOfNotNull on at least one nullable argument`() {
        val code = """
            val strings = listOfNotNull("string", null)
        """
        val findings = subject.compileAndLintWithContext(env, code)
        assertThat(findings).isEmpty()
    }

    @Test
    fun `does not report when calling listOfNotNull with spread operator`() {
        val code = """
            val nullableArray = arrayOf("string", null)
            val strings = listOfNotNull(*nullableArray)
        """
        val findings = subject.compileAndLintWithContext(env, code)
        assertThat(findings).isEmpty()
    }

    @Test
    fun `reports when calling listOfNotNull with spread operator on all non-nullable arguments`() {
        val code = """
            val nonNullableArray = arrayOf("string", "bar")
            val strings = listOfNotNull(*nonNullableArray)
        """
        val findings = subject.compileAndLintWithContext(env, code)
        assertThat(findings).hasSize(1)
        assertThat(findings[0].message).isEqualTo("Replace listOfNotNull with listOf")
    }

    @Test
    fun `does not report when calling listOfNotNull with a mix of null spread and non-null non-spread`() {
        val code = """
            val nullableArray = arrayOf("string", null)
            val nonNullableArray = arrayOf("string", "bar")
            val strings = listOfNotNull("string", *nonNullableArray, "foo", *nullableArray)
        """
        val findings = subject.compileAndLintWithContext(env, code)
        assertThat(findings).isEmpty()
    }

    @Test
    fun `does not report when calling listOfNotNull with a mix of non-null spread and null non-spread`() {
        val code = """
            val nonNullableArray = arrayOf("string", "bar")
            val strings = listOfNotNull("string", *nonNullableArray, null)
        """
        val findings = subject.compileAndLintWithContext(env, code)
        assertThat(findings).isEmpty()
    }

    @Test
    fun `reports when calling listOfNotNull with a mix of spread and non-spread, all non-null`() {
        val code = """
            val nonNullableArray = arrayOf("string", "bar")
            val otherNonNullableArray = arrayOf("foobar")
            val strings = listOfNotNull("string", *nonNullableArray, "foo", *otherNonNullableArray)
        """
        val findings = subject.compileAndLintWithContext(env, code)
        assertThat(findings).hasSize(1)
        assertThat(findings[0].message).isEqualTo("Replace listOfNotNull with listOf")
    }

    @Test
    fun `does not report when calling custom function named listOfNotNull on all non-nullable arguments`() {
        val code = """
            fun <T : Any> listOfNotNull(vararg elements: T?): List<T> = TODO()

            val strings = listOfNotNull("string", null)                
        """
        val findings = subject.compileAndLintWithContext(env, code)
        assertThat(findings).isEmpty()
    }

    @Test
    fun `does not report when calling listOfNotNull with values whose type is unknown`() {
        val code = """
            fun test() {
                listOfNotNull(unknown)
            }
        """.trimIndent()

        val findings = subject.lintWithContext(env, code)
        assertThat(findings).isEmpty()
    }

    @Test
    fun `does not report when calling listOfNotNull with values whose type is derived and unknown`() {
        val code = """
            import kotlin.random.Random

            fun test() {
                listOfNotNull(unknown.takeIf { Random.nextBoolean() })
            }
        """.trimIndent()

        val findings = subject.lintWithContext(env, code)
        assertThat(findings).isEmpty()
    }

    @Test
    fun `reports when calling isNullOrEmpty on a list`() {
        val code = """
            fun test(list: List<Int>) {
                list.isNullOrEmpty()
            }                
        """
        val findings = subject.compileAndLintWithContext(env, code)
        assertThat(findings).hasSize(1)
        assertThat(findings[0].message).isEqualTo("Replace isNullOrEmpty with isEmpty")
    }
}
