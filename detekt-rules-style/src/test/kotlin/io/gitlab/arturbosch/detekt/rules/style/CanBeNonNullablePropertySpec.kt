package io.gitlab.arturbosch.detekt.rules.style

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.rules.setupKotlinEnvironment
import io.gitlab.arturbosch.detekt.test.compileAndLint
import io.gitlab.arturbosch.detekt.test.compileAndLintWithContext
import org.assertj.core.api.Assertions
import org.jetbrains.kotlin.cli.jvm.compiler.KotlinCoreEnvironment
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

class CanBeNonNullablePropertySpec : Spek({
    setupKotlinEnvironment()

    val env: KotlinCoreEnvironment by memoized()
    val subject by memoized { CanBeNonNullableProperty(Config.empty) }

    describe("CanBeNonNullableProperty Rule") {
        it("does not report when there is no context") {
            val code = """
                class A {
                    private var a: Int? = 5
                    fun foo() {
                        a = 6
                    }
                }
                """
            Assertions.assertThat(subject.compileAndLint(code)).isEmpty()
        }

        it("reports private vars that are never assigned nullable values") {
            val code = """
                private var fileA: Int? = 5
                private var fileB: Int? = 5

                fun fileFoo() {
                    fileB = 6
                }

                class A(bVal: Int) {
                    private var a: Int? = 5
                    private var b: Int?
                    
                    init {
                        b = bVal
                    }
                    
                    fun foo(): Int {
                        val b = a
                        a = b + 1
                        fileA = a + 1
                        val a = null
                        return b
                    }
                }
                """
            Assertions.assertThat(subject.compileAndLintWithContext(env, code)).hasSize(4)
        }

        it("does not report private vars that are assigned nullable values") {
            val code = """
                private var fileA: Int? = 5
                private var fileB: Int? = null
                private var fileC: Int? = 5

                fun fileFoo() {
                    fileC = null
                }

                class A(fVal: Int?) {
                    private var a: Int? = 0
                    private var b: Int? = 0
                    private var c: Int? = 0
                    private var d: Int? = 0
                    private var e: Int? = null
                    private var f: Int?
                    
                    init {
                        f = fVal
                    }

                    fun foo(fizz: Int): Int {
                        a = null
                        b = if (fizz % 2 == 0) fizz else null
                        c = buzz(fizz)
                        d = a
                        fileA = null
                        return fizz
                    }

                    private fun buzz(bizz: Int): Int? {
                        return if (bizz % 2 == 0) null else bizz
                    }
                }
                """
            Assertions.assertThat(subject.compileAndLintWithContext(env, code)).isEmpty()
        }

        it("reports vars with private setters that are never assigned nullable values") {
            val code = """
                class A {
                    var a: Int? = 5
                        private set
                    fun foo() {
                        a = 6
                    }
                }
                """
            Assertions.assertThat(subject.compileAndLintWithContext(env, code)).hasSize(1)
        }

        it("does not vars with private setters that are assigned nullable values") {
            val code = """
                class A {
                    var a: Int? = 5
                        private set
                    fun foo() {
                        a = null
                    }
                }
                """
            Assertions.assertThat(subject.compileAndLintWithContext(env, code)).isEmpty()
        }

        it("does not report non-private vars with non-private setters") {
            val code = """
                class A {
                    var a: Int? = 5
                    fun foo() {
                        a = 6
                    }
                }
                """
            Assertions.assertThat(subject.compileAndLintWithContext(env, code)).isEmpty()
        }

        it("does not report private vars that are declared in the constructor") {
            val code = """
                class A(private var a: Int?) {
                    fun foo() {
                        a = 6
                    }
                }
                """
            Assertions.assertThat(subject.compileAndLintWithContext(env, code)).isEmpty()
        }

        it("reports vals that are set to non-nullable values") {
            val code = """
                val fileA: Int? = 5

                class A(cVal: Int) {
                    val a: Int? = 5
                    val b: Int?
                    val c: Int?
                    
                    init {
                        b = 5
                        c = cVal
                    }
                }
                """
            Assertions.assertThat(subject.compileAndLintWithContext(env, code)).hasSize(4)
        }

        it("does not report vals that are assigned a nullable value") {
            val code = """
                val fileA: Int? = null

                class A(cVal: Int?) {
                    val a: Int? = null
                    val b: Int?
                    val c: Int?
                    
                    init {
                        b = null
                        c = cVal
                    }
                }
                """
            Assertions.assertThat(subject.compileAndLintWithContext(env, code)).isEmpty()
        }

        it("does not report vals that are declared non-nullable") {
            val code = """
                class A {
                    val a: Int = 5
                }
                """
            Assertions.assertThat(subject.compileAndLintWithContext(env, code)).isEmpty()
        }

        it("does not report vals that are declared in the constructor") {
            val code = """
                class A(private val a: Int?)
                """
            Assertions.assertThat(subject.compileAndLintWithContext(env, code)).isEmpty()
        }

        it("reports vals with getters that never return nullable values") {
            val code = """
                class A {
                    val a: Int?
                        get() = 5
                    val b: Int?
                        get() {
                            return 5
                        }
                    val c: Int?
                        get() = foo()
                    
                    private fun foo(): Int {
                        return 5
                    }
                }
                """
            Assertions.assertThat(subject.compileAndLintWithContext(env, code)).hasSize(3)
        }

        it("does not report vals with getters that return potentially-nullable values") {
            val code = """
                import kotlin.random.Random
                
                class A {
                    val a: Int?
                        get() = Random.nextInt()?.let { if (it % 2 == 0) it else null }
                    val b: Int?
                        get() {
                            return Random.nextInt()?.let { if (it % 2 == 0) it else null }
                        }
                    val c: Int?
                        get() = foo()
                    
                    private fun foo(): Int? {
                        val randInt = Random.nextInt()
                        return if (randInt % 2 == 0) randInt else null
                    }
                }
            """.trimIndent()
            Assertions.assertThat(subject.compileAndLintWithContext(env, code)).isEmpty()
        }
    }
})
