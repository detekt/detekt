package io.gitlab.arturbosch.detekt.rules.style

import io.gitlab.arturbosch.detekt.rules.Case
import io.gitlab.arturbosch.detekt.test.assertThat
import io.github.detekt.test.utils.compileForTest
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

class NoTabsSpec : Spek({

    val subject by memoized { NoTabs() }

    describe("NoTabs rule") {

        it("should flag a line that contains a tab") {
            val file = compileForTest(Case.NoTabsPositive.path())
            subject.findTabs(file)
            assertThat(subject.findings).hasSize(5)
        }

        it("should not flag a line that does not contain a tab") {
            val file = compileForTest(Case.NoTabsNegative.path())
            subject.findTabs(file)
            assertThat(subject.findings).isEmpty()
        }
    }
})
