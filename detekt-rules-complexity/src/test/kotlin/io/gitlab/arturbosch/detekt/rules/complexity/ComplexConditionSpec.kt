package io.gitlab.arturbosch.detekt.rules.complexity

import io.gitlab.arturbosch.detekt.test.compileAndLint
import org.assertj.core.api.Assertions.assertThat
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

class ComplexConditionSpec : Spek({

    describe("ComplexCondition rule") {

        val code = """
            val a = if (5 > 4 && 4 < 6 || (3 < 5 || 2 < 5)) { 42 } else { 24 }

            fun complexConditions() {
                while (5 > 4 && 4 < 6 || (3 < 5 || 2 < 5)) {}
                do { } while (5 > 4 && 4 < 6 || (3 < 5 || 2 < 5))
            }
        """

        it("reports some complex conditions") {
            assertThat(ComplexCondition().compileAndLint(code)).hasSize(3)
        }
    }
})
