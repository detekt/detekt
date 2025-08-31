package io.gitlab.arturbosch.detekt.rules.coroutines

import dev.detekt.api.Config
import dev.detekt.test.lintWithContext
import dev.detekt.test.utils.KotlinCoreEnvironmentTest
import dev.detekt.test.utils.KotlinEnvironmentContainer
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

@Suppress("BlockingMethodInNonBlockingContext", "RedundantSuspendModifier")
@KotlinCoreEnvironmentTest
class SleepInsteadOfDelaySpec(private val env: KotlinEnvironmentContainer) {

    private val subject = SleepInsteadOfDelay(Config.empty)

    @Test
    fun `should report no issue for delay() in suspend functions`() {
        val code = """
            import kotlinx.coroutines.delay
            suspend fun foo() {
                delay(1000L)
            }
        """.trimIndent()
        assertThat(subject.lintWithContext(env, code)).isEmpty()
    }

    @Test
    @DisplayName("should report Thread.sleep() in suspend functions")
    fun reportThreadSleepInSuspendFunctions() {
        val code = """
            suspend fun foo() {
                Thread.sleep(1000L)
            }
        """.trimIndent()
        assertThat(subject.lintWithContext(env, code)).hasSize(1)
    }

    @Test
    fun `should report overloaded Thread sleep() in suspend functions`() {
        val code = """
            suspend fun foo() {
                Thread.sleep(1000L, 1000)
            }
        """.trimIndent()
        assertThat(subject.lintWithContext(env, code)).hasSize(1)
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
        assertThat(subject.lintWithContext(env, code)).hasSize(1)
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
        assertThat(subject.lintWithContext(env, code)).hasSize(1)
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
        assertThat(subject.lintWithContext(env, code)).isEmpty()
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
        assertThat(subject.lintWithContext(env, code)).isEmpty()
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
        assertThat(subject.lintWithContext(env, code)).isEmpty()
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
        assertThat(subject.lintWithContext(env, code)).isEmpty()
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
        assertThat(subject.lintWithContext(env, code)).hasSize(1)
    }

    @Test
    fun `should report Thread sleep() called in custom function inside suspend lambda wth type in braces`() {
        val code = """
            import kotlinx.coroutines.MainScope
            import kotlinx.coroutines.launch
            
            fun suspendBlock(lambda: (suspend () -> Unit)) {
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
        assertThat(subject.lintWithContext(env, code)).hasSize(1)
    }

    @Test
    fun `should report Thread sleep() called in custom function inside suspend lambda with lambda in braces`() {
        val code = """
            import kotlinx.coroutines.MainScope
            import kotlinx.coroutines.launch
            
            fun suspendBlock(lambda: suspend () -> Unit) {
                MainScope().launch { 
                    lambda()
                }
            }

            fun test() {
                suspendBlock({ 
                    Thread.sleep(1000L)
                })
            }
        """.trimIndent()
        assertThat(subject.lintWithContext(env, code)).hasSize(1)
    }

    @Test
    fun `should report Thread sleep() called in custom function inside suspend lambda with named lambda`() {
        val code = """
            import kotlinx.coroutines.MainScope
            import kotlinx.coroutines.launch
            
            fun suspendBlock(lambda: suspend () -> Unit) {
                MainScope().launch { 
                    lambda()
                }
            }

            fun test() {
                suspendBlock(lambda = { 
                    Thread.sleep(1000L)
                })
            }
        """.trimIndent()
        assertThat(subject.lintWithContext(env, code)).hasSize(1)
    }

    @Test
    fun `should report Thread sleep() called in custom function inside suspend lambda with named lambda with braces`() {
        val code = """
            import kotlinx.coroutines.MainScope
            import kotlinx.coroutines.launch
            
            fun suspendBlock(lambda: suspend () -> Unit) {
                MainScope().launch { 
                    lambda()
                }
            }

            fun test() {
                suspendBlock(lambda = ({ 
                    Thread.sleep(1000L)
                }))
            }
        """.trimIndent()
        assertThat(subject.lintWithContext(env, code)).hasSize(1)
    }

    @Test
    fun `should report Thread sleep() callable ref called in custom function inside suspend lambda`() {
        val code = """
            import kotlinx.coroutines.MainScope
            import kotlinx.coroutines.launch
            
            fun suspendBlock(lambda: (suspend (Long) -> Unit)) {
                MainScope().launch {
                    lambda(1000)
                }
            }
        
            fun test1() {
                suspendBlock(lambda = Thread::sleep)
            }
        """.trimIndent()
        assertThat(subject.lintWithContext(env, code)).hasSize(1)
    }

    @Test
    fun `should not report delay callable ref called in custom function inside suspend lambda`() {
        val code = """
            import kotlinx.coroutines.MainScope
            import kotlinx.coroutines.delay
            import kotlinx.coroutines.launch
            
            fun suspendBlock(lambda: (suspend (Long) -> Unit)) {
                MainScope().launch {
                    lambda(1000)
                }
            }
        
            fun test1() {
                suspendBlock(lambda = ::delay)
            }
        """.trimIndent()
        assertThat(subject.lintWithContext(env, code)).isEmpty()
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
        assertThat(subject.lintWithContext(env, code)).hasSize(1)
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
        assertThat(subject.lintWithContext(env, code)).hasSize(1)
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
        assertThat(subject.lintWithContext(env, code)).hasSize(1)
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
        assertThat(subject.lintWithContext(env, code)).hasSize(1)
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
        assertThat(subject.lintWithContext(env, code)).hasSize(1)
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
        assertThat(subject.lintWithContext(env, code)).isEmpty()
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
        assertThat(subject.lintWithContext(env, code)).hasSize(1)
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
        assertThat(subject.lintWithContext(env, code)).hasSize(1)
    }

    @Test
    fun `should report Thread sleep() called in nested inline function inside coroutine scope lambda`() {
        val code = """
            import kotlinx.coroutines.Dispatchers
            import kotlinx.coroutines.GlobalScope
            import kotlinx.coroutines.launch
            import kotlinx.coroutines.withContext

            fun test() {
                GlobalScope.launch {
                    listOf(emptyList<Long>()).map { delays ->
                        delays.map {
                            Thread.sleep(it)
                        }
                    }
                }
            }
        """.trimIndent()
        assertThat(subject.lintWithContext(env, code)).hasSize(1)
    }

    @Test
    fun `should not report Thread sleep() called in nested inline function with noinline function in-between`() {
        val code = """
            import kotlinx.coroutines.Dispatchers
            import kotlinx.coroutines.GlobalScope
            import kotlinx.coroutines.delay
            import kotlinx.coroutines.launch
            import kotlin.concurrent.thread
            import kotlinx.coroutines.withContext

            fun test() {
                GlobalScope.launch {
                    listOf(emptyList<Long>()).map { delays ->
                        thread {
                            delays.map {
                                Thread.sleep(it)
                            }
                        }
                    }
                }
            }
        """.trimIndent()
        assertThat(subject.lintWithContext(env, code)).isEmpty()
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
        assertThat(subject.lintWithContext(env, code)).hasSize(1)
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
        assertThat(subject.lintWithContext(env, code)).hasSize(1)
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
        assertThat(subject.lintWithContext(env, code)).hasSize(1)
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
        assertThat(subject.lintWithContext(env, code)).hasSize(1)
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
        assertThat(subject.lintWithContext(env, code)).hasSize(1)
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
        assertThat(subject.lintWithContext(env, code)).hasSize(1)
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
        assertThat(subject.lintWithContext(env, code)).hasSize(2)
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
        assertThat(subject.lintWithContext(env, code)).hasSize(1)
    }

    @Test
    fun `should report Thread sleep() when called inside for block`() {
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
        assertThat(subject.lintWithContext(env, code)).hasSize(1)
    }

    @Test
    fun `#6176 - should not report when inlined lambda is inside non suspending function`() {
        val code = """
            class OhBoy {
                private val lock = Object()
            
                fun get(a: Int, b: Int) {
                    synchronized(lock) {
                        Thread.sleep(500)
                    }
                }
            }
        """.trimIndent()
        assertThat(subject.lintWithContext(env, code)).isEmpty()
    }

    @Test
    fun `should not report when inlined lambda is is non suspending inner fun with outer is fun suspend`() {
        val code = """
            suspend fun test() {
                fun testInner() {
                    synchronized(Unit) {
                        Thread.sleep(1000)
                    }
                }
            }
        """.trimIndent()
        assertThat(subject.lintWithContext(env, code)).isEmpty()
    }

    @Nested
    inner class `Given suspend function variable` {
        @Test
        fun `should report Thread sleep() in suspend variable`() {
            val code = """
                val foo = suspend {
                    Thread.sleep(1000)
                }
            """.trimIndent()
            assertThat(subject.lintWithContext(env, code)).hasSize(1)
        }

        @Test
        fun `should report Thread sleep() in suspend lambda`() {
            val code = """
                val foo: suspend () -> Unit = {
                    Thread.sleep(1000)
                }
            """.trimIndent()
            assertThat(subject.lintWithContext(env, code)).hasSize(1)
        }

        @Test
        fun `should report Thread sleep() in suspend lambda with type inside braces`() {
            val code = """
                val foo: (suspend () -> Unit) = {
                    Thread.sleep(1000)
                }
            """.trimIndent()
            assertThat(subject.lintWithContext(env, code)).hasSize(1)
        }

        @Test
        fun `should report Thread sleep() in suspend lambda with expression inside braces`() {
            val code = """
                val foo: suspend () -> Unit = ({
                    Thread.sleep(1000)
                })
            """.trimIndent()
            assertThat(subject.lintWithContext(env, code)).hasSize(1)
        }

        @Test
        fun `should report Thread sleep() in suspend lambda with variable`() {
            val code = """
                val foo: suspend (Int, Int) -> Unit = { bar1, bar2 ->
                    Thread.sleep(1000)
                }
            """.trimIndent()
            assertThat(subject.lintWithContext(env, code)).hasSize(1)
        }

        @Test
        fun `should not report Thread sleep() in non-suspending lambda`() {
            val code = """
                val foo: () -> Unit = {
                    Thread.sleep(1000)
                }
            """.trimIndent()
            assertThat(subject.lintWithContext(env, code)).isEmpty()
        }

        @Test
        fun `should not report Thread sleep() in non-suspending lambda inside suspend fun`() {
            val code = """
                suspend fun foo() {
                    val bar: () -> Unit = {
                        Thread.sleep(1000)
                    }
                }
            """.trimIndent()
            assertThat(subject.lintWithContext(env, code)).isEmpty()
        }

        @Test
        fun `should report Thread sleep() in suspend lambda inside non-suspending fun`() {
            val code = """
                fun foo() {
                    val bar: suspend () -> Unit = {
                        Thread.sleep(1000)
                    }
                }
            """.trimIndent()
            assertThat(subject.lintWithContext(env, code)).hasSize(1)
        }

        @Nested
        inner class `Given inside launch` {
            @Test
            fun `should report Thread sleep() called inside variable declaration inside GlobalScope launch`() {
                val code = """
                    import kotlinx.coroutines.GlobalScope
                    import kotlinx.coroutines.launch
                    
                    val t = GlobalScope.launch {
                        Thread.sleep(1000L)
                    }
                """.trimIndent()
                assertThat(subject.lintWithContext(env, code)).hasSize(1)
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
                assertThat(subject.lintWithContext(env, code)).hasSize(1)
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
                assertThat(subject.lintWithContext(env, code)).isEmpty()
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
                assertThat(subject.lintWithContext(env, code)).isEmpty()
            }
        }
    }
}
