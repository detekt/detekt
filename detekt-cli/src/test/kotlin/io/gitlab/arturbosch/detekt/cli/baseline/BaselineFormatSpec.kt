package io.gitlab.arturbosch.detekt.cli.baseline

import io.gitlab.arturbosch.detekt.test.resource
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe
import java.nio.file.Files
import java.nio.file.Paths
import java.time.Instant

class BaselineFormatSpec : Spek({

    describe("baseline format") {

        context("read a baseline file") {

            it("loads the baseline file") {
                val path = Paths.get(resource("/smell-baseline.xml"))
                val (blacklist, whitelist) = BaselineFormat().read(path)

                assertThat(blacklist.ids).hasSize(2)
                assertThat(blacklist.ids).anySatisfy { it.startsWith("LongParameterList") }
                assertThat(blacklist.ids).anySatisfy { it.startsWith("LongMethod") }
                assertThat(whitelist.ids).hasSize(1)
                assertThat(whitelist.ids).anySatisfy { it.startsWith("FeatureEnvy") }
            }

            it("throws on an invalid baseline file") {
                val path = Paths.get(resource("/invalid-smell-baseline.txt"))
                assertThatThrownBy { BaselineFormat().read(path) }.isInstanceOf(InvalidBaselineState::class.java)
            }
        }

        context("writes a baseline file") {

            val savedBaseline = Baseline(
                Blacklist(setOf("4", "2", "2")),
                Whitelist(setOf("1", "2", "3")))

            it("has a new line at the end of the written baseline file") {
                val tempFile = Files.createTempFile("baseline1", ".xml")

                val format = BaselineFormat()
                format.write(savedBaseline, tempFile)
                val bytes = Files.readAllBytes(tempFile)
                val content = String(bytes, Charsets.UTF_8)

                assertThat(content).endsWith(">\n")
            }

            it("asserts that the saved and loaded baseline files are equal") {
                val tempFile = Files.createTempFile("baseline-saved", ".xml")

                val format = BaselineFormat()
                format.write(savedBaseline, tempFile)
                val loadedBaseline = format.read(tempFile)

                assertThat(loadedBaseline).isEqualTo(savedBaseline)
            }
        }
    }
})
