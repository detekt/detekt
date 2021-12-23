package io.gitlab.arturbosch.detekt.rules.style

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.rules.setupKotlinEnvironment
import io.gitlab.arturbosch.detekt.test.lintWithContext
import org.assertj.core.api.Assertions
import org.jetbrains.kotlin.cli.jvm.compiler.KotlinCoreEnvironment
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

class UnnecessaryInnerClassSpec : Spek({
    setupKotlinEnvironment()

    val env: KotlinCoreEnvironment by memoized()
    val subject by memoized { UnnecessaryInnerClass(Config.empty) }

    describe("UnnecessaryInnerClass Rule") {
        it("reports when an inner class does not access members of its outer class") {
            val code = """
                val fileFoo = "FILE_FOO"
                
                class A {
                    val foo = "BAR"
                    
                    fun printFoo() {
                        println(foo)
                    }
                    
                    inner class B {
                        val fizz = "BUZZ"
                        
                        fun printFizz() {
                            println(fileFoo)
                            println(fizz)
                        }
                    }
                }
            """.trimIndent()

            Assertions.assertThat(subject.lintWithContext(env, code)).hasSize(1)
        }

        context("does not report an inner class accessing outer-class members") {
            it("as a default argument for a constructor") {
                val code = """
                    class A {
                        val foo = "BAR"

                        inner class B(val fizz: String = foo)
                    }
                """.trimIndent()

                Assertions.assertThat(subject.lintWithContext(env, code)).isEmpty()
            }

            it("as a property initializer") {
                val code = """
                    class A {
                        val foo = "BAR"

                        inner class B {
                            val fizz = foo
                        }
                    }
                """.trimIndent()

                Assertions.assertThat(subject.lintWithContext(env, code)).isEmpty()
            }

            context("in a variable assignment") {
                it("where the outer-class variable is on the left") {
                    val code = """
                        class A {
                            var foo = "BAR"
    
                            inner class B {
                                val fizz = "BUZZ"
                                init {
                                    foo = fizz
                                }
                            }
                        }
                    """.trimIndent()

                    Assertions.assertThat(subject.lintWithContext(env, code)).isEmpty()
                }

                it("where the outer-class variable is on the right") {
                    val code = """
                        class A {
                            val foo = "BAR"
    
                            inner class B {
                                val fizz: String
                                init {
                                    fizz = foo
                                }
                            }
                        }
                    """.trimIndent()

                    Assertions.assertThat(subject.lintWithContext(env, code)).isEmpty()
                }

                it("where the outer-class variable is in a compound statement") {
                    val code = """
                        class A {
                            val foo = "BAR"
    
                            inner class B {
                                val fizz: String
                                init {
                                    fizz = "FOO" + foo
                                }
                            }
                        }
                    """.trimIndent()

                    Assertions.assertThat(subject.lintWithContext(env, code)).isEmpty()
                }
            }

            context("in an if-statement") {
                it("where the outer-class variable is the only expression") {
                    val code = """
                        class A(val foo: Boolean) {
                            
                            inner class B {
                                fun printFoo() {
                                    if (foo) {
                                        println("FOO")
                                    }
                                }
                            }
                        }
                    """.trimIndent()

                    Assertions.assertThat(subject.lintWithContext(env, code)).isEmpty()
                }

                it("where the outer-class variable is on the left") {
                    val code = """
                        class A {
                            val foo = "BAR"
                            
                            inner class B {
                                fun printFoo() {
                                    if (foo == "BAR") {
                                        println("FOO")
                                    }
                                }
                            }
                        }
                    """.trimIndent()

                    Assertions.assertThat(subject.lintWithContext(env, code)).isEmpty()
                }

                it("where the outer-class variable is on the right") {
                    val code = """
                        class A {
                            val foo = "BAR"
                            
                            inner class B {
                                fun printFoo() {
                                    if ("BAR" == foo) {
                                        println("FOO")
                                    }
                                }
                            }
                        }
                    """.trimIndent()

                    Assertions.assertThat(subject.lintWithContext(env, code)).isEmpty()
                }

                it("where the outer-class variable is in a compound statement") {
                    val code = """
                        class A {
                            val foo = "BAR"
                            
                            inner class B {
                                val fizz = "BUZZ"
                                fun printFoo() {
                                    if (fizz == "BUZZ" && foo) {
                                        println("FOO")
                                    }
                                }
                            }
                        }
                    """.trimIndent()

                    Assertions.assertThat(subject.lintWithContext(env, code)).isEmpty()
                }
            }

            it("as a function initializer") {
                val code = """
                    class A {
                        fun printFoo() {
                            println("FOO")
                        }
                        
                        inner class B {
                            fun printFizz() = printFoo()
                        }
                    }
                """.trimIndent()

                Assertions.assertThat(subject.lintWithContext(env, code)).isEmpty()
            }

            it("as a function call") {
                val code = """
                    class A {
                        fun printFoo() {
                            println("FOO")
                        }
                        
                        inner class B {
                            fun printFizz() {
                                printFoo()
                            }
                        }
                    }
                """.trimIndent()

                Assertions.assertThat(subject.lintWithContext(env, code)).isEmpty()
            }

            it("as a function argument") {
                val code = """
                    class A {
                        val foo = "BAR"
                        
                        inner class B {
                            fun printFizz() {
                                println(foo)
                            }
                        }
                    }
                """.trimIndent()

                Assertions.assertThat(subject.lintWithContext(env, code)).isEmpty()
            }

            it("as a default value in a function signature") {
                val code = """
                    class A {
                        val foo = "BAR"
                        
                        inner class B {
                            fun printFizz(fizzVal: String = foo) {
                                println(fizzVal)
                            }
                        }
                    }
                """.trimIndent()

                Assertions.assertThat(subject.lintWithContext(env, code)).isEmpty()
            }

            it("to call a function of the member") {
                val code = """
                    class FooClass {
                        fun printFoo() {
                            println("FOO")
                        }
                    }
                    
                    class A {
                        val foo = FooClass()
                        
                        inner class B {
                            fun printFizz() {
                                foo.printFoo()
                            }
                        }
                    }
                """.trimIndent()

                Assertions.assertThat(subject.lintWithContext(env, code)).isEmpty()
            }
        }

        it("does not report a double-nested inner class accessing from an outer-class member") {
            val code = """
                class A {
                    val foo = "BAR"
                    
                    inner class B {
                        val fizz = foo
                        inner class C {
                            fun printFoo() {
                                println(foo)
                            }
                        }
                    }
                }
            """.trimIndent()

            Assertions.assertThat(subject.lintWithContext(env, code)).isEmpty()
        }
    }
})
