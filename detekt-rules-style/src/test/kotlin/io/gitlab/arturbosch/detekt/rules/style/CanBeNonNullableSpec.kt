package io.gitlab.arturbosch.detekt.rules.style

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.rules.setupKotlinEnvironment
import io.gitlab.arturbosch.detekt.test.compileAndLint
import io.gitlab.arturbosch.detekt.test.compileAndLintWithContext
import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.kotlin.cli.jvm.compiler.KotlinCoreEnvironment
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

class CanBeNonNullableSpec : Spek({
    setupKotlinEnvironment()

    val env: KotlinCoreEnvironment by memoized()
    val subject by memoized { CanBeNonNullable(Config.empty) }

    describe("CanBeNonNullable Rule") {
        it("does not report when there is no context") {
            val code = """
                class A {
                    private var a: Int? = 5
                    fun foo() {
                        a = 6
                    }
                }
                """
            assertThat(subject.compileAndLint(code)).isEmpty()
        }

        context("evaluating private vars") {
            it("reports when class-level vars are never assigned nullable values") {
                val code = """
                class A(bVal: Int) {
                    private var a: Int? = 5
                    private var b: Int?
                    
                    init {
                        b = bVal
                    }
                    
                    fun foo(): Int {
                        val b = a!!
                        a = b + 1
                        val a = null
                        return b
                    }
                }
                """
                assertThat(subject.compileAndLintWithContext(env, code)).hasSize(2)
            }

            it("reports when vars utilize non-nullable delegate values") {
                val code = """
                    import kotlin.reflect.KProperty
                    
                    class A {
                        private var a: Int? by PropDelegate()
                    
                        fun foo(): Int {
                            val b = a!!
                            a = b + 1
                            val a = null
                            return b
                        }
                    }
                    
                    class PropDelegate(private var propVal: Int = 0) {
                        operator fun getValue(thisRef: A, property: KProperty<*>): Int {
                            return propVal
                        }
                    
                        operator fun setValue(thisRef: A, property: KProperty<*>, value: Any?) {
                            if (value is Int) {
                                propVal = value
                            }
                        }
                    }
                    """
                assertThat(subject.compileAndLintWithContext(env, code)).hasSize(1)
            }

            it("reports when file-level vars are never assigned nullable values") {
                val code = """
                private var fileA: Int? = 5
                private var fileB: Int? = 5

                fun fileFoo() {
                    fileB = 6
                }
                """
                assertThat(subject.compileAndLintWithContext(env, code)).hasSize(2)
            }

            it("does not report when class-level vars are assigned nullable values") {
                val code = """
                import kotlin.random.Random

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
                        return fizz
                    }

                    private fun buzz(bizz: Int): Int? {
                        return if (bizz % 2 == 0) null else bizz
                    }
                }
                """
                assertThat(subject.compileAndLintWithContext(env, code)).isEmpty()
            }

            it("does not report vars that utilize nullable delegate values") {
                val code = """
                    class A(private var aDelegate: Int?) {
                        private var a: Int? by this::aDelegate
                    }
                    """
                assertThat(subject.compileAndLintWithContext(env, code)).isEmpty()
            }

            it("does not report when file-level vars are assigned nullable values") {
                val code = """
                import kotlin.random.Random

                private var fileA: Int? = 5
                private var fileB: Int? = null
                private var fileC: Int? = 5

                fun fileFoo() {
                    fileC = null
                }

                class A {
                    fun foo() {
                        fileA = null
                    }
                }
                """
                assertThat(subject.compileAndLintWithContext(env, code)).isEmpty()
            }

            it("reports when vars with private setters are never assigned nullable values") {
                val code = """
                class A {
                    var a: Int? = 5
                        private set
                    fun foo() {
                        a = 6
                    }
                }
                """
                assertThat(subject.compileAndLintWithContext(env, code)).hasSize(1)
            }

            it("does not report when vars with private setters are assigned nullable values") {
                val code = """
                class A {
                    var a: Int? = 5
                        private set
                    fun foo() {
                        a = null
                    }
                }
                """
                assertThat(subject.compileAndLintWithContext(env, code)).isEmpty()
            }

            it("does not report when vars use public setters") {
                val code = """
                class A {
                    var a: Int? = 5
                    fun foo() {
                        a = 6
                    }
                }
                """
                assertThat(subject.compileAndLintWithContext(env, code)).isEmpty()
            }

            it("does not report when vars use non-private setters") {
                val code = """
                    class A {
                        var a: Int? = 5
                            internal set
                        fun foo() {
                            a = 6
                        }
                    }
                    """
                assertThat(subject.compileAndLintWithContext(env, code)).isEmpty()
            }

            it("does not report when private vars are declared in the constructor") {
                val code = """
                class A(private var a: Int?) {
                    fun foo() {
                        a = 6
                    }
                }
                """
                assertThat(subject.compileAndLintWithContext(env, code)).isEmpty()
            }
        }

        context("evaluating private vars") {
            it("reports when class-level vals are set to non-nullable values") {
                val code = """
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
                assertThat(subject.compileAndLintWithContext(env, code)).hasSize(3)
            }

            it("reports when vals utilize non-nullable delegate values") {
                val code = """
                class A {
                    val a: Int? by lazy {
                        5
                    }
                }
                """
                assertThat(subject.compileAndLintWithContext(env, code)).hasSize(1)
            }

            it("reports when file-level vals are set to non-nullable values") {
                val code = """
                val fileA: Int? = 5
                """
                assertThat(subject.compileAndLintWithContext(env, code)).hasSize(1)
            }

            it("does not report when class-level vals are assigned a nullable value") {
                val code = """
                import kotlin.random.Random

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
                assertThat(subject.compileAndLintWithContext(env, code)).isEmpty()
            }

            it("does not report when vals utilize nullable delegate values") {
                val code = """
                import kotlin.random.Random

                class A {
                    val d: Int? by lazy {
                        val randVal = Random.nextInt()
                        if (randVal % 2 == 0) randVal else null
                    }
                }
                """
                assertThat(subject.compileAndLintWithContext(env, code)).isEmpty()
            }

            it("does not report when file-level vals are assigned a nullable value") {
                val code = """
                val fileA: Int? = null
                """
                assertThat(subject.compileAndLintWithContext(env, code)).isEmpty()
            }

            it("does not report when vals are declared non-nullable") {
                val code = """
                class A {
                    val a: Int = 5
                }
                """
                assertThat(subject.compileAndLintWithContext(env, code)).isEmpty()
            }

            it("does not report when vals are declared in the constructor") {
                val code = """
                class A(private val a: Int?)
                """
                assertThat(subject.compileAndLintWithContext(env, code)).isEmpty()
            }

            it("reports when vals with getters never return nullable values") {
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
                assertThat(subject.compileAndLintWithContext(env, code)).hasSize(3)
            }

            it("does not report when vals with getters return potentially-nullable values") {
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
                    """
                assertThat(subject.compileAndLintWithContext(env, code)).isEmpty()
            }
        }

        it("does not report open properties") {
            val code = """
                abstract class A {
                    open val a: Int? = 5
                    open var b: Int? = 5
                }
                """
            assertThat(subject.compileAndLintWithContext(env, code)).isEmpty()
        }

        it("does not report properties whose initial assignment derives from unsafe non-Java code") {
            val code = """
                class A(msg: String?) {
                    private val e = Exception(msg)
                    // e.localizedMessage is marked as String! by Kotlin, meaning Kotlin
                    // cannot guarantee that it will be non-null, even though it is treated
                    // as non-null in Kotlin code.
                    private var a: String? = e.localizedMessage
                }
                """
            assertThat(subject.compileAndLintWithContext(env, code)).isEmpty()
        }

        it("does not report interface properties") {
            val code = """
                interface A {
                    val a: Int?
                    var b: Int?
                }
                """
            assertThat(subject.compileAndLintWithContext(env, code)).isEmpty()
        }
    }
})
