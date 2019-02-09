package io.gitlab.arturbosch.detekt.rules.style

import io.gitlab.arturbosch.detekt.api.Finding
import io.gitlab.arturbosch.detekt.rules.Case
import io.gitlab.arturbosch.detekt.test.assertThat
import io.gitlab.arturbosch.detekt.test.compileForTest
import java.nio.file.Path
import org.jetbrains.spek.api.dsl.given
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.subject.SubjectSpek
import org.jetbrains.spek.subject.dsl.SubjectProviderDsl

class NoTabsSpec : SubjectSpek<NoTabs>({

    subject { NoTabs() }

    given("a line that contains a tab") {

        it("should flag it") {
            val path = Case.NoTabsPositive.path()
            assertThat(lint(path)).hasSize(5)
        }
    }

    given("a line that does not contain a tab") {

        it("should not flag it") {
            val path = Case.NoTabsNegative.path()
            assertThat(lint(path)).hasSize(0)
        }
    }
})

private fun SubjectProviderDsl<NoTabs>.lint(path: Path): List<Finding> {
    val file = compileForTest(path)
    subject.findTabs(file)
    return subject.findings
}
