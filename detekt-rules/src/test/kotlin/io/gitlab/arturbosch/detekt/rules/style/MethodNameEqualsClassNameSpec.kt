package io.gitlab.arturbosch.detekt.rules.style

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.rules.Case
import io.gitlab.arturbosch.detekt.test.TestConfig
import io.gitlab.arturbosch.detekt.test.lint
import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.spek.api.dsl.given
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.subject.SubjectSpek

class MethodNameEqualsClassNameSpec : SubjectSpek<MethodNameEqualsClassName>({
	subject { MethodNameEqualsClassName(Config.empty) }

	given("some classes with methods") {

		val path = Case.MethodNameEqualsClassName.path()
		val findings = subject.lint(path)

		it("reports methods which are named after the class") {
			assertThat(findings).hasSize(4)
		}

		it("reports methods which are named after the class object") {
			val objectFindings = findings.filter { it.issue.description.contains("object") }
			assertThat(objectFindings).hasSize(1)
		}

		it("reports methods which are named after the class object including overridden functions") {
			val config = TestConfig(mapOf("ignoreOverriddenFunction" to "false"))
			val rule = MethodNameEqualsClassName(config)
			assertThat(rule.lint(path)).hasSize(5)
		}
	}
})
