package io.gitlab.arturbosch.detekt.rules.bugs

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.rules.Case
import io.gitlab.arturbosch.detekt.test.compileForTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

/**
 * @author Artur Bosch
 */
class ExplicitGarbageCollectionCallSpec {

	@Test
	fun systemGC() {
		val subject = ExplicitGarbageCollectionCall(Config.empty)
		val file = compileForTest(Case.Default.path())

		subject.visit(file)

		assertThat(subject.findings).hasSize(3)
	}

}