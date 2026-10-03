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
    inner class `exceptions with standard constructors - no report` {

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
        fun `non-standard primary but standard secondary constructor`() {
            val code = """
                class FileException(val line: Int) : RuntimeException() {
                    constructor() : this(0)
                }
            """.trimIndent()
            assertThat(subject.lintWithContext(env, code)).isEmpty()
        }
    }

    @Nested
    inner class `exceptions implementing StackTraceRecoverable - no report` {

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
    }

    @Nested
    inner class `exceptions without standard constructors - report` {

        @Test
        fun `subclass cause parameter is not a standard cause`() {
            val code = "class NetworkException(cause: IllegalStateException) : RuntimeException()"
            assertThat(subject.lintWithContext(env, code)).hasSize(1)
        }

        @Test
        fun `custom property in primary constructor`() {
            val code = "class NetworkException(val errorCode: Int) : RuntimeException()"
            val findings = subject.lintWithContext(env, code)
            assertThat(findings).hasSize(1)
            assertThat(findings.first()).hasTextLocation("NetworkException")
        }

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
        fun `only non-standard secondary constructors`() {
            val code = """
                class NetworkException : RuntimeException {
                    constructor(errorCode: Int) : super()
                }
            """.trimIndent()
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
        fun `default parameter values do not make a constructor standard`() {
            val code = "class NetworkException(message: String = \"\", errorCode: Int = 0) : RuntimeException()"
            assertThat(subject.lintWithContext(env, code)).hasSize(1)
        }

        @Test
        fun `reports the message for the flagged exception`() {
            val code = "class NetworkException(val errorCode: Int) : RuntimeException()"
            assertThat(subject.lintWithContext(env, code).first()).hasMessage(
                "The exception `NetworkException` cannot be recovered by the coroutine stack trace recovery " +
                    "mechanism. Add a constructor taking only a message, a cause, both or no arguments, or " +
                    "implement `kotlin.coroutines.debug.StackTraceRecoverable`."
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

        @Test
        fun `reports the base class but not the constructor-less subclass`() {
            val code = """
                open class BaseException(val code: Int) : RuntimeException()

                class TimeoutException : BaseException(42)
            """.trimIndent()
            val findings = subject.lintWithContext(env, code)
            assertThat(findings).hasSize(1)
            assertThat(findings.first()).hasStartSourceLocation(line = 1, column = 12) // start of `BaseException`
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
