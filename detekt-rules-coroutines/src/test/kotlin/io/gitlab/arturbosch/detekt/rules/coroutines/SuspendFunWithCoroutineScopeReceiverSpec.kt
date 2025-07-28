package io.gitlab.arturbosch.detekt.rules.coroutines

import dev.detekt.test.utils.KotlinEnvironmentContainer
import dev.detekt.api.Config
import dev.detekt.test.utils.KotlinCoreEnvironmentTest
import dev.detekt.test.lintWithContext
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

@KotlinCoreEnvironmentTest
class SuspendFunWithCoroutineScopeReceiverSpec(private val env: KotlinEnvironmentContainer) {

    private val subject = SuspendFunWithCoroutineScopeReceiver(Config.empty)

    @Nested
    inner class `SuspendFunWithCoroutineScopeReceiver rule` {

        @Test
        fun `reports when top-level suspend function has explicit CoroutineScope receiver type`() {
            val code = """
                import kotlinx.coroutines.CoroutineScope
                import kotlinx.coroutines.launch
                import kotlinx.coroutines.delay
                
                suspend fun CoroutineScope.foo() {
                    launch {
                        delay(timeMillis = 1000)
                    }
                }
            """.trimIndent()
            assertThat(subject.lintWithContext(env, code)).hasSize(1)
        }

        @Test
        fun `reports when top-level suspend function has explicit CoroutineScope receiver type and star import used`() {
            val code = """
                import kotlinx.coroutines.*
                
                suspend fun CoroutineScope.foo() {
                    launch {
                        delay(timeMillis = 1000)
                    }
                }
            """.trimIndent()
            assertThat(subject.lintWithContext(env, code)).hasSize(1)
        }

        @Test
        fun `reports when top-level suspend function has explicit FQN CoroutineScope receiver type`() {
            val code = """
                import kotlinx.coroutines.launch
                import kotlinx.coroutines.delay
                
                suspend fun kotlinx.coroutines.CoroutineScope.foo() {
                    launch {
                        delay(timeMillis = 1000)
                    }
                }
            """.trimIndent()
            assertThat(subject.lintWithContext(env, code)).hasSize(1)
        }

        @Test
        fun `reports when suspend function has a receiver which inherits from CoroutineScope`() {
            val code = """
                import kotlinx.coroutines.CoroutineScope
                import kotlinx.coroutines.launch
                import kotlinx.coroutines.delay
                
                interface TestScope: CoroutineScope
                
                suspend fun TestScope.foo() {
                    launch {
                        delay(timeMillis = 1000)
                    }
                }
            """.trimIndent()
            assertThat(subject.lintWithContext(env, code)).hasSize(1)
        }

        @Test
        fun `no reports when plain function has a CoroutineScope as receiver`() {
            val code = """
                import kotlinx.coroutines.CoroutineScope
                import kotlinx.coroutines.launch
                import kotlinx.coroutines.delay
                
                fun CoroutineScope.foo() {
                    launch {
                        delay(timeMillis = 1000)
                    }
                }
            """.trimIndent()
            assertThat(subject.lintWithContext(env, code)).isEmpty()
        }

        @Test
        fun `no reports when suspend function has no receiver`() {
            val code = """
                import kotlinx.coroutines.coroutineScope
                import kotlinx.coroutines.delay
                import kotlinx.coroutines.launch
                import kotlin.time.Duration.Companion.seconds
                
                suspend fun foo() = coroutineScope {
                    launch {
                        delay(timeMillis = 1000)
                    }
                }
            """.trimIndent()
            assertThat(subject.lintWithContext(env, code)).isEmpty()
        }

        @Test
        fun `no reports when suspend function has Long as receiver`() {
            val code = """
                import kotlinx.coroutines.delay
                import kotlinx.coroutines.launch
                import kotlinx.coroutines.CoroutineScope
                import kotlinx.coroutines.coroutineScope
                
                suspend fun Long.foo() = coroutineScope {
                    launch {
                        delay(timeMillis = this@foo)
                    }
                }
            """.trimIndent()
            assertThat(subject.lintWithContext(env, code)).isEmpty()
        }

        @Test
        fun `should ignore the issue suppression`() {
            val code = """
                import kotlinx.coroutines.CoroutineScope
                import kotlinx.coroutines.delay

                @Suppress("SuspendFunWithCoroutineScopeReceiver")
                suspend fun CoroutineScope.foo() {
                    delay(timeMillis = 1000)
                }
            """.trimIndent()
            assertThat(subject.lintWithContext(env, code)).isEmpty()
        }
    }

    @Nested
    inner class SuspendCoroutineFunWithCoroutineScopeLambda {

        @Test
        fun `reports when lambda parameter has suspend CoroutineScope receiver type and its lambda too`() {
            val code = """
                import kotlinx.coroutines.CoroutineScope
                import kotlinx.coroutines.launch
                import kotlinx.coroutines.delay
                import kotlinx.coroutines.coroutineScope
                
                suspend fun CoroutineScope.foo(action: suspend CoroutineScope.() -> Unit) {
                    action()
                }
            """.trimIndent()
            assertThat(subject.lintWithContext(env, code)).hasSize(1)
        }
    }
}
