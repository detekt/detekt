package io.gitlab.arturbosch.detekt.rules.complexity

import io.gitlab.arturbosch.detekt.rules.Case
import io.gitlab.arturbosch.detekt.test.lint
import org.jetbrains.spek.api.dsl.given
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.subject.SubjectSpek
import kotlin.test.assertEquals

class MethodOverloadingSpec : SubjectSpek<MethodOverloading>({
	subject { MethodOverloading(threshold = 2) }

	given("several overloaded methods") {

		it("reports overloaded method count over threshold") {
			subject.lint(Case.OverloadedMethods.path())
			assertEquals(subject.findings.size, 3)
		}
	}
})
