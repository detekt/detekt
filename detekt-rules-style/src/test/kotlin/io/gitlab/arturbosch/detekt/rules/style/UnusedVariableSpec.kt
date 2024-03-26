package io.gitlab.arturbosch.detekt.rules.style

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.SourceLocation
import io.gitlab.arturbosch.detekt.rules.KotlinCoreEnvironmentTest
import io.gitlab.arturbosch.detekt.test.assertThat
import io.gitlab.arturbosch.detekt.test.compileAndLintWithContext
import io.gitlab.arturbosch.detekt.test.lintWithContext
import org.jetbrains.kotlin.cli.jvm.compiler.KotlinCoreEnvironment
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

@KotlinCoreEnvironmentTest
@Suppress("unused")
class UnusedVariableSpec(val env: KotlinCoreEnvironment) {
    val subject = UnusedVariable(Config.empty)

    @Nested
    inner class `ignored list` {

        @Test
        fun `not report ignored variables in function scope`() {
            val code = """
                fun test(){
                    val _ = 1 // ignored
                    val foo = 2 // not ignored
                    val ignored = 3 // ignored   
                }
            """.trimIndent()

            assertThat(subject.lintWithContext(env, code))
                .hasSize(1)
        }

        @Test
        fun `not report ignored private variables in top level`() {
            val code = """
               private val _ = 1 // ignored
               private val foo = 2 // not ignored
               private val ignored = 3 // ignored   
            """.trimIndent()

            assertThat(subject.lintWithContext(env, code))
                .hasSize(1)
        }
    }

    @Nested
    inner class `error messages` {
        @Test
        fun `are specific for local variables`() {
            val code = """
                fun foo(){ val unused = 1 }
            """.trimIndent()

            val lint = subject.lintWithContext(env, code)

            assertThat(lint.first())
                .hasMessage("Variable `unused` is unused.")
        }
    }

    @Nested
    inner class `top level variables` {

        @Test
        fun `not report top level public variables`() {
            val code = """
                val notUsedTopLevelVal = 1
                fun using(){
                  println("foo")
                }
            """.trimIndent()

            assertThat(subject.lintWithContext(env, code))
                .isEmpty()
        }

        @Test
        fun `reports top level variables if they are unused`() {
            val code = """
                private val usedTopLevelVal = 1
                private const val unusedTopLevelConst = 1
                private val unusedTopLevelVal = usedTopLevelVal
            """.trimIndent()
            assertThat(subject.lintWithContext(env, code))
                .hasSize(2)
                .hasStartSourceLocations(
                    SourceLocation(2, 19),
                    SourceLocation(3, 13),
                )
        }

        @Test
        fun `not report when top level variables are used in function`() {
            val code = """
                private val usedTopLevelVal = 1
                fun using(){
                  println(usedTopLevelVal)
                }
            """.trimIndent()

            assertThat(subject.lintWithContext(env, code))
                .isEmpty()
        }

        @Test
        fun `report when top level variables have same name as function parameter`() {
            val code = """
                private val foo = 1
                fun using(foo:Int){
                  println(foo)
                }
            """.trimIndent()

            assertThat(subject.lintWithContext(env, code))
                .hasSize(1)
                .hasStartSourceLocations(SourceLocation(1, 13))
        }
    }

    @Nested
    inner class `variables in top level functions` {

        @Test
        fun `reports unused variables in top level functions`() {
            val code = """
                fun foo(){
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
                fun foo(a:Int){
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
                fun foo(bar:Int){ } 
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
        fun `reports unused local variables when they have same name as property`() {
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
    }

    @Nested
    inner class `loop iterators` {

        @Test
        fun `should not report used loop properties`() {
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
        fun `reports unused loop property`() {
            val code = """
                fun use(){                 
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
        fun `reports all unused loop properties in indexed array`() {
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
        fun `does not report used loop properties in indexed array`() {
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
            assertThat(subject.compileAndLintWithContext(env, code)).hasSize(1)
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
            assertThat(subject.compileAndLintWithContext(env, code)).isEmpty()
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
            assertThat(subject.compileAndLintWithContext(env, code)).isEmpty()
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
            assertThat(subject.compileAndLintWithContext(env, code)).isEmpty()
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

            val results = subject.lintWithContext(env, code)
            assertThat(results).hasSize(2)
            assertThat(results).anyMatch { it.message == "Variable `org` is unused." }
            assertThat(results).anyMatch { it.message == "Variable `detekt` is unused." }
        }
    }

    @Nested
    inner class `variable lamda` {

        @Test
        fun `reports unused variable in lambda`() {
            val code = """
                val function = Function1<Unit, Unit> {
                    val used = "used"
                    val unused = "unused"
                    println(used)
                }
            """.trimIndent()

            assertThat(subject.lintWithContext(env, code))
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

            assertThat(subject.lintWithContext(env, code))
                .hasSize(1)
                .hasStartSourceLocations(SourceLocation(3, 9))
        }
    }
}
