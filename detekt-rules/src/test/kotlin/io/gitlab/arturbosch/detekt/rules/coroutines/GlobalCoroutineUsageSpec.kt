package io.gitlab.arturbosch.detekt.rules.coroutines

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.test.assertThat
import io.gitlab.arturbosch.detekt.test.compileAndLint
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

object GlobalCoroutineUsageSpec : Spek({
    val subject by memoized { GlobalCoroutineUsage(Config.empty) }

    describe("GlobalCoroutineUsage rule") {

        it("should report GlobalScope.launch") {
            val code = """
                import kotlinx.coroutines.delay
                import kotlinx.coroutines.GlobalScope
                import kotlinx.coroutines.launch

                fun foo() {
                    GlobalScope.launch { delay(1_000L) }
                }
            """
            assertThat(subject.compileAndLint(code)).hasSize(1)
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
            assertThat(subject.compileAndLint(code)).hasSize(1)
        }

        it("should not report bar(GlobalScope)") {
            val code = """
                import kotlinx.coroutines.CoroutineScope
                import kotlinx.coroutines.GlobalScope

                fun bar(scope: CoroutineScope) = Unit

                fun foo() {
                    bar(GlobalScope)
                }
            """
            assertThat(subject.compileAndLint(code)).isEmpty()
        }

        it("should not report `val scope = GlobalScope`") {
            val code = """
                import kotlinx.coroutines.CoroutineScope
                import kotlinx.coroutines.GlobalScope

                fun bar(scope: CoroutineScope) = Unit

                fun foo() {
                    val scope = GlobalScope
                    bar(scope)
                }
            """
            assertThat(subject.compileAndLint(code)).isEmpty()
        }
    }
})
