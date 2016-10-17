package io.gitlab.arturbosch.detekt.rules

import io.gitlab.arturbosch.detekt.Case
import io.gitlab.arturbosch.detekt.load
import org.jetbrains.spek.api.SubjectSpek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.api.dsl.itBehavesLike
import kotlin.test.assertEquals

/**
 * @author Artur Bosch
 */
class LargeClassSpec : SubjectSpek<LargeClass>({
	subject { LargeClass() }
	itBehavesLike(CommonSpec::class)

	describe("nested classes are also considered") {
		it("should detect only the nested large class") {
			val root = load(Case.NestedClasses)
			subject.visit(root)
			assertEquals(subject.findings.size, 1)
		}
	}
})