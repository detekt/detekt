package io.gitlab.arturbosch.detekt.cli.baseline

import io.gitlab.arturbosch.detekt.test.resource
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Test
import java.nio.file.Files
import java.nio.file.Paths
import java.time.Instant

/**
 * @author Artur Bosch
 * @author schalkms
 */
internal class BaselineFormatTest {

	@Test
	fun loadBaseline() {
		val path = Paths.get(resource("/smell-baseline.xml"))
		val (blacklist, whitelist) = BaselineFormat.read(path)

		assertThat(blacklist.ids).hasSize(2)
		assertThat(blacklist.ids).anySatisfy { it.startsWith("LongParameterList") }
		assertThat(blacklist.ids).anySatisfy { it.startsWith("LongMethod") }
		assertThat(blacklist.timestamp).isEqualTo("123456789")
		assertThat(whitelist.ids).hasSize(1)
		assertThat(whitelist.ids).anySatisfy { it.startsWith("FeatureEnvy") }
		assertThat(whitelist.timestamp).isEqualTo("987654321")
	}

	@Test
	fun savedAndLoadedXmlAreEqual() {
		val now = Instant.now().toEpochMilli().toString()
		val tempFile = Files.createTempFile("baseline", now)

		val savedBaseline = Baseline(
				Blacklist(setOf("4", "2", "2"), now),
				Whitelist(setOf("1", "2", "3"), now))

		BaselineFormat.write(savedBaseline, tempFile)
		val loadedBaseline = BaselineFormat.read(tempFile)

		assertThat(loadedBaseline).isEqualTo(savedBaseline)
	}

	@Test
	fun loadInvalidBaseline() {
		val path = Paths.get(resource("/invalid-smell-baseline.txt"))
		assertThatThrownBy { BaselineFormat.read(path) }.isInstanceOf(InvalidBaselineState::class.java)
	}
}
