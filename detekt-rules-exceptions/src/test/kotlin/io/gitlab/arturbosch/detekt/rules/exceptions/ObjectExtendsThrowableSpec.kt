package io.gitlab.arturbosch.detekt.rules.exceptions

import io.gitlab.arturbosch.detekt.rules.setupKotlinEnvironment
import io.gitlab.arturbosch.detekt.test.compileAndLintWithContext
import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.kotlin.cli.jvm.compiler.KotlinCoreEnvironment
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

class ObjectExtendsThrowableSpec : Spek({
    setupKotlinEnvironment()

    val subject by memoized { ObjectExtendsThrowable() }
    val env: KotlinCoreEnvironment by memoized()

    describe("ObjectExtendsThrowable rule") {

        it("reports objects that extend Throwable") {
            val code = """
            object BanException : Throwable()
            object AuthException : RuntimeException()
            object ReportedException : Exception()
            object FatalException : Error()
            object ObjectCustomException : CustomException("singleton custom exception")

            sealed class DomainException : RuntimeException() {
                data class Exception1(val prop1: String, val prop2: Boolean) : DomainException()
                class Exception2 : DomainException()
                object Exception3 : DomainException()
            }

            open class CustomException(message: String) : RuntimeException(message)
            """
            assertThat(subject.compileAndLintWithContext(env, code)).hasSize(6)
        }

        it("reports companion objects that extend Throwable") {
            val code = """
            class Test1 {
                companion object : Throwable() {
                    const val NAME = "Test 1"
                }
            }

            class Test2 {
                companion object : Exception() {
                    const val NAME = "Test 2"
                }
            }

            class Test3 {
                companion object Named : Error() {
                    const val NAME = "Test 3"
                }
            }

            class Test4 {
                companion object Named : RuntimeException() {
                    const val NAME = "Test 4"
                }
            }
            """
            assertThat(subject.compileAndLintWithContext(env, code)).hasSize(4)
        }

        it("does not report objects that do not extend Throwable") {
            val code = """
            object BanException
            object AuthException : CustomException(message = "Authentication failed!")

            sealed class DomainException {
                object Exception1 : DomainException()
                object Exception2 : DomainException()
                object Exception3 : DomainException()
            }

            open class CustomException(message: String) 
            """
            assertThat(subject.compileAndLintWithContext(env, code)).isEmpty()
        }

        it("does not report companion objects that do not extend Throwable") {
            val code = """
            class Test1 {
                companion object {
                    const val NAME = "Test 1"
                }
            }

            class Test2 {
                companion object Named {
                    const val NAME = "Test 3"
                }
            }
            """
            assertThat(subject.compileAndLintWithContext(env, code)).isEmpty()
        }

        it("does not report non-objects that do extend Throwable") {
            val code = """
            class BanException : Throwable()
            data class AuthException(val code: Int) : RuntimeException()
            class ReportedException : Exception()
            class FatalException : Error()
            class ObjectCustomException : CustomException("singleton custom exception")

            sealed class DomainException : RuntimeException() {
                data class Exception1(val prop1: String, val prop2: Boolean) : DomainException()
                class Exception2 : DomainException()
                class Exception3 : DomainException()
            }

            open class CustomException(message: String) 
            """
            assertThat(subject.compileAndLintWithContext(env, code)).isEmpty()
        }

        it("does not report an anonymous object that extends Throwable") {
            val code = """
            val exception = object : AbstractCustomException() {}

            abstract class AbstractCustomException : RuntimeException()
            """
            assertThat(subject.compileAndLintWithContext(env, code)).isEmpty()
        }
    }
})
