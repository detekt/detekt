package io.gitlab.arturbosch.detekt.rules.coroutines

import dev.detekt.api.Config
import dev.detekt.test.lintWithContext
import dev.detekt.test.utils.KotlinCoreEnvironmentTest
import dev.detekt.test.utils.KotlinEnvironmentContainer
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

@KotlinCoreEnvironmentTest
class RedundantSuspendModifierSpec(val env: KotlinEnvironmentContainer) {

    private val subject = RedundantSuspendModifier(Config.empty)

    @Test
    fun `reports when public function returns expression of platform type`() {
        val code = """
            import kotlin.coroutines.Continuation
            import kotlin.coroutines.resume
            import kotlin.coroutines.suspendCoroutine
            
            suspend fun suspendCoroutine() = suspendCoroutine { continuation: Continuation<String> ->
                continuation.resume("string")
            }
            
            class RedundantSuspend {
                suspend fun redundantSuspend() {
                    println("hello world")
                }
            }
        """.trimIndent()
        assertThat(subject.lintWithContext(env, code)).hasSize(1)
    }

    @Test
    fun `does not report when private`() {
        val code = """
            import kotlin.coroutines.Continuation
            import kotlin.coroutines.resume
            import kotlin.coroutines.suspendCoroutine
            
            suspend fun suspendCoroutine() = suspendCoroutine { continuation: Continuation<String> ->
                continuation.resume("string")
            }
            
            suspend fun doesSuspend() {
                suspendCoroutine()
            }
        """.trimIndent()
        assertThat(subject.lintWithContext(env, code)).isEmpty()
    }

    @Test
    fun `does not report when public function returns expression of platform type`() {
        val code = """
            class RedundantClass {
                open suspend fun redundantSuspend() {
                    println("hello world")
                }
            }
        """.trimIndent()
        assertThat(subject.lintWithContext(env, code)).isEmpty()
    }

    @Test
    fun `does not report suspend function without body`() {
        val code = """
            interface SuspendInterface {
                suspend fun empty()
            }
        """.trimIndent()
        assertThat(subject.lintWithContext(env, code)).isEmpty()
    }

    @Test
    fun `does not report overridden suspend function`() {
        val code = """
            interface SuspendInterface {
                suspend fun empty()
            }
            
            class SuspendClass : SuspendInterface {
                override suspend fun empty() {
                    println("hello world")
                }
            }
        """.trimIndent()
        assertThat(subject.lintWithContext(env, code)).isEmpty()
    }

    @Test
    fun `does not report actual suspend function`() {
        val code = """
            expect class Foo {
                suspend fun bar()
            }
            
            actual class Foo {
                actual suspend fun bar() {}
            }
        """.trimIndent()
        assertThat(subject.lintWithContext(env, code, compile = false)).isEmpty()
    }

    @Test
    fun `ignores when iterator is suspending`() {
        val code = """
            class SuspendingIterator {
                suspend operator fun iterator(): Iterator<Any> = iterator { yield("value") }
            }
            
            suspend fun bar() {
                for (x in SuspendingIterator()) {
                    println(x)
                }
            }
        """.trimIndent()
        assertThat(subject.lintWithContext(env, code)).isEmpty()
    }

    @Test
    fun `ignores when suspending function used in property delegate`() {
        val code = """
            class SuspendingIterator {
                suspend operator fun iterator(): Iterator<Any> = iterator { yield("value") }
            }
            
            fun coroutine(block: suspend () -> Unit) {}
            
            suspend fun bar() {
                val lazyValue: String by lazy {
                    coroutine {
                        SuspendingIterator().iterator()
                    }
                    "Hello"
                }
            }
        """.trimIndent()
        assertThat(subject.lintWithContext(env, code)).isEmpty()
    }

    @Test
    fun `does not report when suspend function is called in extension method`() {
        val code = """
            import kotlinx.coroutines.delay
            suspend fun foo() { delay(1000) }
            suspend fun String.bar() {
                foo()
            }
            suspend fun  String.baz() = foo()
        """.trimIndent()
        assertThat(subject.lintWithContext(env, code)).isEmpty()
    }
}
