package io.gitlab.arturbosch.detekt.rules.style

import io.gitlab.arturbosch.detekt.api.SourceLocation
import io.gitlab.arturbosch.detekt.test.assert
import io.gitlab.arturbosch.detekt.test.compileAndLint
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

class SingleAbstractMethodSpec : Spek({

    val subject by memoized { SingleAbstractMethod() }

    describe("SingleAbstractMethod rule") {

        context("report convertible expression") {
            it("is property") {
                val code = """
                val a = object : Something {
                    override fun foo() {
                    }
                }
                """
                subject.compileAndLint(code)
                    .assert()
                    .hasSize(1)
                    .hasSourceLocations(SourceLocation(1, 9))
            }

            it("is in function") {
                val code = """
                fun a() {
                    object : Something {
                        override fun foo() {
                        }
                    }
                }
                """
                subject.compileAndLint(code)
                    .assert()
                    .hasSize(1)
                    .hasSourceLocations(SourceLocation(2, 5))
            }

            it("is in init") {
                val code = """
                object B {
                    init {
                        object : Something {
                            override fun foo() {
                            }
                        }
                    }
                }
                """
                subject.compileAndLint(code)
                    .assert()
                    .hasSize(1)
                    .hasSourceLocations(SourceLocation(3, 9))
            }
        }

        context("is not implement just one") {
            it("is not interface") {
                val code = """
                val a = object : Something() {
                    override fun foo() {
                    }
                }
                """
                subject.compileAndLint(code).assert().isEmpty()
            }

            it("has multi implement") {
                val code = """
                val a = object : Something1, Something2 {
                    override fun foo() {
                    }
                }
                """
                subject.compileAndLint(code).assert().isEmpty()
            }

            it("has complex implement") {
                val code = """
                val a = object : Something1(), Something2 {
                    override fun foo() {
                    }
                }
                """
                subject.compileAndLint(code).assert().isEmpty()
            }
        }

        context("has impurities") {
            it("has more than one method") {
                val code = """
                val a = object : Something {
                    override fun foo() {
                    }
                    override fun bar() {
                    }
                }
                """
                subject.compileAndLint(code).assert().isEmpty()
            }

            it("has own method") {
                val code = """
                val a = object : Something {
                    fun foo() {
                    }
                    override fun bar() {
                    }
                }
                """
                subject.compileAndLint(code).assert().isEmpty()
            }

            it("has property") {
                val code = """
                val a = object : Something {
                    private var bar = 0
                    override fun foo() {
                    }
                }
                """
                subject.compileAndLint(code).assert().isEmpty()
            }

            it("has init") {
                val code = """
                val a = object : Something {
                    init {
                    }
                    override fun foo() {
                    }
                }
                """
                subject.compileAndLint(code).assert().isEmpty()
            }

            it("has class") {
                val code = """
                val a = object : Something {
                    class B
                    override fun foo() {
                    }
                }
                """
                subject.compileAndLint(code).assert().isEmpty()
            }

            it("has object") {
                val code = """
                val a = object : Something {
                    object B
                    override fun foo() {
                    }
                }
                """
                subject.compileAndLint(code).assert().isEmpty()
            }
        }
    }
})
