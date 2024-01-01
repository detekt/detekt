package io.gitlab.arturbosch.detekt.core.baseline

import io.github.detekt.test.utils.resourceAsPath
import io.gitlab.arturbosch.detekt.test.TestDetektion
import io.gitlab.arturbosch.detekt.test.createEntity
import io.gitlab.arturbosch.detekt.test.createFinding
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class BaselineFilteredResultSpec {

    private val baselineFile = resourceAsPath("/baseline_feature/valid-baseline.xml")

    private val result = TestDetektion(
        createFinding(
            ruleName = "LongParameterList",
            entity = createEntity(signature = "Signature"),
        ),
        createFinding(
            ruleName = "LongMethod",
            entity = createEntity(signature = "Signature"),
        ),
        createFinding(
            ruleName = "FeatureEnvy",
            entity = createEntity(signature = "Signature"),
        ),
    )

    @Test
    fun `does return the same finding on empty baseline`() {
        val actual = BaselineFilteredResult(result, DefaultBaseline(emptySet(), emptySet()))
        assertThat(actual.findings).hasSize(3)
    }

    @Test
    fun `filters with an existing baseline file`() {
        val baseline = DefaultBaseline.load(baselineFile)
        val actual = BaselineFilteredResult(result, baseline)
        // Note: Detektion works with Map<RuleSetId, List<Finding>
        // but the TestDetektion maps the RuleId as RuleSetId
        actual.findings.forEach { (_, value) -> assertThat(value).isEmpty() }
    }
}
