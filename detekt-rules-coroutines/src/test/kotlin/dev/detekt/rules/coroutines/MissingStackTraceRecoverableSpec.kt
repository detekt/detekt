package dev.detekt.rules.coroutines

import dev.detekt.api.Config
import dev.detekt.test.assertj.assertThat
import dev.detekt.test.junit.KotlinCoreEnvironmentTest
import dev.detekt.test.lintWithContext
import dev.detekt.test.utils.KotlinEnvironmentContainer
import org.jetbrains.kotlin.config.AnalysisFlags
import org.jetbrains.kotlin.config.LanguageVersionSettingsImpl
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

@KotlinCoreEnvironmentTest
class MissingStackTraceRecoverableSpec(private val env: KotlinEnvironmentContainer) {

    private val subject = MissingStackTraceRecoverable(Config.empty)

    @Nested
    inner class `recoverable exceptions - no report` {

        @Test
        fun `no-arg primary constructor`() {
            val code = "class TimeoutException : RuntimeException()"
            assertThat(subject.lintWithContext(env, code)).isEmpty()
        }

        @Test
        fun `explicit empty primary constructor`() {
            val code = "class TimeoutException() : RuntimeException()"
            assertThat(subject.lintWithContext(env, code)).isEmpty()
        }

        @Test
        fun `message-only primary constructor`() {
            val code = "class TimeoutException(message: String) : RuntimeException(message)"
            assertThat(subject.lintWithContext(env, code)).isEmpty()
        }

        @Test
        fun `nullable message primary constructor`() {
            val code = "class TimeoutException(message: String?) : RuntimeException(message)"
            assertThat(subject.lintWithContext(env, code)).isEmpty()
        }

        @Test
        fun `cause-only primary constructor`() {
            val code = "class TimeoutException(cause: Throwable) : RuntimeException(cause)"
            assertThat(subject.lintWithContext(env, code)).isEmpty()
        }

        @Test
        fun `nullable cause primary constructor`() {
            val code = "class TimeoutException(cause: Throwable?) : RuntimeException(cause)"
            assertThat(subject.lintWithContext(env, code)).isEmpty()
        }

        @Test
        fun `message and cause primary constructor`() {
            val code = "class TimeoutException(message: String, cause: Throwable?) : RuntimeException(message, cause)"
            assertThat(subject.lintWithContext(env, code)).isEmpty()
        }

        @Test
        fun `typealias message parameter`() {
            val code = """
                typealias Message = String

                class TimeoutException(message: Message) : RuntimeException(message)
            """.trimIndent()
            assertThat(subject.lintWithContext(env, code)).isEmpty()
        }

        @Test
        fun `standard secondary constructor without primary constructor`() {
            val code = """
                class TimeoutException : RuntimeException {
                    constructor(message: String) : super(message)
                }
            """.trimIndent()
            assertThat(subject.lintWithContext(env, code)).isEmpty()
        }

        @Test
        fun `all-default primary parameters generate a recoverable no-arg constructor`() {
            val code =
                "class TimeoutException(message: String = \"timeout\", code: Int = 0) : RuntimeException(message)"
            assertThat(subject.lintWithContext(env, code)).isEmpty()
        }

        @Test
        fun `JvmOverloads generates a standard overload`() {
            val code =
                "class TimeoutException @JvmOverloads constructor(message: String, code: Int = 0) : " +
                    "RuntimeException(message)"
            assertThat(subject.lintWithContext(env, code)).isEmpty()
        }

        @Test
        fun `property with custom getter only does not create an instance field`() {
            val code = """
                class TimeoutException(message: String) : RuntimeException(message) {
                    val greeting: String get() = "hello"
                }
            """.trimIndent()
            assertThat(subject.lintWithContext(env, code)).isEmpty()
        }

        @Test
        fun `companion object property does not create an instance field`() {
            val code = """
                class TimeoutException(message: String) : RuntimeException(message) {
                    companion object {
                        val DEFAULT_MESSAGE = "timeout"
                    }
                }
            """.trimIndent()
            assertThat(subject.lintWithContext(env, code)).isEmpty()
        }

        @Test
        fun `directly implementing the interface`() {
            val code = """
                import kotlin.coroutines.debug.StackTraceRecoverable

                class FileEditException(val line: Int) : RuntimeException(), StackTraceRecoverable<FileEditException> {
                    override fun copyForStackTraceRecovery(): FileEditException = FileEditException(line)
                }
            """.trimIndent()
            assertThat(
                subject.lintWithContext(
                    env,
                    code,
                    STACK_TRACE_RECOVERABLE_DEPENDENCY,
                    languageVersionSettings = allowKotlinPackage,
                )
            ).isEmpty()
        }

        @Test
        fun `implementing the interface through a superclass`() {
            val code = """
                import kotlin.coroutines.debug.StackTraceRecoverable

                abstract class RecoverableException(val code: Int) : RuntimeException(),
                    StackTraceRecoverable<RecoverableException> {
                    override fun copyForStackTraceRecovery(): RecoverableException? = null
                }

                class SpecificException(code: Int, message: String) : RecoverableException(code)
            """.trimIndent()
            assertThat(
                subject.lintWithContext(
                    env,
                    code,
                    STACK_TRACE_RECOVERABLE_DEPENDENCY,
                    languageVersionSettings = allowKotlinPackage,
                )
            ).isEmpty()
        }

        @Test
        fun `inner class implementing the interface`() {
            val code = """
                import kotlin.coroutines.debug.StackTraceRecoverable

                class Outer {
                    inner class FileEditException(val line: Int) : RuntimeException(),
                        StackTraceRecoverable<FileEditException> {
                        override fun copyForStackTraceRecovery(): FileEditException = this
                    }
                }
            """.trimIndent()
            assertThat(
                subject.lintWithContext(
                    env,
                    code,
                    STACK_TRACE_RECOVERABLE_DEPENDENCY,
                    languageVersionSettings = allowKotlinPackage,
                )
            ).isEmpty()
        }

        @Test
        fun `interface declaration`() {
            val code = "interface Handler"
            assertThat(subject.lintWithContext(env, code)).isEmpty()
        }
    }

    @Nested
    inner class `non-exceptions - no report` {

        @Test
        fun `plain class with constructor`() {
            val code = "class Config(val retries: Int)"
            assertThat(subject.lintWithContext(env, code)).isEmpty()
        }

        @Test
        fun `class extending a non-exception class`() {
            val code = """
                open class Entity(val id: Int)

                class User(id: Int, val name: String) : Entity(id)
            """.trimIndent()
            assertThat(subject.lintWithContext(env, code)).isEmpty()
        }

        @Test
        fun `class implementing AutoCloseable`() {
            val code = """
                class MyResource(val name: String) : AutoCloseable {
                    override fun close() {}
                }
            """.trimIndent()
            assertThat(subject.lintWithContext(env, code)).isEmpty()
        }

        @Test
        fun `object declaration extending an exception`() {
            val code = "object SingletonException : RuntimeException()"
            assertThat(subject.lintWithContext(env, code)).isEmpty()
        }

        @Test
        fun `anonymous object extending an exception`() {
            val code = "val exception = object : RuntimeException() {}"
            assertThat(subject.lintWithContext(env, code)).isEmpty()
        }
    }

    @Nested
    inner class `exceptions without recoverable constructors - report` {

        @Test
        fun `message followed by additional required parameter`() {
            val code = "class NetworkException(message: String, errorCode: Int) : RuntimeException(message)"
            assertThat(subject.lintWithContext(env, code)).hasSize(1)
        }

        @Test
        fun `wrong parameter order`() {
            val code = "class NetworkException(errorCode: Int, message: String) : RuntimeException(message)"
            assertThat(subject.lintWithContext(env, code)).hasSize(1)
        }

        @Test
        fun `CharSequence message parameter is not a standard message`() {
            val code = "class NetworkException(message: CharSequence) : RuntimeException()"
            assertThat(subject.lintWithContext(env, code)).hasSize(1)
        }

        @Test
        fun `vararg parameter is not a standard constructor`() {
            val code = "class NetworkException(vararg details: String) : RuntimeException()"
            assertThat(subject.lintWithContext(env, code)).hasSize(1)
        }

        @Test
        fun `subclass cause parameter is not a standard cause`() {
            val code = "class NetworkException(cause: IllegalStateException) : RuntimeException()"
            assertThat(subject.lintWithContext(env, code)).hasSize(1)
        }

        @Test
        fun `only non-standard secondary constructors`() {
            val code = """
                class NetworkException : RuntimeException {
                    constructor(errorCode: Int) : super()
                }
            """.trimIndent()
            assertThat(subject.lintWithContext(env, code)).hasSize(1)
        }

        @Test
        fun `more than two constructor parameters`() {
            val code =
                "class NetworkException(message: String, errorCode: Int, retryable: Boolean) : " +
                    "RuntimeException(message)"
            assertThat(subject.lintWithContext(env, code)).hasSize(1)
        }

        @Test
        fun `private constructor is not visible to the recovery`() {
            val code = "class NetworkException private constructor(message: String) : RuntimeException(message)"
            assertThat(subject.lintWithContext(env, code)).hasSize(1)
        }

        @Test
        fun `inner class constructor takes the enclosing instance`() {
            val code = "class Outer { inner class NetworkException(message: String) : RuntimeException(message) }"
            assertThat(subject.lintWithContext(env, code)).hasSize(1)
        }

        @Test
        fun `defaults do not help while a required non-standard parameter remains`() {
            val code = "class NetworkException(message: String = \"\", errorCode: Int) : RuntimeException()"
            assertThat(subject.lintWithContext(env, code)).hasSize(1)
        }

        @Test
        fun `JvmOverloads without a standard overload`() {
            val code =
                "class NetworkException @JvmOverloads constructor(errorCode: Int, message: String = \"\") : " +
                    "RuntimeException(message)"
            assertThat(subject.lintWithContext(env, code)).hasSize(1)
        }

        @Test
        fun `private nested class with implicit private constructor`() {
            val code = "class Outer { private class NetworkException : RuntimeException() }"
            assertThat(subject.lintWithContext(env, code)).hasSize(1)
        }

        @Test
        fun `exception declared several inheritance levels above`() {
            val code = """
                abstract class FirstLevelException(message: String) : Exception(message)

                abstract class SecondLevelException(message: String) : FirstLevelException(message)

                class DiskFailureException(val sector: Long, message: String) : SecondLevelException(message)
            """.trimIndent()
            val findings = subject.lintWithContext(env, code)
            assertThat(findings).hasSize(1)
            assertThat(findings.first()).hasTextLocation("DiskFailureException")
        }

        @Test
        fun `non-standard parameter type is flagged`() {
            val code = "class DataException(val data: Any) : Exception()"
            assertThat(subject.lintWithContext(env, code)).hasSize(1)
        }

        @Test
        fun `reports the constructor reason message`() {
            val code = "class NetworkException(message: String, errorCode: Int) : RuntimeException(message)"
            assertThat(subject.lintWithContext(env, code).first()).hasMessage(
                "The exception `NetworkException` has no public constructor taking no arguments, only a message, " +
                    "only a cause, or a message and a cause, which prevents the coroutine stack trace recovery " +
                    "from copying it. Add such a constructor or implement " +
                    "`kotlin.coroutines.debug.StackTraceRecoverable`."
            )
        }

        @Test
        fun `reports every non-recoverable exception in a file`() {
            val code = """
                class NetworkException(val errorCode: Int) : RuntimeException()

                class DatabaseException(message: String, query: String) : RuntimeException(message)
            """.trimIndent()
            assertThat(subject.lintWithContext(env, code)).hasSize(2)
        }
    }

    @Nested
    inner class `exceptions with instance fields - report` {

        @Test
        fun `custom property in primary constructor`() {
            val code = "class NetworkException(val errorCode: Int) : RuntimeException()"
            val findings = subject.lintWithContext(env, code)
            assertThat(findings).hasSize(1)
            assertThat(findings.first()).hasTextLocation("NetworkException")
        }

        @Test
        fun `val primary constructor parameter prevents recovery even with a standard constructor`() {
            val code = "class NetworkException(val logMessage: String) : Exception(logMessage)"
            assertThat(subject.lintWithContext(env, code)).hasSize(1)
        }

        @Test
        fun `body property with initializer creates an instance field`() {
            val code = """
                class NetworkException(message: String) : RuntimeException(message) {
                    val createdAt: Long = 0L
                }
            """.trimIndent()
            assertThat(subject.lintWithContext(env, code)).hasSize(1)
        }

        @Test
        fun `delegated property creates an instance field`() {
            val code = """
                class NetworkException(message: String) : RuntimeException(message) {
                    val tag: String by lazy { "network" }
                }
            """.trimIndent()
            assertThat(subject.lintWithContext(env, code)).hasSize(1)
        }

        @Test
        fun `standard secondary constructor does not help when the class has fields`() {
            val code = """
                class FileException(val line: Int) : RuntimeException() {
                    constructor() : this(0)
                }
            """.trimIndent()
            assertThat(subject.lintWithContext(env, code)).hasSize(1)
        }

        @Test
        fun `field in the superclass prevents recovery of the subclass`() {
            val code = """
                open class BaseException(val code: Int, message: String) : RuntimeException(message)

                class SubException(message: String) : BaseException(0, message)
            """.trimIndent()
            assertThat(subject.lintWithContext(env, code)).hasSize(2)
        }

        @Test
        fun `reports constructor-less subclass of exception with fields`() {
            val code = """
                open class BaseException(val code: Int) : RuntimeException()

                class TimeoutException : BaseException(42)
            """.trimIndent()
            assertThat(subject.lintWithContext(env, code)).hasSize(2)
        }

        @Test
        fun `reports the fields reason message`() {
            val code = "class NetworkException(val errorCode: Int) : RuntimeException()"
            assertThat(subject.lintWithContext(env, code).first()).hasMessage(
                "The exception `NetworkException` declares instance fields which prevent the coroutine stack " +
                    "trace recovery from copying it. Implement `kotlin.coroutines.debug.StackTraceRecoverable` " +
                    "and override `copyForStackTraceRecovery()`."
            )
        }
    }

    @Nested
    inner class suppression {

        @Test
        fun `suppressed exception is not reported`() {
            val code =
                "@Suppress(\"MissingStackTraceRecoverable\") class NetworkException(val errorCode: Int) : " +
                    "RuntimeException()"
            assertThat(subject.lintWithContext(env, code)).isEmpty()
        }
    }

    private companion object {
        /**
         * A stub of the `StackTraceRecoverable` interface introduced in Kotlin 2.4.20. It is placed in the
         * `kotlin.coroutines.debug` package so the rule finds it under the fully qualified name it checks for.
         * Compiling code into the `kotlin` package requires the `allowKotlinPackage` analysis flag.
         */
        val STACK_TRACE_RECOVERABLE_DEPENDENCY = """
            package kotlin.coroutines.debug

            interface StackTraceRecoverable<T> {
                fun copyForStackTraceRecovery(): T?
            }
        """.trimIndent()

        val allowKotlinPackage = LanguageVersionSettingsImpl(
            languageVersion = LanguageVersionSettingsImpl.DEFAULT.languageVersion,
            apiVersion = LanguageVersionSettingsImpl.DEFAULT.apiVersion,
            analysisFlags = mapOf(AnalysisFlags.allowKotlinPackage to true),
        )
    }
}
