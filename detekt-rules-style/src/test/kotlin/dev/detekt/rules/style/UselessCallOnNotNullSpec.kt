package dev.detekt.rules.style

import dev.detekt.api.Config
import dev.detekt.test.assertj.assertThat
import dev.detekt.test.junit.KotlinCoreEnvironmentTest
import dev.detekt.test.lintWithContext
import dev.detekt.test.utils.KotlinEnvironmentContainer
import org.junit.jupiter.api.Test

@KotlinCoreEnvironmentTest
class UselessCallOnNotNullSpec(val env: KotlinEnvironmentContainer) {
    val subject = UselessCallOnNotNull(Config.empty)

    @Test
    fun `reports when calling orEmpty on a list`() {
        val code = """val testList = listOf("string").orEmpty()"""
        val findings = subject.lintWithContext(env, code)
        assertThat(findings).singleElement()
            .hasMessage("Remove redundant call to orEmpty")
    }

    @Test
    fun `reports when calling orEmpty on a list with a safe call`() {
        val code = """val testList = listOf("string")?.orEmpty()"""
        val findings = subject.lintWithContext(env, code)
        assertThat(findings).singleElement()
            .hasMessage("Remove redundant call to orEmpty")
    }

    @Test
    fun `reports when calling orEmpty on a list in a chain`() {
        val code = """val testList = listOf("string").orEmpty().map { }"""
        val findings = subject.lintWithContext(env, code)
        assertThat(findings).singleElement()
            .hasMessage("Remove redundant call to orEmpty")
    }

    @Test
    fun `reports when calling orEmpty on a list with a platform type`() {
        // System.getenv().keys.toList() will be of type List<String!>.
        val code = """val testSequence = System.getenv().keys.toList().orEmpty()"""
        val findings = subject.lintWithContext(env, code)
        assertThat(findings).singleElement()
            .hasMessage("Remove redundant call to orEmpty")
    }

    @Test
    fun `does not report when calling orEmpty on a nullable list`() {
        val code = """
            val testList: List<String>? = listOf("string")
            val nonNullableTestList = testList.orEmpty()
        """.trimIndent()
        assertThat(subject.lintWithContext(env, code)).isEmpty()
    }

    @Test
    fun `reports when calling isNullOrBlank on a string with a safe call`() {
        val code = """val testString = ""?.isNullOrBlank()"""
        val findings = subject.lintWithContext(env, code)
        assertThat(findings).singleElement()
            .hasMessage("Replace isNullOrBlank with isBlank")
    }

    @Test
    fun `does not report when calling isNullOrBlank on a nullable string`() {
        val code = """
            val testString: String? = ""
            val nonNullableTestString = testString.isNullOrBlank()
        """.trimIndent()
        assertThat(subject.lintWithContext(env, code)).isEmpty()
    }

    @Test
    fun `reports when calling isNullOrEmpty on a string`() {
        val code = """val testString = "".isNullOrEmpty()"""
        val findings = subject.lintWithContext(env, code)
        assertThat(findings).singleElement()
            .hasMessage("Replace isNullOrEmpty with isEmpty")
    }

    @Test
    fun `reports when calling isNullOrEmpty on a string with a safe call`() {
        val code = """val testString = ""?.isNullOrEmpty()"""
        assertThat(subject.lintWithContext(env, code)).hasSize(1)
    }

    @Test
    fun `does not report when calling isNullOrEmpty on a nullable string`() {
        val code = """
            val testString: String? = ""
            val nonNullableTestString = testString.isNullOrEmpty()
        """.trimIndent()
        assertThat(subject.lintWithContext(env, code)).isEmpty()
    }

    @Test
    fun `reports when calling orEmpty on a string`() {
        val code = """val testString = "".orEmpty()"""
        val findings = subject.lintWithContext(env, code)
        assertThat(findings).singleElement()
            .hasMessage("Remove redundant call to orEmpty")
    }

    @Test
    fun `does not report when calling orEmpty on a nullable string`() {
        val code = """
            val testString: String? = ""
            val nonNullableTestString = testString.orEmpty()
        """.trimIndent()
        assertThat(subject.lintWithContext(env, code)).isEmpty()
    }

    @Test
    fun `reports when calling orEmpty on a sequence`() {
        val code = """val testSequence = listOf(1).asSequence().orEmpty()"""
        val findings = subject.lintWithContext(env, code)
        assertThat(findings).singleElement()
            .hasMessage("Remove redundant call to orEmpty")
    }

    @Test
    fun `does not report when calling orEmpty on a nullable sequence`() {
        val code = """
            val testSequence: Sequence<Int>? = listOf(1).asSequence()
            val nonNullableTestSequence = testSequence.orEmpty()
        """.trimIndent()
        assertThat(subject.lintWithContext(env, code)).isEmpty()
    }

    @Test
    fun `only reports on a Kotlin list`() {
        val code = """
            fun String.orEmpty(): List<Char> = this.toCharArray().asList()
            
            val noList = "str".orEmpty()
            val list = listOf(1, 2, 3).orEmpty()
        """.trimIndent()
        val findings = subject.lintWithContext(env, code)
        assertThat(findings).singleElement()
            .hasMessage("Remove redundant call to orEmpty")
    }

    @Test
    fun `reports when calling listOfNotNull on all non-nullable arguments`() {
        val code = """
            val strings = listOfNotNull("string")
        """.trimIndent()
        val findings = subject.lintWithContext(env, code)
        assertThat(findings).singleElement()
            .hasMessage("Replace listOfNotNull with listOf")
    }

    @Test
    fun `reports when calling listOfNotNull with no arguments`() {
        val code = """
            val strings = listOfNotNull<String>()
        """.trimIndent()
        val findings = subject.lintWithContext(env, code)
        assertThat(findings).singleElement()
            .hasMessage("Replace listOfNotNull with listOf")
    }

    @Test
    fun `does not report when calling listOfNotNull on at least one nullable argument`() {
        val code = """
            val strings = listOfNotNull("string", null)
        """.trimIndent()
        val findings = subject.lintWithContext(env, code)
        assertThat(findings).isEmpty()
    }

    @Test
    fun `does not report when calling listOfNotNull with spread operator`() {
        val code = """
            val nullableArray = arrayOf("string", null)
            val strings = listOfNotNull(*nullableArray)
        """.trimIndent()
        val findings = subject.lintWithContext(env, code)
        assertThat(findings).isEmpty()
    }

    @Test
    fun `reports when calling listOfNotNull with spread operator on all non-nullable arguments`() {
        val code = """
            val nonNullableArray = arrayOf("string", "bar")
            val strings = listOfNotNull(*nonNullableArray)
        """.trimIndent()
        val findings = subject.lintWithContext(env, code)
        assertThat(findings).singleElement()
            .hasMessage("Replace listOfNotNull with listOf")
    }

    @Test
    fun `does not report when calling listOfNotNull with a mix of null spread and non-null non-spread`() {
        val code = """
            val nullableArray = arrayOf("string", null)
            val nonNullableArray = arrayOf("string", "bar")
            val strings = listOfNotNull("string", *nonNullableArray, "foo", *nullableArray)
        """.trimIndent()
        val findings = subject.lintWithContext(env, code)
        assertThat(findings).isEmpty()
    }

    @Test
    fun `does not report when calling listOfNotNull with a mix of non-null spread and null non-spread`() {
        val code = """
            val nonNullableArray = arrayOf("string", "bar")
            val strings = listOfNotNull("string", *nonNullableArray, null)
        """.trimIndent()
        val findings = subject.lintWithContext(env, code)
        assertThat(findings).isEmpty()
    }

    @Test
    fun `reports when calling listOfNotNull with a mix of spread and non-spread, all non-null`() {
        val code = """
            val nonNullableArray = arrayOf("string", "bar")
            val otherNonNullableArray = arrayOf("foobar")
            val strings = listOfNotNull("string", *nonNullableArray, "foo", *otherNonNullableArray)
        """.trimIndent()
        val findings = subject.lintWithContext(env, code)
        assertThat(findings).singleElement()
            .hasMessage("Replace listOfNotNull with listOf")
    }

    @Test
    fun `does not report when calling custom function named listOfNotNull on all non-nullable arguments`() {
        val code = """
            fun <T : Any> listOfNotNull(vararg elements: T?): List<T> = TODO()
            
            val strings = listOfNotNull("string", null)
        """.trimIndent()
        val findings = subject.lintWithContext(env, code)
        assertThat(findings).isEmpty()
    }

    @Test
    fun `does not report when calling listOfNotNull with values whose type is unknown`() {
        val code = """
            fun test() {
                listOfNotNull(unknown)
            }
        """.trimIndent()

        val findings = subject.lintWithContext(env, code, allowCompilationErrors = true)
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

        val findings = subject.lintWithContext(env, code, allowCompilationErrors = true)
        assertThat(findings).isEmpty()
    }

    @Test
    fun `reports when calling isNullOrEmpty on a list`() {
        val code = """
            fun test(list: List<Int>) {
                list.isNullOrEmpty()
            }
        """.trimIndent()
        val findings = subject.lintWithContext(env, code)
        assertThat(findings).singleElement()
            .hasMessage("Replace isNullOrEmpty with isEmpty")
    }

    @Test
    fun `reports when calling setOfNotNull on all non-nullable arguments`() {
        val code = """
            val strings = setOfNotNull("string")
        """.trimIndent()
        val findings = subject.lintWithContext(env, code)
        assertThat(findings).singleElement()
            .hasMessage("Replace setOfNotNull with setOf")
    }

    @Test
    fun `does not report when calling isNullOrBlank on flexible string`() {
        val code = """
            val flexibleString = System.getProperty("propertyName")
            val isFlexibleStringNullOrBlank = flexibleString.isNullOrBlank()
        """.trimIndent()
        assertThat(subject.lintWithContext(env, code)).isEmpty()
    }
}
