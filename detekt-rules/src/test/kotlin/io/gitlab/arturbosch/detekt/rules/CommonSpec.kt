package io.gitlab.arturbosch.detekt.rules

import io.gitlab.arturbosch.detekt.rules.style.WildcardImport
import io.gitlab.arturbosch.detekt.test.compileForTest
import io.gitlab.arturbosch.detekt.test.lint
import org.assertj.core.api.Assertions.assertThat
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

/**
 * @author Artur Bosch
 */
class CommonSpec : Spek({
    val subject by memoized { WildcardImport() }

    describe("running specified rule") {
        it("should detect one finding") {
            val file = compileForTest(Case.Default.path())
            subject.lint(file)
            assertThat(subject.findings).hasSize(1)
        }
    }
})
