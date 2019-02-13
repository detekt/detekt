package io.gitlab.arturbosch.detekt.rules.style

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.test.lint
import org.assertj.core.api.Assertions.assertThat
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

class UnnecessaryLetSpec : Spek({
    val subject by memoized { UnnecessaryLet(Config.empty) }

    describe("UnnecessaryLet rule") {
        it("reports unnecessary lets that can be changed to ordinary method call") {
            val findings = subject.lint("""
				fun f() {
					val a : Int? = null
					a.let { it.plus(1) }
					a?.let { it.plus(1) }
					a.let { that -> that.plus(1) }
					a?.let { that -> that.plus(1) }
					a?.let { that -> that.plus(1) }?.let { it.plus(1) }
				}""")
            assertThat(findings).hasSize(6)
        }
        it("does not report lets used for function calls") {
            val findings = subject.lint("""
				fun f() {
					val a : Int? = null
					a.let { print(it) }
					a?.let { print(it) }
					a.let { that -> print(that) }
					a?.let { that -> 1.plus(that) }
					a?.let { that -> 1.plus(that) }?.let { print(it) }
				}""")
            assertThat(findings).hasSize(0)
        }
        it("does not report lets with lambda body containing more than one statement") {
            val findings = subject.lint("""
				fun f() {
					val a : Int? = null
					a.let { it.plus(1)
                            it.plus(2) }
					a?.let { it.plus(1)
                             it.plus(2) }
					a.let { that -> that.plus(1)
                                    that.plus(2)  }
					a?.let { that -> that.plus(1)
                                     that.plus(2)  }
					a?.let { that -> 1.plus(that) }
                     ?.let { it.plus(1)
                             it.plus(2) }
				}""")
            assertThat(findings).hasSize(0)
        }
        it("does not report lets where it is used multiple times") {
            val findings = subject.lint("""
				fun f() {
					val a : Int? = null
					a?.let { it.plus(it) }
					a?.let { foo -> foo.plus(foo) }
				}""")
            assertThat(findings).hasSize(0)
        }
    }
})
