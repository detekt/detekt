package io.gitlab.arturbosch.detekt.rules.style

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.rules.KotlinCoreEnvironmentTest
import io.gitlab.arturbosch.detekt.test.assertThat
import io.gitlab.arturbosch.detekt.test.lintWithContext
import org.jetbrains.kotlin.cli.jvm.compiler.KotlinCoreEnvironment
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

@KotlinCoreEnvironmentTest
class UnnecessaryInnerClassSpec(val env: KotlinCoreEnvironment) {
    val subject = UnnecessaryInnerClass(Config.empty)

    @Nested
    inner class `UnnecessaryInnerClass Rule` {
        @Test
        fun `reports when an inner class does not access members of its outer class`() {
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

            assertThat(subject.lintWithContext(env, code)).hasSize(1)
        }

        @Nested
        inner class `does not report an inner class accessing outer-class members` {
            @Test
            fun `as a default argument for a constructor`() {
                val code = """
                    class A {
                        val foo = "BAR"

                        inner class B(val fizz: String = foo)
                    }
                """.trimIndent()

                assertThat(subject.lintWithContext(env, code)).isEmpty()
            }

            @Test
            fun `as a property initializer`() {
                val code = """
                    class A {
                        val foo = "BAR"

                        inner class B {
                            val fizz = foo
                        }
                    }
                """.trimIndent()

                assertThat(subject.lintWithContext(env, code)).isEmpty()
            }

            @Nested
            inner class `in a variable assignment` {
                @Test
                fun `where the outer-class variable is on the left`() {
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

                    assertThat(subject.lintWithContext(env, code)).isEmpty()
                }

                @Test
                fun `where the outer-class variable is on the right`() {
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

                    assertThat(subject.lintWithContext(env, code)).isEmpty()
                }

                @Test
                fun `where the outer-class variable is in a compound statement`() {
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

                    assertThat(subject.lintWithContext(env, code)).isEmpty()
                }
            }

            @Nested
            inner class `in an if-statement` {
                @Test
                fun `where the outer-class variable is the only expression`() {
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

                    assertThat(subject.lintWithContext(env, code)).isEmpty()
                }

                @Test
                fun `where the outer-class variable is on the left`() {
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

                    assertThat(subject.lintWithContext(env, code)).isEmpty()
                }

                @Test
                fun `where the outer-class variable is on the right`() {
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

                    assertThat(subject.lintWithContext(env, code)).isEmpty()
                }

                @Test
                fun `where the outer-class variable is in a compound statement`() {
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

                    assertThat(subject.lintWithContext(env, code)).isEmpty()
                }
            }

            @Test
            fun `as a function initializer`() {
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

                assertThat(subject.lintWithContext(env, code)).isEmpty()
            }

            @Test
            fun `as a function call`() {
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

                assertThat(subject.lintWithContext(env, code)).isEmpty()
            }

            @Test
            fun `as a function argument`() {
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

                assertThat(subject.lintWithContext(env, code)).isEmpty()
            }

            @Test
            fun `as a default value in a function signature`() {
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

                assertThat(subject.lintWithContext(env, code)).isEmpty()
            }

            @Test
            fun `to call a function of the member`() {
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

                assertThat(subject.lintWithContext(env, code)).isEmpty()
            }
        }

        @Nested
        inner class `does not report a double-nested inner class accessing from an outer-class member` {

            @Test
            fun `when the innermost class refers a inner class and the inner class refers the outermost class`() {
                val code = """
                    class A {
                        val foo = "BAR"
                        
                        inner class B {
                            val fizz = foo
                            inner class C {
                                fun printFoo() {
                                    println(fizz)
                                }
                            }
                        }
                    }
                """.trimIndent()

                assertThat(subject.lintWithContext(env, code)).isEmpty()
            }

            @Test
            fun `when the innermost class refers the outermost class`() {
                val code = """
                    class A {
                        val foo = "BAR"
                        
                        inner class B {
                            inner class C {
                                fun printFoo() {
                                    println(foo)
                                }
                            }
                        }
                    }
                """.trimIndent()

                assertThat(subject.lintWithContext(env, code)).isEmpty()
            }
        }

        @Test
        fun `does not report anonymous inner classes`() {
            val code = """
                interface FooInterface {
                    fun doFoo()
                }

                class Foo {
                    fun runFoo(fi: FooInterface) {
                        fi.doFoo()
                    }
                    
                    fun run() {
                        runFoo(object : FooInterface {
                            override fun doFoo() {
                                println("FOO")
                            }
                        })
                    }
                }
            """.trimIndent()

            assertThat(subject.lintWithContext(env, code)).isEmpty()
        }
    }
}
