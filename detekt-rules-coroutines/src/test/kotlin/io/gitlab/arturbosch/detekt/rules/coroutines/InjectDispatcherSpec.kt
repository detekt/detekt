package io.gitlab.arturbosch.detekt.rules.coroutines

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.rules.KotlinCoreEnvironmentTest
import io.gitlab.arturbosch.detekt.test.TestConfig
import io.gitlab.arturbosch.detekt.test.compileAndLintWithContext
import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.kotlin.cli.jvm.compiler.KotlinCoreEnvironment
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

@KotlinCoreEnvironmentTest
class InjectDispatcherSpec(val env: KotlinCoreEnvironment) {

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
            """
            assertThat(subject.compileAndLintWithContext(env, code)).hasSize(1)
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
            """
            assertThat(subject.compileAndLintWithContext(env, code)).isEmpty()
        }

        @Test
        fun `does not report when dispatcher is used as a constructor parameter`() {
            val code = """
                import kotlinx.coroutines.CoroutineDispatcher
                import kotlinx.coroutines.Dispatchers

                class MyRepository(dispatcher: CoroutineDispatcher = Dispatchers.IO)
            """
            assertThat(subject.compileAndLintWithContext(env, code)).isEmpty()
        }

        @Test
        fun `does not report when dispatcher is used as a property`() {
            val code = """
                import kotlinx.coroutines.CoroutineDispatcher
                import kotlinx.coroutines.Dispatchers

                class MyRepository(private val dispatcher: CoroutineDispatcher = Dispatchers.IO)
            """
            assertThat(subject.compileAndLintWithContext(env, code)).isEmpty()
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
            """
            assertThat(subject.compileAndLintWithContext(env, code)).isEmpty()
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
            """
            assertThat(subject.compileAndLintWithContext(env, code)).hasSize(1)
        }
    }
}
