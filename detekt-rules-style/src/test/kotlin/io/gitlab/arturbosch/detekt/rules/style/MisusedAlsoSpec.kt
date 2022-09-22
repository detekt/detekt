package io.gitlab.arturbosch.detekt.rules.style

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.test.assertThat
import io.gitlab.arturbosch.detekt.test.compileAndLint
import org.junit.jupiter.api.Test

class MisusedAlsoSpec {
    val subject = MisusedAlso(Config.empty)

    @Test
    fun `does not report when no also is used`() {
        val code = """
            class Buzz {
                fun init() {}
                fun block() {}
            }
            
            fun foo(block: Buzz.() -> Unit): Buzz =
                Buzz().let {
                  init()
                  block()
                }
        """.trimIndent()
        assertThat(subject.compileAndLint(code)).isEmpty()
    }

    @Test
    fun `reports an also in init of class`() {
        val code = """
            class Test {
                private var a = 5
                
                init {
                    a.also { 
                        it.plus(5)
                    }
                }
            }
        """.trimIndent()
        assertThat(subject.compileAndLint(code)).hasSize(1)
    }

    @Test
    fun `reports an also on nullable type`() {
        val code = """
            class Test {
                private var a: Int? = 5
                
                init {
                    a?.also { 
                        it.plus(5)
                    }
                }
            }
        """.trimIndent()
        assertThat(subject.compileAndLint(code)).hasSize(1)
    }

    @Test
    fun `reports an also with lambda passed as Argument in parenthesis`() {
        val code = """
            class Test {
                private var a: Int? = 5
                
                init {
                    a?.also({ 
                        it.plus(5)
                    })
                }
            }
        """.trimIndent()
        assertThat(subject.compileAndLint(code)).hasSize(1)
    }

    @Test
    fun `does not report if it is not used in also`() {
        val code = """
            class Test {
                private var a: Int? = 5
                private var b: Int = 0
                
                init {
                    a?.also({ 
                        b.plus(5)
                    })
                }
            }
        """.trimIndent()
        assertThat(subject.compileAndLint(code)).isEmpty()
    }
}
