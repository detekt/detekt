package io.gitlab.arturbosch.detekt.formatting

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.formatting.wrappers.MaximumLineLength
import org.assertj.core.api.Assertions.assertThat
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

class SignaturesAndSuppressionSpec : Spek({

    describe("formatting rules can be suppressed") {

        it("can be suppressed by nearest parent KtElement") {
            val findings = MaximumLineLength(Config.empty)
                .lint("@Suppress(\"MaxLineLength\")\n$longLines")
            assertThat(findings).isEmpty()
        }
    }

    describe("formatting rules provide a signature") {

        it("is calculated based on nearest parent element") {
            val findings = MaximumLineLength(Config.empty).lint(longLines)
            assertThat(findings).hasSize(2)
            assertThat(findings.map { it.signature }).contains(
                "Test.kt\$C\$ ",
                "Test.kt\$C.Companion\$ "
            )
        }
    }
})
