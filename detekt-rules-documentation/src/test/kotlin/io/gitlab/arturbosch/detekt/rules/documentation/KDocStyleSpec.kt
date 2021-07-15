package io.gitlab.arturbosch.detekt.rules.documentation

import io.gitlab.arturbosch.detekt.test.compileAndLint
import org.assertj.core.api.Assertions.assertThat
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

class KDocStyleSpec : Spek({
    val subject by memoized { KDocStyle() }

    describe("check referenced multi rule to only lint errors once per case") {

        it("does only lint once") {
            val code = """
            /** Some doc */
            class Test {
            }
            """
            assertThat(subject.compileAndLint(code)).hasSize(1)
        }
    }
})
