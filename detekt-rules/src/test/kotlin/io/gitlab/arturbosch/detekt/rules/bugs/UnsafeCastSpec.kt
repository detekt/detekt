package io.gitlab.arturbosch.detekt.rules.bugs

import io.gitlab.arturbosch.detekt.test.lint
import org.assertj.core.api.Assertions.assertThat
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

/**
 * @author Ivan Balaksha
 */
class UnsafeCastSpec : Spek({
    val subject by memoized { UnsafeCast() }

    describe("check safe and unsafe casts") {

        it("test unsafe cast") {
            val code = """
				fun test(s: Any) {
					println(s as Int)
				}"""
            assertThat(subject.lint(code)).hasSize(1)
        }

        it("test safe cast") {
            val code = """
				fun test(s: Any) {
					println((s as? Int) ?: 0)
				}"""
            assertThat(subject.lint(code)).isEmpty()
        }
    }
})
