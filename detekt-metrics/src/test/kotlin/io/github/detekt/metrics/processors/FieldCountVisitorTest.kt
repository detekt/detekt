package io.github.detekt.metrics.processors

import io.github.detekt.test.utils.compileContentForTest
import org.assertj.core.api.Assertions.assertThat
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

class FieldCountVisitorTest : Spek({
    describe("something") {

        it("defaultFieldCount") {
            val file = compileContentForTest(classWithFields)
            val count = with(file) {
                accept(PropertyCountVisitor())
                getUserData(numberOfFieldsKey)
            }
            assertThat(count).isEqualTo(2)
        }
    }
})
