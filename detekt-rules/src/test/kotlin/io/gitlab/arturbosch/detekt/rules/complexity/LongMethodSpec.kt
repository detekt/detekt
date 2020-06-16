package io.gitlab.arturbosch.detekt.rules.complexity

import io.gitlab.arturbosch.detekt.api.ThresholdedCodeSmell
import io.gitlab.arturbosch.detekt.test.assertThat
import io.gitlab.arturbosch.detekt.test.compileAndLint
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

class LongMethodSpec : Spek({

    val subject by memoized { LongMethod(threshold = 5) }

    describe("nested functions can be long") {

        it("should find two long methods") {
            val code = """
                fun longMethod() { // 5 lines
                    println()
                    println()
                    println()
            
                    fun nestedLongMethod() { // 5 lines
                        println()
                        println()
                        println()
                    }
                }
            """
            val findings = subject.compileAndLint(code)

            assertThat(findings).hasSize(2)
            assertThat(findings).hasTextLocations("longMethod", "nestedLongMethod")
        }

        it("should not find too long methods") {
            val code = """
                fun methodOk() { // 3 lines
                    println()
                    fun localMethodOk() { // 4 lines
                        println()
                        println()
                    }
                }
            """

            assertThat(subject.compileAndLint(code)).isEmpty()
        }

        it("should not find too long method with params on newlines") {
            val code = """
                fun methodWithParams( // 4 lines
                    param1: String
                ) {
                    println()
                }
            """

            assertThat(subject.compileAndLint(code)).isEmpty()
        }

        it("should find too long method with params on newlines") {
            val code = """
                fun longMethodWithParams( // 5 lines
                    param1: String
                ) {
                    println()
                    println()
                }
            """

            val findings = subject.compileAndLint(code)

            assertThat(findings).hasSize(1)
            assertThat(findings[0] as ThresholdedCodeSmell).hasValue(5)
        }

        it("should find long method with method call with params") {
            val code = """
                fun longMethod( // 9 lines
                    point1: Point,
                    point2: Point
                ) {
                    createLine(
                        point1.x,
                        point2.x,
                        point1.y,
                        point2.y
                    )
                }
            """

            val findings = subject.compileAndLint(code)

            assertThat(findings).hasSize(1)
            assertThat(findings[0] as ThresholdedCodeSmell).hasValue(9)
        }

        it("should find two long methods with params on separate lines") {
            val code = """
                fun longMethod(
                    param1: String
                ) { // 5 lines
                    println()
                    println()
            
                    fun nestedLongMethod(
                        param1: String
                    ) { // 5 lines
                        println()
                        println()
                    }
                }
            """

            val findings = subject.compileAndLint(code)

            assertThat(findings).hasSize(2)
            assertThat(findings[0] as ThresholdedCodeSmell).hasValue(5)
            assertThat(findings[1] as ThresholdedCodeSmell).hasValue(5)
        }
    }
})
