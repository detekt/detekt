package io.gitlab.arturbosch.detekt.rules.coroutines

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Finding
import io.gitlab.arturbosch.detekt.rules.KotlinCoreEnvironmentTest
import io.gitlab.arturbosch.detekt.test.compileAndLintWithContext
import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.kotlin.cli.jvm.compiler.KotlinCoreEnvironment
import org.junit.jupiter.api.Test

@KotlinCoreEnvironmentTest
class SuspendFunSwallowedCancellationSpec(private val env: KotlinCoreEnvironment) {

    private val subject = SuspendFunSwallowedCancellation(Config.empty)

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
        val findings = subject.compileAndLintWithContext(env, code)
        assertFindings(findings, "delay")
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
        val findings = subject.compileAndLintWithContext(env, code)
        assertFindings(findings, "delay", "bar")
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
        val findings = subject.compileAndLintWithContext(env, code)
        assertFindings(findings, "delay")
    }

    @Test
    fun `does not report no suspending function is used inside runBlocking`() {
        val code = """
            suspend fun foo() {
                runCatching {
                    Thread.sleep(1000L)
                }
            }
        """.trimIndent()
        val findings = subject.compileAndLintWithContext(env, code)
        assertFindings(findings)
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
        val findings = subject.compileAndLintWithContext(env, code)
        assertFindings(findings, "bar")
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
        val findings = subject.compileAndLintWithContext(env, code)
        assertFindings(findings, "bar")
    }

    @Test
    fun `does report when runCatching is used as function expression`() {
        val code = """
            import kotlinx.coroutines.delay
            suspend fun bar() = delay(1000L)
            suspend fun foo() = runCatching { bar() }
        """.trimIndent()
        val findings = subject.compileAndLintWithContext(env, code)
        assertFindings(findings, "bar")
    }

    @Test
    fun `does not report when try catch is used`() {
        val code = """
            import kotlinx.coroutines.delay

            suspend fun foo() {
                try {
                    delay(1000L)
                } catch (e: IllegalStateException) {
                    // handle error
                }
            }
        """.trimIndent()
        val findings = subject.compileAndLintWithContext(env, code)
        assertFindings(findings)
    }

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

        val findings = subject.compileAndLintWithContext(env, code)
        assertFindings(findings, "delay")
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

        val findings = subject.compileAndLintWithContext(env, code)
        assertFindings(findings, "delay")
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
        
            suspend fun bar()
            {
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

        val findings = subject.compileAndLintWithContext(env, code)
        assertFindings(findings, "delay")
    }

    @Test
    fun `does report when lambda in inline function is passed as crossinline`() {
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

        val findings = subject.compileAndLintWithContext(env, code)
        assertFindings(findings)
    }

    @Test
    fun `does not report when lambda parameter chain has noinline function call`() {
        val code = """
            import kotlinx.coroutines.MainScope
            import kotlinx.coroutines.delay
            import kotlinx.coroutines.launch

            inline fun <R> inline(block: () -> R) = block()
            fun <R> noInline(block: suspend () -> R) = MainScope().launch { block() }
        
            suspend fun bar()
            {
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

        val findings = subject.compileAndLintWithContext(env, code)
        assertFindings(findings)
    }

    @Test
    fun `does report when lambda parameter chain has all inlined function call`() {
        val code = """
            import kotlinx.coroutines.MainScope
            import kotlinx.coroutines.delay
            import kotlinx.coroutines.launch

            inline fun <R> inline(block: () -> R) = block()
        
            suspend fun bar()
            {
                runCatching {
                    inline {
                        inline {
                            delay(1000)
                        }
                    }
                }
            }
        """.trimIndent()

        val findings = subject.compileAndLintWithContext(env, code)
        assertFindings(findings, "delay")
    }

    @Test
    fun `does not report when lambda in inline function is passed as noinline`() {
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

        val findings = subject.compileAndLintWithContext(env, code)
        assertFindings(findings)
    }

    @Test
    fun `does report when suspend fun is called as extension function`() {
        val code = """
            import kotlinx.coroutines.delay

            private suspend fun List<Long>.await() = delay(100L)

            suspend fun foo() {
                runCatching {
                    listOf(1L, 2L, 3L).await()
                }
            }
        """.trimIndent()

        val findings = subject.compileAndLintWithContext(env, code)
        assertThat(findings).hasSize(1)
        assertFindings(findings, "await")
    }

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

        val findings = subject.compileAndLintWithContext(env, code)
        assertThat(findings).hasSize(1)
        assertThat(findings[0].message).isEqualTo(
            "The for-loop expression has suspending operator which is called " +
                "inside `runCatching`. You should either use specific `try-catch` only catching exception that you are " +
                "expecting or rethrow the `CancellationException` if already caught."
        )
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

        val findings = subject.compileAndLintWithContext(env, code)
        assertFindings(findings, "invoke")
    }

    @Test
    fun `does report in case of suspendCancellableCoroutine`() {
        val code = """
            import kotlinx.coroutines.delay
            import kotlinx.coroutines.suspendCancellableCoroutine

            suspend fun foo() {
                runCatching {
                    suspendCancellableCoroutine {
                        it.resume(Unit)
                    }
                }
            }
        """.trimIndent()

        val findings = subject.compileAndLintWithContext(env, code)
        assertThat(findings).hasSize(1)
        assertFindings(findings, "suspendCancellableCoroutine")
    }

    @Test
    fun `does report in case suspend callable refernce is invoked`() {
        val code = """
            import kotlinx.coroutines.delay

            suspend fun bar() = delay(1000)
            suspend fun foo() {
                runCatching {
                    ::bar.invoke()
                }
            }
        """.trimIndent()

        val findings = subject.compileAndLintWithContext(env, code)
        assertFindings(findings, "invoke")
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

        val findings = subject.compileAndLintWithContext(env, code)
        assertThat(findings).hasSize(1)
        assertFindings(findings, "localFun")
    }

    @Test
    fun `does report in suspend operator is invoked`() {
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

        val findings = subject.compileAndLintWithContext(env, code)
        assertFindings(findings, "invoke", "invoke")
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

        val findings = subject.compileAndLintWithContext(env, code)
        assertFindings(findings)
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

        val findings = subject.compileAndLintWithContext(env, code)
        assertFindings(findings, "join")
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

        val findings = subject.compileAndLintWithContext(env, code)
        assertFindings(findings)
    }

    @Test
    fun `does report async is awaited`() {
        val code = """
            import kotlinx.coroutines.async
            import kotlinx.coroutines.delay
            import kotlinx.coroutines.MainScope

            suspend fun foo() {
                runCatching {
                    MainScope().async {
                        delay(1000L)
                    }.await()
                }
            }
        """.trimIndent()

        val findings = subject.compileAndLintWithContext(env, code)
        assertFindings(findings, "await")
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

        val findings = subject.compileAndLintWithContext(env, code)
        assertFindings(findings)
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

        val findings = subject.compileAndLintWithContext(env, code)
        assertFindings(findings)
    }

    @Test
    fun `does report when suspend fun is called in string interpolation`() {
        val code = """
            import kotlinx.coroutines.delay

            suspend fun foo() {
                runCatching {
                    val string = "${'$'}{delay(1000)}"
                }
            }
        """.trimIndent()

        val findings = subject.compileAndLintWithContext(env, code)
        assertFindings(findings, "delay")
    }

    private fun assertFindings(findings: List<Finding>, vararg funCallExpression: String) {
        assertThat(findings).hasSize(funCallExpression.size)
        assertThat(findings.map { it.message }).containsExactlyInAnyOrder(
            *funCallExpression.map {
                "The suspend function call $it called inside " +
                    "`runCatching`. You should either use specific `try-catch` only catching exception that you " +
                    "are expecting or rethrow the `CancellationException` if already caught."
            }.toTypedArray()
        )
    }
}
