package io.gitlab.arturbosch.detekt.rules.coroutines

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.rules.setupKotlinEnvironment
import io.gitlab.arturbosch.detekt.test.TestConfig
import io.gitlab.arturbosch.detekt.test.compileAndLintWithContext
import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.kotlin.cli.jvm.compiler.KotlinCoreEnvironment
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

object InjectDispatcherSpec : Spek({

    setupKotlinEnvironment()
    val env: KotlinCoreEnvironment by memoized()

    describe("InjectDispatcher with default config") {

        val subject by memoized { InjectDispatcher(Config.empty) }

        it("reports when dispatchers is used inside a function") {
            val code = """
                import kotlinx.coroutines.coroutineScope
                import kotlinx.coroutines.Dispatchers

                fun useDispatchers() {
                    coroutineScope(Dispatchers.IO).launch()
                }
                """
            assertThat(subject.compileAndLintWithContext(env, code)).hasSize(1)
        }

        it("does not report when dispatcher is used as a function parameter") {
            val code = """
                import kotlinx.coroutines.coroutineScope
                import kotlinx.coroutines.CoroutineDispatcher
                import kotlinx.coroutines.Dispatchers

                fun useDispatchers(dispatcher: CoroutineDispatcher = Dispatchers.IO) {
                    coroutineScope(dispatcher).launch()
                }
                """
            assertThat(subject.compileAndLintWithContext(env, code)).isEmpty()
        }

        it("does not report when dispatcher is used as a constructor parameter") {
            val code = """
                import kotlinx.coroutines.CoroutineDispatcher
                import kotlinx.coroutines.Dispatchers

                class MyRepository(dispatcher: CoroutineDispatcher = Dispatchers.IO)
                """
            assertThat(subject.compileAndLintWithContext(env, code)).isEmpty()
        }

        it("does not report when dispatcher is used as a property") {
            val code = """
                import kotlinx.coroutines.CoroutineDispatcher
                import kotlinx.coroutines.Dispatchers

                class MyRepository(private val dispatcher: CoroutineDispatcher = Dispatchers.IO)
                """
            assertThat(subject.compileAndLintWithContext(env, code)).isEmpty()
        }

        it("does not report when dispatcher main is used") {
            val code = """
                import kotlinx.coroutines.coroutineScope
                import kotlinx.coroutines.Dispatchers

                fun useDispatchers() {
                    coroutineScope(Dispatchers.Main).launch()
                }
                """
            assertThat(subject.compileAndLintWithContext(env, code)).isEmpty()
        }
    }

    describe("InjectDispatcher with config specifying dispatcher names") {

        val subject by memoized { InjectDispatcher(TestConfig("dispatcherNames" to listOf("Main", "IO", "Default", "Confined"))) }

        it("reports when dispatcher main is used") {
            val code = """
                import kotlinx.coroutines.coroutineScope
                import kotlinx.coroutines.Dispatchers

                fun useDispatchers() {
                    coroutineScope(Dispatchers.Main).launch()
                }
                """
            assertThat(subject.compileAndLintWithContext(env, code)).hasSize(1)
        }
    }
})
