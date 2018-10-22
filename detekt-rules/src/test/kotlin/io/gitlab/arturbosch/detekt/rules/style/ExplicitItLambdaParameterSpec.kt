package io.gitlab.arturbosch.detekt.rules.style

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.test.lint
import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.spek.api.dsl.given
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.api.dsl.on
import org.jetbrains.spek.subject.SubjectSpek

class ExplicitItLambdaParameterSpec : SubjectSpek<ExplicitItLambdaParameter>({
	subject { ExplicitItLambdaParameter(Config.empty) }

	given("lambda with single parameter") {
		on("single parameter with name `it` declared explicitly") {
			it("reports when parameter type is not declared") {
				val findings = subject.lint("""
				fun f() {
					val digits = 1234.let { it -> listOf(it) }
				}""")
				assertThat(findings).hasSize(1)
			}
			it("reports when parameter type is declared explicitly") {
				val findings = subject.lint("""
				fun f() {
					val lambda = { it: Int -> it.toString() }
				}""")
				assertThat(findings).hasSize(1)
			}
		}
		on("no parameter declared explicitly") {
			it("does not report implicit `it` parameter usage") {
				val findings = subject.lint("""
				fun f() {
					val lambda = { it.toString() }
					val digits = 1234.let { lambda(it) }.toList()
					val flat = listOf(listOf(1), listOf(2)).flatMap { it }
				}""")
				assertThat(findings).isEmpty()
			}
		}
	}
	given("some code using lambdas with (slightly) better style guidelines") {
		on("multiple parameters one of which with name `it` declared explicitly") {
			it("reports when parameter types are not declared") {
				val findings = subject.lint("""
				fun f() {
					val flat = listOf(listOf(1), listOf(2)).mapIndexed { index, it -> it + index }
				}""")
				assertThat(findings).hasSize(1)
			}
			it("reports when parameter types are declared explicitly"){
				val findings = subject.lint("""
				fun f() {
					val lambda = { it: Int, that: String -> it.toString() + that }
				}""")
				assertThat(findings).hasSize(1)
			}
		}
	}
})
