package io.gitlab.arturbosch.detekt.rules.style

import io.github.detekt.test.utils.resourceAsPath
import io.gitlab.arturbosch.detekt.api.SourceLocation
import io.gitlab.arturbosch.detekt.rules.setupKotlinEnvironment
import io.gitlab.arturbosch.detekt.test.assert
import io.gitlab.arturbosch.detekt.test.compileAndLint
import io.gitlab.arturbosch.detekt.test.compileAndLintWithContext
import io.gitlab.arturbosch.detekt.test.lintWithContext
import org.jetbrains.kotlin.cli.jvm.compiler.KotlinCoreEnvironment
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

class ObjectLiteralToLambdaSpec : Spek({
    setupKotlinEnvironment(additionalJavaSourceRootPath = resourceAsPath("java"))

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

            it("is generic") {
                val code = """
                fun interface Sam<T> {
                    fun foo(): T
                }
                val a = object : Sam<Int> {
                    override fun foo(): Int {
                        return 1
                    }
                }   
                """
                subject.compileAndLintWithContext(env, code)
                    .assert()
                    .hasSize(1)
                    .hasSourceLocations(SourceLocation(4, 9))
            }

            it("has other default method") {
                val code = """
                fun interface Sam {
                    fun foo()
                    fun bar() {}
                }
                val a = object : Sam {
                    override fun foo() {
                    }
                }   
                """
                subject.compileAndLintWithContext(env, code)
                    .assert()
                    .hasSize(1)
                    .hasSourceLocations(SourceLocation(5, 9))
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

            it("expression body syntax") {
                val code = """
                fun interface Sam {
                    fun foo(): Int
                }
                val a = object : Sam {
                    override fun foo() = 3
                }
                """
                subject.compileAndLintWithContext(env, code)
                    .assert()
                    .hasSize(1)
                    .hasSourceLocations(SourceLocation(4, 9))
            }
        }

        context("is not correct implement") {
            it("without type resolution") {
                val code = """
                fun interface Sam {
                    fun foo()
                }
                val a = object : Sam {
                    override fun foo() {
                    }
                }
                """
                subject.compileAndLint(code).assert().isEmpty()
            }

            it("is empty interface") {
                val code = """
                interface Sam
                val a = object : Sam {}
                """
                subject.compileAndLintWithContext(env, code).assert().isEmpty()
            }

            it("is empty interface and has own function") {
                val code = """
                interface Sam
                val a = object : Sam {
                    fun foo() {
                    }
                }
                """
                subject.compileAndLintWithContext(env, code).assert().isEmpty()
            }

            it("is single property interface") {
                val code = """
                interface Sam {
                    val foo: Int
                }
                val a = object : Sam {
                    override val foo = 1
                }
                """
                subject.compileAndLintWithContext(env, code).assert().isEmpty()
            }

            it("is empty interface and has own property") {
                val code = """
                interface Sam
                val a = object : Sam {
                    val b = 1
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
                    abstract fun foo()
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

                val a: First = object : First, Second {
                    override fun foo() {
                    }
                }
                """
                subject.compileAndLintWithContext(env, code).assert().isEmpty()
            }

            it("has complex implement") {
                val code = """
                abstract class First {
                    abstract fun foo()
                }
                fun interface Second {
                    fun foo()
                }

                val a: First = object : First(), Second {
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
        }

        context("java interface") {
            it("is convertible") {
                val code = """
                val a = object : Runnable { 
                    override fun run(){
                    }
                }
                """
                subject.compileAndLintWithContext(env, code)
                    .assert()
                    .hasSize(1)
                    .hasSourceLocations(SourceLocation(1, 9))
            }

            it("is convertible generic") {
                val code = """
                import java.util.concurrent.Callable
                val a = object : Callable<Int> {
                    override fun call(): Int {
                        return 1
                    }
                }
                """
                subject.compileAndLintWithContext(env, code)
                    .assert()
                    .hasSize(1)
                    .hasSourceLocations(SourceLocation(2, 9))
            }

            it("empty interface") {
                val code = """
                import java.util.EventListener
                val a = object : EventListener {
                    fun foo() {
                    }
                }
                """
                subject.compileAndLintWithContext(env, code).assert().isEmpty()
            }

            it("is convertible generic") {
                val code = """
                import java.util.Enumeration
                val a = object : Enumeration<Int> {
                    override fun hasMoreElements(): Boolean {
                        return true
                    }

                    override fun nextElement(): Int {
                        return 1
                    }
                }
                """
                subject.compileAndLintWithContext(env, code).assert().isEmpty()
            }

            it("has other default methods") {
                val code = """
                import ObjectLiteralToLambda.*
                
                fun main() {
                    val x = object : SamWithDefaultMethods {
                        override fun foo() {
                            println()
                        }
                    }
                } 
                """
                subject.lintWithContext(env, code).assert().hasSize(1)
            }

            it("has only default methods") {
                val code = """
                import ObjectLiteralToLambda.*
                
                fun main() {
                    val x = object : OnlyDefaultMethods {
                    }
                } 
                """
                subject.lintWithContext(env, code).assert().isEmpty()
            }

            it("implements a default method") {
                val code = """
                import ObjectLiteralToLambda.*
                
                fun main() {
                    val x = object : OnlyDefaultMethods {
                        override fun foo() {
                            println()
                        }
                    }
                } 
                """
                subject.lintWithContext(env, code).assert().isEmpty()
            }
        }

        context("object use itself") {
            it("call `this`") {
                val code = """
                fun interface Sam {
                    fun foo()
                }
            
                fun aa() {
                    object : Sam {
                        override fun foo() {
                            this
                        }
                    }
                }
                """
                subject.compileAndLintWithContext(env, code).assert().isEmpty()
            }

            it("use `this`") {
                val code = """
                fun interface Sam {
                    fun foo()
                }

                fun Sam.bar() {}
            
                fun aa() {
                    object : Sam {
                        override fun foo() {
                            bar()
                        }
                    }
                }
                """
                subject.compileAndLintWithContext(env, code).assert().isEmpty()
            }

            it("use class method") {
                val code = """
                fun interface Sam {
                    fun foo()
                }
            
                fun aa() {
                    object : Sam {
                        override fun foo() {
                            hashCode()
                        }
                    }
                }
                """
                subject.compileAndLintWithContext(env, code).assert().isEmpty()
            }

            it("call `this` inside nested object") {
                val code = """
                fun interface Sam {
                    fun foo()
                }
            
                fun aa() {
                    object : Sam {
                        override fun foo() {
                            object : Sam {
                                override fun foo() {
                                    this
                                }
                            }
                        }
                    }
                }
                """
                subject.compileAndLintWithContext(env, code)
                    .assert()
                    .hasSize(1)
                    .hasSourceLocations(SourceLocation(6, 5))
            }

            it("call labeled `this`") {
                val code = """
                fun interface Sam {
                    fun foo()
                }
            
                class Target {
                    init {
                        object : Sam {
                            override fun foo() {
                                this@Target
                            }
                        }
                    }
                }
                """
                subject.compileAndLintWithContext(env, code)
                    .assert()
                    .hasSize(1)
                    .hasSourceLocations(SourceLocation(7, 9))
            }

            it("recursive call") {
                val code = """
                fun interface Sam {
                    fun foo()
                }
            
                fun a() {
                    object : Sam {
                        override fun foo() {
                            foo()
                        }
                    }
                }
                """
                subject.compileAndLintWithContext(env, code).assert().isEmpty()
            }
        }

        context("Edge case") {
            // https://github.com/detekt/detekt/pull/3599#issuecomment-806389701
            it(
                """Anonymous objects are always newly created,
                |but lambdas are singletons,
                |so they have the same reference.""".trimMargin()
            ) {
                val code = """
                fun interface Sam {
                    fun foo()
                }
    
                fun newObject() = object : Sam {
                    override fun foo() {
                    }
                }
    
                fun lambda() = Sam {}

                val a = newObject() === newObject() // false
                val b = lambda() === lambda() // true
                """
                subject.compileAndLintWithContext(env, code)
                    .assert()
                    .hasSize(1)
                    .hasSourceLocations(SourceLocation(5, 19))
            }
        }
    }
})
