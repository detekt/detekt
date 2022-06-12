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
class SleepInsteadOfDelaySpec(val env: KotlinCoreEnvironment) {

    val subject = SleepInsteadOfDelay(Config.empty)

    @Test
    fun `should report no issue for delay() in suspend functions`() {
        val code = """
            import kotlinx.coroutines.delay

            suspend fun foo() {
                delay(1000L)
            }
        """
        assertThat(subject.compileAndLintWithContext(env, code)).hasSize(0)
    }

    @Test
    @DisplayName("should report Thread.sleep() in suspend functions")
    fun reportThreadSleepInSuspendFunctions() {
        val code = """
            suspend fun foo() {
                Thread.sleep(1000L)
            }
        """
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
        """
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
        """
        assertThat(subject.compileAndLintWithContext(env, code)).hasSize(1)
    }
}
