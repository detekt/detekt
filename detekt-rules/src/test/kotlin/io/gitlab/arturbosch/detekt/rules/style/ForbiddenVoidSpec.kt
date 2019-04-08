package io.gitlab.arturbosch.detekt.rules.style

import io.gitlab.arturbosch.detekt.test.TestConfig
import io.gitlab.arturbosch.detekt.test.lint
import org.assertj.core.api.Assertions.assertThat
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

class ForbiddenVoidSpec : Spek({
    describe("ForbiddenVoid rule") {
        it("should report all Void type usage") {
            val code = """
                lateinit var c: () -> Void

                fun method(param: Void) {
                    val a: Void? = null
                    val b: Void = null!!
                }
            """

            val findings = ForbiddenVoid().lint(code)
            assertThat(findings).hasSize(4)
        }

        it("should not report Void class literal") {
            val code = """
                val clazz = java.lang.Void::class
                val klass = Void::class
            """

            val findings = ForbiddenVoid().lint(code)
            assertThat(findings).isEmpty()
        }

        describe("ignoreOverridden is enabled") {
            val config = TestConfig(mapOf(ForbiddenVoid.IGNORE_OVERRIDDEN to "true"))

            it("should not report Void in overriding function declarations") {
                val code = """
                override fun method(param: Void) : Void {
                    doSomething()
                }
                """

                val findings = ForbiddenVoid(config).lint(code)
                assertThat(findings).isEmpty()
            }

            it("should not report Void in overriding function declarations with parametrized types") {
                val code = """
                override fun method(param: Future<Foo<Void>>) : Future<Foo<Void>> {
                    doSomething()
                }
                """

                val findings = ForbiddenVoid(config).lint(code)
                assertThat(findings).isEmpty()
            }

            it("should report Void in body of overriding function even") {
                val code = """
                override fun method(param: String) : Int {
                    val a: Void? = null
                }
                """

                val findings = ForbiddenVoid(config).lint(code)
                assertThat(findings).hasSize(1)
            }

            it("should report Void in not overridden function declarations") {
                val code = """
                fun method(param: Void) : Void {
                    doSomething()
                }
                """

                val findings = ForbiddenVoid(config).lint(code)
                assertThat(findings).hasSize(2)
            }
        }
    }
})
