package io.gitlab.arturbosch.detekt.rules.exceptions

import io.gitlab.arturbosch.detekt.test.compileAndLint
import org.assertj.core.api.Assertions.assertThat
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

class InstanceOfCheckForExceptionSpec : Spek({
    val subject by memoized { InstanceOfCheckForException() }

    describe("InstanceOfCheckForException rule") {

        it("has is and as checks") {

            val code = """
                fun x() {
                    try {
                    } catch(e: Exception) {
                        if (e is IllegalArgumentException || (e as IllegalArgumentException) != null) {
                            return
                        }
                    }
                }
                """
            assertThat(subject.compileAndLint(code)).hasSize(2)
        }

        it("has nested is and as checks") {
            val code = """
                fun x() {
                    try {
                    } catch(e: Exception) {
                        if (1 == 1) {
                            val b = e !is IllegalArgumentException || (e as IllegalArgumentException) != null
                        }
                    }
                }
                """
            assertThat(subject.compileAndLint(code)).hasSize(2)
        }

        it("has no instance of check") {
            val code = """
                fun x() {
                    try {
                    } catch(e: Exception) {
                        val s = ""
                        if (s is String || (s as String) != null) {
                            val other: Exception? = null
                            val b = other !is IllegalArgumentException || (other as IllegalArgumentException) != null
                        }
                    }
                }
                """
            assertThat(subject.compileAndLint(code)).isEmpty()
        }
    }
})
