package io.gitlab.arturbosch.detekt.rules

import org.jetbrains.spek.api.SubjectSpek
import org.jetbrains.spek.api.dsl.itBehavesLike

/**
 * @author Artur Bosch
 */
class OptionalUnitSpec : SubjectSpek<OptionalUnit>({
	subject { OptionalUnit() }
	itBehavesLike(CommonSpec::class)
})