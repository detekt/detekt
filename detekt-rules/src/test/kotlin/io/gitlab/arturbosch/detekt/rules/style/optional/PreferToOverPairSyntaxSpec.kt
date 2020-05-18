package io.gitlab.arturbosch.detekt.rules.style.optional

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.test.KtTestCompiler
import io.gitlab.arturbosch.detekt.test.compileAndLintWithContext
import org.assertj.core.api.Assertions.assertThat
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

object PreferToOverPairSyntaxSpec : Spek({
    val subject by memoized { PreferToOverPairSyntax(Config.empty) }

    val wrapper by memoized(
        factory = { KtTestCompiler.createEnvironment() },
        destructor = { it.dispose() }
    )

    describe("PreferToOverPairSyntax rule") {

        it("reports if pair is created using pair constructor") {
            val code =
                """
                val pair1 = Pair(1, 2)
                val pair2: Pair<Int, Int> = Pair(1, 2)
                val pair3 = Pair(Pair(1, 2), Pair(3, 4))
            """
            assertThat(subject.compileAndLintWithContext(wrapper.env, code)).hasSize(5)
        }

        it("does not report if it is created using the to syntax") {
            val code = "val pair = 1 to 2"
            assertThat(subject.compileAndLintWithContext(wrapper.env, code)).isEmpty()
        }

        it("does not report if a non-Kotlin Pair class was used") {
            val code =
                """
                val pair1 = Pair(1, 2)
                val pair2: Pair<Int, Int> = Pair(1, 2)
                val pair3 = Pair(Pair(1, 2), Pair(3, 4))

                data class Pair<T, Z>(val int1: T, val int2: Z)
            """
            assertThat(subject.compileAndLintWithContext(wrapper.env, code)).isEmpty()
        }
    }
})
