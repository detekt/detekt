package io.gitlab.arturbosch.detekt.core.processors

import io.gitlab.arturbosch.detekt.core.path
import io.gitlab.arturbosch.detekt.test.compileForTest
import java.nio.file.Path
import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.it

/**
 * @author Artur Bosch
 */
class ComplexityVisitorTest : Spek({

    it("complexityOfDefaultCaseIsOne") {
        val path = path.resolve("Default.kt")

        val mcc = calcComplexity(path)

        assertThat(mcc).isEqualTo(0)
    }

    it("complexityOfComplexAndNestedClass") {
        val path = path.resolve("ComplexClass.kt")

        val mcc = calcComplexity(path)

        assertThat(mcc).isEqualTo(56)
    }
})

private fun calcComplexity(path: Path) = with(compileForTest(path)) {
    accept(ComplexityVisitor())
    getUserData(complexityKey)
}
