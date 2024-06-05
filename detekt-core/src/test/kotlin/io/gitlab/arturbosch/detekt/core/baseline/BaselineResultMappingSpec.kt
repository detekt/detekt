package io.gitlab.arturbosch.detekt.core.baseline

import io.github.detekt.test.utils.NullPrintStream
import io.github.detekt.test.utils.createTempDirectoryForTest
import io.github.detekt.test.utils.resourceAsPath
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.SetupContext
import io.gitlab.arturbosch.detekt.test.createEntity
import io.gitlab.arturbosch.detekt.test.createIssue
import io.gitlab.arturbosch.detekt.test.createRuleInstance
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
    private val issues = listOf(
        createIssue(
            ruleInstance = createRuleInstance("SomeIssueId", "RuleSet"),
            entity = createEntity(signature = "SomeSignature"),
        ),
        createIssue(
            ruleName = "LongParameterList",
            entity = createEntity(signature = "Signature")
        ),
        createIssue(
            ruleName = "LongMethod",
            entity = createEntity(signature = "Signature")
        ),
        createIssue(
            ruleName = "FeatureEnvy",
            entity = createEntity(signature = "Signature")
        ),
    )

    @AfterEach
    fun tearDown() {
        baselineFile.deleteIfExists()
    }

    @Test
    fun `should not create a new baseline file when no issues occurred`() {
        val mapping = resultMapping(
            baselineFile = baselineFile,
            createBaseline = true,
        )

        mapping.transformIssues(emptyList())

        assertThat(baselineFile).doesNotExist()
    }

    @Test
    fun `should not update an existing baseline file if option configured as false`() {
        val existing = DefaultBaseline.load(existingBaselineFile)
        val mapping = resultMapping(
            baselineFile = existingBaselineFile,
            createBaseline = false,
        )

        mapping.transformIssues(issues)

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

        mapping.transformIssues(issues)

        val changed = DefaultBaseline.load(existingBaselineFile)
        assertThat(existing).isEqualTo(changed)
    }

    @Test
    fun `should not create a new baseline file if no file is configured`() {
        val mapping = resultMapping(
            baselineFile = null,
            createBaseline = false,
        )

        mapping.transformIssues(issues)

        assertThat(baselineFile).doesNotExist()
    }

    @Test
    fun `should create a new baseline file if a file is configured`() {
        val mapping = resultMapping(
            baselineFile = baselineFile,
            createBaseline = true,
        )

        mapping.transformIssues(issues)

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

        mapping.transformIssues(issues)

        val changed = DefaultBaseline.load(baselineFile)
        assertThat(existing).isNotEqualTo(changed)
    }

    @Test
    fun `returns a filtered issues list when the baseline exists`() {
        existingBaselineFile.copyTo(baselineFile)

        val mapping = resultMapping(
            baselineFile = baselineFile,
            createBaseline = true,
        )

        val filtered = mapping.filterByBaseline(baselineFile, issues)

        assertThat(issues).isNotEqualTo(filtered)
    }

    @Test
    fun `returns the same issues list when the baseline doesn't exist`() {
        val mapping = resultMapping(
            baselineFile = baselineFile,
            createBaseline = false,
        )

        val filtered = mapping.filterByBaseline(baselineFile, issues)

        assertThat(issues).isEqualTo(filtered)
    }

    @Test
    fun `doesn't create a baseline file without issues`() {
        val mapping = resultMapping(
            baselineFile = baselineFile,
            createBaseline = false,
        )

        mapping.createOrUpdate(baselineFile, emptyList())

        assertThat(baselineFile).doesNotExist()
    }

    @Test
    fun `creates on top of an existing a baseline file without issues`() {
        existingBaselineFile.copyTo(baselineFile)
        val mapping = resultMapping(
            baselineFile = baselineFile,
            createBaseline = true,
        )

        mapping.createOrUpdate(baselineFile, emptyList())

        assertThat(baselineFile).hasContent(
            """
                <?xml version="1.0" ?>
                <SmellBaseline>
                  <ManuallySuppressedIssues>
                    <ID>LongParameterList:Signature</ID>
                    <ID>LongMethod:Signature</ID>
                  </ManuallySuppressedIssues>
                  <CurrentIssues></CurrentIssues>
                </SmellBaseline>
            """.trimIndent()
        )
    }

    @Test
    fun `creates on top of an existing a baseline file with issues`() {
        existingBaselineFile.copyTo(baselineFile)
        val mapping = resultMapping(
            baselineFile = baselineFile,
            createBaseline = true,
        )

        mapping.createOrUpdate(baselineFile, listOf(createIssue()))

        assertThat(baselineFile).hasContent(
            """
                <?xml version="1.0" ?>
                <SmellBaseline>
                  <ManuallySuppressedIssues>
                    <ID>LongParameterList:Signature</ID>
                    <ID>LongMethod:Signature</ID>
                  </ManuallySuppressedIssues>
                  <CurrentIssues>
                    <ID>TestSmell:TestEntitySignature</ID>
                  </CurrentIssues>
                </SmellBaseline>
            """.trimIndent()
        )
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
