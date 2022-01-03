package io.gitlab.arturbosch.detekt.core.baseline

import io.github.detekt.test.utils.NullPrintStream
import io.github.detekt.test.utils.createTempDirectoryForTest
import io.github.detekt.test.utils.resourceAsPath
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Finding
import io.gitlab.arturbosch.detekt.api.SetupContext
import io.gitlab.arturbosch.detekt.api.UnstableApi
import io.gitlab.arturbosch.detekt.core.exists
import io.mockk.every
import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThat
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe
import java.io.PrintStream
import java.net.URI
import java.nio.file.Files
import java.nio.file.Path

@OptIn(UnstableApi::class)
class BaselineResultMappingSpec : Spek({

    describe("a baseline result mapping") {

        val dir by memoized { createTempDirectoryForTest("baseline_format") }
        val baselineFile by memoized { dir.resolve("baseline.xml") }
        val existingBaselineFile by memoized { resourceAsPath("/baseline_feature/valid-baseline.xml") }
        val finding by memoized {
            val issue = mockk<Finding>()
            every { issue.id }.returns("SomeIssueId")
            every { issue.signature }.returns("SomeSignature")
            issue
        }
        val findings by memoized { mapOf("RuleSet" to listOf(finding)) }

        afterEachTest {
            Files.deleteIfExists(baselineFile)
        }

        it("should not create a new baseline file when no findings occurred") {
            val mapping = resultMapping(
                baselineFile = baselineFile,
                createBaseline = true,
            )

            mapping.transformFindings(emptyMap())

            assertThat(baselineFile.exists()).isFalse()
        }

        it("should not update an existing baseline file when no findings occurred") {
            val existing = Baseline.load(existingBaselineFile)
            val mapping = resultMapping(
                baselineFile = existingBaselineFile,
                createBaseline = true,
            )

            mapping.transformFindings(emptyMap())

            val changed = Baseline.load(existingBaselineFile)
            assertThat(existing).isEqualTo(changed)
        }

        it("should not update an existing baseline file if option configured as false") {
            val existing = Baseline.load(existingBaselineFile)
            val mapping = resultMapping(
                baselineFile = existingBaselineFile,
                createBaseline = false,
            )

            mapping.transformFindings(findings)

            val changed = Baseline.load(existingBaselineFile)
            assertThat(existing).isEqualTo(changed)
        }

        it("should not update an existing baseline file if option is not configured") {
            val existing = Baseline.load(existingBaselineFile)
            val mapping = resultMapping(
                baselineFile = existingBaselineFile,
                createBaseline = null,
            )

            mapping.transformFindings(findings)

            val changed = Baseline.load(existingBaselineFile)
            assertThat(existing).isEqualTo(changed)
        }

        it("should not create a new baseline file if no file is configured") {
            val mapping = resultMapping(
                baselineFile = null,
                createBaseline = false,
            )

            mapping.transformFindings(findings)

            assertThat(baselineFile.exists()).isFalse()
        }

        it("should create a new baseline file if a file is configured") {
            val mapping = resultMapping(
                baselineFile = baselineFile,
                createBaseline = true,
            )

            mapping.transformFindings(findings)

            assertThat(baselineFile.exists()).isTrue()
        }

        it("should update an existing baseline file if a file is configured") {
            Files.copy(existingBaselineFile, baselineFile)
            val existing = Baseline.load(baselineFile)
            val mapping = resultMapping(
                baselineFile = baselineFile,
                createBaseline = true,
            )

            mapping.transformFindings(findings)

            val changed = Baseline.load(baselineFile)
            assertThat(existing).isNotEqualTo(changed)
        }
    }
})

@OptIn(UnstableApi::class)
private fun resultMapping(baselineFile: Path?, createBaseline: Boolean?) =
    BaselineResultMapping().apply {
        init(object : SetupContext {
            override val configUris: Collection<URI> = mockk()
            override val config: Config = mockk()
            override val outputChannel: PrintStream = NullPrintStream()
            override val errorChannel: PrintStream = NullPrintStream()
            override val properties: MutableMap<String, Any?> = mutableMapOf(
                DETEKT_BASELINE_PATH_KEY to baselineFile,
                DETEKT_BASELINE_CREATION_KEY to createBaseline
            )

            override fun register(key: String, value: Any) {
                properties[key] = value
            }
        })
    }
