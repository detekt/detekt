package io.gitlab.arturbosch.detekt.rules.formatting

import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.greaterThanOrEqualTo
import com.natpryce.hamkrest.hasSize
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Unstable
import io.gitlab.arturbosch.detekt.rules.Case
import io.gitlab.arturbosch.detekt.rules.providers.FormattingProvider
import io.gitlab.arturbosch.detekt.test.compileForTest
import org.junit.jupiter.api.Test

/**
 * @author Artur Bosch
 */
@Unstable("GreaterThanOrEqualTo must be 8 - see #47")
class FormattingProviderTest {

	@Test
	fun test() {
		val ruleSet = FormattingProvider().instance(Config.empty)
		val (id, findings) = ruleSet.acceptAll(listOf(compileForTest(Case.Formatting.path())))
		assertThat(findings.groupBy { it.id }.values, hasSize(greaterThanOrEqualTo(7)))
	}
}