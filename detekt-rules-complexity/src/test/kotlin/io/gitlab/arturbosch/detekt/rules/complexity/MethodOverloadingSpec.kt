package io.gitlab.arturbosch.detekt.rules.complexity

import io.gitlab.arturbosch.detekt.test.compileAndLint
import org.assertj.core.api.Assertions.assertThat
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

class MethodOverloadingSpec : Spek({

    val subject by memoized { MethodOverloading(threshold = 3) }

    describe("MethodOverloading rule") {

        context("several overloaded methods") {

            it("reports overloaded methods which exceed the threshold") {
                val code = """
                    class Test {
                        fun x() {}
                        fun x(i: Int) {}
                        fun x(i: Int, j: Int) {}
                    }
                """
                val findings = subject.compileAndLint(code)
                assertThat(findings).hasSize(1)
                assertThat(findings[0].message).isEqualTo("The method 'x' is overloaded 3 times.")
            }

            it("reports overloaded top level methods which exceed the threshold") {
                val code = """
                    fun x() {}
                    fun x(i: Int) {}
                    fun x(i: Int, j: Int) {}
                """
                assertThat(subject.compileAndLint(code)).hasSize(1)
            }

            it("does not report overloaded methods which do not exceed the threshold") {
                subject.compileAndLint("""
                class Test {
                    fun x() { }
                    fun x(i: Int) { }
                }""")
                assertThat(subject.findings.size).isZero()
            }
        }

        context("several overloaded extensions methods") {

            it("does not report extension methods with a different receiver") {
                subject.compileAndLint("""
                fun Boolean.foo() {}
                fun Int.foo() {}
                fun Long.foo() {}""")
                assertThat(subject.findings.size).isZero()
            }

            it("reports extension methods with the same receiver") {
                subject.compileAndLint("""
                fun Int.foo() {}
                fun Int.foo(i: Int) {}
                fun Int.foo(i: String) {}""")
                assertThat(subject.findings.size).isEqualTo(1)
            }
        }

        context("several nested overloaded methods") {

            it("reports nested overloaded methods which exceed the threshold") {
                val code = """
                    class Outer {
                        internal class Inner {
                            fun f() {}
                            fun f(i: Int) {}
                            fun f(i: Int, j: Int) {}
                        }
                    }
                """
                assertThat(subject.compileAndLint(code)).hasSize(1)
            }

            it("does not report nested overloaded methods which do not exceed the threshold") {
                val code = """
                    class Outer {

                        fun f() {}

                        internal class Inner {
                            fun f(i: Int) {}
                            fun f(i: Int, j: Int) {}
                        }
                    }
                """
                assertThat(subject.compileAndLint(code)).isEmpty()
            }
        }

        context("several overloaded methods inside objects") {

            it("reports overloaded methods inside an object which exceed the threshold") {
                val code = """
                    object Test {
                        fun f() {}
                        fun f(i: Int) {}
                        fun f(i: Int, j: Int) {}
                    }
                """
                assertThat(subject.compileAndLint(code)).hasSize(1)
            }

            it("does not report overloaded methods inside an object which do not exceed the threshold") {
                val code = """
                    object Test {
                        fun f() {}
                        fun f(i: Int) {}
                    }
                """
                assertThat(subject.compileAndLint(code)).isEmpty()
            }

            it("reports overloaded methods inside a companion object which exceed the threshold") {
                val code = """
                    class Test {
                        companion object {
                            fun f() {}
                            fun f(i: Int) {}
                            fun f(i: Int, j: Int) {}
                        }
                    }
                """
                assertThat(subject.compileAndLint(code)).hasSize(1)
            }

            it("does not report overloaded methods in a companion object that do not exceed the threshold") {
                val code = """
                    class Test {
                        companion object {
                            fun f() {}
                            fun f(i: Int) {}
                        }
                    }
                """
                assertThat(subject.compileAndLint(code)).isEmpty()
            }

            it("does not report overloaded methods in classes/objects that do not exceed the threshold") {
                val code = """
                    class Test {

                        fun f() {}

                        companion object {
                            fun f() {}
                            fun f(i: Int) {}
                        }
                    }
                """
                assertThat(subject.compileAndLint(code)).isEmpty()
            }

            it("reports overloaded methods inside an anonymous object expression") {
                val code = """
                    class A {
                    
                        fun f() {
                            object : Runnable {
                                override fun run() {}
                                fun run(i: Int) {}
                                fun run(i: Int, j: Int) {}
                            }
                        }
                    }
                """
                assertThat(subject.compileAndLint(code)).hasSize(1)
            }
        }

        context("several overloaded methods inside enum classes") {

            it("does not report overridden methods inside enum entries") {
                val code = """
                    enum class Test {
                        E1 {
                            override fun f() {}
                        },
                        E2 {
                            override fun f() {}
                        },
                        E3 {
                            override fun f() {}
                        };

                        abstract fun f()
                    }
                """
                assertThat(subject.compileAndLint(code)).isEmpty()
            }

            it("reports overloaded methods in enum entry") {
                val code = """
                    enum class Test {
                        E {
                            fun f(i: Int) {}
                            fun f(i: Int, j: Int) {}
                        };
                        fun f() {}
                    }
                """
                assertThat(subject.compileAndLint(code)).hasSize(1)
            }

            it("reports overloaded methods in enum class") {
                val code = """
                    enum class Test {
                        E;
                    
                        fun f() {}
                        fun f(i: Int) {}
                        fun f(i: Int, j: Int) {}
                    }
                """
                assertThat(subject.compileAndLint(code)).hasSize(1)
            }
        }

        it("does not report a class without a body") {
            val code = "class A"
            assertThat(subject.compileAndLint(code)).isEmpty()
        }

        it("does not report overloaded local functions") {
            val code = """
                fun top() {
                    fun f() {}
                    fun f(i: Int) {}
                    fun f(i: Int, j: Int) {}
                }
            """
            assertThat(subject.compileAndLint(code)).isEmpty()
        }
    }
})
