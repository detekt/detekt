package io.gitlab.arturbosch.detekt.cli

import io.gitlab.arturbosch.detekt.cli.baseline.Baseline
import io.gitlab.arturbosch.detekt.cli.baseline.BaselineFormat
import io.gitlab.arturbosch.detekt.cli.baseline.Blacklist
import io.gitlab.arturbosch.detekt.cli.baseline.Whitelist
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.nio.file.Files
import java.nio.file.Paths
import java.time.Instant

/**
 * @author Artur Bosch
 */
internal class BaselineFormatTest {

	@Test
	fun loadBaseline() {
		val path = Paths.get(javaClass.getResource("/smell-baseline.xml").path)
		val (blacklist, whitelist) = BaselineFormat.read(path)

		assertThat(blacklist.ids).hasSize(2)
		assertThat(blacklist.timestamp).isEqualTo("123456789")
		assertThat(whitelist.ids).hasSize(2)
		assertThat(whitelist.timestamp).isEqualTo("123456789")
	}

	@Test
	fun savedbaselinesavedAndLoadedXmlAreEqual() {
		val now = Instant.now().toString()
		val tempFile = Files.createTempFile("baseline", now)

		val savedBaseline = Baseline(
				Blacklist(listOf("1", "2", "3"), now),
				Whitelist(listOf("1", "2", "3"), now))

		BaselineFormat.write(savedBaseline, tempFile)
		val loadedBaseline = BaselineFormat.read(tempFile)

		assertThat(loadedBaseline).isEqualTo(savedBaseline)
	}
}