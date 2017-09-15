package io.gitlab.arturbosch.detekt.rules.style.optional

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.test.lint
import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.subject.SubjectSpek

/**
 * @author Artur Bosch
 */
class OptionalReturnKeywordSpec : SubjectSpek<OptionalReturnKeyword>({

	subject { OptionalReturnKeyword(Config.empty) }

	describe("") {

		it("should be val z = if (true) x else y") {
			val actual = "val z = if (true) return x else return y"

			assertThat(subject.lint(actual)).hasSize(2)
		}

		it("should not report return which is used for breaking out of a function") {
			val actual = """
				fun test() {
					val a = try {
						returnsIntOrThrows()
					} catch (e: Exception) {
						return
					}
					println(a)
				}
			"""

			assertThat(subject.lint(actual).size).isEqualTo(0)
		}

		/* should be
		val z =
		if (true)
			if (true) {
				if (false) return b;
				x
			} else a
		else y
		*/
		it("should report 5 in complex condition") {
			val actual = """
			val z =
			if (true)
				return if (true) { // positive
					if (false) return b;
					return x // positive
				} else a
			else return y // positive"""

			assertThat(subject.lint(actual).size).isEqualTo(3)
		}
	}
})
