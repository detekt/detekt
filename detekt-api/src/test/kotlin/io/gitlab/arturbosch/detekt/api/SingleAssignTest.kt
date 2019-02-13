package io.gitlab.arturbosch.detekt.api

import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatIllegalStateException
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

internal class SingleAssignTest : Spek({
    describe("value is unset") {
        var unassigned: Int by SingleAssign()
        it("should fail when value is retrieved") {
            assertThatIllegalStateException().isThrownBy {
                @Suppress("UNUSED_EXPRESSION")
                unassigned
            }
        }

        it("should succeed when value is assigned") {
            unassigned = 15
        }
    }

    describe("value is set") {
        var assigned: Int by SingleAssign()
        assigned = 15

        it("should succeed when value is retrieved") {
            assertThat(assigned).isEqualTo(15)
        }

        it("should fail when value is assigned") {
            assertThatIllegalStateException().isThrownBy { assigned = -1 }
        }
    }
})
