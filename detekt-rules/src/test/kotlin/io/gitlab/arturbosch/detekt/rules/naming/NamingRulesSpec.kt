package io.gitlab.arturbosch.detekt.rules.naming

import io.gitlab.arturbosch.detekt.test.assertThat
import io.gitlab.arturbosch.detekt.test.compileAndLint
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

class NamingRulesSpec : Spek({

    describe("naming like in constants is allowed for destructuring and lambdas") {
        it("should not detect any") {
            val code = """
                data class D(val i: Int, val j: Int)
                fun doStuff() {
                    val (_, HOLY_GRAIL) = D(5, 4)
                    emptyMap<String, String>().forEach { _, V -> println(V) }
                }
            """
            assertThat(NamingRules().compileAndLint(code)).isEmpty()
        }
    }
})
