package io.gitlab.arturbosch.detekt.rules.naming

import io.gitlab.arturbosch.detekt.test.TestConfig
import io.gitlab.arturbosch.detekt.test.assertThat
import io.gitlab.arturbosch.detekt.test.compileAndLint
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

class FunctionParameterNamingSpec : Spek({

    describe("parameters in a function of a class") {

        it("should detect no violations") {
            val code = """
                class C {
                    fun someStuff(param: String) {}
                }
            """
            assertThat(ConstructorParameterNaming().compileAndLint(code)).isEmpty()
        }

        it("should not detect violations in overridden function by default") {
            val code = """
                class C : I {
                    override fun someStuff(`object`: String) {}
                }
                interface I { fun someStuff(@Suppress("FunctionParameterNaming") `object`: String) }
            """
            assertThat(FunctionParameterNaming().compileAndLint(code)).isEmpty()
        }

        it("should detect violations in overridden function if ignoreOverridden is false") {
            val code = """
                class C : I {
                    override fun someStuff(`object`: String) {}
                }
                interface I { fun someStuff(`object`: String) }
            """
            val config = TestConfig(mapOf(FunctionParameterNaming.IGNORE_OVERRIDDEN to "false"))
            assertThat(FunctionParameterNaming(config).compileAndLint(code)).hasSize(2)
        }

        it("should find some violations") {
            val code = """
                class C {
                    fun someStuff(PARAM: String) {}
                }
            """
            assertThat(FunctionParameterNaming().compileAndLint(code)).hasSize(1)
        }
    }

    describe("parameters in a function of an excluded class") {

        val config by memoized { TestConfig(mapOf(FunctionParameterNaming.EXCLUDE_CLASS_PATTERN to "Excluded")) }

        it("should not detect function parameter") {
            val code = """
                class Excluded {
                    fun f(PARAM: Int) {}
                }
            """
            assertThat(FunctionParameterNaming(config).compileAndLint(code)).isEmpty()
        }

        it("should not detect constructor parameter") {
            val code = "class Excluded(val PARAM: Int) {}"
            assertThat(ConstructorParameterNaming(config).compileAndLint(code)).isEmpty()
        }
    }
})
