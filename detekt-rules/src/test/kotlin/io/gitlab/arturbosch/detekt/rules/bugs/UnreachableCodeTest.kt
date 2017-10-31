package io.gitlab.arturbosch.detekt.rules.bugs

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.rules.Case
import io.gitlab.arturbosch.detekt.test.compileForTest
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test

class UnreachableCodeTest {

	@Test
	fun unreachableCode() {
		val subject = UnreachableCode(Config.empty)
		val file = compileForTest(Case.UnreachableCode.path())
		subject.visit(file)
		Assertions.assertThat(subject.findings).hasSize(6)
	}
}
