package io.gitlab.arturbosch.detekt.rules.coroutines

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.rules.KotlinCoreEnvironmentTest
import io.gitlab.arturbosch.detekt.test.compileAndLintWithContext
import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.kotlin.cli.jvm.compiler.KotlinCoreEnvironment
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

@Suppress("BlockingMethodInNonBlockingContext", "RedundantSuspendModifier")
@KotlinCoreEnvironmentTest
class SleepInsteadOfDelaySpec(private val env: KotlinCoreEnvironment) {

    private val subject = SleepInsteadOfDelay(Config.empty)

    @Test
    fun `should report no issue for delay() in suspend functions`() {
        val code = """
            import kotlinx.coroutines.delay
            
            suspend fun foo() {
                delay(1000L)
            }
        """.trimIndent()
        assertThat(subject.compileAndLintWithContext(env, code)).hasSize(0)
    }

    @Test
    @DisplayName("should report Thread.sleep() in suspend functions")
    fun reportThreadSleepInSuspendFunctions() {
        val code = """
            suspend fun foo() {
                Thread.sleep(1000L)
            }
        """.trimIndent()
        assertThat(subject.compileAndLintWithContext(env, code)).hasSize(1)
    }

    @Test
    fun `should report overloaded Thread sleep() in suspend functions`() {
        val code = """
            suspend fun foo() {
                Thread.sleep(1000L, 1000)
            }
        """.trimIndent()
        assertThat(subject.compileAndLintWithContext(env, code)).hasSize(1)
    }

    @Test
    @DisplayName("should report Thread.sleep() in CoroutineScope.launch()")
    fun reportThreadSleepInCoroutineScopeLaunch() {
        val code = """
            import kotlinx.coroutines.CoroutineScope
            import kotlinx.coroutines.Dispatchers
            import kotlinx.coroutines.launch
            
            fun foo() {
                CoroutineScope(Dispatchers.IO).launch {
                    Thread.sleep(1000L)
                }
            }
        """.trimIndent()
        assertThat(subject.compileAndLintWithContext(env, code)).hasSize(1)
    }

    @Test
    @DisplayName("should report Thread.sleep() in CoroutineScope.async()")
    fun reportThreadSleepInCoroutineScopeAsync() {
        @Suppress("DeferredResultUnused")
        val code = """
            import kotlinx.coroutines.CoroutineScope
            import kotlinx.coroutines.Dispatchers
            import kotlinx.coroutines.async
            
            fun foo() {
                CoroutineScope(Dispatchers.IO).async {
                    Thread.sleep(1000L)
                }
            }
        """.trimIndent()
        assertThat(subject.compileAndLintWithContext(env, code)).hasSize(1)
    }

    @Test
    fun `should not report Thread sleep() called in non-suspending block`() {
        @Suppress("DeferredResultUnused")
        val code = """
            import kotlinx.coroutines.CoroutineScope
            import kotlinx.coroutines.Dispatchers
            import kotlinx.coroutines.async
            
            fun bar(lambda: () -> Unit) {
                lambda()
            }
            fun foo() {
                CoroutineScope(Dispatchers.IO).async {
                    bar {
                        Thread.sleep(1000L)
                    }
                }
            }
        """.trimIndent()
        assertThat(subject.compileAndLintWithContext(env, code)).isEmpty()
    }

    @Test
    fun `should not report Thread sleep() called in inline fun with noinline lambda`() {
        val code = """
            import kotlinx.coroutines.CoroutineScope
            import kotlinx.coroutines.Dispatchers
            import kotlinx.coroutines.launch
            
            inline fun bar(noinline lambda: () -> Unit) {
                lambda()
            }
            fun foo() {
                CoroutineScope(Dispatchers.IO).launch {
                    bar {
                        Thread.sleep(1000L)
                    }
                }
            }
        """.trimIndent()
        assertThat(subject.compileAndLintWithContext(env, code)).isEmpty()
    }

    @Test
    fun `should not report Thread sleep() called in inline fun with crossinline lambda`() {
        val code = """
            import kotlinx.coroutines.CoroutineScope
            import kotlinx.coroutines.Dispatchers
            import kotlinx.coroutines.launch
            
            inline fun bar(crossinline lambda: () -> Unit) {
                lambda()
            }
            fun foo() {
                CoroutineScope(Dispatchers.IO).launch {
                    bar {
                        Thread.sleep(1000L)
                    }
                }
            }
        """.trimIndent()
        assertThat(subject.compileAndLintWithContext(env, code)).isEmpty()
    }

    @Test
    fun `should not report Thread sleep() called in non suspending coroutine block`() {
        val code = """
            import kotlinx.coroutines.CoroutineScope
            import kotlinx.coroutines.Dispatchers
            import kotlinx.coroutines.MainScope
            import kotlinx.coroutines.async
            import kotlinx.coroutines.delay
            
            fun customAsync(lambda: CoroutineScope.() -> Unit) {
                MainScope().lambda()
            }
            fun foo() {
                customAsync {
                    Thread.sleep(1000L)
                }
            }
        """.trimIndent()
        assertThat(subject.compileAndLintWithContext(env, code)).isEmpty()
    }

    @Test
    fun `should report Thread sleep() called inside variable declaration`() {
        val code = """
            import kotlinx.coroutines.GlobalScope
            import kotlinx.coroutines.launch
            
            val t = GlobalScope.launch {
                Thread.sleep(1000L)
            }
        """.trimIndent()
        assertThat(subject.compileAndLintWithContext(env, code)).hasSize(1)
    }

    @Test
    fun `should report Thread sleep() called in custom function inside suspend lambda`() {
        val code = """
            import kotlinx.coroutines.MainScope
            import kotlinx.coroutines.launch
            
            fun suspendBlock(lambda: suspend () -> Unit) {
                MainScope().launch { 
                    lambda()
                }
            }

            fun test() {
                suspendBlock { 
                    Thread.sleep(1000L)
                }
            }
        """.trimIndent()
        assertThat(subject.compileAndLintWithContext(env, code)).hasSize(1)
    }

    @Test
    fun `should report Thread sleep() called in custom inline function inside suspend crossinline lambda`() {
        val code = """
            import kotlinx.coroutines.MainScope
            import kotlinx.coroutines.launch
            
            inline fun suspendBlock(crossinline lambda: suspend () -> Unit) {
                MainScope().launch {
                    lambda()
                }
            }

            fun test() {
                suspendBlock {
                    Thread.sleep(1000L)
                }
            }
        """.trimIndent()
        assertThat(subject.compileAndLintWithContext(env, code)).hasSize(1)
    }

    @Test
    fun `should report Thread sleep() called in custom inline function inside suspend noinline lambda`() {
        val code = """
            import kotlinx.coroutines.MainScope
            import kotlinx.coroutines.launch

            inline fun suspendBlock(noinline lambda: suspend () -> Unit) {
                MainScope().launch {
                    lambda()
                }
            }

            fun test() {
                suspendBlock {
                    Thread.sleep(1000L)
                }
            }
        """.trimIndent()
        assertThat(subject.compileAndLintWithContext(env, code)).hasSize(1)
    }

    @Test
    fun `should report Thread sleep() inside local function defined inside lambda`() {
        val code = """
            fun normalBlock(lambda: () -> Unit) {
                lambda()
            }

            fun test() {
                normalBlock {
                    suspend fun test() {
                        Thread.sleep(1000L)
                    }
                }
            }
        """.trimIndent()
        assertThat(subject.compileAndLintWithContext(env, code)).hasSize(1)
    }

    @Test
    fun `should report Thread sleep() when called inside withContext`() {
        @Suppress("DeferredResultUnused")
        val code = """
            import kotlinx.coroutines.Dispatchers
            import kotlinx.coroutines.GlobalScope
            import kotlinx.coroutines.launch
            import kotlinx.coroutines.withContext

            fun test() {
                GlobalScope.launch { 
                    withContext(Dispatchers.IO) {
                        Thread.sleep(1000L)
                    }
                }
            }
        """.trimIndent()
        assertThat(subject.compileAndLintWithContext(env, code)).hasSize(1)
    }

    @Test
    fun `should report Thread sleep() called and used as dot qualified expression`() {
        @Suppress("DeferredResultUnused")
        val code = """
            import kotlinx.coroutines.Dispatchers
            import kotlinx.coroutines.GlobalScope
            import kotlinx.coroutines.launch
            import kotlinx.coroutines.withContext
            
            fun test() {
                GlobalScope.launch { 
                    Thread.sleep(1000L).also { 
                        println(it)
                    }
                }
            }
        """.trimIndent()
        assertThat(subject.compileAndLintWithContext(env, code)).hasSize(1)
    }

    @Test
    fun `should not report Thread sleep() when used a callable reference variable`() {
        @Suppress("DeferredResultUnused")
        val code = """
            import kotlinx.coroutines.Dispatchers
            import kotlinx.coroutines.GlobalScope
            import kotlinx.coroutines.launch
            import kotlinx.coroutines.withContext

            fun test() {
                GlobalScope.launch {
                     val funRef: (Long) -> Unit = Thread::sleep
                }
            }
        """.trimIndent()
        assertThat(subject.compileAndLintWithContext(env, code)).isEmpty()
    }

    @Test
    fun `should not report Thread sleep() when used inside map as callable reference variable`() {
        @Suppress("DeferredResultUnused")
        val code = """
            import kotlinx.coroutines.Dispatchers
            import kotlinx.coroutines.GlobalScope
            import kotlinx.coroutines.launch
            import kotlinx.coroutines.withContext

            @Suppress("RedundantSuspendModifier")
            suspend fun test() {
                GlobalScope.launch {
                    listOf(1L, 2L, 3L).map {
                        @Suppress("UNUSED_VARIABLE")
                        val temp: (Long) -> Unit = Thread::sleep
                    }
                }
            }
        """.trimIndent()
        assertThat(subject.compileAndLintWithContext(env, code)).isEmpty()
    }

    @Test
    fun `should report Thread sleep() callable reference is used as value parameter`() {
        @Suppress("DeferredResultUnused")
        val code = """
            import kotlinx.coroutines.Dispatchers
            import kotlinx.coroutines.GlobalScope
            import kotlinx.coroutines.launch
            import kotlinx.coroutines.withContext

            fun test() {
                GlobalScope.launch {
                    listOf(1L, 2L, 3L).map(Thread::sleep)
                }
            }
        """.trimIndent()
        assertThat(subject.compileAndLintWithContext(env, code)).hasSize(1)
    }

    @Test
    fun `should report Thread sleep() called in library inline function`() {
        @Suppress("DeferredResultUnused")
        val code = """
            import kotlinx.coroutines.Dispatchers
            import kotlinx.coroutines.GlobalScope
            import kotlinx.coroutines.launch
            import kotlinx.coroutines.withContext

            fun test() {
                GlobalScope.launch {
                    listOf(1L, 2L, 3L).map {
                        Thread.sleep(it)
                    }
                }
            }
        """.trimIndent()
        assertThat(subject.compileAndLintWithContext(env, code)).hasSize(1)
    }

    @Test
    fun `should report Thread sleep() called in when called inside custom inline function`() {
        val code = """
            import kotlinx.coroutines.Dispatchers
            import kotlinx.coroutines.GlobalScope
            import kotlinx.coroutines.launch
            import kotlinx.coroutines.withContext

            inline fun inlineFun(lambda: () -> Unit) = lambda()
            
            suspend fun test() {
                inlineFun { Thread.sleep(1000L) }
            }
        """.trimIndent()
        assertThat(subject.compileAndLintWithContext(env, code)).hasSize(1)
    }

    @Test
    fun `should report Thread sleep() called inside suspend lambda variable`() {
        val code = """
            import kotlinx.coroutines.Dispatchers
            import kotlinx.coroutines.GlobalScope
            import kotlinx.coroutines.launch
            import kotlinx.coroutines.withContext

            fun test() {
                GlobalScope.launch {
                    val localLambda: suspend () -> Unit = {
                        Thread.sleep(1000)
                    }
                }
            }
        """.trimIndent()
        assertThat(subject.compileAndLintWithContext(env, code)).hasSize(1)
    }

    @Test
    fun `should not report Thread sleep() called inside non-suspend lambda variable`() {
        val code = """
            import kotlinx.coroutines.GlobalScope
            import kotlinx.coroutines.launch

            suspend fun test() {
                GlobalScope.launch {
                    val localLambda: () -> Unit = {
                        Thread.sleep(1000)
                    }
                }
            }
        """.trimIndent()
        assertThat(subject.compileAndLintWithContext(env, code)).isEmpty()
    }

    @Test
    fun `should report Thread sleep() called inside suspend local fun inside non-suspend lambda variable`() {
        val code = """
            fun test() {
                val localLambda: () -> Unit = {
                    suspend fun test() {
                        Thread.sleep(1000L)
                    }
                }
            }
        """.trimIndent()
        assertThat(subject.compileAndLintWithContext(env, code)).hasSize(1)
    }

    @Test
    fun `should report Thread sleep() called inside suspend local fun inside nested non-suspend lambda variable`() {
        val code = """
            val localLambda: () -> Unit = {
                val localLambda2: () -> Unit = {
                    suspend fun test() {
                        Thread.sleep(1000L)
                    }
                }
            }
        """.trimIndent()
        assertThat(subject.compileAndLintWithContext(env, code)).hasSize(1)
    }

    @Test
    fun `should report Thread sleep() only once when called inside both suspend function and suspend lambda block`() {
        val code = """
            import kotlinx.coroutines.GlobalScope
            import kotlinx.coroutines.launch

            suspend fun test() {
                GlobalScope.launch {
                    Thread.sleep(1000)
                }
            }
        """.trimIndent()
        assertThat(subject.compileAndLintWithContext(env, code)).hasSize(1)
    }

    @Test
    fun `should report Thread sleep() only once called inside nested suspend fun`() {
        val code = """

            suspend fun test() {
                suspend fun test2() {
                    Thread.sleep(1000)
                }
            }
        """.trimIndent()
        assertThat(subject.compileAndLintWithContext(env, code)).hasSize(1)
    }

    @Test
    fun `should report Thread sleep() only once when called inside nested suspend lambda`() {
        val code = """
            import kotlinx.coroutines.GlobalScope
            import kotlinx.coroutines.launch

            fun test() {
                GlobalScope.launch {
                    GlobalScope.launch { 
                        Thread.sleep(1000)
                     }
                }
            }
        """.trimIndent()
        assertThat(subject.compileAndLintWithContext(env, code)).hasSize(1)
    }

    @Test
    fun `should report Thread sleep() called inside when block`() {
        val code = """
            suspend fun test(bool: Boolean) {
                when(bool) {
                    true -> {
                        Thread.sleep(1000L)
                    }
                    false -> {
                        Thread.sleep(1000L)
                    }
                }
            }
        """.trimIndent()
        assertThat(subject.compileAndLintWithContext(env, code)).hasSize(2)
    }

    @Test
    fun `should report Thread sleep() called inside when subject variable assignment`() {
        val code = """
            suspend fun test(bool: Boolean) {
                when(val a = Thread.sleep(1000L)) {
                    a -> {
                        /* no-op */
                    }
                    else -> {
                        /* no-op */
                    }
                }
            }
        """.trimIndent()
        assertThat(subject.compileAndLintWithContext(env, code)).hasSize(1)
    }

    @Test
    fun `should report Thread sleep() when called isnide for block`() {
        val code = """
            import kotlinx.coroutines.Dispatchers
            import kotlinx.coroutines.GlobalScope
            import kotlinx.coroutines.launch
            import kotlinx.coroutines.withContext
            
            suspend fun test() {
                val bool = false
                for (i in 1..2) {
                    Thread.sleep(1000L)
                }
            }
        """.trimIndent()
        assertThat(subject.compileAndLintWithContext(env, code)).hasSize(1)
    }
}
