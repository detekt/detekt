package io.gitlab.arturbosch.detekt.cli.baseline

import io.github.detekt.test.utils.resourceAsPath
import io.gitlab.arturbosch.detekt.api.Debt
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Severity
import io.gitlab.arturbosch.detekt.cli.createFinding
import io.gitlab.arturbosch.detekt.cli.createLocation
import io.gitlab.arturbosch.detekt.test.TestDetektion
import org.assertj.core.api.Assertions.assertThat
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

class BaselineFilteredResultSpec : Spek({

    describe("baseline based result transformation") {

        val baselineFile = resourceAsPath("/smell-baseline.xml")

        val result = TestDetektion(
            createFinding(
                Issue("LongParameterList", Severity.CodeSmell, "test", Debt.FIVE_MINS),
                Entity("", "", "Signature", createLocation(baselineFile.toString()))
            )
        )

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
