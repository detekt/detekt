package io.gitlab.arturbosch.detekt.rules.coroutines

import dev.detekt.api.Config
import dev.detekt.api.Finding
import dev.detekt.api.SourceLocation
import dev.detekt.test.lintWithContext
import dev.detekt.test.utils.KotlinCoreEnvironmentTest
import dev.detekt.test.utils.KotlinEnvironmentContainer
import org.assertj.core.api.Assertions.assertThat
import org.intellij.lang.annotations.Language
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import dev.detekt.test.assertThat as assertThatFindings

@KotlinCoreEnvironmentTest
class SuspendFunSwallowedCancellationSpec(private val env: KotlinEnvironmentContainer) {

    private val subject = SuspendFunSwallowedCancellation(Config.empty)

    @Test
    fun `does report if swallowing generic exception in suspend fun`() {
        val code = """
            import kotlinx.coroutines.delay

            suspend fun foo() {
                try {
                    delay(1000L)
                } catch (e: Exception) {
                    // Exception is a super class of CancellationException, so this counts as swallowing
                }
            }
        """.trimIndent()
        assertOneFindingAt(
            code = code,
            start = SourceLocation(line = 6, column = 7), // start of Exception block
            end = SourceLocation(line = 8, column = 6), // end of Exception block
        )
    }

    @Test
    fun `doesnt report if only catching a non-superclass`() {
        val code = """
            import kotlinx.coroutines.delay

            class SomeOtherException : Exception()

            suspend fun foo() {
                try {
                  delay(1000L)
                } catch (e: SomeOtherException) {
                    // handle error
                }
            }
        """.trimIndent()
        assertNoFindings(code)
    }

    @Test
    fun `doesnt report if the try-catch block is not in a suspending context`() {
        val code = """
            import kotlinx.coroutines.CancellationException
            import kotlinx.coroutines.delay

            fun foo() {
                try {
                    Thread.sleep(1000L)
                } catch (e: IllegalStateException) {
                    // this would be flagged if we were in a suspending context
                }
            }
        """.trimIndent()
        assertNoFindings(code)
    }

    @Test
    fun `doesnt report if CancellationException is caught and rethrown immediately`() {
        val code = """
            import kotlinx.coroutines.CancellationException
            import kotlinx.coroutines.delay

            suspend fun foo() {
                try {
                    delay(1000L)
                } catch (e: CancellationException) {
                    throw e
                }
            }
        """.trimIndent()
        assertNoFindings(code)
    }

    @Test
    fun `doesnt report if superclass is caught and rethrown immediately`() {
        val code = """
            import kotlinx.coroutines.delay

            class SomeOtherException : Exception()

            suspend fun foo() {
                try {
                    delay(5_000)
                } catch (e: SomeOtherException) {
                    // handle error
                } catch (e: IllegalStateException) {
                    // superclass of CancellationException, so this is fine 
                    throw e
                } catch (e: Exception) {
                    // handle other error cases
                }
            }
        """.trimIndent()
        assertNoFindings(code)
    }

    @Test
    fun `does report if CancellationException is caught anonymously`() {
        val code = """
            import kotlinx.coroutines.CancellationException
            import kotlinx.coroutines.delay

            suspend fun foo() {
                try {
                    delay(1000L)
                } catch (_: CancellationException) {
                    // do nothing?
                }
            }
        """.trimIndent()
        assertOneFindingAt(
            code = code,
            start = SourceLocation(line = 7, column = 7), // start of CancellationException block
            end = SourceLocation(line = 9, column = 6), // end of CancellationException block
        )
    }

    @Test
    fun `does report if CancellationException is caught but an exception with a shadowed name is rethrown`() {
        val code = """
            import kotlinx.coroutines.CancellationException
            import kotlinx.coroutines.delay

            suspend fun foo() {
                try {
                    delay(1000L)
                } catch (e: CancellationException) {
                    val e = Exception() // shadowed name, not propagating the right thing!
                    throw e
                }
            }
        """.trimIndent()
        assertOneFindingAt(
            code = code,
            start = SourceLocation(line = 7, column = 7), // start of CancellationException block
            end = SourceLocation(line = 10, column = 6), // end of CancellationException block
        )
    }

    @Test
    fun `does report if CancellationException is caught but only rethrown after a suspending call`() {
        val code = """
            import kotlinx.coroutines.CancellationException
            import kotlinx.coroutines.delay

            suspend fun performLongRunningWork() = delay(1_000_000_000L)

            suspend fun foo() {
                try {
                    delay(1000L)
                } catch (e: CancellationException) {
                    performLongRunningWork()
                    throw e
                }
            }
        """.trimIndent()
        assertOneFindingAt(
            code = code,
            start = SourceLocation(line = 9, column = 7), // start of CancellationException block
            end = SourceLocation(line = 12, column = 6), // end of CancellationException block
        )
    }

    @Test
    fun `does report if CancellationException is caught but only rethrown after a non-suspending call`() {
        val code = """
            import java.util.concurrent.TimeUnit
            import kotlinx.coroutines.CancellationException
            import kotlinx.coroutines.delay

            suspend fun foo() {
                try {
                    delay(1000L)
                } catch (e: CancellationException) {
                    TimeUnit.SECONDS.sleep(1_000_000_000L)
                    throw e
                }
            }
        """.trimIndent()
        assertOneFindingAt(
            code = code,
            start = SourceLocation(line = 8, column = 7), // start of CancellationException block
            end = SourceLocation(line = 11, column = 6), // end of CancellationException block
        )
    }

    @Test
    fun `does report if CancellationException is caught but only rethrown after doing some other work`() {
        val code = """
            import kotlinx.coroutines.CancellationException
            import kotlinx.coroutines.delay

            private fun longRunningCalculation(): Int = 1 + 2

            suspend fun foo() {
                try {
                    delay(1000L)
                } catch (e: CancellationException) {
                    val result = longRunningCalculation() 
                    println("Result = " + result)
                    throw e
                }
            }
        """.trimIndent()
        assertOneFindingAt(
            code = code,
            start = SourceLocation(line = 9, column = 7), // start of CancellationException block
            end = SourceLocation(line = 13, column = 6), // end of CancellationException block
        )
    }

    @Test
    fun `does report if CancellationException is caught but not rethrown`() {
        val code = """
            import kotlinx.coroutines.CancellationException
            import kotlinx.coroutines.delay

            suspend fun foo() {
                try {
                    delay(1000L)
                } catch (e: CancellationException) {
                    // Not re-thrown - this gets flagged
                } catch (e: Exception) {
                    // Other error handling
                }
            }
        """.trimIndent()
        assertOneFindingAt(
            code = code,
            start = SourceLocation(line = 7, column = 7), // start of CancellationException block
            end = SourceLocation(line = 9, column = 6), // end of CancellationException block
        )
    }

    @Test
    fun `does report if CancellationException is caught after a supertype exception`() {
        val code = """
            import kotlinx.coroutines.CancellationException
            import kotlinx.coroutines.delay

            suspend fun foo() {
                try {
                    delay(1000L)
                } catch (e: IllegalStateException) {
                    // handle error case
                } catch (e: CancellationException) {
                    // never reached!
                    throw e
                }
            }
        """.trimIndent()
        assertOneFindingAt(
            code = code,
            start = SourceLocation(line = 7, column = 7), // start of IllegalStateException block
            end = SourceLocation(line = 9, column = 6), // end of IllegalStateException block
        )
    }

    @Test
    fun `doesnt report if CancellationException is caught after an unrelated exception type`() {
        val code = """
            import kotlinx.coroutines.CancellationException
            import kotlinx.coroutines.delay

            class SomeOtherException : Exception()

            suspend fun foo() {
                try {
                    delay(1000L)
                } catch (e: SomeOtherException) {
                    // handle error case
                } catch (e: CancellationException) {
                    throw e
                }
            }
        """.trimIndent()
        assertNoFindings(code)
    }

    @Test
    fun `does report if CancellationException is caught but a different exception is re-thrown`() {
        val code = """
            import kotlinx.coroutines.CancellationException
            import kotlinx.coroutines.delay

            class WrapperException(val e: CancellationException) : Exception()

            suspend fun foo() {
                try {
                    delay(1000L)
                } catch (e: CancellationException) {
                    throw WrapperException(e)
                }
            }
        """.trimIndent()
        assertOneFindingAt(
            code = code,
            start = SourceLocation(line = 9, column = 7), // start of CancellationException block
            end = SourceLocation(line = 11, column = 6), // end of CancellationException block
        )
    }

    @Test
    fun `doesn't report if a CancellationException super-class is caught and re-thrown`() {
        val code = """
            import kotlinx.coroutines.delay

            suspend fun foo() {
                try {
                    delay(1000L)
                } catch (e: IllegalStateException) {
                    // IllegalStateException is a superclass of CancellationException
                    throw e
                }
            }
        """.trimIndent()
        assertNoFindings(code)
    }

    @Test
    fun `Does report if catching anonymously from inside launch block`() {
        val code = """
            import kotlinx.coroutines.GlobalScope
            import kotlinx.coroutines.delay
            import kotlinx.coroutines.launch

            fun foo() {
              GlobalScope.launch {
                try {
                   delay(1_000)
                } catch (_: Exception) {
                    // general error handling
                }
              }
            }
        """.trimIndent()
        assertOneFindingAt(
            code = code,
            start = SourceLocation(line = 9, column = 7), // start of Exception block
            end = SourceLocation(line = 11, column = 6), // end of Exception block
        )
    }

    @Test
    fun `does report suspend function call in runCatching`() {
        val code = """
            import kotlinx.coroutines.delay

            suspend fun foo() {
                runCatching {
                    delay(1000L)
                }
            }
        """.trimIndent()
        val findings = subject.lintWithContext(env, code)
        assertFindingsForSuspendCall(
            findings,
            listOf(SourceLocation(4, 5)),
            listOf(SourceLocation(4, 16))
        )
    }

    @Test
    fun `does report for in case of nested runCatching`() {
        val code = """
            import kotlinx.coroutines.delay

            suspend fun bar() = delay(2000)

            suspend fun foo() {
                runCatching {
                    delay(1000L)
                    runCatching { 
                        bar()
                    }
                }
            }
        """.trimIndent()
        val findings = subject.lintWithContext(env, code)
        assertFindingsForSuspendCall(
            findings,
            listOf(SourceLocation(6, 5), SourceLocation(8, 9)),
            listOf(SourceLocation(6, 16), SourceLocation(8, 20))
        )
    }

    @Test
    fun `does report for in case of nested runCatching with suspend fun call in inner runCatching`() {
        val code = """
            import kotlinx.coroutines.MainScope
            import kotlinx.coroutines.delay
            import kotlinx.coroutines.launch

            suspend fun bar() = delay(2000)

            suspend fun foo() {
                runCatching {
                    MainScope().launch { 
                        delay(1000L)
                        runCatching { 
                            bar()
                        }
                    }
                }
            }
        """.trimIndent()
        val findings = subject.lintWithContext(env, code)
        assertFindingsForSuspendCall(
            findings,
            listOf(SourceLocation(11, 13)),
            listOf(SourceLocation(11, 24))
        )
    }

    @Test
    fun `does report for in case of nested runCatching with suspend fun call in outer runCatching`() {
        val code = """
            import kotlinx.coroutines.MainScope
            import kotlinx.coroutines.delay
            import kotlinx.coroutines.launch

            suspend fun bar() = delay(2000)

            suspend fun foo() {
                runCatching {
                    delay(1000L)
                    runCatching {
                        MainScope().launch { 
                            bar()
                        }
                    }
                }
            }
        """.trimIndent()
        val findings = subject.lintWithContext(env, code)
        assertFindingsForSuspendCall(
            findings,
            listOf(SourceLocation(8, 5)),
            listOf(SourceLocation(8, 16))
        )
    }

    @Test
    fun `does report for delay() in suspend functions`() {
        val code = """
            import kotlinx.coroutines.delay

            suspend fun foo() {
                runCatching {
                    delay(1000L)
                }
            }
        """.trimIndent()
        val findings = subject.lintWithContext(env, code)
        assertFindingsForSuspendCall(
            findings,
            listOf(SourceLocation(4, 5)),
            listOf(SourceLocation(4, 16))
        )
    }

    @Test
    fun `does report for coroutineContext in suspend functions`() {
        val code = """
            import kotlinx.coroutines.delay
            import kotlin.coroutines.coroutineContext

            suspend fun foo() {
                runCatching {
                    coroutineContext    
                }
            }
        """.trimIndent()
        val findings = subject.lintWithContext(env, code)
        assertFindingsForSuspendCall(
            findings,
            listOf(SourceLocation(5, 5)),
            listOf(SourceLocation(5, 16))
        )
    }

    @Test
    fun `does not report no suspending function is used inside runBlocking`() {
        val code = """
            @Suppress("RedundantSuspendModifier")
            suspend fun foo() {
                runCatching {
                    Thread.sleep(1000L)
                }
            }
        """.trimIndent()
        val findings = subject.lintWithContext(env, code)
        assertThat(findings).isEmpty()
    }

    @Test
    fun `does report when _when_ is used in result`() {
        val code = """
            import kotlinx.coroutines.delay
            suspend fun bar() = delay(1000L)
            suspend fun foo(): Result<*> {
                val result = runCatching { bar() }
                when(result.isSuccess) {
                    true -> TODO()
                    false -> TODO()
                }
                return result
            }
        """.trimIndent()
        val findings = subject.lintWithContext(env, code)
        assertFindingsForSuspendCall(
            findings,
            listOf(SourceLocation(4, 18)),
            listOf(SourceLocation(4, 29))
        )
    }

    @Test
    fun `does report when onSuccess is used in result`() {
        val code = """
            import kotlinx.coroutines.delay
            suspend fun bar() = delay(1000L)
            suspend fun foo() {
                runCatching { bar() }.onSuccess { 
                    TODO()
                }
            }
        """.trimIndent()
        val findings = subject.lintWithContext(env, code)
        assertFindingsForSuspendCall(
            findings,
            listOf(SourceLocation(4, 5)),
            listOf(SourceLocation(4, 16))
        )
    }

    @Test
    fun `does report when runCatching is used as function expression`() {
        val code = """
            import kotlinx.coroutines.delay
            suspend fun bar() = delay(1000L)
            suspend fun foo() = runCatching { bar() }
        """.trimIndent()
        val findings = subject.lintWithContext(env, code)
        assertFindingsForSuspendCall(
            findings,
            listOf(SourceLocation(3, 21)),
            listOf(SourceLocation(3, 32))
        )
    }

    @Nested
    inner class WithLambda {
        @Test
        fun `does report when suspend fun is called inside inline function`() {
            val code = """
                import kotlinx.coroutines.delay

                suspend fun foo() {
                    runCatching {
                        listOf(1L, 2L, 3L).map {
                            delay(it)
                        }
                    }
                }
            """.trimIndent()

            val findings = subject.lintWithContext(env, code)
            assertFindingsForSuspendCall(
                findings,
                listOf(SourceLocation(4, 5)),
                listOf(SourceLocation(4, 16))
            )
        }

        @Test
        fun `does not report when lambda in non suspending inline function is passed as crossinline`() {
            val code = """
                import kotlinx.coroutines.MainScope
                import kotlinx.coroutines.delay
                import kotlinx.coroutines.launch

                inline fun <R> foo(crossinline block: suspend () -> R) = MainScope().launch {
                    block()
                }
                suspend fun bar() {
                    runCatching {
                        foo {
                            delay(1000L)
                        }
                    }
                }
            """.trimIndent()

            val findings = subject.lintWithContext(env, code)
            assertThat(findings).isEmpty()
        }

        @Test
        fun `does report when lambda in suspend inline function is passed as crossinline`() {
            val code = """
                import kotlinx.coroutines.MainScope
                import kotlinx.coroutines.delay
                import kotlinx.coroutines.launch

                suspend inline fun <R> foo(crossinline block: suspend () -> R) = block()
                suspend fun bar() {
                    runCatching {
                        foo {
                            delay(1000L)
                        }
                    }
                }
            """.trimIndent()

            val findings = subject.lintWithContext(env, code)
            assertFindingsForSuspendCall(
                findings,
                listOf(SourceLocation(7, 5)),
                listOf(SourceLocation(7, 16))
            )
        }

        @Test
        fun `does not report when lambda parameter chain has noinline function call`() {
            val code = """
                import kotlinx.coroutines.MainScope
                import kotlinx.coroutines.delay
                import kotlinx.coroutines.launch

                inline fun <R> inline(block: () -> R) = block()
                fun <R> noInline(block: suspend () -> R) = MainScope().launch { block() }

                suspend fun bar() {
                    runCatching {
                        inline {
                            noInline {
                                inline {
                                    delay(1000)
                                }
                            }
                            
                        }
                    }
                }
            """.trimIndent()

            val findings = subject.lintWithContext(env, code)
            assertThat(findings).isEmpty()
        }

        @Test
        fun `does report when lambda parameter chain has all inlined function call`() {
            val code = """
                import kotlinx.coroutines.MainScope
                import kotlinx.coroutines.delay
                import kotlinx.coroutines.launch

                inline fun <R> inline(block: () -> R) = block()

                suspend fun bar() {
                    runCatching {
                        inline {
                            inline {
                                delay(1000)
                            }
                        }
                    }
                }
            """.trimIndent()

            val findings = subject.lintWithContext(env, code)
            assertFindingsForSuspendCall(
                findings,
                listOf(SourceLocation(8, 5)),
                listOf(SourceLocation(8, 16))
            )
        }

        @Test
        fun `does not report when lambda in non suspending inline function is passed as noinline`() {
            val code = """
                import kotlinx.coroutines.MainScope
                import kotlinx.coroutines.delay
                import kotlinx.coroutines.launch

                inline fun <R> foo(noinline block: suspend () -> R) = MainScope().launch {
                    block()
                }
                suspend fun suspendFun() = delay(1000)
                suspend fun bar() {
                    runCatching {
                        foo {
                            delay(1000L)
                        }

                        val baz = suspend {
                            delay(1000L)
                        }
                        foo(baz)
                        foo(::suspendFun)
                    }
                }
            """.trimIndent()

            val findings = subject.lintWithContext(env, code)
            assertThat(findings).isEmpty()
        }

        @Test
        fun `does report when lambda in suspend inline function is passed as noinline`() {
            val code = """
                import kotlinx.coroutines.delay

                suspend inline fun <R> foo(noinline block: suspend () -> R) = block()
                suspend fun bar() {
                    runCatching {
                        foo {
                            delay(1000L)
                        }
                    }
                }
            """.trimIndent()

            val findings = subject.lintWithContext(env, code)
            assertFindingsForSuspendCall(
                findings,
                listOf(SourceLocation(5, 5)),
                listOf(SourceLocation(5, 16))
            )
        }

        @Test
        fun `does report when suspend fun is called as extension function`() {
            val code = """
                import kotlinx.coroutines.delay
                @Suppress("RedundantSuspendModifier")
                suspend fun List<Long>.await() = delay(this.size.toLong())

                suspend fun foo() {
                    runCatching {
                        listOf(1L, 2L, 3L).await()
                    }
                }
            """.trimIndent()

            val findings = subject.lintWithContext(env, code)
            assertThat(findings).hasSize(1)
            assertFindingsForSuspendCall(
                findings,
                listOf(SourceLocation(6, 5)),
                listOf(SourceLocation(6, 16))
            )
        }

        @Test
        fun `does report when inside inline function with noinline and cross inline parameters in same order`() {
            val code = """
                import kotlinx.coroutines.MainScope
                import kotlinx.coroutines.delay
                import kotlinx.coroutines.launch
                import kotlinx.coroutines.runBlocking

                inline fun foo(
                    noinline noinlineBlock: suspend () -> Unit,
                    inlineBlock: () -> Unit,
                    crossinline crossinlineBlock: suspend () -> Unit,
                ) = inlineBlock().toString() + MainScope().launch {
                    noinlineBlock()
                } + runBlocking {
                    crossinlineBlock()
                }.toString()

                suspend fun bar() {
                    runCatching {
                        foo(
                            noinlineBlock = {
                                delay(2000L)
                            },
                            inlineBlock = { delay(1000L) },
                        ) {

                        }
                    }
                }
            """.trimIndent()

            val findings = subject.lintWithContext(env, code)
            assertFindingsForSuspendCall(
                findings,
                listOf(SourceLocation(17, 5)),
                listOf(SourceLocation(17, 16))
            )
        }

        @Test
        fun `does report when inside inline function with noinline and cross inline parameters not in same order`() {
            val code = """
                import kotlinx.coroutines.MainScope
                import kotlinx.coroutines.delay
                import kotlinx.coroutines.launch
                import kotlinx.coroutines.runBlocking

                inline fun foo(
                    noinline noinlineBlock: suspend () -> Unit,
                    inlineBlock: () -> Unit,
                    crossinline crossinlineBlock: suspend () -> Unit,
                ) = inlineBlock().toString() + MainScope().launch {
                    noinlineBlock()
                } + runBlocking {
                    crossinlineBlock()
                }.toString()

                suspend fun bar() {
                    runCatching {
                        foo(
                            inlineBlock = { delay(1000L) },
                            noinlineBlock = {
                                delay(2000L)
                            },
                        ) {

                        }
                    }
                }
            """.trimIndent()

            val findings = subject.lintWithContext(env, code)
            assertFindingsForSuspendCall(
                findings,
                listOf(SourceLocation(17, 5)),
                listOf(SourceLocation(17, 16))
            )
        }
    }

    @Test
    fun `does report when suspend fun in for subject is used`() {
        val code = """
            @Suppress("RedundantSuspendModifier")
            suspend fun bar() = 10
            
            suspend fun foo() {
                runCatching {
                    for (i in 1..bar()) {
                        println(i)
                    }
                }
            }
        """.trimIndent()

        val findings = subject.lintWithContext(env, code)
        assertFindingsForSuspendCall(
            findings,
            listOf(SourceLocation(5, 5)),
            listOf(SourceLocation(5, 16))
        )
    }

    @Test
    fun `does report in case of suspendCancellableCoroutine`() {
        val code = """
            import kotlinx.coroutines.delay
            import kotlinx.coroutines.suspendCancellableCoroutine
            import kotlin.coroutines.resume

            suspend fun foo() {
                runCatching {
                    suspendCancellableCoroutine {
                        it.resume(Unit)
                    }
                }
            }
        """.trimIndent()

        val findings = subject.lintWithContext(env, code)
        assertFindingsForSuspendCall(
            findings,
            listOf(SourceLocation(6, 5)),
            listOf(SourceLocation(6, 16))
        )
    }

    @Test
    fun `does report in case suspend callable reference is invoked`() {
        val code = """
            import kotlinx.coroutines.delay

            suspend fun bar() = delay(1000)
            suspend fun foo() {
                runCatching {
                    ::bar.invoke()
                }
            }
        """.trimIndent()

        val findings = subject.lintWithContext(env, code)
        assertFindingsForSuspendCall(
            findings,
            listOf(SourceLocation(5, 5)),
            listOf(SourceLocation(5, 16))
        )
    }

    @Test
    fun `does report in case suspend local function is invoked`() {
        val code = """
            import kotlinx.coroutines.delay

            suspend fun foo() {
                suspend fun localFun() = delay(1000L)
                runCatching {
                    localFun()
                }
            }
        """.trimIndent()

        val findings = subject.lintWithContext(env, code)
        assertFindingsForSuspendCall(
            findings,
            listOf(SourceLocation(5, 5)),
            listOf(SourceLocation(5, 16))
        )
    }

    @Test
    fun `does not report coroutine is launched`() {
        val code = """
            import kotlinx.coroutines.delay
            import kotlinx.coroutines.MainScope
            import kotlinx.coroutines.launch

            suspend fun foo() {
                runCatching {
                    MainScope().launch {
                        delay(1000L)
                    }
                }
            }
        """.trimIndent()

        val findings = subject.lintWithContext(env, code)
        assertThat(findings).isEmpty()
    }

    @Test
    fun `does report when job is joined`() {
        val code = """
            import kotlinx.coroutines.delay
            import kotlinx.coroutines.MainScope
            import kotlinx.coroutines.launch

            suspend fun foo() {
                runCatching {
                    MainScope().launch {
                        delay(1000L)
                    }.join()
                }
            }
        """.trimIndent()

        val findings = subject.lintWithContext(env, code)
        assertFindingsForSuspendCall(
            findings,
            listOf(SourceLocation(6, 5)),
            listOf(SourceLocation(6, 16))
        )
    }

    @Test
    fun `does not report async is used`() {
        val code = """
            import kotlinx.coroutines.async
            import kotlinx.coroutines.delay
            import kotlinx.coroutines.MainScope

            suspend fun foo() {
                runCatching {
                    MainScope().async {
                        delay(1000L)
                    }
                }
            }
        """.trimIndent()

        val findings = subject.lintWithContext(env, code)
        assertThat(findings).isEmpty()
    }

    @Test
    fun `does report async is awaited`() {
        val code = """
            import kotlinx.coroutines.async
            import kotlinx.coroutines.delay
            import kotlinx.coroutines.MainScope

            suspend fun foo() {
                runCatching {
                    @Suppress("RedundantAsync")
                    MainScope().async {
                        delay(1000L)
                    }.await()
                }
            }
        """.trimIndent()

        val findings = subject.lintWithContext(env, code)
        assertFindingsForSuspendCall(
            findings,
            listOf(SourceLocation(6, 5)),
            listOf(SourceLocation(6, 16))
        )
    }

    @Test
    fun `does not report when suspend fun is called inside runBlocking`() {
        val code = """
            import kotlinx.coroutines.delay
            import kotlinx.coroutines.runBlocking

            suspend fun foo() {
                runCatching {
                    runBlocking {
                        delay(1000L)
                    }
                }
            }
        """.trimIndent()

        val findings = subject.lintWithContext(env, code)
        assertThat(findings).isEmpty()
    }

    @Test
    fun `does report when suspend fun is called in string interpolation`() {
        val code = """
            import kotlinx.coroutines.delay
            @Suppress("RedundantSuspendModifier")
            suspend fun foo() {
                runCatching {
                    val string = "${'$'}{delay(1000)}"
                }
            }
        """.trimIndent()

        val findings = subject.lintWithContext(env, code)
        assertFindingsForSuspendCall(
            findings,
            listOf(SourceLocation(4, 5)),
            listOf(SourceLocation(4, 16))
        )
    }

    @Nested
    inner class WithOperators {
        @Test
        fun `does report in case of suspend invoked operator`() {
            val code = """
                import kotlinx.coroutines.delay

                class C {
                    suspend operator fun invoke() = delay(1000L)
                }
                
                suspend fun foo() {
                    runCatching {
                        C()()
                        C().invoke()
                    }
                }
            """.trimIndent()

            val findings = subject.lintWithContext(env, code)
            assertFindingsForSuspendCall(
                findings,
                listOf(SourceLocation(8, 5)),
                listOf(SourceLocation(8, 16))
            )
        }

        @Test
        fun `does report in case of suspend inc and dec operator`() {
            val code = """
                @Suppress("RedundantSuspendModifier")
                class OperatorClass {
                    suspend operator fun inc(): OperatorClass = OperatorClass()
                    suspend operator fun dec(): OperatorClass = OperatorClass()
                }
                
                suspend fun foo() {
                    runCatching {
                        var operatorClass = OperatorClass()
                        operatorClass++
                    }
                    runCatching {
                        var operatorClass = OperatorClass()
                        operatorClass--
                    }
                }
            """.trimIndent()

            val findings = subject.lintWithContext(env, code)
            assertFindingsForSuspendCall(
                findings,
                listOf(SourceLocation(8, 5), SourceLocation(12, 5)),
                listOf(SourceLocation(8, 16), SourceLocation(12, 16))
            )
        }

        @Test
        fun `does report in case of suspend inc and dec operators called as function`() {
            val code = """
                @Suppress("RedundantSuspendModifier")
                class OperatorClass {
                    suspend operator fun inc(): OperatorClass = OperatorClass()
                    suspend operator fun dec(): OperatorClass = OperatorClass()
                }
                
                suspend fun foo() {
                    runCatching {
                        val operatorClass = OperatorClass()
                        operatorClass.inc()
                    }
                    runCatching {
                        val operatorClass = OperatorClass()
                        operatorClass.dec()
                    }
                }
            """.trimIndent()

            val findings = subject.lintWithContext(env, code)
            assertFindingsForSuspendCall(
                findings,
                listOf(SourceLocation(8, 5), SourceLocation(12, 5)),
                listOf(SourceLocation(8, 16), SourceLocation(12, 16))
            )
        }

        @Test
        fun `does report in case of suspend not operator`() {
            val code = """
                class OperatorClass {
                    @Suppress("RedundantSuspendModifier")
                    suspend operator fun not() = false
                }
                
                suspend fun foo() {
                    runCatching {
                        val operatorClass = OperatorClass()
                        println(!operatorClass)
                    }
                }
            """.trimIndent()

            val findings = subject.lintWithContext(env, code)
            assertFindingsForSuspendCall(
                findings,
                listOf(SourceLocation(7, 5)),
                listOf(SourceLocation(7, 16))
            )
        }

        @Test
        fun `does report in case of suspend unaryPlus operator`() {
            val code = """
                class OperatorClass {
                    @Suppress("RedundantSuspendModifier")
                    suspend operator fun unaryPlus() = OperatorClass()
                }
                
                suspend fun foo() {
                    runCatching {
                        val operatorClass = OperatorClass()
                        println(+operatorClass)
                    }
                }
            """.trimIndent()

            val findings = subject.lintWithContext(env, code)
            assertFindingsForSuspendCall(
                findings,
                listOf(SourceLocation(7, 5)),
                listOf(SourceLocation(7, 16))
            )
        }

        @Test
        fun `does report in case of suspend plus operator`() {
            val code = """
                class OperatorClass {
                    @Suppress("RedundantSuspendModifier")
                    suspend operator fun plus(test: OperatorClass) = test
                }
                
                suspend fun foo() {
                    runCatching {
                        val operatorClass1 = OperatorClass()
                        val operatorClass2 = OperatorClass()
                        println(operatorClass1 + operatorClass2)
                    }
                }
            """.trimIndent()

            val findings = subject.lintWithContext(env, code)
            assertFindingsForSuspendCall(
                findings,
                listOf(SourceLocation(7, 5)),
                listOf(SourceLocation(7, 16))
            )
        }

        @Test
        fun `does report in case of suspend div operator`() {
            val code = """
                class OperatorClass {
                    @Suppress("RedundantSuspendModifier")
                    suspend operator fun div(value: Int) = OperatorClass()
                }
                
                suspend fun foo() {
                    runCatching {
                        val operatorClass = OperatorClass()
                        println(operatorClass / 2)
                    }
                }
            """.trimIndent()

            val findings = subject.lintWithContext(env, code)
            assertFindingsForSuspendCall(
                findings,
                listOf(SourceLocation(7, 5)),
                listOf(SourceLocation(7, 16))
            )
        }

        @Test
        fun `does report in case of suspend compareTo operator`() {
            val code = """
                class OperatorClass {
                    @Suppress("RedundantSuspendModifier")
                    suspend operator fun compareTo(operatorClass: OperatorClass) = 0
                }
                
                suspend fun foo() {
                    runCatching {
                        val operatorClass1 = OperatorClass()
                        val operatorClass2 = OperatorClass()
                        println(operatorClass1 < operatorClass2)
                    }
                }
            """.trimIndent()

            val findings = subject.lintWithContext(env, code)
            assertFindingsForSuspendCall(
                findings,
                listOf(SourceLocation(7, 5)),
                listOf(SourceLocation(7, 16))
            )
        }

        @Test
        fun `does report in case of suspend plusAssign operator`() {
            val code = """
                class OperatorClass {
                    @Suppress("RedundantSuspendModifier")
                    suspend operator fun plusAssign(operatorClass: OperatorClass) { }
                }
                
                suspend fun foo() {
                    runCatching {
                        val operatorClass1 = OperatorClass()
                        val operatorClass2 = OperatorClass()
                        operatorClass1 += (operatorClass2)
                    }
                }
            """.trimIndent()

            val findings = subject.lintWithContext(env, code)
            assertFindingsForSuspendCall(
                findings,
                listOf(SourceLocation(7, 5)),
                listOf(SourceLocation(7, 16))
            )
        }

        @Test
        fun `does report in case of suspend plus called as plusAssign operator`() {
            val code = """
                class C
                @Suppress("RedundantSuspendModifier")
                suspend operator fun C.plus(i: Int): C = TODO()

                suspend fun f() {
                    runCatching {
                        var x = C()
                        x += 1
                    }
                    
                }
            """.trimIndent()

            val findings = subject.lintWithContext(env, code)
            assertFindingsForSuspendCall(
                findings,
                listOf(SourceLocation(6, 5)),
                listOf(SourceLocation(6, 16))
            )
        }

        @Test
        fun `does report in case of suspend plus called as an infix function`() {
            val code = """
                class C
                @Suppress("RedundantSuspendModifier")
                suspend infix operator fun C.plus(i: Int): C = TODO()

                suspend fun f() {
                    runCatching {
                        var x = C()
                        x = x plus 1
                    }
                    
                }
            """.trimIndent()

            val findings = subject.lintWithContext(env, code)
            assertFindingsForSuspendCall(
                findings,
                listOf(SourceLocation(6, 5)),
                listOf(SourceLocation(6, 16))
            )
        }

        @Test
        fun `does report in case of suspend divAssign operator`() {
            val code = """
                import kotlinx.coroutines.delay
                class OperatorClass {
                    suspend operator fun divAssign(operatorClass: OperatorClass) {
                        delay(1000)
                    }
                }
                
                suspend fun foo() {
                    runCatching {
                        val operatorClass1 = OperatorClass()
                        val operatorClass2 = OperatorClass()
                        operatorClass1 /= (operatorClass2)
                    }
                }
            """.trimIndent()

            val findings = subject.lintWithContext(env, code)
            assertFindingsForSuspendCall(
                findings,
                listOf(SourceLocation(9, 5)),
                listOf(SourceLocation(9, 16))
            )
        }

        @Test
        fun `does report in case of suspend rangeTo operator`() {
            val code = """
                class OperatorClass {
                    @Suppress("RedundantSuspendModifier")
                    suspend operator fun rangeTo(operatorClass: OperatorClass) = OperatorClass()
                }
                
                suspend fun foo() {
                    runCatching {
                        val operatorClass1 = OperatorClass()
                        val operatorClass2 = OperatorClass()
                        println(operatorClass1..operatorClass2)
                    }
                }
            """.trimIndent()

            val findings = subject.lintWithContext(env, code)
            assertFindingsForSuspendCall(
                findings,
                listOf(SourceLocation(7, 5)),
                listOf(SourceLocation(7, 16))
            )
        }

        @Test
        fun `does report in case of suspend times operator`() {
            val code = """
                class OperatorClass {
                    @Suppress("RedundantSuspendModifier")
                    suspend operator fun times(operatorClass: OperatorClass) = OperatorClass()
                }
                
                suspend fun foo() {
                    runCatching {
                        val operatorClass1 = OperatorClass()
                        val operatorClass2 = OperatorClass()
                        println(operatorClass1 * operatorClass2)
                    }
                }
            """.trimIndent()

            val findings = subject.lintWithContext(env, code)
            assertFindingsForSuspendCall(
                findings,
                listOf(SourceLocation(7, 5)),
                listOf(SourceLocation(7, 16))
            )
        }

        @Nested
        inner class WithSuspendingIterator {

            @Test
            fun `does report when suspending iterator is used`() {
                val code = """
                    import kotlinx.coroutines.delay

                    class SuspendingIterator {
                        suspend operator fun iterator(): Iterator<Any> = iterator { yield("value") }
                    }

                    suspend fun bar() {
                        runCatching { 
                            for (x in SuspendingIterator()) {
                                println(x)
                            }
                        }
                    }
                """.trimIndent()

                val findings = subject.lintWithContext(env, code)
                assertFindingsForSuspendCall(
                    findings,
                    listOf(SourceLocation(8, 5)),
                    listOf(SourceLocation(8, 16))
                )
            }

            @Test
            fun `does report when nested suspending iterator is used`() {
                val code = """
                    import kotlinx.coroutines.delay

                    class SuspendingIterator {
                        suspend operator fun iterator(): Iterator<String> = iterator { yield("value") }
                    }

                    suspend fun bar() {
                        runCatching { 
                            for (x in SuspendingIterator()) {
                                for (y in SuspendingIterator()) {
                                    println(x + y)
                                }
                            }
                        }
                    }
                """.trimIndent()

                val findings = subject.lintWithContext(env, code)
                assertFindingsForSuspendCall(
                    findings,
                    listOf(SourceLocation(8, 5)),
                    listOf(SourceLocation(8, 16))
                )
            }

            @Test
            fun `does report when nested iterator with one suspending iterator is used`() {
                val code = """
                    import kotlinx.coroutines.delay

                    class SuspendingIterator {
                        suspend operator fun iterator(): Iterator<Any> = iterator { yield("value") }
                    }

                    suspend fun bar() {
                        runCatching { 
                            for (x in 1..10) {
                                for (y in SuspendingIterator()) {
                                    println(x.toString() + y)
                                }
                            }
                        }
                    }
                """.trimIndent()

                val findings = subject.lintWithContext(env, code)
                assertFindingsForSuspendCall(
                    findings,
                    listOf(SourceLocation(8, 5)),
                    listOf(SourceLocation(8, 16))
                )
            }

            @Test
            fun `does report when suspending iterator is used withing inlined block`() {
                val code = """
                    import kotlinx.coroutines.MainScope
                    import kotlinx.coroutines.delay
                    import kotlinx.coroutines.launch

                    class SuspendingIterator {
                        suspend operator fun iterator(): Iterator<Any> = iterator { yield("value") }
                    }
                    
                    inline fun foo(lambda: () -> Unit) {
                        lambda()
                    }

                    suspend fun bar() {
                        runCatching {
                            foo {
                                for (x in SuspendingIterator()) {
                                    println(x)
                                }
                            }
                        }
                    }
                """.trimIndent()

                val findings = subject.lintWithContext(env, code)
                assertFindingsForSuspendCall(
                    findings,
                    listOf(SourceLocation(14, 5)),
                    listOf(SourceLocation(14, 16))
                )
            }

            @Test
            fun `does not report when suspending iterator is used withing non inlined block`() {
                val code = """
                    import kotlinx.coroutines.MainScope
                    import kotlinx.coroutines.delay
                    import kotlinx.coroutines.launch

                    class SuspendingIterator {
                        suspend operator fun iterator(): Iterator<Any> = iterator { yield("value") }
                    }

                    suspend fun bar() {
                        runCatching { 
                            MainScope().launch { 
                                for (x in SuspendingIterator()) {
                                    println(x)
                                }
                            }
                        }
                    }
                """.trimIndent()

                val findings = subject.lintWithContext(env, code)
                assertThat(findings).isEmpty()
            }

            @Test
            fun `does report when suspend function is invoked`() {
                val code = """
                    import kotlinx.coroutines.delay

                    suspend fun foo() {
                        val suspendBlock = suspend { }
                        runCatching {
                            suspendBlock()
                        }
                    }
                """.trimIndent()

                val findings = subject.lintWithContext(env, code)
                assertFindingsForSuspendCall(
                    findings,
                    listOf(SourceLocation(5, 5)),
                    listOf(SourceLocation(5, 16))
                )
            }

            @Test
            fun `does not report when suspend block is passed to non inline function`() {
                val code = """
                    import kotlinx.coroutines.delay
                    import kotlinx.coroutines.MainScope
                    import kotlinx.coroutines.launch

                    fun bar(lambda: suspend () -> Unit) {
                        MainScope().launch { lambda() }
                    }
                    
                    suspend fun foo() {
                        runCatching {
                            bar {
                                delay(1000L)
                            }
                        }
                    }
                """.trimIndent()

                val findings = subject.lintWithContext(env, code)
                assertThat(findings).isEmpty()
            }

            @Test
            fun `does report when suspend block is passed to an inline function`() {
                val code = """
                    import kotlinx.coroutines.delay
                    import kotlinx.coroutines.MainScope
                    import kotlinx.coroutines.launch

                    inline fun bar(crossinline lambda: suspend () -> Unit) {
                        MainScope().launch { lambda() }
                    }

                    suspend fun foo() {
                        runCatching {
                            bar {
                                delay(1000L)
                            }
                        }
                    }
                """.trimIndent()

                val findings = subject.lintWithContext(env, code)
                assertThat(findings).isEmpty()
            }
        }
    }

    private fun assertFindingsForSuspendCall(
        findings: List<Finding>,
        listOfStartLocation: List<SourceLocation>,
        listOfEndLocation: List<SourceLocation>,
    ) {
        assertThat(listOfEndLocation).hasSameSizeAs(listOfStartLocation)
        assertThatFindings(findings).hasSameSizeAs(listOfStartLocation)
        assertThatFindings(findings).hasStartSourceLocations(*listOfStartLocation.toTypedArray())
        assertThatFindings(findings).hasEndSourceLocations(*listOfEndLocation.toTypedArray())
    }

    private fun assertOneFindingAt(@Language("kotlin") code: String, start: SourceLocation, end: SourceLocation) {
        val findings = subject.lintWithContext(env, code)
        assertFindingsForSuspendCall(
            findings = findings,
            listOfStartLocation = listOf(start),
            listOfEndLocation = listOf(end),
        )
    }

    private fun assertNoFindings(@Language("kotlin") code: String) {
        val findings = subject.lintWithContext(env, code)
        assertThat(findings).isEmpty()
    }
}
