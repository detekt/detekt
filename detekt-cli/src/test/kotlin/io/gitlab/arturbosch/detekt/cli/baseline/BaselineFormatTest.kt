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
			val (_, blacklist, whitelist) = BaselineFormat().read(path)

			assertThat(blacklist.ids).hasSize(2)
			assertThat(blacklist.ids).anySatisfy { it.startsWith("LongParameterList") }
			assertThat(blacklist.ids).anySatisfy { it.startsWith("LongMethod") }
			assertThat(blacklist.timestamp).isEqualTo("123456789")
			assertNotNull(whitelist).apply {
				assertThat(ids).hasSize(1)
				assertThat(ids).anySatisfy { it.startsWith("FeatureEnvy") }
				assertThat(timestamp).isEqualTo("987654321")
			}
		}

		it("saved and loaded xml are equal") {
			val now = Instant.now().toEpochMilli().toString()
			val tempFile = Files.createTempFile("baseline", now)

			val blacklist = Blacklist(setOf("4", "2", "2"), now)
			val whitelist = Whitelist(setOf("1", "2", "3"), now)
			val baseline = Baseline(null, blacklist, whitelist)
			val savedBaseline = ConsolidatedBaseline(listOf(baseline))

			val format = BaselineFormat()
			format.write(savedBaseline, tempFile)
			val loadedBaseline = format.read(tempFile)

			assertThat(loadedBaseline).isEqualTo(baseline)
		}

		it("load fails if file is invalid") {
			val path = Paths.get(resource("/invalid-smell-baseline.txt"))
			assertThatThrownBy { BaselineFormat().read(path) }.isInstanceOf(InvalidBaselineState::class.java)
		}
	}

	describe("With a named source set") {
		it("default baseline can be loaded") {
			val path = Paths.get(resource("/smell-consolidated-baseline.xml"))
			val (_, blacklist, whitelist) = BaselineFormat().read(path)

			assertThat(blacklist.ids).hasSize(1)
			assertThat(blacklist.ids).anySatisfy { it.startsWith("LongMethod") }
			assertThat(blacklist.timestamp).isEqualTo("123456789")
			assertThat(whitelist.ids).hasSize(1)
			assertThat(whitelist.ids).anySatisfy { it.startsWith("ComplexInterface") }
			assertThat(whitelist.timestamp).isEqualTo("666666666")
		}
		it("whitelist for source set can be loaded") {
			val path = Paths.get(resource("/smell-consolidated-baseline.xml"))
			val (sourceSetId, blacklist, whitelist) = BaselineFormat().read(path, "foo")

			assertThat(sourceSetId).isEqualTo("foo")
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
			val (sourceSetId, blacklist, whitelist) = BaselineFormat().read(path, "bar")

			assertThat(sourceSetId).isEqualTo("bar")
			assertThat(blacklist.ids).isEmpty()
			assertThat(blacklist.timestamp).isEqualTo("123456789")
			assertThat(whitelist.ids).isEmpty()
			assertThat(whitelist.timestamp).isEqualTo("555555555")
		}
		it("can be saved and loaded with an single source set") {
			val sourceSetId = "foo"
			val now = Instant.now().toEpochMilli().toString()
			val tempFile = Files.createTempFile("baseline", now)

			val blacklist = Blacklist(setOf("4", "2", "2"), now)
			val whitelist = Whitelist(setOf("1", "2", "3"), now)
			val baseline = Baseline(sourceSetId, blacklist, whitelist)
			val savedBaseline = ConsolidatedBaseline(listOf(baseline))

			val format = BaselineFormat()
			format.write(savedBaseline, tempFile)
			val loadedBaseline = format.read(tempFile, sourceSetId)

			assertThat(loadedBaseline).isEqualTo(baseline)
		}
		it("multiple baselines can be saved and loaded") {
			val now = "12345"
			val tempFile = Files.createTempFile("baseline", now)
			val format = BaselineFormat()

			// create initially
			val fooBaseline = Baseline(
					"foo",
					Blacklist(setOf("4"), now),
					Whitelist(setOf("1"), now)
			)
			val barBaseline = Baseline(
					"bar",
					Blacklist(setOf("5"), now),
					Whitelist(setOf("1", "2", "3"), now)
			)
			val defaultBaseline = Baseline(
					null,
					Blacklist(setOf("6", "7"), now),
					Whitelist(setOf("4"), now)
			)
			val savedBaseline = ConsolidatedBaseline(listOf(fooBaseline, barBaseline, defaultBaseline))
			format.write(savedBaseline, tempFile)

			// load and verify
			val loadedBaseline = format.readConsolidated(tempFile)
			assertThat(loadedBaseline).isEqualTo(savedBaseline)
		}
	}
})
