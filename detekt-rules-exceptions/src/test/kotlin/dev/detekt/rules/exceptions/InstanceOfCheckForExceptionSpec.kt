package dev.detekt.rules.exceptions

import dev.detekt.api.Config
import dev.detekt.test.junit.KotlinCoreEnvironmentTest
import dev.detekt.test.lintWithContext
import dev.detekt.test.utils.KotlinEnvironmentContainer
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

@KotlinCoreEnvironmentTest
class InstanceOfCheckForExceptionSpec(private val env: KotlinEnvironmentContainer) {
    val subject = InstanceOfCheckForException(Config.empty)

    private val cancellationExceptionDefinitions = arrayOf(
        """
            package java.concurrent
            import java.lang.IllegalStateException
            
            class CancellationException: IllegalStateException()
        """.trimIndent(),
        """
            package kotlin.coroutines
            
            public actual typealias CancellationException = java.util.concurrent.CancellationException
        """.trimIndent()
    )

    @Test
    fun `has is and as checks`() {
        val code = """
            fun x() {
                try {
                } catch(e: Exception) {
                    if (e is IllegalArgumentException || (e as IllegalArgumentException) != null) {
                        return
                    }
                }
            }
        """.trimIndent()
        assertThat(subject.lintWithContext(env, code)).hasSize(2)
    }

    @Test
    fun `has nested is and as checks`() {
        val code = """
            fun x() {
                try {
                } catch(e: Exception) {
                    if (1 == 1) {
                        val b = e !is IllegalArgumentException || (e as IllegalArgumentException) != null
                    }
                }
            }
        """.trimIndent()
        assertThat(subject.lintWithContext(env, code)).hasSize(2)
    }

    @Test
    fun `has no instance of check`() {
        val code = """
            fun x() {
                try {
                } catch(e: Exception) {
                    val s = ""
                    if (s is String || (s as String) != null) {
                        val other: Exception? = null
                        val b = other !is IllegalArgumentException || (other as IllegalArgumentException) != null
                    }
                }
            }
        """.trimIndent()
        assertThat(subject.lintWithContext(env, code)).isEmpty()
    }

    @Test
    fun `has no checks for the subtype of an exception`() {
        val code = """
            interface I
            
            fun foo() {
                try {
                } catch(e: Exception) {
                    if (e is I || (e as I) != null) {
                    }
                }
            }
        """.trimIndent()
        assertThat(subject.lintWithContext(env, code)).isEmpty()
    }

    @Test
    fun `checks for cancellation exception`() {
        // This is case where instance checking is recommended.
        // Required to work around https://github.com/Kotlin/kotlinx.coroutines/issues/3658

        val code = """
            import kotlinx.coroutines.CancellationException
            import kotlinx.coroutines.currentCoroutineContext
            import kotlinx.coroutines.ensureActive


            suspend fun x() {
                try {
                } catch(e: Exception) {
                    if (e is CancellationException) currentCoroutineContext().ensureActive()
                }
            }
        """.trimIndent()

        assertThat(subject.lintWithContext(env, code, *cancellationExceptionDefinitions)).isEmpty()
    }
}
