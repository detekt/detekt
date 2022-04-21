package io.gitlab.arturbosch.detekt.rules.style

import io.github.detekt.test.utils.compileForTest
import io.gitlab.arturbosch.detekt.rules.Case
import io.gitlab.arturbosch.detekt.test.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class NoTabsSpec {

    @Nested
    inner class `NoTabs rule` {

        @Test
        fun `should flag a line that contains a tab`() {
            val subject = NoTabs()
            val file = compileForTest(Case.NoTabsPositive.path())
            subject.findTabs(file)
            assertThat(subject.findings).hasSize(5)
        }

        @Test
        fun `should not flag a line that does not contain a tab`() {
            val subject = NoTabs()
            val file = compileForTest(Case.NoTabsNegative.path())
            subject.findTabs(file)
            assertThat(subject.findings).isEmpty()
        }
    }
}
