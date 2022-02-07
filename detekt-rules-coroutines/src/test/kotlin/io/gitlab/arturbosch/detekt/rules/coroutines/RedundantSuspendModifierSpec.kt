package io.gitlab.arturbosch.detekt.rules.coroutines

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.rules.KotlinCoreEnvironmentTest
import io.gitlab.arturbosch.detekt.test.compileAndLintWithContext
import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.kotlin.cli.jvm.compiler.KotlinCoreEnvironment
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

@KotlinCoreEnvironmentTest
class RedundantSuspendModifierSpec(val env: KotlinCoreEnvironment) {

    val subject = RedundantSuspendModifier(Config.empty)

    @Nested
    inner class `RedundantSuspendModifier` {

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
                """
            assertThat(subject.compileAndLintWithContext(env, code)).hasSize(1)
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
                """
            assertThat(subject.compileAndLintWithContext(env, code)).isEmpty()
        }

        @Test
        fun `does not report when public function returns expression of platform type`() {
            val code = """
                class RedundantClass {
                    open suspend fun redundantSuspend() {
                        println("hello world")
                    }
                }
                """
            assertThat(subject.compileAndLintWithContext(env, code)).isEmpty()
        }

        @Test
        fun `does not report suspend function without body`() {
            val code = """
                interface SuspendInterface {
                    suspend fun empty()
                }
                """
            assertThat(subject.compileAndLintWithContext(env, code)).isEmpty()
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
                """
            assertThat(subject.compileAndLintWithContext(env, code)).isEmpty()
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
                """
            assertThat(subject.compileAndLintWithContext(env, code)).isEmpty()
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
                """
            assertThat(subject.compileAndLintWithContext(env, code)).isEmpty()
        }
    }
}
