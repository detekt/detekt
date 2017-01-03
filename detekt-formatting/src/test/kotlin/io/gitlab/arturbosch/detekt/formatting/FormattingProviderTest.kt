package io.gitlab.arturbosch.detekt.formatting

import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.greaterThanOrEqualTo
import com.natpryce.hamkrest.hasSize
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Unstable
import io.gitlab.arturbosch.detekt.test.compileForTest
import org.junit.jupiter.api.Test
import java.nio.file.Path
import java.nio.file.Paths

/**
 * @author Artur Bosch
 */
@Unstable("GreaterThanOrEqualTo must be 8 - see #47")
class FormattingProviderTest {

	val path: Path = Paths.get(FormattingProviderTest::class.java.getResource("/cases/Formatting.kt").path)

	@Test
	fun test() {
		val ruleSet = FormattingProvider().instance(Config.empty)
		val (id, findings) = ruleSet.acceptAll(listOf(compileForTest(path)))
		assertThat(findings.groupBy { it.id }.values, hasSize(greaterThanOrEqualTo(7)))
	}
}