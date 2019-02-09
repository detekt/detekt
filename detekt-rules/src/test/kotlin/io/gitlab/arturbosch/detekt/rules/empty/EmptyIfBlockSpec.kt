package io.gitlab.arturbosch.detekt.rules.empty

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.rules.Case
import io.gitlab.arturbosch.detekt.test.lint
import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.spek.api.dsl.given
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.subject.SubjectSpek

/**
 * @author Artur Bosch
 */
class EmptyIfBlockSpec : SubjectSpek<EmptyIfBlock>({

    subject { EmptyIfBlock(Config.empty) }

    given("several empty if statements") {

        it("reports positive cases") {
            val path = Case.EmptyIfPositive.path()
            assertThat(subject.lint(path)).hasSize(4)
        }

        it("does not report negative cases") {
            val path = Case.EmptyIfNegative.path()
            assertThat(subject.lint(path)).hasSize(0)
        }
    }
})
