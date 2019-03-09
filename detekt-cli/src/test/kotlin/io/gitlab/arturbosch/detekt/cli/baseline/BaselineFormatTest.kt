package io.gitlab.arturbosch.detekt.cli.baseline

import io.gitlab.arturbosch.detekt.test.resource
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe
import java.nio.file.Files
import java.nio.file.Paths
import java.time.Instant

/**
 * @author Artur Bosch
 * @author schalkms
 */
class BaselineFormatTest : Spek({

    describe("baseline format") {

        it("loadBaseline") {
            val path = Paths.get(resource("/smell-baseline.xml"))
            val (blacklist, whitelist) = BaselineFormat().read(path)

            assertThat(blacklist.ids).hasSize(2)
            assertThat(blacklist.ids).anySatisfy { it.startsWith("LongParameterList") }
            assertThat(blacklist.ids).anySatisfy { it.startsWith("LongMethod") }
            assertThat(whitelist.ids).hasSize(1)
            assertThat(whitelist.ids).anySatisfy { it.startsWith("FeatureEnvy") }
        }

        it("savedAndLoadedXmlAreEqual") {
            val now = Instant.now().toEpochMilli().toString()
            val tempFile = Files.createTempFile("baseline", now)

            val savedBaseline = Baseline(
                    Blacklist(setOf("4", "2", "2")),
                    Whitelist(setOf("1", "2", "3")))

            val format = BaselineFormat()
            format.write(savedBaseline, tempFile)
            val loadedBaseline = format.read(tempFile)

            assertThat(loadedBaseline).isEqualTo(savedBaseline)
        }

        it("loadInvalidBaseline") {
            val path = Paths.get(resource("/invalid-smell-baseline.txt"))
            assertThatThrownBy { BaselineFormat().read(path) }.isInstanceOf(InvalidBaselineState::class.java)
        }

        it("newlineAtTheEndOfFile") {
            val now = Instant.now().toEpochMilli().toString()
            val tempFile = Files.createTempFile("baseline", now)

            val savedBaseline = Baseline(
                Blacklist(setOf("4", "2", "2")),
                Whitelist(setOf("1", "2", "3")))

            val format = BaselineFormat()
            format.write(savedBaseline, tempFile)
            val bytes = Files.readAllBytes(tempFile)
            val content = String(bytes, Charsets.UTF_8)
            assertThat(content).endsWith(">\n")
        }
    }
})
