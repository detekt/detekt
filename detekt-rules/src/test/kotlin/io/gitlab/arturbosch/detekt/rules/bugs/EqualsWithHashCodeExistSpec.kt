package io.gitlab.arturbosch.detekt.rules.bugs

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.rules.Case
import io.gitlab.arturbosch.detekt.rules.CommonSpec
import io.gitlab.arturbosch.detekt.test.compileForTest
import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.spek.api.SubjectSpek
import org.jetbrains.spek.api.dsl.itBehavesLike
import org.junit.jupiter.api.Test

/**
 * @author Artur Bosch
 */
class EqualsWithHashCodeExistSpec : SubjectSpek<EqualsWithHashCodeExist>({
	subject { EqualsWithHashCodeExist(Config.empty) }
	itBehavesLike(CommonSpec::class)
})

class EqualsWithHashCodeExistTest {

	@Test
	fun nestedClasses() {
		val subject = EqualsWithHashCodeExist(Config.empty)
		val file = compileForTest(Case.NestedClasses.path())

		subject.visit(file)

		assertThat(subject.findings).hasSize(2)
	}
}