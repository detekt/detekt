package io.gitlab.arturbosch.detekt.rules.bugs

import io.gitlab.arturbosch.detekt.rules.setupKotlinEnvironment
import io.gitlab.arturbosch.detekt.test.assertThat
import io.gitlab.arturbosch.detekt.test.compileAndLintWithContext
import org.jetbrains.kotlin.cli.jvm.compiler.KotlinCoreEnvironment
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

class UnnecessaryNotNullOperatorSpec : Spek({
    setupKotlinEnvironment()

    val env: KotlinCoreEnvironment by memoized()
    val subject by memoized { UnnecessaryNotNullOperator() }

    describe("check unnecessary not null operators") {

        it("reports a simple not null operator usage") {
            val code = """
                val a = 1
                val b = a!!
                """
            val findings = subject.compileAndLintWithContext(env, code)
            assertThat(findings).hasSize(1)
            assertThat(findings).hasTextLocations(18 to 21)
        }

        it("reports a chained not null operator usage") {
            val code = """
                val a = 1
                val b = a!!.plus(42)
                """
            val findings = subject.compileAndLintWithContext(env, code)
            assertThat(findings).hasSize(1)
            assertThat(findings).hasTextLocations(18 to 21)
        }

        it("reports multiple chained not null operator usage") {
            val code = """
                val a = 1
                val b = a!!.plus(42)!!
                """
            val findings = subject.compileAndLintWithContext(env, code)
            assertThat(findings).hasSize(2)
            assertThat(findings).hasTextLocations(18 to 21, 18 to 32)
        }
    }

    describe("check valid not null operators usage") {

        it("does not report a simple not null operator usage on nullable type") {
            val code = """
                val a : Int? = 1
                val b = a!!
                """
            val findings = subject.compileAndLintWithContext(env, code)
            assertThat(findings).isEmpty()
        }
    }
})
