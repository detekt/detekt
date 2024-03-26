package io.gitlab.arturbosch.detekt.core.baseline

import io.github.detekt.test.utils.resourceAsPath
import io.gitlab.arturbosch.detekt.test.TestDetektion
import io.gitlab.arturbosch.detekt.test.createEntity
import io.gitlab.arturbosch.detekt.test.createIssue
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class BaselineFilteredResultSpec {

    private val baselineFile = resourceAsPath("/baseline_feature/valid-baseline.xml")

    private val result = TestDetektion(
        createIssue("LongParameterList", createEntity(signature = "Signature")),
        createIssue("LongMethod", createEntity(signature = "Signature")),
        createIssue("FeatureEnvy", createEntity(signature = "Signature")),
    )

    @Test
    fun `does return the same issue on empty baseline`() {
        val actual = BaselineFilteredResult(result, DefaultBaseline(emptySet(), emptySet()))
        assertThat(actual.issues).hasSize(3)
    }

    @Test
    fun `filters with an existing baseline file`() {
        val baseline = DefaultBaseline.load(baselineFile)
        val actual = BaselineFilteredResult(result, baseline)
        assertThat(actual.issues).isEmpty()
    }
}
