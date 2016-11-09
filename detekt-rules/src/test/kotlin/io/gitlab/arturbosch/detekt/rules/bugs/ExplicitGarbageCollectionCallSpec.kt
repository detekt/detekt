package io.gitlab.arturbosch.detekt.rules.bugs

import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import com.natpryce.hamkrest.hasSize
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.compileForTest
import io.gitlab.arturbosch.detekt.rules.Case
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

		assertThat(subject.findings, hasSize(equalTo(3)))
	}

}