package io.gitlab.arturbosch.detekt.rules.style

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.test.lint
import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.spek.api.dsl.given
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.subject.SubjectSpek

/**
 * @author Artur Bosch
 */
class ExpressionBodySyntaxSpec : SubjectSpek<ExpressionBodySyntax>({
	subject { ExpressionBodySyntax(Config.empty) }

	given("several return statements") {

		it("reports constant return") {
			assertThat(subject.lint("""
				fun stuff(): Int {
					return 5
				}
			"""
			)).hasSize(1)
		}

		it("reports return statement with method chain") {
			assertThat(subject.lint("""
				fun stuff(): Int {
					return moreStuff().getStuff().stuffStuff()
				}
			"""
			)).hasSize(1)
		}

		it("reports return statement with multiline method chain") {
			assertThat(subject.lint("""
				fun stuff(): Int {
					return moreStuff()
					.getStuff()
					.stuffStuff()
				}
			""")).hasSize(1)
		}

		it("reports return statements with conditionals") {
			assertThat(subject.lint("""
				fun stuff(): Int {
					return if (true) return 5 else return 3
				}
				fun stuff(): Int {
					return try { return 5 } catch (e: Exception) { return 3 }
				}
			""")).hasSize(2)
		}

		it("does not report multiple if statements") {
			assertThat(subject.lint("""
				fun stuff(): Int {
					if (true) return true
					return false
				}
			""")).hasSize(0)
		}
	}
})
