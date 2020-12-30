package io.gitlab.arturbosch.detekt.rules.coroutines

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.rules.setupKotlinEnvironment
import io.gitlab.arturbosch.detekt.test.compileAndLintWithContext
import org.assertj.core.api.Assertions
import org.jetbrains.kotlin.cli.jvm.compiler.KotlinCoreEnvironment
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

@Suppress("BlockingMethodInNonBlockingContext", "RedundantSuspendModifier")
object SleepInsteadOfDelaySpec : Spek({
    setupKotlinEnvironment()

    val env: KotlinCoreEnvironment by memoized()
    val subject by memoized { SleepInsteadOfDelay(Config.empty) }

    describe("SleepInsteadOfDelay rule") {
        it("should report Thread.sleep() in suspend functions") {
            val code = """
                suspend fun foo() {
                    Thread.sleep(1000L)
                }
            """
            Assertions.assertThat(subject.compileAndLintWithContext(env, code)).hasSize(1)
        }

        it("should report Thread.sleep() when Thread.sleep() is imported") {
            val code = """
                import java.lang.Thread.sleep

                suspend fun foo() {
                    sleep(1000L)
                }
            """
            Assertions.assertThat(subject.compileAndLintWithContext(env, code)).hasSize(1)
        }

        it("should report Thread.sleep() when Thread.sleep() is imported using wildcard") {
            val code = """
                import java.lang.Thread.*

                suspend fun foo() {
                    sleep(1000L)
                }
            """
            Assertions.assertThat(subject.compileAndLintWithContext(env, code)).hasSize(1)
        }

        it("should report Thread.sleep() in Coroutine blocks") {
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
            Assertions.assertThat(subject.compileAndLintWithContext(env, code)).hasSize(1)
        }
    }
})
