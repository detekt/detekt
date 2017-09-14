package io.gitlab.arturbosch.detekt.rules.complexity

import io.gitlab.arturbosch.detekt.api.ThresholdedCodeSmell
import io.gitlab.arturbosch.detekt.rules.Case
import io.gitlab.arturbosch.detekt.test.lint
import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.it

/**
 * @author Artur Bosch
 */
class ComplexMethodSpec : Spek({

	it("finds one complex method") {
		val subject = ComplexMethod()
		subject.lint(Case.ComplexClass.path())
		assertThat(subject.findings).hasSize(1)
		assertThat((subject.findings[0] as ThresholdedCodeSmell).value).isEqualTo(20)
		assertThat((subject.findings[0] as ThresholdedCodeSmell).threshold).isEqualTo(10)
	}
})
