package io.gitlab.arturbosch.detekt.rules.bugs

import io.gitlab.arturbosch.detekt.test.lint
import org.assertj.core.api.Assertions.assertThat
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

/**
 * @author Ivan Balaksha
 */
class UnsafeCallOnNullableTypeSpec : Spek({
    val subject by memoized { UnsafeCallOnNullableType() }

    describe("check all variants of safe/unsafe calls on nullable types") {

        it("unsafe call on nullable type") {
            val code = """
				fun test(str: String?) {
					println(str!!.length)
				}"""
            assertThat(subject.lint(code)).hasSize(1)
        }

        it("safe call on nullable type") {
            val code = """
				fun test(str: String?) {
					println(str?.length)
				}"""
            assertThat(subject.lint(code)).hasSize(0)
        }

        it("elvis") {
            val code = """
				fun test(str: String?) {
					println(str?.length ?: 0)
				}"""
            assertThat(subject.lint(code)).hasSize(0)
        }
    }
})
