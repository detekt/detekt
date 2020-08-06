package io.gitlab.arturbosch.detekt.rules.style

import io.gitlab.arturbosch.detekt.rules.setupKotlinEnvironment
import io.gitlab.arturbosch.detekt.test.compileAndLintWithContext
import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.kotlin.cli.jvm.compiler.KotlinCoreEnvironment
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

object UselessCallOnNotNullSpec : Spek({
    setupKotlinEnvironment()

    val env: KotlinCoreEnvironment by memoized()
    val subject by memoized { UselessCallOnNotNull() }

    describe("UselessCallOnNotNull rule") {
        it("reports when calling orEmpty on a list") {
            val code = """val testList = listOf("string").orEmpty()"""
            val findings = subject.compileAndLintWithContext(env, code)
            assertThat(findings).hasSize(1)
            assertThat(findings[0].message).isEqualTo("Remove redundant call to orEmpty")
        }

        it("reports when calling orEmpty on a list with a safe call") {
            val code = """val testList = listOf("string")?.orEmpty()"""
            val findings = subject.compileAndLintWithContext(env, code)
            assertThat(findings).hasSize(1)
            assertThat(findings[0].message).isEqualTo("Remove redundant call to orEmpty")
        }

        it("reports when calling orEmpty on a list in a chain") {
            val code = """val testList = listOf("string").orEmpty().map { }"""
            val findings = subject.compileAndLintWithContext(env, code)
            assertThat(findings).hasSize(1)
            assertThat(findings[0].message).isEqualTo("Remove redundant call to orEmpty")
        }

        it("reports when calling orEmpty on a list with a platform type") {
            // System.getenv().keys.toList() will be of type List<String!>.
            val code = """val testSequence = System.getenv().keys.toList().orEmpty()"""
            val findings = subject.compileAndLintWithContext(env, code)
            assertThat(findings).hasSize(1)
            assertThat(findings[0].message).isEqualTo("Remove redundant call to orEmpty")
        }

        it("does not report when calling orEmpty on a nullable list") {
            val code = """
                val testList: List<String>? = listOf("string")
                val nonNullableTestList = testList.orEmpty()
            """
            assertThat(subject.compileAndLintWithContext(env, code)).isEmpty()
        }

        it("reports when calling isNullOrBlank on a string with a safe call") {
            val code = """val testString = ""?.isNullOrBlank()"""
            val findings = subject.compileAndLintWithContext(env, code)
            assertThat(findings).hasSize(1)
            assertThat(findings[0].message).isEqualTo("Replace isNullOrBlank with isBlank")
        }

        it("does not report when calling isNullOrBlank on a nullable string") {
            val code = """
                val testString: String? = ""
                val nonNullableTestString = testString.isNullOrBlank()
            """
            assertThat(subject.compileAndLintWithContext(env, code)).isEmpty()
        }

        it("reports when calling isNullOrEmpty on a string") {
            val code = """val testString = "".isNullOrEmpty()"""
            val findings = subject.compileAndLintWithContext(env, code)
            assertThat(findings).hasSize(1)
            assertThat(findings[0].message).isEqualTo("Replace isNullOrEmpty with isEmpty")
        }

        it("reports when calling isNullOrEmpty on a string with a safe call") {
            val code = """val testString = ""?.isNullOrEmpty()"""
            assertThat(subject.compileAndLintWithContext(env, code)).hasSize(1)
        }

        it("does not report when calling isNullOrEmpty on a nullable string") {
            val code = """
                val testString: String? = ""
                val nonNullableTestString = testString.isNullOrEmpty()
            """
            assertThat(subject.compileAndLintWithContext(env, code)).isEmpty()
        }

        it("reports when calling orEmpty on a string") {
            val code = """val testString = "".orEmpty()"""
            val findings = subject.compileAndLintWithContext(env, code)
            assertThat(findings).hasSize(1)
            assertThat(findings[0].message).isEqualTo("Remove redundant call to orEmpty")
        }

        it("does not report when calling orEmpty on a nullable string") {
            val code = """
                val testString: String? = ""
                val nonNullableTestString = testString.orEmpty()
            """
            assertThat(subject.compileAndLintWithContext(env, code)).isEmpty()
        }

        it("reports when calling orEmpty on a sequence") {
            val code = """val testSequence = listOf(1).asSequence().orEmpty()"""
            val findings = subject.compileAndLintWithContext(env, code)
            assertThat(findings).hasSize(1)
            assertThat(findings[0].message).isEqualTo("Remove redundant call to orEmpty")
        }

        it("does not report when calling orEmpty on a nullable sequence") {
            val code = """
                val testSequence: Sequence<Int>? = listOf(1).asSequence()                
                val nonNullableTestSequence = testSequence.orEmpty()
            """
            assertThat(subject.compileAndLintWithContext(env, code)).isEmpty()
        }

        it("only reports on a Kotlin list") {
            val code = """
                fun String.orEmpty(): List<Char> = this.toCharArray().asList()

                val noList = "str".orEmpty()
                val list = listOf(1, 2, 3).orEmpty()
            """
            val findings = subject.compileAndLintWithContext(env, code)
            assertThat(findings).hasSize(1)
            assertThat(findings[0].message).isEqualTo("Remove redundant call to orEmpty")
        }

        it("reports when calling listOfNotNull on all non-nullable arguments") {
            val code = """
                val strings = listOfNotNull("string")                
            """
            val findings = subject.compileAndLintWithContext(env, code)
            assertThat(findings).hasSize(1)
            assertThat(findings[0].message).isEqualTo("Replace listOfNotNull with listOf")
        }

        it("reports when calling listOfNotNull with no arguments") {
            val code = """
                val strings = listOfNotNull<String>()                
            """
            val findings = subject.compileAndLintWithContext(env, code)
            assertThat(findings).hasSize(1)
            assertThat(findings[0].message).isEqualTo("Replace listOfNotNull with listOf")
        }

        it("does not report when calling listOfNotNull on at least one nullable argument") {
            val code = """
                val strings = listOfNotNull("string", null)                
            """
            val findings = subject.compileAndLintWithContext(env, code)
            assertThat(findings).isEmpty()
        }

        it("does not report when calling custom function named listOfNotNull on all non-nullable arguments") {
            val code = """
                fun <T : Any> listOfNotNull(vararg elements: T?): List<T> = TODO()

                val strings = listOfNotNull("string", null)                
            """
            val findings = subject.compileAndLintWithContext(env, code)
            assertThat(findings).isEmpty()
        }
    }
})
