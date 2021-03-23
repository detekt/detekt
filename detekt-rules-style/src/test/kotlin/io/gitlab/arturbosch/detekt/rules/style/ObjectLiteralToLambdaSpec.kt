package io.gitlab.arturbosch.detekt.rules.style

import io.gitlab.arturbosch.detekt.api.SourceLocation
import io.gitlab.arturbosch.detekt.rules.setupKotlinEnvironment
import io.gitlab.arturbosch.detekt.test.assert
import io.gitlab.arturbosch.detekt.test.compileAndLintWithContext
import org.jetbrains.kotlin.cli.jvm.compiler.KotlinCoreEnvironment
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

class ObjectLiteralToLambdaSpec : Spek({
    setupKotlinEnvironment()

    val env: KotlinCoreEnvironment by memoized()
    val subject by memoized { ObjectLiteralToLambda() }

    describe("ObjectLiteralToLambda rule") {

        context("report convertible expression") {
            it("is property") {
                val code = """
                fun interface Sam {
                    fun foo()
                }
                val a = object : Sam {
                    override fun foo() {
                    }
                }   
                """
                subject.compileAndLintWithContext(env, code)
                    .assert()
                    .hasSize(1)
                    .hasSourceLocations(SourceLocation(4, 9))
            }

            it("is in function") {
                val code = """
                fun interface Sam {
                    fun foo()
                }
                fun bar() {
                    object : Sam {
                        override fun foo() {
                        }
                    }
                }
                """
                subject.compileAndLintWithContext(env, code)
                    .assert()
                    .hasSize(1)
                    .hasSourceLocations(SourceLocation(5, 5))
            }

            it("is in init") {
                val code = """
                fun interface Sam {
                    fun foo()
                }
                object B {
                    init {
                        object : Sam {
                            override fun foo() {
                            }
                        }
                    }
                }
                """
                subject.compileAndLintWithContext(env, code)
                    .assert()
                    .hasSize(1)
                    .hasSourceLocations(SourceLocation(6, 9))
            }

            it("nested declaration") {
                val code = """
                interface First {
                    fun foo()
                }
                fun interface Second: First

                fun bar() {
                    object : Second {
                        override fun foo(){
                        }
                    }
                }
                """
                subject.compileAndLintWithContext(env, code)
                    .assert()
                    .hasSize(1)
                    .hasSourceLocations(SourceLocation(7, 5))
            }
        }

        context("is not correct implement") {
            it("is unknown") {
                val code = """
                val a = object : Sam {
                    override fun foo() {
                    }
                }
                """
                subject.compileAndLintWithContext(env, code).assert().isEmpty()
            }

            it("is not fun interface") {
                val code = """
                interface Sam {
                    fun foo()
                }
                val a = object : Sam {
                    override fun foo() {
                    }
                }
                """
                subject.compileAndLintWithContext(env, code).assert().isEmpty()
            }

            it("is not interface") {
                val code = """
                abstract class Something {
                    fun foo()
                }
                val a = object : Something() {
                    override fun foo() {
                    }
                }
                """
                subject.compileAndLintWithContext(env, code).assert().isEmpty()
            }

            it("has multi implement") {
                val code = """
                fun interface First {
                    fun foo()
                }
                interface Second

                val a = object : First, Second {
                    override fun foo() {
                    }
                }
                """
                subject.compileAndLintWithContext(env, code).assert().isEmpty()
            }

            it("has complex implement") {
                val code = """
                abstract class First {
                    fun foo()
                }
                fun interface Second {
                    fun foo()
                }

                val a = object : First(), Second {
                    override fun foo() {
                    }
                }
                """
                subject.compileAndLintWithContext(env, code).assert().isEmpty()
            }
        }

        context("has impurities") {
            it("has more than one method") {
                val code = """
                fun interface Sam {
                    fun foo()
                }
                val a = object : Sam {
                    override fun foo() {
                    }
                    fun bar() {
                    }
                }
                """
                subject.compileAndLintWithContext(env, code).assert().isEmpty()
            }

            it("has property") {
                val code = """
                fun interface Sam {
                    fun foo()
                }
                val a = object : Sam {
                    private var bar = 0
                    override fun foo() {
                    }
                }
                """
                subject.compileAndLintWithContext(env, code).assert().isEmpty()
            }

            it("has init") {
                val code = """
                fun interface Sam {
                    fun foo()
                }
                val a = object : Sam {
                    init {
                    }
                    override fun foo() {
                    }
                }
                """
                subject.compileAndLintWithContext(env, code).assert().isEmpty()
            }

            it("has class") {
                val code = """
                fun interface Sam {
                    fun foo()
                }
                val a = object : Sam {
                    class B
                    override fun foo() {
                    }
                }
                """
                subject.compileAndLintWithContext(env, code).assert().isEmpty()
            }

            it("has object") {
                val code = """
                fun interface Sam {
                    fun foo()
                }
                val a = object : Sam {
                    object B
                    override fun foo() {
                    }
                }
                """
                subject.compileAndLintWithContext(env, code).assert().isEmpty()
            }
        }
    }
})
