package io.gitlab.arturbosch.detekt.rules.bugs

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.rules.CommonSpec
import org.jetbrains.spek.api.SubjectSpek
import org.jetbrains.spek.api.dsl.itBehavesLike

/**
 * @author Artur Bosch
 */
class EqualsWithHashCodeExistSpec : SubjectSpek<EqualsWithHashCodeExist>({
	subject { EqualsWithHashCodeExist(Config.empty) }
	itBehavesLike(CommonSpec::class)
})