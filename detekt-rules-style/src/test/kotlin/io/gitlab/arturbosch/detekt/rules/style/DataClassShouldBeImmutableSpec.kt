package io.gitlab.arturbosch.detekt.rules.style

import io.gitlab.arturbosch.detekt.test.compileAndLint
import org.assertj.core.api.Assertions.assertThat
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

class DataClassShouldBeImmutableSpec : Spek({
    val subject by memoized { DataClassShouldBeImmutable() }

    describe("DataClassShouldBeImmutable rule") {

        it("reports mutable variable in primary constructor") {
            val code = "data class C(var i: Int)"
            assertThat(subject.compileAndLint(code)).hasSize(1)
        }

        it("reports mutable property in class body") {
            val code = """
                data class C(val i: Int) {
                    var s: String? = null
                }
            """
            assertThat(subject.compileAndLint(code)).hasSize(1)
        }

        it("reports mutable private property in class body") {
            val code = """
                data class C(val i: Int) {
                    var s: String = ""
                        private set
                }
            """
            assertThat(subject.compileAndLint(code)).hasSize(1)
        }

        it("reports lateinit property in class body") {
            val code = """
                data class C(val i: Int) {
                    lateinit var s: String
                }
            """
            assertThat(subject.compileAndLint(code)).hasSize(1)
        }

        it("does not report readonly variable in primary constructor") {
            val code = "data class C(val i: Int)"
            assertThat(subject.compileAndLint(code)).isEmpty()
        }

        it("does not report readonly property in class body") {
            val code = """
                data class C(val i: Int) {
                    val s: String? = null
                }
            """
            assertThat(subject.compileAndLint(code)).isEmpty()
        }

        it("does not report lazy property in class body") {
            val code = """
                data class C(val i: Int) {
                    val s: String by lazy { "" }
                }
            """
            assertThat(subject.compileAndLint(code)).isEmpty()
        }

        it("does not report mutable variables in non-data classes") {
            val code = """
                class C(var i: Int) {
                    val s: String by lazy { "" }
                }
            """
            assertThat(subject.compileAndLint(code)).isEmpty()
        }
    }
})
