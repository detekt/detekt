package io.gitlab.arturbosch.detekt.formatting

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.test.compileForTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.nio.file.Path
import java.nio.file.Paths

/**
 * @author Artur Bosch
 */
class FormattingProviderTest {

	val path: Path = Paths.get(FormattingProviderTest::class.java.getResource("/cases/Formatting.kt").path)

	@Test
	fun test() {
		val ruleSet = FormattingProvider().instance(Config.empty)
		val findings = ruleSet.accept(compileForTest(path))
		assertThat(findings.size).isGreaterThanOrEqualTo(8)
	}
}