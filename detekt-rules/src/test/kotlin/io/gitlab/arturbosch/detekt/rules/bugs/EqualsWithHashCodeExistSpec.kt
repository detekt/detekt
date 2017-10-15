package io.gitlab.arturbosch.detekt.rules.bugs

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.rules.Case
import io.gitlab.arturbosch.detekt.rules.CommonSpec
import io.gitlab.arturbosch.detekt.test.lint
import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.spek.api.dsl.given
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.subject.SubjectSpek
import org.jetbrains.spek.subject.itBehavesLike

/**
 * @author Artur Bosch
 */
class EqualsWithHashCodeExistSpec : SubjectSpek<EqualsWithHashCodeExist>({
	subject { EqualsWithHashCodeExist(Config.empty) }
	itBehavesLike(CommonSpec())

	given("some classes with equals() functions") {

		it("reports equals() without hashCode() functions") {
			val path = Case.NestedClasses.path()
			assertThat(subject.lint(path)).hasSize(2)
		}
	}
})
