package io.gitlab.arturbosch.detekt.cli.baseline

import io.gitlab.arturbosch.detekt.test.resource
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.jetbrains.kotlin.com.intellij.util.ObjectUtils.assertNotNull
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it
import java.nio.file.Files
import java.nio.file.Paths
import java.time.Instant

/**
 * @author Artur Bosch
 * @author schalkms
 */
class BaselineFormatTest : Spek({

	describe("Without baseline id") {
		it("can be loaded") {
			val path = Paths.get(resource("/smell-baseline.xml"))
			val (blacklist, defaultWhitelist, _) = BaselineFormat().readConsolidated(path)

			assertThat(blacklist.ids).hasSize(2)
			assertThat(blacklist.ids).anySatisfy { it.startsWith("LongParameterList") }
			assertThat(blacklist.ids).anySatisfy { it.startsWith("LongMethod") }
			assertThat(blacklist.timestamp).isEqualTo("123456789")
			assertNotNull(defaultWhitelist).let { whitelist ->
				assertThat(whitelist.ids).hasSize(1)
				assertThat(whitelist.ids).anySatisfy { it.startsWith("FeatureEnvy") }
				assertThat(whitelist.timestamp).isEqualTo("987654321")
			}
		}

		it("saved and loaded xml are equal") {
			val now = Instant.now().toEpochMilli().toString()
			val tempFile = Files.createTempFile("baseline", now)

			val blacklist = Blacklist(setOf("4", "2", "2"), now)
			val whitelist = Whitelist(null, setOf("1", "2", "3"), now)
			val savedBaseline = ConsolidatedBaseline(blacklist, whitelist)

			val format = BaselineFormat()
			format.write(savedBaseline, tempFile)
			val loadedBaseline = format.read(tempFile)

			assertThat(loadedBaseline).isEqualTo(Baseline(blacklist, whitelist))
		}

		it("load fails if file is invalid") {
			val path = Paths.get(resource("/invalid-smell-baseline.txt"))
			assertThatThrownBy { BaselineFormat().read(path) }.isInstanceOf(InvalidBaselineState::class.java)
		}
	}

	describe("With a named source set") {
		it("default baseline can be loaded") {
			val path = Paths.get(resource("/smell-consolidated-baseline.xml"))
			val (blacklist, whitelist) = BaselineFormat().read(path)

			assertThat(blacklist.ids).hasSize(2)
			assertThat(blacklist.ids).anySatisfy { it.startsWith("LongParameterList") }
			assertThat(blacklist.ids).anySatisfy { it.startsWith("LongMethod") }
			assertThat(blacklist.timestamp).isEqualTo("123456789")
			assertThat(whitelist.ids).hasSize(1)
			assertThat(whitelist.ids).anySatisfy { it.startsWith("ComplexInterface") }
			assertThat(whitelist.timestamp).isEqualTo("666666666")
		}
		it("whitelist for source set can be loaded") {
			val path = Paths.get(resource("/smell-consolidated-baseline.xml"))
			val (blacklist, whitelist) = BaselineFormat().read(path, "foo")

			assertThat(blacklist.ids).hasSize(2)
			assertThat(blacklist.ids).anySatisfy { it.startsWith("LongParameterList") }
			assertThat(blacklist.ids).anySatisfy { it.startsWith("LongMethod") }
			assertThat(blacklist.timestamp).isEqualTo("123456789")
			assertThat(whitelist.ids).hasSize(1)
			assertThat(whitelist.ids).anySatisfy { it.startsWith("FeatureEnvy") }
			assertThat(whitelist.timestamp).isEqualTo("987654321")
		}
		it("whitelist for source set with empty whitelist can be loaded") {
			val path = Paths.get(resource("/smell-consolidated-baseline.xml"))
			val (blacklist, whitelist) = BaselineFormat().read(path, "bar")

			assertThat(blacklist.ids).hasSize(2)
			assertThat(blacklist.ids).anySatisfy { it.startsWith("LongParameterList") }
			assertThat(blacklist.ids).anySatisfy { it.startsWith("LongMethod") }
			assertThat(blacklist.timestamp).isEqualTo("123456789")
			assertThat(whitelist.ids).isEmpty()
			assertThat(whitelist.timestamp).isEqualTo("555555555")
		}
		it("can be saved and loaded with an single source set") {
			val sourceSetId = "foo"
			val now = Instant.now().toEpochMilli().toString()
			val tempFile = Files.createTempFile("baseline", now)

			val blacklist = Blacklist(setOf("4", "2", "2"), now)
			val whitelist = Whitelist(sourceSetId, setOf("1", "2", "3"), now)
			val savedBaseline = ConsolidatedBaseline(blacklist, whitelists = mapOf(sourceSetId to whitelist))

			val format = BaselineFormat()
			format.write(savedBaseline, tempFile)
			val loadedBaseline = format.read(tempFile, sourceSetId)

			assertThat(loadedBaseline).isEqualTo(Baseline(blacklist, whitelist))
		}
		it("a single named source set can be replaced and loaded") {
			val sourceSetId = "foo"
			val now = "12345"
			val tempFile = Files.createTempFile("baseline", now)
			val format = BaselineFormat()

			// create initially
			val blacklist = Blacklist(setOf("4", "2", "2"), now)
			val sourceSetWhitelist = Whitelist(sourceSetId, setOf("1", "2", "3"), now)
			val savedBaseline = ConsolidatedBaseline(
					blacklist,
					whitelists = mapOf(sourceSetId to sourceSetWhitelist))
			format.write(savedBaseline, tempFile)

			// update
			val updatedWhitelist = sourceSetWhitelist.copy(ids = setOf("9"))
			val updatedBaseline = savedBaseline.copy(
					whitelists = savedBaseline.whitelists + (sourceSetId to updatedWhitelist)
			)
			format.write(updatedBaseline, tempFile)

			// load and verify
			val loadedBaseline = format.read(tempFile, sourceSetId)
			assertThat(loadedBaseline).isEqualTo(Baseline(blacklist, updatedWhitelist))
		}
		it("a second named source set can be added without overwriting the first") {
			val firstSourceSetId = "foo"
			val secondSourceSetId = "bar"
			val now = "12345"
			val tempFile = Files.createTempFile("baseline", now)
			val format = BaselineFormat()

			// create initially
			val blacklist = Blacklist(setOf("4", "2", "2"), now)
			val firstSourceWhitelist = Whitelist(firstSourceSetId, setOf("1", "2", "3"), now)
			val savedBaseline = ConsolidatedBaseline(
					blacklist,
					whitelists = mapOf(firstSourceSetId to firstSourceWhitelist)
			)
			format.write(savedBaseline, tempFile)

			// update
			val secondSourceWhitelist = Whitelist(secondSourceSetId, setOf("9"), now)
			val updatedBaseline = savedBaseline.copy(
					whitelists = savedBaseline.whitelists + (secondSourceSetId to secondSourceWhitelist)
			)
			format.write(updatedBaseline, tempFile)

			// load and verify
			format.read(tempFile, firstSourceSetId).let {
				assertThat(it).isEqualTo(Baseline(blacklist, firstSourceWhitelist))
			}
			format.read(tempFile, secondSourceSetId).let {
				assertThat(it).isEqualTo(Baseline(blacklist, secondSourceWhitelist))
			}
		}
	}
})
