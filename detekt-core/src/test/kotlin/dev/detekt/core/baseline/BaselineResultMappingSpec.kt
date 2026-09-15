package dev.detekt.core.baseline

import dev.detekt.api.testfixtures.createIssue
import dev.detekt.api.testfixtures.createIssueEntity
import dev.detekt.api.testfixtures.createRuleInstance
import dev.detekt.test.utils.resourceAsPath
import dev.detekt.tooling.api.spec.ProcessingSpec
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import java.nio.file.Path
import kotlin.io.path.copyTo

class BaselineResultMappingSpec {
    @TempDir
    @Suppress("VarCouldBeVal")
    private lateinit var dir: Path
    private lateinit var baselineFile: Path
    private val existingBaselineFile = resourceAsPath("/baseline_feature/valid-baseline.xml")
    private val issues = listOf(
        createIssue(
            ruleInstance = createRuleInstance("SomeIssue", "RuleSet"),
            entity = createIssueEntity(signature = "SomeSignature"),
        ),
        createIssue(
            ruleId = "LongParameterList",
            entity = createIssueEntity(signature = "Signature"),
        ),
        createIssue(
            ruleId = "LongMethod",
            entity = createIssueEntity(signature = "Signature"),
        ),
        createIssue(
            ruleId = "FeatureEnvy",
            entity = createIssueEntity(signature = "Signature"),
        ),
    )

    @BeforeEach
    fun setup() {
        baselineFile = dir.resolve("baseline.xml")
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
        assertThat(changed).isEqualTo(existing)
    }

    @Test
    fun `should not create a new baseline file if no file is configured`() {
        val mapping = resultMapping(
            baselineFile = null,
            createBaseline = false,
        )

        val transformed = mapping.transformIssues(issues)

        assertThat(transformed).isEqualTo(issues)
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
        assertThat(changed).isNotEqualTo(existing)
    }

    @Test
    fun `returns a filtered issues list when the baseline exists`() {
        existingBaselineFile.copyTo(baselineFile)

        val mapping = resultMapping(
            baselineFile = baselineFile,
            createBaseline = false,
        )

        val filtered = mapping.transformIssues(issues)

        assertThat(filtered).containsExactly(issues[0], issues[3])
    }

    @Test
    fun `returns the same issues list when the baseline doesn't exist`() {
        val mapping = resultMapping(
            baselineFile = baselineFile,
            createBaseline = false,
        )

        val filtered = mapping.transformIssues(issues)

        assertThat(filtered).isEqualTo(issues)
    }

    @Test
    fun `updates an existing baseline without current issues`() {
        existingBaselineFile.copyTo(baselineFile)
        val mapping = resultMapping(
            baselineFile = baselineFile,
            createBaseline = true,
        )

        mapping.transformIssues(emptyList())

        assertThat(baselineFile).hasContent(
            """
                <?xml version="1.0" ?>
                <SmellBaseline>
                  <ManuallySuppressedIssues>
                    <ID>LongParameterList:TestFile.kt:Signature</ID>
                    <ID>LongMethod:TestFile.kt:Signature</ID>
                  </ManuallySuppressedIssues>
                  <CurrentIssues/>
                </SmellBaseline>
            """.trimIndent()
        )
    }

    @Test
    fun `updates an existing baseline with current issues`() {
        existingBaselineFile.copyTo(baselineFile)
        val mapping = resultMapping(
            baselineFile = baselineFile,
            createBaseline = true,
        )

        mapping.transformIssues(listOf(createIssue()))

        assertThat(baselineFile).hasContent(
            """
                <?xml version="1.0" ?>
                <SmellBaseline>
                  <ManuallySuppressedIssues>
                    <ID>LongParameterList:TestFile.kt:Signature</ID>
                    <ID>LongMethod:TestFile.kt:Signature</ID>
                  </ManuallySuppressedIssues>
                  <CurrentIssues>
                    <ID>TestSmell/id:TestFile.kt:TestEntitySignature</ID>
                  </CurrentIssues>
                </SmellBaseline>
            """.trimIndent()
        )
    }

    @Test
    fun `fails to create a baseline without a configured file`() {
        val mapping = resultMapping(
            baselineFile = null,
            createBaseline = true,
        )

        assertThatThrownBy { mapping.transformIssues(issues) }
            .isInstanceOf(IllegalArgumentException::class.java)
            .hasMessage("Invalid baseline options invariant.")
    }
}

private fun resultMapping(baselineFile: Path?, createBaseline: Boolean): BaselineResultMapping {
    val spec = ProcessingSpec {
        baseline {
            path = baselineFile
            shouldCreateDuringAnalysis = createBaseline
        }
    }
    return BaselineResultMapping(spec.baselineSpec)
}
