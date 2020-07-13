package io.gitlab.arturbosch.detekt.rules.style

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.test.compileAndLint
import org.assertj.core.api.Assertions.assertThat
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

class EqualsNullCallSpec : Spek({
    val subject by memoized { EqualsNullCall(Config.empty) }

    describe("EqualsNullCall rule") {

        it("reports equals call with null as parameter") {
            val code = """
                fun x(a: String) {
                    a.equals(null)
                }"""
            assertThat(subject.compileAndLint(code).size).isEqualTo(1)
        }

        it("reports nested equals call with null as parameter") {
            val code = """
                fun x(a: String, b: String) {
                    a.equals(b.equals(null))
                }"""
            assertThat(subject.compileAndLint(code).size).isEqualTo(1)
        }

        it("does not report equals call with parameter of type string") {
            val code = """
                fun x(a: String, b: String) {
                    a.equals(b)
                }"""
            assertThat(subject.compileAndLint(code).size).isEqualTo(0)
        }
    }
})
