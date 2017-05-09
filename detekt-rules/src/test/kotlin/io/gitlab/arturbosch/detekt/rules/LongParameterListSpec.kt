package io.gitlab.arturbosch.detekt.rules

import io.gitlab.arturbosch.detekt.rules.complexity.LongParameterList
import org.jetbrains.spek.subject.SubjectSpek
import org.jetbrains.spek.subject.itBehavesLike

/**
 * @author Artur Bosch
 */
class LongParameterListSpec : SubjectSpek<LongParameterList>({
	subject { LongParameterList() }
	itBehavesLike(CommonSpec())
})