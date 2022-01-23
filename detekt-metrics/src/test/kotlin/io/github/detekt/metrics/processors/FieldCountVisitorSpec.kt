package io.github.detekt.metrics.processors

import io.github.detekt.test.utils.compileContentForTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class FieldCountVisitorSpec {
    @Nested
    inner class `something` {

        @Test
        fun `defaultFieldCount`() {
            val file = compileContentForTest(classWithFields)
            val count = with(file) {
                accept(PropertyCountVisitor())
                getUserData(numberOfFieldsKey)
            }
            assertThat(count).isEqualTo(2)
        }
    }
}
