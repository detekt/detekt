package io.gitlab.arturbosch.detekt.rules

import io.gitlab.arturbosch.detekt.rules.complexity.LongParameterList
import org.jetbrains.spek.api.SubjectSpek
import org.jetbrains.spek.api.dsl.itBehavesLike

/**
 * @author Artur Bosch
 */
class LongParameterListSpec : SubjectSpek<LongParameterList>({
	subject { LongParameterList() }
	itBehavesLike(CommonSpec::class)
})
