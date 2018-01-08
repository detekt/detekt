package io.gitlab.arturbosch.detekt.rules.empty

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.test.lint
import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.spek.api.dsl.given
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.subject.SubjectSpek

/**
 * @author Egor Neliuba
 */
class EmptyClassBlockSpec : SubjectSpek<EmptyClassBlock>({

	subject { EmptyClassBlock(Config.empty) }

	given("a class with an empty body") {

		it("flags the empty body") {
			val findings = subject.lint("class SomeClass {}")
			assertThat(findings).hasSize(1)
		}
	}

	given("an object with an empty body") {

		it("flags the object if it is of a non-anonymous class") {
			val findings = subject.lint("object SomeObject {}")
			assertThat(findings).hasSize(1)
		}

		it("does not flag the object if it is of an anonymous class") {
			val findings = subject.lint("object : SomeClass {}")
			assertThat(findings).isEmpty()
		}
	}
})
