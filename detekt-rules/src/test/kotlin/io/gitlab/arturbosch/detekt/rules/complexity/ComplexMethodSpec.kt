package io.gitlab.arturbosch.detekt.rules.complexity

import io.gitlab.arturbosch.detekt.api.ThresholdedCodeSmell
import io.gitlab.arturbosch.detekt.rules.Case
import io.gitlab.arturbosch.detekt.test.TestConfig
import io.gitlab.arturbosch.detekt.test.lint
import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.given
import org.jetbrains.spek.api.dsl.it

/**
 * @author Artur Bosch
 */
class ComplexMethodSpec : Spek({

	given("a complex method") {

		it("finds one complex method") {
			val subject = ComplexMethod()
			subject.lint(Case.ComplexClass.path())
			assertThat(subject.findings).hasSize(1)
			assertThat((subject.findings[0] as ThresholdedCodeSmell).value).isEqualTo(20)
			assertThat((subject.findings[0] as ThresholdedCodeSmell).threshold).isEqualTo(10)
		}
	}

	given("several complex methods") {

		val path = Case.ComplexMethods.path()

		it("does not report complex methods with a single when expression") {
			val config = TestConfig(mapOf(ComplexMethod.IGNORE_SINGLE_WHEN_EXPRESSION to "true"))
			val subject = ComplexMethod(config, threshold = 4)
			assertThat(subject.lint(path)).hasSize(1)
		}

		it("reports all complex methods") {
			val subject = ComplexMethod(threshold = 4)
			assertThat(subject.lint(path)).hasSize(3)
		}
	}
})
