package io.gitlab.arturbosch.detekt.core.baseline

import io.github.detekt.test.utils.resourceAsPath
import io.gitlab.arturbosch.detekt.api.Finding
import io.gitlab.arturbosch.detekt.test.TestDetektion
import io.mockk.every
import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThat
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

class BaselineFilteredResultSpec : Spek({

    describe("baseline based result transformation") {

        val baselineFile = resourceAsPath("/baseline_feature/valid-baseline.xml")

        val finding by memoized {
            val issue = mockk<Finding>()
            every { issue.id }.returns("LongParameterList")
            every { issue.signature }.returns("Signature")
            issue
        }

        val result by memoized { TestDetektion(finding) }

        it("does return the same finding on empty baseline") {
            val actual = BaselineFilteredResult(result, Baseline(emptySet(), emptySet()))
            assertThat(actual.findings).hasSize(1)
        }

        it("filters with an existing baseline file") {
            val baseline = Baseline.load(baselineFile)
            val actual = BaselineFilteredResult(result, baseline)
            // Note: Detektion works with Map<RuleSetId, List<Finding>
            // but the TestDetektion maps the RuleId as RuleSetId
            assertThat(actual.findings["LongParameterList"]).isEmpty()
        }
    }
})
