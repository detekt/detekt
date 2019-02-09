package io.gitlab.arturbosch.detekt.core.processors

import io.gitlab.arturbosch.detekt.core.path
import io.gitlab.arturbosch.detekt.test.compileForTest
import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.it

class FieldCountVisitorTest : Spek({

    it("defaultFieldCount") {
        val file = compileForTest(path.resolve("../fields/ClassWithFields.kt"))
        val count = with(file) {
            accept(PropertyCountVisitor())
            getUserData(numberOfFieldsKey)
        }
        assertThat(count).isEqualTo(2)
    }
})
