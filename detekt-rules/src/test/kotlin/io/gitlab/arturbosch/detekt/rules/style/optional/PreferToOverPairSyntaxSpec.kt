package io.gitlab.arturbosch.detekt.rules.style.optional

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.test.compileAndLint
import org.assertj.core.api.Assertions.assertThat
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

/**
 * @author jkaan
 * @author schalkms
 */
class PreferToOverPairSyntaxSpec : Spek({
    val subject by memoized { PreferToOverPairSyntax(Config.empty) }

    describe("PreferToOverPairSyntax rule") {

        it("reports if pair is created using pair constructor") {
            val code = """
                val pair1 = Pair(1, 2)
                val pair2: Pair<Int, Int> = Pair(1, 2)
                val pair3 = Pair(Pair(1, 2), Pair(3, 4))
            """
            assertThat(subject.compileAndLint(code)).hasSize(5)
        }

        it("does not report if it is created using the to syntax") {
            val code = "val pair = 1 to 2"
            assertThat(subject.compileAndLint(code)).hasSize(0)
        }
    }
})
