package io.gitlab.arturbosch.detekt.rules.complexity

import io.gitlab.arturbosch.detekt.api.SourceLocation
import io.gitlab.arturbosch.detekt.api.ThresholdedCodeSmell
import io.gitlab.arturbosch.detekt.test.TestConfig
import io.gitlab.arturbosch.detekt.test.assertThat
import io.gitlab.arturbosch.detekt.test.compileAndLint
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

class LongMethodSpec : Spek({

    val subject by memoized { LongMethod(TestConfig(mapOf("threshold" to 5))) }

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
                fun methodWithParams(
                    param1: String
                ) { // 4 lines
                    println()
                    println()
                }
            """

            assertThat(subject.compileAndLint(code)).isEmpty()
        }

        it("should find too long method with params on newlines") {
            val code = """
                fun longMethodWithParams(
                    param1: String
                ) { // 5 lines
                    println()
                    println()
                    println()
                }
            """

            val findings = subject.compileAndLint(code)

            assertThat(findings).hasSize(1)
            assertThat(findings[0] as ThresholdedCodeSmell).hasValue(5)
        }

        it("should find long method with method call with params on separate lines") {
            val code = """
                fun longMethod( 
                    x1: Int,
                    x2: Int,
                    y1: Int,
                    y2: Int
                ) { // 8 lines
                    listOf(
                        x1,
                        y1,
                        x2,
                        y2
                    )
                }
            """

            val findings = subject.compileAndLint(code)

            assertThat(findings).hasSize(1)
            assertThat(findings[0] as ThresholdedCodeSmell).hasValue(8)
        }

        it("should find two long methods with params on separate lines") {
            val code = """
                fun longMethod(
                    param1: String
                ) { // 5 lines
                    println()
                    println()
                    println()
            
                    fun nestedLongMethod(
                        param1: String
                    ) { // 5 lines
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

        it("should find nested long methods with params on separate lines") {
            val code = """
                fun longMethod(
                    param1: String
                ) { // 4 lines
                    println()
                    println()
            
                    fun nestedLongMethod(
                        param1: String
                    ) { // 5 lines
                        println()
                        println()
                        println()
                    }
                }
            """

            val findings = subject.compileAndLint(code)

            assertThat(findings).hasSize(1)
            assertThat(findings).hasTextLocations("nestedLongMethod")
            assertThat(findings[0] as ThresholdedCodeSmell).hasValue(5)
        }
    }

    describe("annotated functions") {
        val code = """
                annotation class Composable
                annotation class TestAnn

                fun foo() {
                    println()
                    println()
                }

                @Composable
                fun bar() {
                    println()
                    println()
                }

                @TestAnn
                fun baz() {
                    println()
                    println()
                }
            """

        it("does not ignore annotated functions if ignoreAnnotated is empty") {
            val config = TestConfig(mapOf("threshold" to 2))
            assertThat(LongMethod(config).compileAndLint(code)).hasSourceLocations(
                SourceLocation(4, 5),
                SourceLocation(10, 5),
                SourceLocation(16, 5)
            )
        }

        it("ignores annotated functions if ignoreAnnotated includes the given annotation class") {
            val config = TestConfig(mapOf("threshold" to 2, "ignoreAnnotated" to listOf("Composable")))
            assertThat(LongMethod(config).compileAndLint(code)).hasSourceLocations(
                SourceLocation(4, 5),
                SourceLocation(16, 5)
            )
        }
    }
})
