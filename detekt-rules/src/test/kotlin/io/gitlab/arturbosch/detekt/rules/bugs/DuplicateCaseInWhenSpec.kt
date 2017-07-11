package io.gitlab.arturbosch.detekt.rules.bugs

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.rules.CommonSpec
import org.jetbrains.spek.subject.SubjectSpek
import org.jetbrains.spek.subject.itBehavesLike

/**
 * @author Artur Bosch
 */
class DuplicateCaseInWhenSpec : SubjectSpek<DuplicateCaseInWhenExpression>({
	subject { DuplicateCaseInWhenExpression(Config.empty) }
	itBehavesLike(CommonSpec())
})
