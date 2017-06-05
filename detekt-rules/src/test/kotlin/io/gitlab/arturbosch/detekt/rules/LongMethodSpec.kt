package io.gitlab.arturbosch.detekt.rules

import io.gitlab.arturbosch.detekt.rules.complexity.LongMethod
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.subject.SubjectSpek
import kotlin.test.assertEquals

/**
 * @author Artur Bosch
 */
class LongMethodSpec : SubjectSpek<LongMethod>({
	subject { LongMethod(threshold = 10) }

	describe("nested functions can be long") {
		it("should find two long methods") {
			val root = load(Case.NestedLongMethods)
			subject.visit(root)
			assertEquals(subject.findings.size, 2)
		}
	}

	describe("nested classes can contain long methods") {
		it("should detect one nested long method") {
			val root = load(Case.NestedClasses)
			subject.visit(root)
			assertEquals(subject.findings.size, 1)
		}

	}
})