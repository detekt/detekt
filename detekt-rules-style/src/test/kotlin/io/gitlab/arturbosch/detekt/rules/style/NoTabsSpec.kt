package io.gitlab.arturbosch.detekt.rules.style

import io.github.detekt.test.utils.compileForTest
import io.gitlab.arturbosch.detekt.rules.Case
import io.gitlab.arturbosch.detekt.test.assertThat
import org.junit.jupiter.api.Test

class NoTabsSpec {
    private val subject = NoTabs()

    @Test
    fun `should flag a line that contains a tab`() {
        val file = compileForTest(Case.NoTabsPositive.path())
        subject.visitFile(file)
        assertThat(subject.findings).hasSize(5)
    }

    @Test
    fun `should not flag a line that does not contain a tab`() {
        val file = compileForTest(Case.NoTabsNegative.path())
        subject.visitFile(file)
        assertThat(subject.findings).isEmpty()
    }
}
