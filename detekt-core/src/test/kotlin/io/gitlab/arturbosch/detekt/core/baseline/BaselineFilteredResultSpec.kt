package io.gitlab.arturbosch.detekt.core.baseline

import io.github.detekt.test.utils.resourceAsPath
import io.gitlab.arturbosch.detekt.test.TestDetektion
import io.mockk.every
import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThat
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

class BaselineFilteredResultSpec : Spek({

    describe("baseline based result transformation") {

        val baselineFile = resourceAsPath("/baseline_feature/valid-baseline.xml")

        val result by memoized {
            TestDetektion(
                mockk {
                    every { id }.returns("LongParameterList")
                    every { signature }.returns("Signature")
                },
                mockk {
                    every { id }.returns("LongMethod")
                    every { signature }.returns("Signature")
                },
                mockk {
                    every { id }.returns("FeatureEnvy")
                    every { signature }.returns("Signature")
                },
            )
        }

        it("does return the same finding on empty baseline") {
            val actual = BaselineFilteredResult(result, Baseline(emptySet(), emptySet()))
            assertThat(actual.findings).hasSize(3)
        }

        it("filters with an existing baseline file") {
            val baseline = Baseline.load(baselineFile)
            val actual = BaselineFilteredResult(result, baseline)
            // Note: Detektion works with Map<RuleSetId, List<Finding>
            // but the TestDetektion maps the RuleId as RuleSetId
            actual.findings.forEach { (_, value) -> assertThat(value).isEmpty() }
        }
    }
})
