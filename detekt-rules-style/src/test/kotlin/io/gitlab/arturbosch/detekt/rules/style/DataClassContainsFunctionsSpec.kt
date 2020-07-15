package io.gitlab.arturbosch.detekt.rules.style

import io.gitlab.arturbosch.detekt.test.compileAndLint
import io.gitlab.arturbosch.detekt.test.TestConfig
import org.assertj.core.api.Assertions.assertThat
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

class DataClassContainsFunctionsSpec : Spek({
    val subject by memoized { DataClassContainsFunctions() }

    describe("DataClassContainsFunctions rule") {

        context("flagged functions in data class") {
            val code = """
                data class C(val s: String) {
                    fun f() {}

                    data class Nested(val i: Int) {
                        fun toConversion() = C(i.toString())
                    }
                }
            """

            it("reports valid data class w/o conversion function") {
                assertThat(subject.compileAndLint(code)).hasSize(2)
            }

            it("reports valid data class w/ conversion function") {
                val config = TestConfig(mapOf(DataClassContainsFunctions.CONVERSION_FUNCTION_PREFIX to "to"))
                val rule = DataClassContainsFunctions(config)
                assertThat(rule.compileAndLint(code)).hasSize(1)
            }
        }

        it("does not report a data class without a function") {
            val code = "data class C(val i: Int)"
            assertThat(subject.compileAndLint(code)).isEmpty()
        }

        it("does not report a non-data class without a function") {
            val code = """
                class C {
                    fun f() {}
                }
            """
            assertThat(subject.compileAndLint(code)).isEmpty()
        }

        it("does not report a data class with overridden functions") {
            val code = """
                data class C(val i: Int) {

                    override fun hashCode(): Int {
                        return super.hashCode()
                    }

                    override fun equals(other: Any?): Boolean {
                        return super.equals(other)
                    }

                    override fun toString(): String {
                        return super.toString()
                    }
                }
            """
            assertThat(subject.compileAndLint(code)).isEmpty()
        }
    }
})
