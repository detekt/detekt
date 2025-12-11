package dev.detekt.metrics.processors

import dev.detekt.test.compileContentForTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class FieldCountVisitorSpec {

    @Test
    fun defaultFieldCount() {
        val file = compileContentForTest(classWithFields)
        val count = with(file) {
            accept(PropertyCountVisitor())
            getUserData(numberOfFieldsKey)
        }
        assertThat(count).isEqualTo(2)
    }
}
