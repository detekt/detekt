package io.gitlab.arturbosch.detekt.rules.complexity

import io.gitlab.arturbosch.detekt.rules.Case
import io.gitlab.arturbosch.detekt.test.lint
import org.jetbrains.spek.api.dsl.given
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.subject.SubjectSpek
import kotlin.test.assertEquals

class LargeInterfaceSpec : SubjectSpek<LargeInterface>({
	subject { LargeInterface(threshold = 3) }

	given("several interface declarations") {

		it("reports interfaces which member size exceeds the threshold") {
			subject.lint(Case.LargeInterface.path())
			assertEquals(2, subject.findings.size)
		}
	}
})
