package io.gitlab.arturbosch.detekt.rules.complexity

import io.gitlab.arturbosch.detekt.rules.Case
import io.gitlab.arturbosch.detekt.test.TestConfig
import io.gitlab.arturbosch.detekt.test.lint
import org.jetbrains.spek.api.dsl.given
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.subject.SubjectSpek
import kotlin.test.assertEquals

class ComplexInterfaceSpec : SubjectSpek<ComplexInterface>({
	subject { ComplexInterface(threshold = THRESHOLD) }

	given("several interface declarations") {

		val path = Case.ComplexInterface.path()

		it("reports interfaces which member size exceeds the threshold") {
			subject.lint(path)
			assertEquals(2, subject.findings.size)
		}

		it("reports interfaces which member size exceeds the threshold including static declarations") {
			val config = TestConfig(mapOf(ComplexInterface.INCLUDE_STATIC_DECLARATIONS to "true"))
			val rule = ComplexInterface(config, threshold = THRESHOLD)
			rule.lint(path)
			assertEquals(3, rule.findings.size)
		}
	}
})

private const val THRESHOLD = 3
