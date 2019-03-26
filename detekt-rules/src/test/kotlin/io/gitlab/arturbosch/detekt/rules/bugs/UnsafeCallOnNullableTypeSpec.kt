package io.gitlab.arturbosch.detekt.rules.bugs

import io.gitlab.arturbosch.detekt.test.compileAndLint
import org.assertj.core.api.Assertions.assertThat
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

/**
 * @author Ivan Balaksha
 * @author schalkms
 */
class UnsafeCallOnNullableTypeSpec : Spek({
    val subject by memoized { UnsafeCallOnNullableType() }

    describe("check all variants of safe/unsafe calls on nullable types") {

        it("reports unsafe call on nullable type") {
            val code = """
				fun test(str: String?) {
					println(str!!.length)
				}"""
            assertThat(subject.compileAndLint(code)).hasSize(1)
        }

        it("does not report safe call on nullable type") {
            val code = """
				fun test(str: String?) {
					println(str?.length)
				}"""
            assertThat(subject.compileAndLint(code)).hasSize(0)
        }

        it("does not report safe call in combination with the elvis operator") {
            val code = """
				fun test(str: String?) {
					println(str?.length ?: 0)
				}"""
            assertThat(subject.compileAndLint(code)).hasSize(0)
        }
    }
})
