package io.gitlab.arturbosch.detekt.rules.naming

import io.gitlab.arturbosch.detekt.api.SourceLocation
import io.gitlab.arturbosch.detekt.test.TestConfig
import io.gitlab.arturbosch.detekt.test.assertThat
import io.gitlab.arturbosch.detekt.test.compileAndLint
import io.gitlab.arturbosch.detekt.test.lint
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

class NamingRulesSpec : Spek({

    val subject by memoized { NamingRules() }

    describe("properties in classes") {

        it("should detect all positive cases") {
            val code = """
                class C(val CONST_PARAMETER: String, private val PRIVATE_CONST_PARAMETER: Int) {
                    private val _FIELD = 5
                    val FIELD get() = _field
                    val camel_Case_Property = 5
                    const val MY_CONST = 7
                    const val MYCONST = 7
                    fun doStuff(FUN_PARAMETER: String) {}
                }
            """
            assertThat(subject.lint(code))
                .hasSourceLocations(
                    SourceLocation(1, 9),
                    SourceLocation(1, 38),
                    SourceLocation(2, 5),
                    SourceLocation(3, 5),
                    SourceLocation(4, 5),
                    SourceLocation(5, 5),
                    SourceLocation(6, 5),
                    SourceLocation(7, 17)
                )
        }

        it("checks all negative cases") {
            val code = """
                class C(val constParameter: String, private val privateConstParameter: Int) {
                    private val _field = 5
                    val field get() = _field
                    val camelCaseProperty = 5
                    const val myConst = 7

                    data class D(val i: Int, val j: Int)
                    fun doStuff() {
                        val (_, holyGrail) = D(5, 4)
                        emptyMap<String, String>().forEach { _, v -> println(v) }
                    }
                    val doable: (Int) -> Unit = { _ -> Unit }
                    fun doStuff(funParameter: String) {}
                }
            """
            assertThat(subject.lint(code)).isEmpty()
        }

        it("should not flag overridden member properties by default") {
            val code = """
                class C : I {
                    override val SHOULD_NOT_BE_FLAGGED = "banana"
                }
                interface I : I2 {
                    override val SHOULD_NOT_BE_FLAGGED: String
                }
                interface I2 {
                    @Suppress("VariableNaming") val SHOULD_NOT_BE_FLAGGED: String
                }
            """
            assertThat(NamingRules().compileAndLint(code)).isEmpty()
        }

        it("doesn't ignore overridden member properties if ignoreOverridden is false") {
            val code = """
                class C : I {
                    override val SHOULD_BE_FLAGGED = "banana"
                }
                interface I : I2 {
                    override val SHOULD_BE_FLAGGED: String
                }
                interface I2 {
                    @Suppress("VariableNaming") val SHOULD_BE_FLAGGED: String
                }
            """
            val config = TestConfig(mapOf(IGNORE_OVERRIDDEN to "false"))
            assertThat(NamingRules(config).compileAndLint(code))
                .hasSourceLocations(
                    SourceLocation(2, 5),
                    SourceLocation(5, 5)
                )
        }
    }

    describe("naming like in constants is allowed for destructuring and lambdas") {
        it("should not detect any") {
            val code = """
                data class D(val i: Int, val j: Int)
                fun doStuff() {
                    val (_, HOLY_GRAIL) = D(5, 4)
                    emptyMap<String, String>().forEach { _, V -> println(V) }
                }
            """
            assertThat(subject.compileAndLint(code)).isEmpty()
        }
    }
})

const val IGNORE_OVERRIDDEN = "ignoreOverridden"
