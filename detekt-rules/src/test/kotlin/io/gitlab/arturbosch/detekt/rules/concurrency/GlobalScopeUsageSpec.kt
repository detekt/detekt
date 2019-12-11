package io.gitlab.arturbosch.detekt.rules.concurrency

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.test.KtTestCompiler
import io.gitlab.arturbosch.detekt.test.assertThat
import io.gitlab.arturbosch.detekt.test.compileAndLintWithContext
import kotlinx.coroutines.GlobalScope
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe
import java.io.File

private val coroutineClasspath = File(GlobalScope::class.java.protectionDomain.codeSource.location.path)

object GlobalScopeUsageSpec : Spek({
    val subject by memoized { GlobalScopeUsage(Config.empty) }

    val wrapper by memoized(
        factory = { KtTestCompiler.createEnvironment(listOf(coroutineClasspath)) },
        destructor = { it.dispose() }
    )

    describe("GlobalScopeUsage rule") {

        it("should report GlobalScope.launch") {
            val code = """
                import kotlinx.coroutines.delay
                import kotlinx.coroutines.GlobalScope
                import kotlinx.coroutines.launch

                fun foo() {
                    GlobalScope.launch { delay(1_000L) }
                }
            """
            assertThat(subject.compileAndLintWithContext(wrapper.env, code)).hasSize(1)
        }

        it("should report GlobalScope.async") {
            val code = """
                import kotlinx.coroutines.async
                import kotlinx.coroutines.delay
                import kotlinx.coroutines.GlobalScope

                fun foo() {
                    GlobalScope.async { delay(1_000L) }
                }
            """
            assertThat(subject.compileAndLintWithContext(wrapper.env, code)).hasSize(1)
        }

        it("should report bar(GlobalScope)") {
            val code = """
                import kotlinx.coroutines.CoroutineScope
                import kotlinx.coroutines.GlobalScope

                fun bar(scope: CoroutineScope) = Unit

                fun foo() {
                    bar(GlobalScope)
                }
            """
            assertThat(subject.compileAndLintWithContext(wrapper.env, code)).hasSize(1)
        }

        it("should report `val scope = GlobalScope`") {
            val code = """
                import kotlinx.coroutines.CoroutineScope
                import kotlinx.coroutines.GlobalScope

                fun bar(scope: CoroutineScope) = Unit

                fun foo() {
                    val scope = GlobalScope
                    bar(scope)
                }
            """
            assertThat(subject.compileAndLintWithContext(wrapper.env, code)).hasSize(1)
        }

        it("should not report false positive") {
            val code = """
                interface CoroutineScope
                object GlobalScope : CoroutineScope

                fun bar(scope: CoroutineScope) = Unit

                fun foo() {
                    bar(GlobalScope)
                }
            """
            assertThat(subject.compileAndLintWithContext(wrapper.env, code)).hasSize(0)
        }
    }
})
