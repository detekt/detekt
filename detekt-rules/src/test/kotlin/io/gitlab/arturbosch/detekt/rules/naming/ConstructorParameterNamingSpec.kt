package io.gitlab.arturbosch.detekt.rules.naming

import io.gitlab.arturbosch.detekt.test.TestConfig
import io.gitlab.arturbosch.detekt.test.assertThat
import io.gitlab.arturbosch.detekt.test.compileAndLint
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

class ConstructorParameterNamingSpec : Spek({

    describe("parameters in a constructor of a class") {

        it("should detect no violations") {
            val code = """
                class C(val param: String, private val privateParam: String)

                class D {
                    constructor(param: String) {}
                    constructor(param: String, privateParam: String) {}
                }
            """
            assertThat(ConstructorParameterNaming().compileAndLint(code)).isEmpty()
        }

        it("should find some violations") {
            val code = """
                class C(val PARAM: String, private val PRIVATE_PARAM: String)

                class C {
                    constructor(PARAM: String) {}
                    constructor(PARAM: String, PRIVATE_PARAM: String) {}
                }
            """
            assertThat(ConstructorParameterNaming().compileAndLint(code)).hasSize(5)
        }

        it("should find a violation in the correct text locaction") {
            val code = """
                class C(val PARAM: String)
            """
            assertThat(ConstructorParameterNaming().compileAndLint(code)).hasTextLocations(8 to 25)
        }

        it("should not complain about override") {
            val code = """
                class C(override val PARAM: String) : I

                interface I { val PARAM: String }
            """
            assertThat(ConstructorParameterNaming().compileAndLint(code)).isEmpty()
        }

        it("should not complain about override") {
            val code = """
                class C(override val PARAM: String) : I

                interface I { val PARAM: String }
            """
            val config = TestConfig(mapOf(IGNORE_OVERRIDDEN to "false"))
            assertThat(ConstructorParameterNaming(config).compileAndLint(code)).hasTextLocations(8 to 34)
        }
    }
})

private const val IGNORE_OVERRIDDEN = "ignoreOverridden"
