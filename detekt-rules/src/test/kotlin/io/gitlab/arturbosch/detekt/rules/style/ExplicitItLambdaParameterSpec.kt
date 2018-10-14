package io.gitlab.arturbosch.detekt.rules.style

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.test.lint
import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.spek.api.dsl.given
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.subject.SubjectSpek

class ExplicitItLambdaParameterSpec : SubjectSpek<ExplicitItLambdaParameter>({
	subject { ExplicitItLambdaParameter(Config.empty) }

	given("some code using single parameter lambdas extensively with poor style guidelines") {
		it("reports every single it parameter declared explicitly") {
			val findings = subject.lint("""
				fun f() {
					val lambda = { it: Int -> it.toString() }
					val digits = 1234.let { it -> lambda(it) }.toList()
					val flat = listOf(listOf(1), listOf(2)).flatMap { it -> it }
				}""")
			assertThat(findings).hasSize(3)
		}
	}
	given("some code using lambdas with (slightly) better style guidelines"){
		it("does not report explicit it parameter when there are multiple parameters") {
			val findings = subject.lint("""
				fun f() {
					val lambda = { it: Int, that: String -> it.toString() + that }
					val flat = listOf(listOf(1), listOf(2)).mapIndexed { index, it -> it + index }
				}""")
			assertThat(findings).isEmpty()
		}
		it("does not report compliant (implicit) it parameters") {
			val findings = subject.lint("""
				fun f() {
					val lambda = { it.toString() }
					val digits = 1234.let { lambda(it) }.toList()
					val flat = listOf(listOf(1), listOf(2)).flatMap { it }
				}""")
			assertThat(findings).isEmpty()
		}
	}
})
