package io.gitlab.arturbosch.detekt.rules.bugs

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.rules.Case
import io.gitlab.arturbosch.detekt.test.lint
import org.assertj.core.api.Assertions.assertThat
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

class UnreachableCodeSpec : Spek({
    val subject by memoized { UnreachableCode(Config.empty) }

    describe("UnreachableCode rule") {

        it("reports unreachable code") {
            val path = Case.UnreachableCode.path()
            assertThat(subject.lint(path)).hasSize(6)
        }
    }
})
