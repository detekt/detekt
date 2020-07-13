package io.gitlab.arturbosch.detekt.rules.coroutines

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.rules.setupKotlinEnvironment
import io.gitlab.arturbosch.detekt.test.compileAndLintWithContext
import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.kotlin.cli.jvm.compiler.KotlinCoreEnvironment
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

object RedundantSuspendModifierSpec : Spek({
    setupKotlinEnvironment()

    val env: KotlinCoreEnvironment by memoized()
    val subject by memoized { RedundantSuspendModifier(Config.empty) }

    describe("RedundantSuspendModifier") {

        it("reports when public function returns expression of platform type") {
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

        it("does not report when private") {
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

        it("does not report when public function returns expression of platform type") {
            val code = """
                class RedundantClass {
                    open suspend fun redundantSuspend() {
                        println("hello world")
                    }
                }
                """
            assertThat(subject.compileAndLintWithContext(env, code)).isEmpty()
        }

        it("ignores when iterator is suspending") {
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

        it("ignores when suspending function used in property delegate") {
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
})
