package io.gitlab.arturbosch.detekt.rules.bugs

import io.gitlab.arturbosch.detekt.test.compileAndLint
import org.assertj.core.api.Assertions.assertThat
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

/**
 * @author Ivan Balaksha
 * @author schalkms
 */
class UnsafeCastSpec : Spek({
    val subject by memoized { UnsafeCast() }

    describe("check safe and unsafe casts") {

        it("reports unsafe cast") {
            val code = """
				fun test(s: Any) {
					println(s as Int)
				}"""
            assertThat(subject.compileAndLint(code)).hasSize(1)
        }

        it("does not report safe cast") {
            val code = """
				fun test(s: Any) {
					println((s as? Int) ?: 0)
				}"""
            assertThat(subject.compileAndLint(code)).isEmpty()
        }
    }
})
