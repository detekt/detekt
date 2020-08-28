package io.gitlab.arturbosch.detekt.core.baseline

import io.github.detekt.test.utils.NullPrintStream
import io.github.detekt.test.utils.StringPrintStream
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

        it("should not create a new baseline file when no findings occurred") {
            val output = StringPrintStream()
            val mapping = resultMapping(
                baselineFile = baselineFile,
                createBaseline = true,
                outputChannel = output
            )

            mapping.transformFindings(emptyMap())

            assertThat(baselineFile.exists()).isFalse()
            assertThat(output.toString()).isEqualTo("No issues found, baseline file will not be created.\n")
        }

        it("should not update an existing baseline file when no findings occurred") {
            val output = StringPrintStream()
            val existing = Baseline.load(existingBaselineFile)
            val mapping = resultMapping(
                baselineFile = existingBaselineFile,
                createBaseline = true,
                outputChannel = output
            )

            mapping.transformFindings(emptyMap())

            val changed = Baseline.load(existingBaselineFile)
            assertThat(existing).isEqualTo(changed)
            assertThat(output.toString()).isEqualTo("No issues found, baseline file will not be updated.\n")
        }

        it("should not update an existing baseline file if option configured as false") {
            val output = StringPrintStream()
            val existing = Baseline.load(existingBaselineFile)
            val mapping = resultMapping(
                baselineFile = existingBaselineFile,
                createBaseline = false,
                outputChannel = output
            )

            mapping.transformFindings(findings)

            val changed = Baseline.load(existingBaselineFile)
            assertThat(existing).isEqualTo(changed)
            assertThat(output.toString()).isEmpty()
        }

        it("should not update an existing baseline file if option is not configured") {
            val output = StringPrintStream()
            val existing = Baseline.load(existingBaselineFile)
            val mapping = resultMapping(
                baselineFile = existingBaselineFile,
                createBaseline = null,
                outputChannel = output
            )

            mapping.transformFindings(findings)

            val changed = Baseline.load(existingBaselineFile)
            assertThat(existing).isEqualTo(changed)
            assertThat(output.toString()).isEmpty()
        }

        it("should not create a new baseline file if no file is configured") {
            val output = StringPrintStream()
            val mapping = resultMapping(
                baselineFile = null,
                createBaseline = false,
                outputChannel = output
            )

            mapping.transformFindings(findings)

            assertThat(baselineFile.exists()).isFalse()
            assertThat(output.toString()).isEmpty()
        }

        it("should create a new baseline file if a file is configured") {
            val output = StringPrintStream()
            val mapping = resultMapping(
                baselineFile = baselineFile,
                createBaseline = true,
                outputChannel = output
            )

            mapping.transformFindings(findings)

            assertThat(baselineFile.exists()).isTrue()
            assertThat(output.toString()).isEmpty()
        }

        it("should update an existing baseline file if a file is configured") {
            val output = StringPrintStream()
            val existing = Baseline.load(existingBaselineFile)
            val mapping = resultMapping(
                baselineFile = existingBaselineFile,
                createBaseline = true,
                outputChannel = output
            )

            mapping.transformFindings(findings)

            val changed = Baseline.load(existingBaselineFile)
            assertThat(existing).isNotEqualTo(changed)
            assertThat(output.toString()).isEmpty()
        }
    }
})

@OptIn(UnstableApi::class)
internal fun resultMapping(baselineFile: Path?, createBaseline: Boolean?, outputChannel: PrintStream) =
    BaselineResultMapping().apply {
        init(object : SetupContext {
            override val configUris: Collection<URI> = mockk()
            override val config: Config = mockk()
            override val outputChannel: PrintStream = outputChannel
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
