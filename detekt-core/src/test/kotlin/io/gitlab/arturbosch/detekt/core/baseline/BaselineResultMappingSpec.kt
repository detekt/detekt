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
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.io.PrintStream
import java.net.URI
import java.nio.file.Files
import java.nio.file.Path

@OptIn(UnstableApi::class)
class BaselineResultMappingSpec {

    private val dir = createTempDirectoryForTest("baseline_format")
    private val baselineFile = dir.resolve("baseline.xml")
    private val existingBaselineFile = resourceAsPath("/baseline_feature/valid-baseline.xml")
    private lateinit var findings: Map<String, List<Finding>>
    private lateinit var finding: Finding

    @BeforeEach
    fun setupMocks() {
        finding = mockk()
        every { finding.id }.returns("SomeIssueId")
        every { finding.signature }.returns("SomeSignature")
        findings = mapOf("RuleSet" to listOf(finding))
    }

    @AfterEach
    fun tearDown() {
        Files.deleteIfExists(baselineFile)
    }

    @Test
    fun `should not create a new baseline file when no findings occurred`() {
        val mapping = resultMapping(
            baselineFile = baselineFile,
            createBaseline = true,
        )

        mapping.transformFindings(emptyMap())

        assertThat(baselineFile.exists()).isFalse()
    }

    @Test
    fun `should not update an existing baseline file if option configured as false`() {
        val existing = DefaultBaseline.load(existingBaselineFile)
        val mapping = resultMapping(
            baselineFile = existingBaselineFile,
            createBaseline = false,
        )

        mapping.transformFindings(findings)

        val changed = DefaultBaseline.load(existingBaselineFile)
        assertThat(existing).isEqualTo(changed)
    }

    @Test
    fun `should not update an existing baseline file if option is not configured`() {
        val existing = DefaultBaseline.load(existingBaselineFile)
        val mapping = resultMapping(
            baselineFile = existingBaselineFile,
            createBaseline = null,
        )

        mapping.transformFindings(findings)

        val changed = DefaultBaseline.load(existingBaselineFile)
        assertThat(existing).isEqualTo(changed)
    }

    @Test
    fun `should not create a new baseline file if no file is configured`() {
        val mapping = resultMapping(
            baselineFile = null,
            createBaseline = false,
        )

        mapping.transformFindings(findings)

        assertThat(baselineFile.exists()).isFalse()
    }

    @Test
    fun `should create a new baseline file if a file is configured`() {
        val mapping = resultMapping(
            baselineFile = baselineFile,
            createBaseline = true,
        )

        mapping.transformFindings(findings)

        assertThat(baselineFile.exists()).isTrue()
    }

    @Test
    fun `should update an existing baseline file if a file is configured`() {
        Files.copy(existingBaselineFile, baselineFile)
        val existing = DefaultBaseline.load(baselineFile)
        val mapping = resultMapping(
            baselineFile = baselineFile,
            createBaseline = true,
        )

        mapping.transformFindings(findings)

        val changed = DefaultBaseline.load(baselineFile)
        assertThat(existing).isNotEqualTo(changed)
    }
}

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
