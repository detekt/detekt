package io.gitlab.arturbosch.detekt.rules.coroutines

import dev.detekt.api.Config
import dev.detekt.test.TestConfig
import dev.detekt.test.lintWithContext
import dev.detekt.test.utils.KotlinCoreEnvironmentTest
import dev.detekt.test.utils.KotlinEnvironmentContainer
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

@KotlinCoreEnvironmentTest
class InjectDispatcherSpec(val env: KotlinEnvironmentContainer) {

    @Nested
    inner class `InjectDispatcher with default config` {

        val subject = InjectDispatcher(Config.empty)

        @Test
        fun `reports when dispatchers is used inside a function`() {
            val code = """
                import kotlinx.coroutines.launch
                import kotlinx.coroutines.runBlocking
                import kotlinx.coroutines.Dispatchers
                
                fun useDispatchers() {
                    runBlocking {
                        launch(Dispatchers.IO) { }
                    }
                }
            """.trimIndent()
            assertThat(subject.lintWithContext(env, code)).hasSize(1)
        }

        @Test
        fun `does not report when dispatcher is used as a function parameter`() {
            val code = """
                import kotlinx.coroutines.launch
                import kotlinx.coroutines.runBlocking
                import kotlinx.coroutines.CoroutineDispatcher
                import kotlinx.coroutines.Dispatchers
                
                fun useDispatchers(dispatcher: CoroutineDispatcher = Dispatchers.IO) {
                    runBlocking {
                        launch(dispatcher) { }
                    }
                }
            """.trimIndent()
            assertThat(subject.lintWithContext(env, code)).isEmpty()
        }

        @Test
        fun `does not report when dispatcher is used as a constructor parameter`() {
            val code = """
                import kotlinx.coroutines.CoroutineDispatcher
                import kotlinx.coroutines.Dispatchers
                
                class MyRepository(dispatcher: CoroutineDispatcher = Dispatchers.IO)
            """.trimIndent()
            assertThat(subject.lintWithContext(env, code)).isEmpty()
        }

        @Test
        fun `does not report when dispatcher is used as a secondary constructor parameter`() {
            val code = """
                import kotlinx.coroutines.CoroutineDispatcher
                import kotlinx.coroutines.Dispatchers
                
                class MyRepository(dispatcher: CoroutineDispatcher) {
                    constructor() : this(Dispatchers.IO)
                }
            """.trimIndent()
            assertThat(subject.lintWithContext(env, code)).isEmpty()
        }

        @Test
        fun `does not report when dispatcher is used as a property`() {
            val code = """
                import kotlinx.coroutines.CoroutineDispatcher
                import kotlinx.coroutines.Dispatchers
                
                class MyRepository(private val dispatcher: CoroutineDispatcher = Dispatchers.IO)
            """.trimIndent()
            assertThat(subject.lintWithContext(env, code)).isEmpty()
        }

        @Test
        fun `does not report when dispatcher main is used`() {
            val code = """
                import kotlinx.coroutines.launch
                import kotlinx.coroutines.runBlocking
                import kotlinx.coroutines.Dispatchers
                
                fun useDispatchers() {
                    runBlocking {
                        launch(Dispatchers.Main) { }
                    }
                }
            """.trimIndent()
            assertThat(subject.lintWithContext(env, code)).isEmpty()
        }

        @Test
        fun `reports when a custom dispatchers class is used inside of a function`() {
            val code = """
                import kotlinx.coroutines.launch
                import kotlinx.coroutines.runBlocking
                import kotlinx.coroutines.CoroutineDispatcher
                
                interface IDispatchers {
                    val IO: CoroutineDispatcher
                }
                
                @Suppress("InjectDispatcher")
                object Dispatchers : IDispatchers {
                    override val IO = kotlinx.coroutines.Dispatchers.IO
                }
                
                class DispatcherUser(private val dispatchers: Dispatchers) {
                    fun dispatch() {
                        runBlocking { launch(Dispatchers.IO) { } }
                    }
                }
            """.trimIndent()
            assertThat(subject.lintWithContext(env, code)).hasSize(1)
        }

        @Test
        fun `does not report when a custom dispatchers class is used as a function parameter`() {
            val code = """
                import kotlinx.coroutines.launch
                import kotlinx.coroutines.runBlocking
                import kotlinx.coroutines.CoroutineDispatcher
                
                interface IDispatchers {
                    val IO: CoroutineDispatcher
                }
                
                @Suppress("InjectDispatcher")
                object Dispatchers : IDispatchers {
                    override val IO = kotlinx.coroutines.Dispatchers.IO
                }
                
                fun useDispatchers(dispatchers: Dispatchers) {
                    runBlocking {
                        launch(dispatchers.IO) { }
                    }
                }
            """.trimIndent()
            assertThat(subject.lintWithContext(env, code)).isEmpty()
        }

        @Test
        fun `does not report when a custom dispatchers class is used as a constructor parameter`() {
            val code = """
                import kotlinx.coroutines.launch
                import kotlinx.coroutines.runBlocking
                import kotlinx.coroutines.CoroutineDispatcher
                
                interface IDispatchers {
                    val IO: CoroutineDispatcher
                }
                
                @Suppress("InjectDispatcher")
                object Dispatchers : IDispatchers {
                    override val IO = kotlinx.coroutines.Dispatchers.IO
                }
                
                class MyRepository(dispatchers: Dispatchers) {
                    init {
                        runBlocking { launch(dispatchers.IO) { } }
                    }
                }
            """.trimIndent()
            assertThat(subject.lintWithContext(env, code)).isEmpty()
        }

        @Test
        fun `does not report when a custom dispatchers class is used as a secondary constructor parameter`() {
            val code = """
                import kotlinx.coroutines.launch
                import kotlinx.coroutines.runBlocking
                import kotlinx.coroutines.CoroutineDispatcher
                
                interface IDispatchers {
                    val IO: CoroutineDispatcher
                }
                
                @Suppress("InjectDispatcher")
                object Dispatchers : IDispatchers {
                    override val IO = kotlinx.coroutines.Dispatchers.IO
                }
                
                class MyRepository(dispatchers: Dispatchers) {
                    constructor() : this(Dispatchers)
                }
            """.trimIndent()
            assertThat(subject.lintWithContext(env, code)).isEmpty()
        }

        @Test
        fun `does not report when a custom dispatchers class is used as a property`() {
            val code = """
                import kotlinx.coroutines.launch
                import kotlinx.coroutines.runBlocking
                import kotlinx.coroutines.CoroutineDispatcher
                
                interface IDispatchers {
                    val IO: CoroutineDispatcher
                }
                
                @Suppress("InjectDispatcher")
                object Dispatchers : IDispatchers {
                    override val IO = kotlinx.coroutines.Dispatchers.IO
                }
                
                class DispatcherUser(private val dispatchers: Dispatchers) {
                    fun dispatch() {
                        runBlocking { launch(dispatchers.IO) { } }
                    }
                }
            """.trimIndent()
            assertThat(subject.lintWithContext(env, code)).isEmpty()
        }

        @Test
        fun `does not report when a custom dispatchers class is used as a function parameter and there is a property with the same name but different type`() {
            val code = """
                import kotlinx.coroutines.launch
                import kotlinx.coroutines.runBlocking
                import kotlinx.coroutines.CoroutineDispatcher
                
                interface IDispatchers {
                    val IO: CoroutineDispatcher
                }
                
                @Suppress("InjectDispatcher")
                object Dispatchers : IDispatchers {
                    override val IO = kotlinx.coroutines.Dispatchers.IO
                }
                
                class DispatcherUser(private val dispatchers: String) {
                    fun dispatch(dispatchers: Dispatchers) {
                        runBlocking { launch(dispatchers.IO) { } }
                    }
                }
            """.trimIndent()
            assertThat(subject.lintWithContext(env, code)).isEmpty()
        }
    }

    @Nested
    inner class `InjectDispatcher with config specifying dispatcher names` {

        val subject = InjectDispatcher(TestConfig("dispatcherNames" to listOf("Main", "IO", "Default", "Confined")))

        @Test
        fun `reports when dispatcher main is used`() {
            val code = """
                import kotlinx.coroutines.launch
                import kotlinx.coroutines.runBlocking
                import kotlinx.coroutines.Dispatchers
                
                fun useDispatchers() {
                    runBlocking {
                        launch(Dispatchers.Main) { }
                    }
                }
            """.trimIndent()
            assertThat(subject.lintWithContext(env, code)).hasSize(1)
        }
    }
}
