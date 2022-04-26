package io.gitlab.arturbosch.detekt.rules.style

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.rules.KotlinCoreEnvironmentTest
import io.gitlab.arturbosch.detekt.test.compileAndLint
import io.gitlab.arturbosch.detekt.test.compileAndLintWithContext
import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.kotlin.cli.jvm.compiler.KotlinCoreEnvironment
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

@KotlinCoreEnvironmentTest
class UnnecessaryApplySpec(val env: KotlinCoreEnvironment) {

    val subject = UnnecessaryApply(Config.empty)

    @Nested
    inner class `unnecessary apply expressions that can be changed to ordinary method call` {

        @Test
        fun `reports an apply on non-nullable type`() {
            val findings = subject.compileAndLintWithContext(
                env,
                """
                fun f() {
                    val a: Int = 0
                    a.apply {
                        plus(1)
                    }
                }
                """
            )
            assertThat(findings).hasSize(1)
            assertThat(findings.first().message).isEqualTo("apply expression can be omitted")
        }

        @Test
        fun `reports an apply on nullable type`() {
            val findings = subject.compileAndLintWithContext(
                env,
                """
                fun f() {
                    val a: Int? = null
                    // Resolution: we can't say here if plus is on 'this' or just a side effect when a is not null
                    // However such cases should be better handled with an if-null check instead of misusing apply
                    a?.apply {
                        plus(1)
                    }
                }
                """
            )
            assertThat(findings).hasSize(1)
            assertThat(findings.first().message).isEqualTo("apply can be replaced with let or an if")
        }

        @Test
        fun `reports a false negative apply on nullable type - #1485`() {
            assertThat(
                subject.compileAndLintWithContext(
                    env,
                    """
                val a: Any? = Any()
                fun Any.b() = Unit
                
                fun main() {
                    a?.apply {
                        b()
                    }
                }
                    """
                )
            ).hasSize(1)
        }

        @Test
        fun `does not report an apply with lambda block`() {
            assertThat(
                subject.compileAndLintWithContext(
                    env,
                    """
                fun f() {
                    val a: Int? = null
                    a?.apply({
                        plus(1)
                    })
                }
                    """
                )
            ).isEmpty()
        }

        @Test
        fun `does not report single statement in apply used as function argument`() {
            assertThat(
                subject.compileAndLintWithContext(
                    env,
                    """
                fun b(i: Int?) {}

                fun main() {
                    val a: Int? = null
                    b(a.apply {
                        toString()
                    })
                }
                    """
                )
            ).isEmpty()
        }

        @Test
        fun `does not report single assignment statement in apply used as function argument - #1517`() {
            assertThat(
                subject.compileAndLintWithContext(
                    env,
                    """
                class C {
                    var prop = 0
                }
                    
                fun main() {
                    val list = ArrayList<C>()
                    list.add(
                        if (true) {
                            C().apply { 
                                prop = 1
                            }
                        } else {
                            C()
                        }
                    )
                }
                    """
                )
            ).isEmpty()
        }

        @Test
        fun `does not report if result of apply is used - #2938`() {
            assertThat(
                subject.compileAndLint(
                    """
                fun main() {
                    val a = listOf(mutableListOf(""))
                                .map { it.apply { add("") } }
                }
                    """
                )
            ).isEmpty()
        }

        @Test
        fun `does not report applies with lambda body containing more than one statement`() {
            assertThat(
                subject.compileAndLintWithContext(
                    env,
                    """
                fun b(i: Int?) {}

                fun main() {
                    val a: Int? = null
                    a?.apply {
                        plus(1)
                        plus(2)
                    }
                    a?.apply {
                        plus(1)
                        plus(2)
                    }
                    b(1.apply {
                        plus(1)
                        plus(2)
                    })
                }
                    """
                )
            ).isEmpty()
        }

        @Test
        fun `reports when lambda has a dot qualified expression`() {
            val findings = subject.compileAndLintWithContext(
                env,
                """
                fun test(foo: Foo) {
                    foo.apply {
                        bar.bar()
                    }
                }
                
                class Foo(val bar: Bar)
                
                class Bar {
                    fun bar() {}
                }
                """
            )
            assertThat(findings).hasSize(1)
        }

        @Test
        fun `reports when lambda has a dot qualified expression which has 'this' receiver`() {
            val findings = subject.compileAndLintWithContext(
                env,
                """
                fun test(foo: Foo) {
                    foo.apply {
                        this.bar.bar()
                    }
                }
                
                class Foo(val bar: Bar)
                
                class Bar {
                    fun bar() {}
                }
                """
            )
            assertThat(findings).hasSize(1)
        }

        @Test
        fun `reports when lambda has a 'this' expression`() {
            val findings = subject.compileAndLintWithContext(
                env,
                """
                fun test() {
                    "foo".apply {
                        this
                    }
                }
                """
            )
            assertThat(findings).hasSize(1)
        }
    }

    @Nested
    inner class `reported false positives - #1305` {

        @Test
        fun `is used within an assignment expr itself`() {
            assertThat(
                subject.compileAndLintWithContext(
                    env,
                    """
                class C {
                    fun f() = true
                }
                
                val a = C().apply { f() }
                    """
                )
            ).isEmpty()
        }

        @Test
        fun `is used as return type of extension function`() {
            assertThat(
                subject.compileAndLintWithContext(
                    env,
                    """
                class C(var prop: Int)
                
                fun Int.f() = C(5).apply { prop = 10 }
                    """
                )
            ).isEmpty()
        }

        @Test
        fun `should not flag apply when assigning property on this`() {
            assertThat(
                subject.compileAndLintWithContext(
                    env,
                    """
                class C(var prop: Int) {
                    private val c by lazy {
                        C(1).apply { prop = 3 }
                    }
                }
                    """
                )
            ).isEmpty()
        }

        @Test
        fun `should not report apply when using it after returning something`() {
            assertThat(
                subject.compileAndLintWithContext(
                    env,
                    """
                class C(var prop: Int)
                
                fun f() = (C(5)).apply { prop = 10 }
                    """
                )
            ).isEmpty()
        }

        @Test
        fun `should not report apply usage inside safe chained expressions`() {
            assertThat(
                subject.compileAndLintWithContext(
                    env,
                    """
                fun f() {
                    val arguments = listOf(1,2,3)
                        ?.map { it * 2 }
                        ?.apply { if (true) 4 }
                        ?: listOf(0)
                }
                    """
                )
            ).isEmpty()
        }
    }

    @Nested
    inner class `false positive in single nesting expressions - #1473` {

        @Test
        fun `should not report the if expression`() {
            assertThat(
                subject.compileAndLintWithContext(
                    env,
                    """
                class C {
                    fun has() = true
                }

                fun f() {
                    C().apply {
                        if (has()) {
                            // actions
                        }
                    }
                }
                    """
                )
            ).isEmpty()
        }

        @Test
        fun `should report reference expressions`() {
            assertThat(
                subject.compileAndLintWithContext(
                    env,
                    """
                class C { 
                    val prop = 5 
                }

                fun f() {
                    C().apply {
                        prop
                    }

                    C().apply {
                        this.prop
                    }
                }
                    """
                )
            ).hasSize(2)
        }
    }

    @Nested
    inner class `false positive when it's used as an expression - #2435` {

        @Test
        fun `do not report when it's used as an assignment`() {
            assertThat(
                subject.compileAndLintWithContext(
                    env,
                    """
                class C {
                    fun f() {}
                }
                
                fun main() {
                    val c = if (5 >= 3) {
                        C().apply { f() }
                    } else {
                        C()
                    }
                }
                    """
                )
            ).isEmpty()
        }

        @Test
        fun `do not report when it's used as the last statement of a block inside lambda`() {
            assertThat(
                subject.compileAndLintWithContext(
                    env,
                    """
                class C {
                    fun f() {}
                }

                fun print(block: () -> C) {
                    println(block())
                }
                
                fun main() {
                    print {
                        println("Does nothing")
                        C().apply { f() }
                    }
                }
                    """
                )
            ).isEmpty()
        }
    }

    @Nested
    inner class `false positive when lambda has multiple member references - #3561` {

        @Test
        fun `do not report when lambda has multiple member references`() {
            assertThat(
                subject.compileAndLintWithContext(
                    env,
                    """
                    fun test(foo: Foo) {
                        foo.apply {
                            bar {
                                baz = 2
                            }
                        }
                    }
                    
                    class Foo {
                        fun bar(f: () -> Unit) {
                        }
                        var baz = 1
                    }

                    """
                )
            ).isEmpty()
        }
    }
}
