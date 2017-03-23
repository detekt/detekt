package io.gitlab.arturbosch.detekt.rules

import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import com.natpryce.hamkrest.hasSize
import io.gitlab.arturbosch.detekt.api.ThresholdedCodeSmell
import io.gitlab.arturbosch.detekt.rules.complexity.ComplexMethod
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
		assertThat(subject.findings, hasSize(equalTo(1)))
		assertThat((subject.findings[0] as ThresholdedCodeSmell).value, equalTo(13))
		assertThat((subject.findings[0] as ThresholdedCodeSmell).threshold, equalTo(10))
	}
})

