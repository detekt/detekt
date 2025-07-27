package io.gitlab.arturbosch.detekt.rules.style

import io.github.detekt.test.utils.KotlinEnvironmentContainer
import dev.detekt.api.Config
import dev.detekt.api.SourceLocation
import io.gitlab.arturbosch.detekt.rules.KotlinCoreEnvironmentTest
import io.gitlab.arturbosch.detekt.test.assertThat
import io.gitlab.arturbosch.detekt.test.lintWithContext
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

@KotlinCoreEnvironmentTest
@Suppress("unused")
class UnusedVariableSpec(val env: KotlinEnvironmentContainer) {
    val subject = UnusedVariable(Config.empty)

    @Nested
    inner class `ignored list` {

        @Test
        fun `not report ignored variables in function scope`() {
            val code = """
                fun test() {
                    val _ = 1 // ignored
                    val foo = 2 // not ignored
                    val ignored = 3 // ignored   
                }
            """.trimIndent()

            assertThat(subject.lintWithContext(env, code, compile = false))
                .hasSize(1)
        }
    }

    @Nested
    inner class `error messages` {
        @Test
        fun `are specific for local variables`() {
            val code = """
                fun foo() { val unused = 1 }
            """.trimIndent()

            val lint = subject.lintWithContext(env, code)

            assertThat(lint.first())
                .hasMessage("Variable `unused` is unused.")
        }
    }

    @Nested
    inner class `variables in top level functions` {

        @Test
        fun `reports unused variables in top level functions`() {
            val code = """
                fun foo() {
                    val a = 12
                    val b = 32
                    println(a)
                }
            """.trimIndent()

            assertThat(subject.lintWithContext(env, code))
                .hasSize(1)
                .hasStartSourceLocations(SourceLocation(3, 9))
        }

        @Test
        fun `reports when variable has same name as function parameter`() {
            val code = """
                fun foo(a:Int) {
                    println(a)
                    val a = 12
                    val b = 12
                }
            """.trimIndent()

            assertThat(subject.lintWithContext(env, code))
                .hasSize(2)
                .hasStartSourceLocations(
                    SourceLocation(3, 9),
                    SourceLocation(4, 9)
                )
        }

        @Test
        fun `should not report unused function parameters`() {
            val code = """
                fun foo(bar:Int) { }
            """.trimIndent()

            assertThat(subject.lintWithContext(env, code)).hasSize(0)
        }
    }

    @Nested
    inner class `in class member functions` {
        @Test
        fun `reports unused local variables`() {
            val code = """
                class Test {
                    private val used = "This is used"                
                    fun use() {
                        val unused = used
                        println(used)
                    }
                }
            """.trimIndent()
            assertThat(subject.lintWithContext(env, code))
                .hasSize(1)
                .hasStartSourceLocations(SourceLocation(4, 13))
        }

        @Test
        fun `reports unused local variables when they have same name as parameter`() {
            val code = """
                class Test {
                  private val foo = "member"
                
                  fun usingMember() {
                      val foo = "non-member" // not used
                      println(this.foo)
                  }
    
                  fun usingLocal() {
                        val foo = "non-member" // used
                        println(foo)
                  }
                }
            """.trimIndent()

            assertThat(subject.lintWithContext(env, code))
                .hasSize(1)
                .hasStartSourceLocations(SourceLocation(5, 11))
        }

        @Test
        fun `does not report when variable is used in dot call`() {
            val code = """
                fun main() {
                    val variable = "used variable"
                    val consumer = Function1<String, String> { it }
                    consumer.apply(variable)
                }
            """.trimIndent()

            assertThat(subject.lintWithContext(env, code, compile = false))
                .isEmpty()
        }
    }

    @Nested
    inner class `loop iterators` {

        @Test
        fun `should not report used loop parameter`() {
            val code = """
                fun use() {
                    for (i in 0 until 10) {
                        println(i)
                    }
                }
            """.trimIndent()
            assertThat(subject.lintWithContext(env, code)).isEmpty()
        }

        @Test
        fun `reports unused loop parameter`() {
            val code = """
                fun use() {
                  for (i in 0 until 10) { }
                }
            """.trimIndent()
            assertThat(subject.lintWithContext(env, code))
                .hasSize(1)
                .hasStartSourceLocations(SourceLocation(2, 8))
        }

        @Test
        fun `reports unused loop property in indexed array`() {
            val code = """
                fun use() {
                    val array = intArrayOf(1, 2, 3)
                    for ((index, value) in array.withIndex()) {
                        println(index)
                    }
                }
            """.trimIndent()
            assertThat(subject.lintWithContext(env, code))
                .hasSize(1)
        }

        @Test
        fun `reports all unused loop parameters in indexed array`() {
            val code = """
                fun use() {
                    val array = intArrayOf(1, 2, 3)
                    for ((index, value) in array.withIndex()) {
                    }
                }
            """.trimIndent()
            assertThat(subject.lintWithContext(env, code)).hasSize(2)
        }

        @Test
        fun `does not report used loop parameters in indexed array`() {
            val code = """
                fun use() {
                    val array = intArrayOf(1, 2, 3)
                    for ((index, value) in array.withIndex()) {
                        println(index)
                        println(value)
                    }
                }
            """.trimIndent()
            assertThat(subject.lintWithContext(env, code)).isEmpty()
        }
    }

    @Nested
    inner class `backtick identifiers - #3825` {

        @Test
        fun `does report unused variables with keyword name`() {
            val code = """
                fun main() {
                    val `in` = "foo"
                }
            """.trimIndent()
            assertThat(subject.lintWithContext(env, code)).hasSize(1)
        }

        @Test
        fun `does not report used variables with keyword name`() {
            val code = """
                fun main() {
                    val `in` = "fee"
                    val expected = "foo"
                    println(expected == `in`)
                }
            """.trimIndent()
            assertThat(subject.lintWithContext(env, code)).isEmpty()
        }

        @Test
        fun `does not report used variables when referenced with backticks`() {
            val code = """
                fun main() {
                    val actual = "fee"
                    val expected = "foo"
                    println(expected == `actual`)
                }
            """.trimIndent()
            assertThat(subject.lintWithContext(env, code)).isEmpty()
        }

        @Test
        fun `does not report used variables when declared with backticks`() {
            val code = """
                fun main() {
                    val `actual` = "fee"
                    val expected = "foo"
                    println(expected == actual)
                }
            """.trimIndent()
            assertThat(subject.lintWithContext(env, code)).isEmpty()
        }
    }

    @Nested
    inner class `irrelevant references are ignored` {
        @Test
        fun `package declarations are ignored`() {
            val code = """
                package org.detekt
                fun main() {
                    val org = 1
                    val detekt = 1
                    println("foo")
                }
            """.trimIndent()

            val results = subject.lintWithContext(env, code)
            assertThat(results).hasSize(2)
            assertThat(results).anyMatch { it.message == "Variable `org` is unused." }
            assertThat(results).anyMatch { it.message == "Variable `detekt` is unused." }
        }

        @Test
        fun `import declarations are ignored`() {
            val code = """
                import org.detekt.Foo
                fun main() {
                    val org = 1
                    val detekt = 1
                    println("foo")
                }
            """.trimIndent()

            val results = subject.lintWithContext(env, code, compile = false)
            assertThat(results).hasSize(2)
            assertThat(results).anyMatch { it.message == "Variable `org` is unused." }
            assertThat(results).anyMatch { it.message == "Variable `detekt` is unused." }
        }
    }

    @Nested
    inner class `variable in lamda` {

        @Test
        fun `reports unused variable in lambda`() {
            val code = """
                val function = Function1<Unit, Unit> {
                    val used = "used"
                    val unused = "unused"
                    println(used)
                }
            """.trimIndent()

            assertThat(subject.lintWithContext(env, code, compile = false))
                .hasSize(1)
                .hasStartSourceLocations(SourceLocation(3, 9))
        }

        @Test
        fun `reports unused variable in lambda with return`() {
            val code = """
                val function = Function1<Unit, String> {
                    val used = "used"
                    val unused = "unused"
                    used
                }
            """.trimIndent()

            assertThat(subject.lintWithContext(env, code, compile = false))
                .hasSize(1)
                .hasStartSourceLocations(SourceLocation(3, 9))
        }
    }

    @Nested
    inner class `descturing variable` {
        @Test
        fun `correctly reports the unused structured variable`() {
            val code = """
               fun main(){
                   val (used, unused) = 1 to 2   
                   println(used)                
               }
            """.trimIndent()

            assertThat(subject.lintWithContext(env, code))
                .hasSize(1)
                .hasStartSourceLocations(SourceLocation(2, 16))
        }
    }
}
