package io.gitlab.arturbosch.detekt.rules.bugs

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.test.compileAndLint
import org.assertj.core.api.Assertions.assertThat
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

class EqualsWithHashCodeExistSpec : Spek({
    val subject by memoized { EqualsWithHashCodeExist(Config.empty) }

    describe("Equals With Hash Code Exist rule") {

        context("some classes with equals() and hashCode() functions") {

            it("reports hashCode() without equals() function") {
                val code = """
                class A {
                    override fun hashCode(): Int { return super.hashCode() }
                }"""
                assertThat(subject.compileAndLint(code)).hasSize(1)
            }

            it("reports equals() without hashCode() function") {
                val code = """
                class A {
                    override fun equals(other: Any?): Boolean { return super.equals(other) }
                }"""
                assertThat(subject.compileAndLint(code)).hasSize(1)
            }

            it("reports a different equals() function signature") {
                val code = """
                class A {
                    fun equals(other: Any?, i: Int): Boolean { return super.equals(other) }
                    override fun hashCode(): Int { return super.hashCode() }
                }"""
                assertThat(subject.compileAndLint(code)).hasSize(1)
            }

            it("reports a different hashcode() function signature") {
                val code = """
                class A {
                    override fun equals(other: Any?): Boolean { return super.equals(other) }
                    fun hashCode(i: Int): Int { return super.hashCode() }
                }"""
                assertThat(subject.compileAndLint(code)).hasSize(1)
            }

            it("reports a different overridden equals() function signature") {
                val code = """
                interface I {
                    fun equals(other: Any?, i: Int): Boolean
                }                    

                class A : I {
                    override fun equals(other: Any?, i: Int): Boolean { return super.equals(other) }
                    override fun hashCode(): Int { return super.hashCode() }
                }"""
                assertThat(subject.compileAndLint(code)).hasSize(1)
            }

            it("reports a different overridden hashCode() function signature") {
                val code = """
                interface I {
                    fun hashCode(i: Int): Int
                }                    

                class A : I {
                    override fun equals(other: Any?): Boolean { return super.equals(other) }
                    override fun hashCode(i: Int): Int { return super.hashCode() }
                }"""
                assertThat(subject.compileAndLint(code)).hasSize(1)
            }

            it("does not report equals() with hashCode() function") {
                val code = """
                class A {
                    override fun equals(other: Any?): Boolean { return super.equals(other) }
                    override fun hashCode(): Int { return super.hashCode() }
                }"""
                assertThat(subject.compileAndLint(code)).isEmpty()
            }

            it("does not report when using kotlin.Any?") {
                val code = """
                class A {
                    override fun equals(other: kotlin.Any?): Boolean { return super.equals(other) }
                    override fun hashCode(): Int { return super.hashCode() }
                }"""
                assertThat(subject.compileAndLint(code)).isEmpty()
            }
        }

        context("a data class") {

            it("does not report equals() or hashcode() violation on data class") {
                val code = """
                data class EqualsData(val i: Int) {
                    override fun equals(other: Any?): Boolean {
                        return super.equals(other)
                    }
                }"""
                assertThat(subject.compileAndLint(code)).isEmpty()
            }
        }
    }
})
