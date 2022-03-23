package io.gitlab.arturbosch.detekt.rules.coroutines

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.rules.KotlinCoreEnvironmentTest
import io.gitlab.arturbosch.detekt.test.compileAndLintWithContext
import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.kotlin.cli.jvm.compiler.KotlinCoreEnvironment
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

@KotlinCoreEnvironmentTest
class SuspendFunWithCoroutineScopeReceiverSpec(val env: KotlinCoreEnvironment) {

    val subject = SuspendFunWithCoroutineScopeReceiver(Config.empty)

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
            """
            assertThat(subject.compileAndLintWithContext(env, code)).hasSize(1)
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
            """
            assertThat(subject.compileAndLintWithContext(env, code)).hasSize(1)
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
            """
            assertThat(subject.compileAndLintWithContext(env, code)).hasSize(1)
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
            """
            assertThat(subject.compileAndLintWithContext(env, code)).hasSize(1)
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
            """
            assertThat(subject.compileAndLintWithContext(env, code)).isEmpty()
        }

        @Test
        fun `no reports when suspend function has no receiver`() {
            val code = """
                import kotlinx.coroutines.coroutineScope
                import kotlinx.coroutines.delay
                import kotlin.time.Duration.Companion.seconds

                suspend fun foo() = coroutineScope {
                    launch {
                        delay(timeMillis = 1000)
                    }
                }
            """
            assertThat(subject.compileAndLintWithContext(env, code)).isEmpty()
        }

        @Test
        fun `no reports when suspend function has Long as receiver`() {
            val code = """
                import kotlinx.coroutines.delay

                suspend fun Long.foo() = coroutineScope {
                    launch {
                        delay(timeMillis = this@foo)
                    }
                }
            """
            assertThat(subject.compileAndLintWithContext(env, code)).isEmpty()
        }
    }

    @Nested
    inner class SuspendFunWithCoroutineScopeLambda {

        @Test
        fun `reports when lambda parameter has suspend and explicit CoroutineScope receiver type`() {
            val code = """
                import kotlinx.coroutines.CoroutineScope
                import kotlinx.coroutines.launch
                import kotlinx.coroutines.delay
                import kotlinx.coroutines.coroutineScope

                suspend fun foo(action: suspend CoroutineScope.() -> Unit) = coroutineScope {
                    action()
                }
            """
            assertThat(subject.compileAndLintWithContext(env, code)).hasSize(1)
        }

        @Test
        fun `reports when lambda parameter has suspend and inherited CoroutineScope receiver type`() {
            val code = """
                import kotlinx.coroutines.CoroutineScope
                import kotlinx.coroutines.launch
                import kotlinx.coroutines.delay
                import kotlinx.coroutines.coroutineScope

                interface TestScope: CoroutineScope

                suspend fun foo(action: suspend TestScope.() -> Unit) = coroutineScope {
                    val scope = object: TestScope, CoroutineScope by this { }
                    scope.action()
                }
            """
            assertThat(subject.compileAndLintWithContext(env, code)).hasSize(1)
        }

        @Test
        fun `no report when lambda parameter has only CoroutineScope receiver type`() {
            val code = """
                import kotlinx.coroutines.CoroutineScope
                import kotlinx.coroutines.launch
                import kotlinx.coroutines.delay
                import kotlinx.coroutines.coroutineScope

                suspend fun foo(action: CoroutineScope.() -> Unit) = coroutineScope {
                    action()
                }
            """
            assertThat(subject.compileAndLintWithContext(env, code)).isEmpty()
        }

        @Test
        fun `no report when suspend lambda parameter has no CoroutineScope receiver type`() {
            val code = """
                import kotlinx.coroutines.CoroutineScope
                import kotlinx.coroutines.launch
                import kotlinx.coroutines.delay
                import kotlinx.coroutines.coroutineScope

                suspend fun foo(action: suspend Int.() -> Unit) {
                    1.action()
                }
            """
            assertThat(subject.compileAndLintWithContext(env, code)).isEmpty()
        }

        @Test
        fun `no report when suspend lambda parameter has no receiver`() {
            val code = """
                import kotlinx.coroutines.CoroutineScope
                import kotlinx.coroutines.launch
                import kotlinx.coroutines.delay
                import kotlinx.coroutines.coroutineScope

                suspend fun foo(action: suspend () -> Unit) {
                    action()
                }
            """
            assertThat(subject.compileAndLintWithContext(env, code)).isEmpty()
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
            """
            assertThat(subject.compileAndLintWithContext(env, code)).hasSize(2)
        }
    }
}
