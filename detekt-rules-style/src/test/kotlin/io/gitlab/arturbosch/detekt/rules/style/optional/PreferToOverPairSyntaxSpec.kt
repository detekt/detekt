package io.gitlab.arturbosch.detekt.rules.style.optional

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.rules.setupKotlinEnvironment
import io.gitlab.arturbosch.detekt.test.compileAndLintWithContext
import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.kotlin.cli.jvm.compiler.KotlinCoreEnvironment
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

object PreferToOverPairSyntaxSpec : Spek({
    setupKotlinEnvironment()

    val env: KotlinCoreEnvironment by memoized()
    val subject by memoized { PreferToOverPairSyntax(Config.empty) }

    describe("PreferToOverPairSyntax rule") {

        it("reports if pair is created using pair constructor") {
            val code = """
                val pair1 = Pair(1, 2)
                val pair2: Pair<Int, Int> = Pair(1, 2)
                val pair3 = Pair(Pair(1, 2), Pair(3, 4))
            """

            val findings = subject.compileAndLintWithContext(env, code)
            assertThat(findings).hasSize(5)
            assertThat(findings[0].message).endsWith("`1 to 2`")
        }

        it("does not report if it is created using the to syntax") {
            val code = "val pair = 1 to 2"
            assertThat(subject.compileAndLintWithContext(env, code)).isEmpty()
        }

        it("does not report if a non-Kotlin Pair class was used") {
            val code = """
                val pair1 = Pair(1, 2)
                val pair2: Pair<Int, Int> = Pair(1, 2)
                val pair3 = Pair(Pair(1, 2), Pair(3, 4))

                data class Pair<T, Z>(val int1: T, val int2: Z)
            """
            assertThat(subject.compileAndLintWithContext(env, code)).isEmpty()
        }
    }
})
