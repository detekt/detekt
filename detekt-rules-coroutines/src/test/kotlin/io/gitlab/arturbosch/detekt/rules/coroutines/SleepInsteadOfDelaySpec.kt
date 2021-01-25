package io.gitlab.arturbosch.detekt.rules.coroutines

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.rules.setupKotlinEnvironment
import io.gitlab.arturbosch.detekt.test.compileAndLintWithContext
import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.kotlin.cli.jvm.compiler.KotlinCoreEnvironment
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

@Suppress("BlockingMethodInNonBlockingContext", "RedundantSuspendModifier")
object SleepInsteadOfDelaySpec : Spek({
    setupKotlinEnvironment()

    val env: KotlinCoreEnvironment by memoized()
    val subject by memoized { SleepInsteadOfDelay(Config.empty) }

    describe("SleepInsteadOfDelay rule") {
        it("should report no issue for delay() in suspend functions") {
            val code = """
                import kotlinx.coroutines.delay

                suspend fun foo() {
                    delay(1000L)
                }
            """
            assertThat(subject.compileAndLintWithContext(env, code)).hasSize(0)
        }

        it("should report Thread.sleep() in suspend functions") {
            val code = """
                suspend fun foo() {
                    Thread.sleep(1000L)
                }
            """
            assertThat(subject.compileAndLintWithContext(env, code)).hasSize(1)
        }

        it("should report Thread.sleep() in CoroutineScope.launch()") {
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

        it("should report Thread.sleep() in CoroutineScope.async()") {
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
})
