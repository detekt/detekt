package io.gitlab.arturbosch.detekt.rules.style

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.test.TestConfig
import io.gitlab.arturbosch.detekt.test.compileAndLint
import org.assertj.core.api.Assertions.assertThat
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

/**
 * @author Egor Neliuba
 */
class ForbiddenVoidSpec : Spek({
    val subject by memoized { ForbiddenVoid(Config.empty) }

    describe("ForbiddenVoid rule") {
        it("should report all Void type usage") {
            val code = """
				lateinit var c: () -> Void

				fun method(param: Void) {
					val a: Void? = null
					val b: Void = null!!
				}
			"""

            assertThat(subject.compileAndLint(code)).hasSize(4)
        }

        it("should not report Void class literal") {
            val code = """
				val clazz = java.lang.Void::class
				val klass = Void::class
			"""

            assertThat(subject.compileAndLint(code)).isEmpty()
        }

        describe("ignoreOverridden is enabled") {
            val config = TestConfig(mapOf(ForbiddenVoid.IGNORE_OVERRIDDEN to "true"))

            it("should not report Void in overriding function declarations") {
                val code = """
                    abstract class A {
                        @Suppress("ForbiddenVoid")
                        abstract fun method(param: Void) : Void
                    }

                    class B : A() {
                        override fun method(param: Void) : Void {
                            throw IllegalStateException()
                        }
                    }
                """

                val findings = ForbiddenVoid(config).compileAndLint(code)
                assertThat(findings).isEmpty()
            }

            it("should not report Void in overriding function declarations with parametrized types") {
                val code = """
                    class Foo<T> {}

                    abstract class A {
                        @Suppress("ForbiddenVoid")
                        abstract fun method(param: Foo<Void>) : Foo<Void>
                    }

                    class B : A() {
                        override fun method(param: Foo<Void>) : Foo<Void> {
                            @Suppress("ForbiddenVoid")
                            return Foo<Void>()
                        }
                    }
                """

                val findings = ForbiddenVoid(config).compileAndLint(code)
                assertThat(findings).isEmpty()
            }

            it("should report Void in body of overriding function even") {
                val code = """
                    abstract class A {
                        abstract fun method(param: String)
                    }

                    class B : A() {
                        override fun method(param: String) {
                            val a: Void? = null
                        }
                    }
                """

                val findings = ForbiddenVoid(config).compileAndLint(code)
                assertThat(findings).hasSize(1)
            }

            it("should report Void in not overridden function declarations") {
                val code = """
                    fun method(param: Void) : Void {
                        return param
                    }
                """

                val findings = ForbiddenVoid(config).compileAndLint(code)
                assertThat(findings).hasSize(2)
            }
        }
    }
})
