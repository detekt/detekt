package io.gitlab.arturbosch.detekt.rules.bugs

import io.gitlab.arturbosch.detekt.rules.CommonSpec
import org.jetbrains.spek.api.SubjectSpek
import org.jetbrains.spek.api.dsl.itBehavesLike

/**
 * @author Artur Bosch
 */
class NoElseInWhenSpec : SubjectSpek<NoElseInWhenExpression>({
	subject { NoElseInWhenExpression() }
	itBehavesLike(CommonSpec::class)
})