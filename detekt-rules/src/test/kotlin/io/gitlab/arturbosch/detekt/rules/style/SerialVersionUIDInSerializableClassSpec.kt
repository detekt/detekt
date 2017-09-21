package io.gitlab.arturbosch.detekt.rules.style

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.rules.Case
import io.gitlab.arturbosch.detekt.test.lint
import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.spek.api.dsl.given
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.subject.SubjectSpek

class SerialVersionUIDInSerializableClassSpec : SubjectSpek<SerialVersionUIDInSerializableClass>({
	subject { SerialVersionUIDInSerializableClass(Config.empty) }

	given("several serializable classes") {

		it("reports serializable classes which do not implement the serialVersionUID correctly") {
			assertThat(subject.lint(Case.Serializable.path())).hasSize(5)
		}
	}
})
