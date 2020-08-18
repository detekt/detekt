package io.gitlab.arturbosch.detekt.rules.complexity

import io.gitlab.arturbosch.detekt.test.assertThat
import io.gitlab.arturbosch.detekt.test.compileAndLint
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

class RedundantMapSpec : Spek({

    describe("positive case") {
        it("should report top level function") {
            assertThat(RedundantMap().compileAndLint("""
                    fun test(list: List<String>) : List<String> {
                        return list.map { it }
                    }    
                """)).hasSize(1)
        }

        it("should report class function") {
            assertThat(RedundantMap().compileAndLint("""
                    class A {
                        fun test(list: List<String>) : List<String> {
                            return list.map { it }
                        }
                    }
                """)).hasSize(1)
        }
    }

    describe("negative case") {
        it("should not report top function") {
            assertThat(RedundantMap().compileAndLint("""
                    fun test(list: List<String>) : List<String> {
                        return list.map { "test" }
                    }
                """)).isEmpty()
        }

        it("should not report class function") {
            assertThat(RedundantMap().compileAndLint("""
                    class A {
                        fun test(list: List<String>) : List<String> {
                            return list.map { it + "test" }
                        }
                    }
                """)).isEmpty()
        }

        it("should not report filter function") {
            assertThat(RedundantMap().compileAndLint("""
                    fun test(list: List<Boolean>) : List<Boolean> {
                        return list.filter { it }
                    }
                """)).isEmpty()
        }

        it("should not report filter function") {
            assertThat(RedundantMap().compileAndLint("""
                    fun test(list: List<Boolean>) : List<Boolean> {
                         return list.filter { it }
                    }
                """)).isEmpty()
        }
    }
})
