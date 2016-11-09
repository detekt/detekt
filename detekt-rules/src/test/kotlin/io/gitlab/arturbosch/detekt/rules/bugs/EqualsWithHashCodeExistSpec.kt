package io.gitlab.arturbosch.detekt.rules.bugs

import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import com.natpryce.hamkrest.hasSize
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.compileForTest
import io.gitlab.arturbosch.detekt.rules.Case
import io.gitlab.arturbosch.detekt.rules.CommonSpec
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

		assertThat(subject.findings, hasSize(equalTo(2)))
	}
}