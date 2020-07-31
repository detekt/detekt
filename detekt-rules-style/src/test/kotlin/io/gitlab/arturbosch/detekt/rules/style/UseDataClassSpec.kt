package io.gitlab.arturbosch.detekt.rules.style

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.rules.setupKotlinEnvironment
import io.gitlab.arturbosch.detekt.test.TestConfig
import io.gitlab.arturbosch.detekt.test.assertThat
import io.gitlab.arturbosch.detekt.test.compileAndLint
import io.gitlab.arturbosch.detekt.test.compileAndLintWithContext
import io.gitlab.arturbosch.detekt.test.lint
import org.jetbrains.kotlin.cli.jvm.compiler.KotlinCoreEnvironment
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

class UseDataClassSpec : Spek({
    setupKotlinEnvironment()

    val env: KotlinCoreEnvironment by memoized()
    val subject by memoized { UseDataClass(Config.empty) }

    describe("UseDataClass rule") {

        describe("does not report invalid data class candidates") {

            it("does not report a valid class") {
                val code = """
                    class NoDataClassCandidate(val i: Int) {
                        val i2: Int = 0
                        fun f() {
                            println()
                        }
                        object Obj
                    }
                """
                assertThat(subject.compileAndLint(code)).isEmpty()
            }

            it("does not report a candidate class with additional method") {
                val code = """
                    class NoDataClassCandidateWithAdditionalMethod(val i: Int) {
                        fun f1() {
                            println()
                        }
                    }
                """
                assertThat(subject.compileAndLint(code)).isEmpty()
            }

            it("does not report a candidate class with a private constructor") {
                val code = """
                    class NoDataClassCandidateWithOnlyPrivateCtor1 private constructor(val i: Int)
                """
                assertThat(subject.compileAndLint(code)).isEmpty()
            }

            it("does not report a candidate class with a private explicit constructor") {
                val code = """
                    class NoDataClassCandidateWithOnlyPrivateCtor2 {
                        private constructor(i: Int)
                    }
                """
                assertThat(subject.compileAndLint(code)).isEmpty()
            }

            it("does not report a candidate sealed class") {
                val code = """
                    sealed class NoDataClassBecauseItsSealed {
                        data class Success(val any: Any) : NoDataClassBecauseItsSealed()
                        data class Error(val error: Throwable) : NoDataClassBecauseItsSealed()
                    }
                """
                assertThat(subject.compileAndLint(code)).isEmpty()
            }

            it("does not report a candidate enum class") {
                val code = """
                    enum class EnumNoDataClass(val i: Int) {
                        FIRST(1), SECOND(2);
                    }
                """
                assertThat(subject.compileAndLint(code)).isEmpty()
            }

            it("does not report a candidate annotation class") {
                val code = """
                    annotation class AnnotationNoDataClass(val i: Int)
                """
                assertThat(subject.compileAndLint(code)).isEmpty()
            }

            it("does not report a candidate with an interface that has methods") {
                val code = """
                    interface SomeInterface {
                        fun foo(): Int
                    }
                    
                    class NotDataClassBecauseItsImplementingInterfaceWithMethods(val i : Int): SomeInterface {
                        override fun foo() = i
                    }
                """.trimIndent()

                assertThat(subject.compileAndLint(code)).isEmpty()
            }

            it("does not report an existing data class candidate with an interface") {
                val code = """
                    interface SimpleInterface {
                        val i: Int
                    }
                    
                    data class DataClass(override val i: Int): SimpleInterface
                """.trimIndent()

                assertThat(subject.compileAndLint(code)).isEmpty()
            }

            it("does not report a class extending a class and implementing an interface") {
                val code = """
                    interface SimpleInterface {
                        val i: Int
                    }
                    
                    open class BaseClass(open val j: Int)
                    
                    class DataClass(override val i: Int, override val j: Int): SimpleInterface, BaseClass(j)
                """.trimIndent()

                assertThat(subject.compileAndLint(code)).isEmpty()
            }

            it("does not report a class with delegating interface") {
                val code = """
                    interface I
                    class B() : I
                    class A(val b: B) : I by b
                """.trimIndent()

                assertThat(subject.compileAndLint(code)).isEmpty()
            }
        }

        describe("does report data class candidates") {

            it("does report a data class candidate") {
                val code = """
                    class DataClassCandidate1(val i: Int)
                """
                assertThat(subject.compileAndLint(code)).hasSize(1)
            }

            it("does report a candidate class with extra property") {
                val code = """
                    class DataClassCandidateWithProperties(val i: Int) {
                        val i2: Int = 0
                    }
                """
                assertThat(subject.compileAndLint(code)).hasSize(1)
            }

            it("does report a candidate class with extra public constructor") {
                val code = """
                    class DataClassCandidate2(val s: String) {
                        private constructor(i: Int) : this(i.toString())
                    }
                """
                assertThat(subject.compileAndLint(code)).hasSize(1)
            }

            it("does report a candidate class with both a private and public constructor") {
                val code = """
                    class DataClassCandidate3 private constructor(val s: String) {
                        constructor(i: Int) : this(i.toString())
                    }
                """
                assertThat(subject.compileAndLint(code)).hasSize(1)
            }

            it("does report a candidate class with overridden data class methods") {
                val code = """
                    class DataClassCandidateWithOverriddenMethods(val i: Int) {
                        override fun equals(other: Any?): Boolean {
                            return super.equals(other)
                        }
                        override fun hashCode(): Int {
                            return super.hashCode()
                        }
                        override fun toString(): String {
                            return super.toString()
                        }
                    }
                """
                assertThat(subject.compileAndLint(code)).hasSize(1)
            }

            it("does report a candidate class with a simple interface extension") {
                val code = """
                    interface SimpleInterface
                    class DataClass(val i: Int): SimpleInterface
                """.trimIndent()

                assertThat(subject.compileAndLint(code)).hasSize(1)
            }

            it("does report a candidate class with an interface extension that overrides vals") {
                val code = """
                    interface SimpleInterface {
                        val i: Int
                    }
                    
                    class DataClass(override val i: Int): SimpleInterface
                """.trimIndent()

                assertThat(subject.compileAndLint(code)).hasSize(1)
            }
        }

        describe("copy method") {

            it("does report with copy method") {
                val code = """
                    class D(val a: Int, val b: String) {
                        fun copy(a: Int, b: String): D = D(a, b)
                    }
                """
                assertThat(subject.compileAndLintWithContext(env, code)).hasSize(1)
            }

            it("does report with copy method which has an implicit return type") {
                val code = """
                    class D(val a: Int, val b: String) {
                        fun copy(a: Int, b: String) = D(a, b)
                    }
                """
                assertThat(subject.compileAndLintWithContext(env, code)).hasSize(1)
            }

            it("does not report with copy method which has no parameters") {
                val code = """
                    class D(val a: Int, val b: String) {
                        fun copy(): D = D(0, "")
                    }
                """
                assertThat(subject.compileAndLintWithContext(env, code)).isEmpty()
            }

            it("does not report with copy method which has more parameters than the primary constructor") {
                val code = """
                    class D(val a: Int, val b: String) {
                        fun copy(a: Int, b: String, c: String): D = D(a, b + c)
                    }
                """
                assertThat(subject.compileAndLintWithContext(env, code)).isEmpty()
            }

            it("does not report with copy method which has different parameter types") {
                val code = """
                    class D(val a: Int, val b: String) {
                        fun copy(a: Int, b: Int): D = D(a, b.toString())
                    }
                """
                assertThat(subject.compileAndLintWithContext(env, code)).isEmpty()
            }

            it("does not report with copy method which has different parameter types 2") {
                val code = """
                    class D(val a: Int, val b: String) {
                        fun copy(a: Int, b: String?): D = D(a, b.toString())
                    }
                """
                assertThat(subject.compileAndLintWithContext(env, code)).isEmpty()
            }

            it("does not report with copy method which has a different return type") {
                val code = """
                    class D(val a: Int, val b: String) {
                        fun copy(a: Int, b: String) {
                        }
                    }
                """
                assertThat(subject.compileAndLintWithContext(env, code)).isEmpty()
            }
        }

        describe("does report class with vars and allowVars") {

            it("does not report class with mutable constructor parameter") {
                val code = """class DataClassCandidateWithVar(var i: Int)"""
                val config = TestConfig(mapOf(UseDataClass.ALLOW_VARS to "true"))
                assertThat(UseDataClass(config).compileAndLint(code)).isEmpty()
            }

            it("does not report class with mutable properties") {
                val code = """
                    class DataClassCandidateWithProperties(var i: Int) {
                        var i2: Int = 0
                    }
                """
                val config = TestConfig(mapOf(UseDataClass.ALLOW_VARS to "true"))
                assertThat(UseDataClass(config).compileAndLint(code)).isEmpty()
            }

            it("does not report class with both mutable property and immutable parameters") {
                val code = """
                    class DataClassCandidateWithMixedProperties(val i: Int) {
                        var i2: Int = 0
                    }
                """
                val config = TestConfig(mapOf(UseDataClass.ALLOW_VARS to "true"))
                assertThat(UseDataClass(config).compileAndLint(code)).isEmpty()
            }

            it("does not report class with both mutable parameter and immutable property") {
                val code = """
                    class DataClassCandidateWithMixedProperties(var i: Int) {
                        val i2: Int = 0
                    }
                """
                val config = TestConfig(mapOf(UseDataClass.ALLOW_VARS to "true"))
                assertThat(UseDataClass(config).compileAndLint(code)).isEmpty()
            }
        }

        it("does not report inline classes") {
            assertThat(subject.lint("inline class A(val x: Int)")).isEmpty()
        }

        it("does not report a class which has an ignored annotation") {
            val code = """
                import kotlin.SinceKotlin

                @SinceKotlin("1.0.0")
                class AnnotatedClass(val i: Int) {}
                """
            val config = TestConfig(mapOf(UseDataClass.EXCLUDE_ANNOTATED_CLASSES to "kotlin.*"))
            assertThat(UseDataClass(config).compileAndLint(code)).isEmpty()
        }

        it("does not report a class with a delegated property") {
            val code = """
                import kotlin.properties.Delegates
                class C(val i: Int) {
                    var prop: String by Delegates.observable("") {
                            prop, old, new -> println("")
                    }
                }
                """
            assertThat(subject.compileAndLint(code)).isEmpty()
        }

        it("reports class with nested delegation") {
            val code = """
                import kotlin.properties.Delegates
                class C(val i: Int) {
                    var prop: C = C(1).apply {
                        var str: String by Delegates.observable("") {
                                prop, old, new -> println("")
                        }
                    }
                }
            """
            assertThat(subject.compileAndLint(code)).hasSize(1)
        }
    }
})
