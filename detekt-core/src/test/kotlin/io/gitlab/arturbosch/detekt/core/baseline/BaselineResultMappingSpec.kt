package io.gitlab.arturbosch.detekt.core.baseline

import io.github.detekt.test.utils.NullPrintStream
import io.github.detekt.test.utils.createTempDirectoryForTest
import io.github.detekt.test.utils.resourceAsPath
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Finding2
import io.gitlab.arturbosch.detekt.api.RuleSet
import io.gitlab.arturbosch.detekt.api.SetupContext
import io.gitlab.arturbosch.detekt.test.createEntity
import io.gitlab.arturbosch.detekt.test.createFinding
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test
import java.io.PrintStream
import java.net.URI
import java.nio.file.Path
import kotlin.io.path.copyTo
import kotlin.io.path.deleteIfExists

class BaselineResultMappingSpec {

    private val dir = createTempDirectoryForTest("baseline_format")
    private val baselineFile = dir.resolve("baseline.xml")
    private val existingBaselineFile = resourceAsPath("/baseline_feature/valid-baseline.xml")
    private val finding: Finding2 = createFinding(
        ruleName = "SomeIssueId",
        entity = createEntity(signature = "SomeSignature"),
    )
    private val findings: Map<RuleSet.Id, List<Finding2>> = mapOf(RuleSet.Id("RuleSet") to listOf(finding))

    @AfterEach
    fun tearDown() {
        baselineFile.deleteIfExists()
    }

    @Test
    fun `should not create a new baseline file when no findings occurred`() {
        val mapping = resultMapping(
            baselineFile = baselineFile,
            createBaseline = true,
        )

        mapping.transformFindings(emptyMap())

        assertThat(baselineFile).doesNotExist()
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

        assertThat(baselineFile).doesNotExist()
    }

    @Test
    fun `should create a new baseline file if a file is configured`() {
        val mapping = resultMapping(
            baselineFile = baselineFile,
            createBaseline = true,
        )

        mapping.transformFindings(findings)

        assertThat(baselineFile).exists()
    }

    @Test
    fun `should update an existing baseline file if a file is configured`() {
        existingBaselineFile.copyTo(baselineFile)
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

private fun resultMapping(baselineFile: Path?, createBaseline: Boolean?) =
    BaselineResultMapping().apply {
        init(object : SetupContext {
            override val configUris: Collection<URI> = emptyList()
            override val config: Config = Config.empty
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
