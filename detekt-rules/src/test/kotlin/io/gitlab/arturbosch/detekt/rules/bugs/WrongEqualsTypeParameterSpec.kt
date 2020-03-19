package io.gitlab.arturbosch.detekt.rules.bugs

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.test.compileAndLint
import org.assertj.core.api.Assertions.assertThat
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

class WrongEqualsTypeParameterSpec : Spek({
    val subject by memoized { WrongEqualsTypeParameter(Config.empty) }

    describe("WrongEqualsTypeParameter rule") {

        it("does not report Any? as parameter") {
            val code = """
                class A {
                    override fun equals(other: Any?): Boolean {
                        return super.equals(other)
                    }
                }"""
            assertThat(subject.compileAndLint(code)).isEmpty()
        }

        it("reports a String as parameter") {
            val code = """
                class A {
                    fun equals(other: String): Boolean {
                        return super.equals(other)
                    }
                }"""
            assertThat(subject.compileAndLint(code)).hasSize(1)
        }

        it("does not report equals() with an additional parameter") {
            val code = """
                class A {
                    fun equals(other: Any?, i: Int): Boolean {
                        return super.equals(other)
                    }
                }"""
            assertThat(subject.compileAndLint(code)).isEmpty()
        }

        it("does not report an overridden equals() with a different signature") {
            val code = """
                interface I {
                    fun equals(other: Any?, i: Int): Boolean
                    fun equals(): Boolean
                }
                
                class A : I {
                    override fun equals(other: Any?, i: Int): Boolean {
                        return super.equals(other)
                    }
                    
                    override fun equals() = true
                }"""
            assertThat(subject.compileAndLint(code)).isEmpty()
        }

        it("does not report an interface declaration") {
            val code = """
                interface I {
                    fun equals(other: String)
                }"""
            assertThat(subject.compileAndLint(code)).isEmpty()
        }

        it("does not report top level functions") {
            val code = """
                fun equals(other: String) {}
                fun equals(other: Any?) {}
                """
            assertThat(subject.compileAndLint(code)).isEmpty()
        }
    }
})
