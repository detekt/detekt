package io.gitlab.arturbosch.detekt.rules.style

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.test.compileAndLint
import org.assertj.core.api.Assertions.assertThat
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

class EqualsOnSignatureLineSpec : Spek({
    val subject by memoized { EqualsOnSignatureLine(Config.empty) }

    describe("EqualsOnSignatureLine rule") {

        context("with expression syntax and without a return type") {
            it("reports when the equals is on a new line") {
                val findings = subject.compileAndLint("""
                    fun foo()
                        = 1
                """)
                assertThat(findings).hasSize(1)
            }

            it("does not report when the equals is on the same line") {
                val findings = subject.compileAndLint("""
                fun foo() = 1

                fun bar() =
                    2
                """)
                assertThat(findings).isEmpty()
            }
        }

        context("with expression syntax and with a return type") {
            it("reports when the equals is on a new line") {
                val findings = subject.compileAndLint("""
                fun one(): Int
                    = 1

                fun two(
                    foo: String
                )
                    = 2

                fun three(
                    foo: String
                ): Int
                    = 3
                """)
                assertThat(findings).hasSize(3)
            }

            it("does not report when the equals is on the same line") {
                val findings = subject.compileAndLint("""
                fun one(): Int =
                    1

                fun two()
                    : Int =
                    2

                fun three():
                    Int =
                    3

                fun four(
                    foo: String
                ): Int =
                    4

                fun five(
                    foo: String
                )
                : Int =
                    5

                fun six(
                    foo: String
                )
                :
                Int =
                    6
                """)
                assertThat(findings).isEmpty()
            }
        }

        context("with expression syntax and with a where clause") {
            it("reports when the equals is on a new line") {
                val findings = subject.compileAndLint("""
                fun <V> one(): Int where V : Number
                    = 1

                fun <V> two(
                    foo: String
                ) where V : Number
                    = 2

                fun <V> three(
                    foo: String
                ): Int
                    where V : Number
                    = 3
                """)
                assertThat(findings).hasSize(3)
            }

            it("does not report when the equals is on the same line") {
                val findings = subject.compileAndLint("""
                fun <V> one(): Int where V : Number =
                    1

                fun <V> two() : Int
                    where V : Number =
                    2

                """)
                assertThat(findings).isEmpty()
            }
        }

        it("does not report non-expression functions") {
            val findings = subject.compileAndLint("""
            fun foo() {
            }

            fun bar()
            {
            }

            fun baz()
            :
            Unit
            {
            }
            """)
            assertThat(findings).isEmpty()
        }
    }
})
