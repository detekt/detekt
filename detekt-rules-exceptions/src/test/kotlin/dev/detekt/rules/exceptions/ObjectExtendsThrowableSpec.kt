package dev.detekt.rules.exceptions

import dev.detekt.api.Config
import dev.detekt.test.KotlinEnvironmentContainer
import dev.detekt.test.junit.KotlinCoreEnvironmentTest
import dev.detekt.test.lintWithContext
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

@KotlinCoreEnvironmentTest
class ObjectExtendsThrowableSpec(val env: KotlinEnvironmentContainer) {

    val subject = ObjectExtendsThrowable(Config.empty)

    @Test
    fun `reports top-level objects that extend Throwable`() {
        val code = """
            object BanException : Throwable()
            object AuthException : RuntimeException()
            object ReportedException : Exception()
            object FatalException : Error()
        """.trimIndent()
        assertThat(subject.lintWithContext(env, code)).hasSize(4)
    }

    @Test
    fun `reports object subtype of sealed class that extends Throwable`() {
        val code = """
            sealed class DomainException : RuntimeException() {
                data class Exception1(val prop1: String, val prop2: Boolean) : DomainException()
                class Exception2 : DomainException()
                object Exception3 : DomainException()
            }
        """.trimIndent()
        assertThat(subject.lintWithContext(env, code)).hasSize(1)
    }

    @Test
    fun `reports object that extends custom exception`() {
        val code = """
            object ObjectCustomException : CustomException("singleton custom exception")
            
            open class CustomException(message: String) : RuntimeException(message)
        """.trimIndent()
        assertThat(subject.lintWithContext(env, code)).hasSize(1)
    }

    @Test
    fun `reports companion objects that extend Throwable`() {
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
        """.trimIndent()
        assertThat(subject.lintWithContext(env, code)).hasSize(4)
    }

    @Test
    fun `does not report objects that do not extend Throwable`() {
        val code = """
            object BanException
            object AuthException : CustomException(message = "Authentication failed!")
            
            sealed class DomainException {
                object Exception1 : DomainException()
                object Exception2 : DomainException()
                object Exception3 : DomainException()
            }
            
            open class CustomException(message: String)
        """.trimIndent()
        assertThat(subject.lintWithContext(env, code)).isEmpty()
    }

    @Test
    fun `does not report companion objects that do not extend Throwable`() {
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
        """.trimIndent()
        assertThat(subject.lintWithContext(env, code)).isEmpty()
    }

    @Test
    fun `does not report non-objects that do extend Throwable`() {
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
        """.trimIndent()
        assertThat(subject.lintWithContext(env, code)).isEmpty()
    }

    @Test
    fun `does not report an anonymous object that extends Throwable`() {
        val code = """
            val exception = object : AbstractCustomException() {}
            
            abstract class AbstractCustomException : RuntimeException()
        """.trimIndent()
        assertThat(subject.lintWithContext(env, code)).isEmpty()
    }
}
