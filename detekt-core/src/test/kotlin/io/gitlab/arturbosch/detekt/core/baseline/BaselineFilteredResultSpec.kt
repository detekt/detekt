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
        createFinding("LongParameterList", createEntity(signature = "Signature")),
        createFinding("LongMethod", createEntity(signature = "Signature")),
        createFinding("FeatureEnvy", createEntity(signature = "Signature")),
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
        assertThat(actual.findings).isEmpty()
    }
}
