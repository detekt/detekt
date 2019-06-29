package io.gitlab.arturbosch.detekt.formatting

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.formatting.wrappers.ChainWrapping
import io.gitlab.arturbosch.detekt.test.format
import org.assertj.core.api.Assertions
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

class ChainWrappingRegressionSpec : Spek({
    describe("tests integration of formatting") {

        it("should work like KtLint") {
            val subject = loadFile("configTests/chain-wrapping-before.kt")
            val expected = loadFileContent("configTests/chain-wrapping-after.kt")

            val findings = ChainWrapping(Config.empty).format(subject.text)

            Assertions.assertThat(findings).isNotEmpty()
            Assertions.assertThat(subject.text).isEqualTo(expected)
        }
    }
})
