package io.gitlab.arturbosch.detekt.rules.style

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.rules.Case
import io.gitlab.arturbosch.detekt.test.lint
import org.assertj.core.api.Assertions.assertThat
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

class ProtectedMemberInFinalClassSpec : Spek({
    val subject by memoized { ProtectedMemberInFinalClass(Config.empty) }

    describe("check all variants of protected visibility modifier in final class") {

        it("reports protected visibility") {
            val path = Case.FinalClassPositive.path()
            assertThat(subject.lint(path)).hasSize(13)
        }

        it("does not report protected visibility") {
            val path = Case.FinalClassNegative.path()
            assertThat(subject.lint(path)).isEmpty()
        }
    }
})
