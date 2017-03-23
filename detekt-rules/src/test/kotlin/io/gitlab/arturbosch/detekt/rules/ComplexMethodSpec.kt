package io.gitlab.arturbosch.detekt.rules

import io.gitlab.arturbosch.detekt.api.ThresholdedCodeSmell
import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.it

/**
 * @author Artur Bosch
 */
class ComplexMethodSpec : Spek({

	it("finds one complex method") {
		val subject = ComplexMethod()
		val root = load(Case.ComplexClass)
		subject.visit(root)
		assertThat(subject.findings).hasSize(1)
		assertThat((subject.findings[0] as ThresholdedCodeSmell).value).isEqualTo(13)
		assertThat((subject.findings[0] as ThresholdedCodeSmell).threshold).isEqualTo(10)
	}
})

