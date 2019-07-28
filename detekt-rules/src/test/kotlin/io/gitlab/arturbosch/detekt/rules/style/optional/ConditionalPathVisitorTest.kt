package io.gitlab.arturbosch.detekt.rules.style.optional

import io.gitlab.arturbosch.detekt.rules.Case
import io.gitlab.arturbosch.detekt.test.compileForTest
import org.assertj.core.api.Assertions.assertThat
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

class ConditionalPathVisitorTest : Spek({

    describe("ConditionalPathVisitor") {

        it("pathCount") {
            var counter = 0

            val visitor = ConditionalPathVisitor {
                counter++
            }

            val ktFile = compileForTest(Case.ConditionalPath.path())

            ktFile.accept(visitor)

            assertThat(counter).isEqualTo(5)
        }
    }
})
