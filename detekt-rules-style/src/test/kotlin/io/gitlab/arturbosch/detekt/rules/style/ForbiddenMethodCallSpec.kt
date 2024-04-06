package io.gitlab.arturbosch.detekt.rules.style

import io.gitlab.arturbosch.detekt.api.SourceLocation
import io.gitlab.arturbosch.detekt.rules.KotlinCoreEnvironmentTest
import io.gitlab.arturbosch.detekt.test.TestConfig
import io.gitlab.arturbosch.detekt.test.assertThat
import io.gitlab.arturbosch.detekt.test.compileAndLintWithContext
import org.jetbrains.kotlin.cli.jvm.compiler.KotlinCoreEnvironment
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

private const val METHODS = "methods"

@KotlinCoreEnvironmentTest
class ForbiddenMethodCallSpec(val env: KotlinCoreEnvironment) {

    @Test
    fun `should report kotlin print usages by default`() {
        val code = """
            fun main() {
                print("3")
                println("4")
            }
        """.trimIndent()
        val findings = ForbiddenMethodCall(TestConfig()).compileAndLintWithContext(env, code)

        assertThat(findings)
            .hasSize(2)
            .hasStartSourceLocations(
                SourceLocation(2, 5),
                SourceLocation(3, 5),
            )
            .extracting("message")
            .containsExactly(
                "The method `kotlin.io.print` has been forbidden: print does not allow you to configure the output stream. Use a logger instead.",
                "The method `kotlin.io.println` has been forbidden: println does not allow you to configure the output stream. Use a logger instead.",
            )
    }

    @Test
    fun `should report nothing when methods are blank`() {
        val code = """
            import java.lang.System
            fun main() {
                System.out.println("hello")
            }
        """.trimIndent()
        val findings = ForbiddenMethodCall(
            TestConfig(METHODS to listOf("  "))
        ).compileAndLintWithContext(env, code)
        assertThat(findings).isEmpty()
    }

    @Test
    fun `should report nothing when methods do not match`() {
        val code = """
            import java.lang.System
            fun main() {
                System.out.println("hello")
            }
        """.trimIndent()
        val findings = ForbiddenMethodCall(
            TestConfig(METHODS to listOf("java.lang.System.gc"))
        ).compileAndLintWithContext(env, code)
        assertThat(findings).isEmpty()
    }

    @Test
    fun `should report method call when using the fully qualified name`() {
        val code = """
            fun main() {
                java.lang.System.out.println("hello")
            }
        """.trimIndent()
        val findings = ForbiddenMethodCall(
            TestConfig(METHODS to listOf("java.io.PrintStream.println"))
        ).compileAndLintWithContext(env, code)
        assertThat(findings).hasSize(1)
        assertThat(findings).hasTextLocations(38 to 54)
    }

    @Test
    fun `should report method call when not using the fully qualified name`() {
        val code = """
            import java.lang.System.out
            fun main() {
                out.println("hello")
            }
        """.trimIndent()
        val findings = ForbiddenMethodCall(
            TestConfig(METHODS to listOf("java.io.PrintStream.println"))
        ).compileAndLintWithContext(env, code)
        assertThat(findings).hasSize(1)
        assertThat(findings).hasTextLocations(49 to 65)
    }

    @Test
    fun `should report multiple different methods`() {
        val code = """
            import java.lang.System
            fun main() {
            System.out.println("hello")
                System.gc()
            }
        """.trimIndent()
        val findings = ForbiddenMethodCall(
            TestConfig(
                METHODS to listOf(
                    "java.io.PrintStream.println",
                    "java.lang.System.gc",
                )
            )
        ).compileAndLintWithContext(env, code)
        assertThat(findings).hasSize(2)
        assertThat(findings).hasTextLocations(48 to 64, 76 to 80)
    }

    @Test
    fun `should report equals operator`() {
        val code = """
            fun main() {
                java.math.BigDecimal(5.5) == java.math.BigDecimal(5.5)
            }
        """.trimIndent()
        val findings = ForbiddenMethodCall(
            TestConfig(METHODS to listOf("java.math.BigDecimal.equals"))
        ).compileAndLintWithContext(env, code)
        assertThat(findings).hasSize(1)
    }

    @Test
    fun `should report prefix operator`() {
        val code = """
            fun test() {
                var i = 1
                ++i
            }
        """.trimIndent()
        val findings = ForbiddenMethodCall(
            TestConfig(METHODS to listOf("kotlin.Int.inc"))
        ).compileAndLintWithContext(env, code)
        assertThat(findings).hasSize(1)
    }

    @Test
    fun `should report postfix operator`() {
        val code = """
            fun test() {
                var i = 1
                i--
            }
        """.trimIndent()
        val findings = ForbiddenMethodCall(
            TestConfig(METHODS to listOf("kotlin.Int.dec"))
        ).compileAndLintWithContext(env, code)
        assertThat(findings).hasSize(1)
    }

    @Test
    fun `should report both methods when using just method name without full signature`() {
        val code = """
            import java.time.Clock
            import java.time.LocalDate
            fun test() {
                val clock = Clock.systemUTC()
                val date = LocalDate.now()
                val date2 = LocalDate.now(clock)
            }
        """.trimIndent()
        val findings = ForbiddenMethodCall(
            TestConfig(METHODS to listOf("java.time.LocalDate.now"))
        ).compileAndLintWithContext(env, code)
        assertThat(findings).hasSize(2)
    }

    @Test
    fun `should report parameterless method when full signature matches`() {
        val code = """
            import java.time.Clock
            import java.time.LocalDate
            fun test() {
                val clock = Clock.systemUTC()
                val date = LocalDate.now()
                val date2 = LocalDate.now(clock)
            }
        """.trimIndent()
        val findings = ForbiddenMethodCall(
            TestConfig(METHODS to listOf("java.time.LocalDate.now()"))
        ).compileAndLintWithContext(env, code)
        assertThat(findings).hasStartSourceLocation(5, 26)
        assertThat(findings).singleElement()
            .hasMessage("The method `java.time.LocalDate.now()` has been forbidden in the detekt config.")
    }

    @Test
    fun `should report method with param when full signature matches`() {
        val code = """
            import java.time.Clock
            import java.time.LocalDate
            fun test() {
                val clock = Clock.systemUTC()
                val date = LocalDate.now()
                val date2 = LocalDate.now(clock)
            }
        """.trimIndent()
        val findings = ForbiddenMethodCall(
            TestConfig(METHODS to listOf("java.time.LocalDate.now(java.time.Clock)"))
        ).compileAndLintWithContext(env, code)
        assertThat(findings).hasSize(1)
        assertThat(findings).hasStartSourceLocation(6, 27)
    }

    @Test
    fun `should report method with multiple params when full signature matches`() {
        val code = """
            import java.time.LocalDate
            fun test() {
                val date = LocalDate.of(2020, 1, 1)
            }
        """.trimIndent()
        val findings = ForbiddenMethodCall(
            TestConfig(METHODS to listOf("java.time.LocalDate.of(kotlin.Int, kotlin.Int, kotlin.Int)"))
        ).compileAndLintWithContext(env, code)
        assertThat(findings).hasSize(1)
        assertThat(findings).hasStartSourceLocation(3, 26)
    }

    @Test
    fun `should report method with multiple params when full signature matches with additional spacing`() {
        val code = """
            import java.time.LocalDate
            fun test() {
                val date = LocalDate.of(2020, 1, 1)
            }
        """.trimIndent()
        val findings = ForbiddenMethodCall(
            TestConfig(METHODS to listOf("java.time.LocalDate.of(kotlin.Int,kotlin.Int,kotlin.Int)"))
        ).compileAndLintWithContext(env, code)
        assertThat(findings).hasSize(1)
        assertThat(findings).hasStartSourceLocation(3, 26)
    }

    @Test
    fun `should report method with multiple params when method has spaces and commas`() {
        val code = """
            package io.gitlab.arturbosch.detekt.rules.style
            
            fun `some, test`() = "String"
            
            fun test() {
                val s = `some, test`()
            }
        """.trimIndent()
        val findings = ForbiddenMethodCall(
            TestConfig(METHODS to listOf("io.gitlab.arturbosch.detekt.rules.style.`some, test`()"))
        ).compileAndLintWithContext(env, code)
        assertThat(findings).hasSize(1)
        assertThat(findings).hasStartSourceLocation(6, 13)
    }

    @Test
    fun `should report method with default params`() {
        val code = """
            package io.gitlab.arturbosch.detekt.rules.style
            
            fun defaultParamsMethod(s:String, i:Int = 0) = s + i
            
            fun test() {
                val s = defaultParamsMethod("test")
            }
        """.trimIndent()
        val findings = ForbiddenMethodCall(
            TestConfig(
                METHODS to
                    listOf("io.gitlab.arturbosch.detekt.rules.style.defaultParamsMethod(kotlin.String,kotlin.Int)")
            )
        ).compileAndLintWithContext(env, code)
        assertThat(findings).hasSize(1)
        assertThat(findings).hasStartSourceLocation(6, 13)
    }

    @Test
    fun `should report method with array argument`() {
        val code = """
            package io.gitlab.arturbosch.detekt.rules.style
            
            fun arrayMethod(args: Array<Any>) = args.size
            
            fun test() {
                val s = arrayMethod(arrayOf("test"))
            }
        """.trimIndent()
        val methodName = "io.gitlab.arturbosch.detekt.rules.style.arrayMethod(kotlin.Array)"
        val findings = ForbiddenMethodCall(TestConfig(METHODS to listOf(methodName)))
            .compileAndLintWithContext(env, code)
        assertThat(findings).hasSize(1).hasStartSourceLocation(6, 13)
    }

    @Test
    fun `should report method with list argument`() {
        val code = """
            package io.gitlab.arturbosch.detekt.rules.style
            
            fun listMethod(args: List<Any>) = args.size
            
            fun test() {
                val s = listMethod(listOf("test"))
            }
        """.trimIndent()
        val methodName = "io.gitlab.arturbosch.detekt.rules.style.listMethod(kotlin.collections.List)"
        val findings = ForbiddenMethodCall(TestConfig(METHODS to listOf(methodName)))
            .compileAndLintWithContext(env, code)
        assertThat(findings).hasSize(1).hasStartSourceLocation(6, 13)
    }

    @Test
    fun `should report method with vararg argument`() {
        val code = """
            package io.gitlab.arturbosch.detekt.rules.style
            
            fun varargMethod(vararg args: Any) = args.size
            
            fun test() {
                val s = varargMethod(arrayOf("test"))
            }
        """.trimIndent()
        val methodName = "io.gitlab.arturbosch.detekt.rules.style.varargMethod(kotlin.Array)"
        val findings = ForbiddenMethodCall(TestConfig(METHODS to listOf(methodName)))
            .compileAndLintWithContext(env, code)
        assertThat(findings).hasSize(1).hasStartSourceLocation(6, 13)
    }

    @Test
    fun `should report companion object method`() {
        val code = """
            package io.gitlab.arturbosch.detekt.rules.style
            
            class TestClass {
                companion object {
                    fun staticMethod() {}
                }
            }
            
            fun test() {
                TestClass.staticMethod()
            }
        """.trimIndent()
        val methodName = "io.gitlab.arturbosch.detekt.rules.style.TestClass.Companion.staticMethod()"
        val findings = ForbiddenMethodCall(TestConfig(METHODS to listOf(methodName)))
            .compileAndLintWithContext(env, code)
        assertThat(findings).hasSize(1).hasStartSourceLocation(10, 15)
    }

    @Test
    fun `should report @JvmStatic method`() {
        val code = """
            package io.gitlab.arturbosch.detekt.rules.style
            
            class TestClass {
                companion object {
                    @JvmStatic
                    fun staticMethod() {}
                }
            }
            
            fun test() {
                TestClass.staticMethod()
            }
        """.trimIndent()
        val methodName = "io.gitlab.arturbosch.detekt.rules.style.TestClass.Companion.staticMethod()"
        val findings = ForbiddenMethodCall(TestConfig(METHODS to listOf(methodName)))
            .compileAndLintWithContext(env, code)
        assertThat(findings).hasSize(1).hasStartSourceLocation(11, 15)
    }

    @Test
    fun `should report overriding method calls`() {
        val code = """
            package org.example.com
            
            interface I {
                fun f()
            }
            
            class C : I {
                override fun f() {}
            }
            
            fun foo(i: I, c: C) {
                i.f()
                c.f()
            }
        """.trimIndent()
        val findings = ForbiddenMethodCall(
            TestConfig(METHODS to listOf("org.example.com.I.f"))
        ).compileAndLintWithContext(env, code)
        assertThat(findings).hasSize(2)
    }

    @Test
    fun `should report interface default method`() {
        val code = """
            package org.example.com
            
            fun foo() {
                String.CASE_INSENSITIVE_ORDER.reversed()
            }
        """.trimIndent()
        val findings = ForbiddenMethodCall(
            TestConfig(METHODS to listOf("java.util.Comparator.reversed"))
        ).compileAndLintWithContext(env, code)
        assertThat(findings).hasSize(1)
    }

    @Test
    fun `should report interface default method in a derived class`() {
        val code = """
            package org.example.com

            class SimpleComparator : Comparator<String> {
                override fun compare(p0: String?, p1: String?): Int {
                    return 0
                }
            }
            
            fun foo(i: SimpleComparator) {
                i.reversed()
            }
        """.trimIndent()
        val findings = ForbiddenMethodCall(
            TestConfig(METHODS to listOf("java.util.Comparator.reversed"))
        ).compileAndLintWithContext(env, code)
        assertThat(findings).hasSize(1)
    }

    @Test
    fun `should report class grandparent interface default method is not allowed`() {
        val code = """
            package org.example.com
            open class SimpleComparator : Comparator<String> {
                override fun compare(p0: String?, p1: String?): Int {
                    return 0
                }
            }

            class ComplexComparator : SimpleComparator() {
                override fun compare(p0: String?, p1: String?): Int {
                    return 0
                }
            }
            
            fun foo(bar1: ComplexComparator) {
                bar1.reversed()
                val bar2 = ComplexComparator()
                bar2.reversed()
            }
        """.trimIndent()
        val findings = ForbiddenMethodCall(
            TestConfig(METHODS to listOf("java.util.Comparator.reversed"))
        ).compileAndLintWithContext(env, code)
        assertThat(findings).hasSize(2)
    }

    @Test
    fun `should report class when grandparent default interface is not allowed extending other class`() {
        val code = """
            package org.example.com
            open class Parent
            open class SimpleComparator : Parent(), Comparator<String> {
                override fun compare(p0: String?, p1: String?): Int {
                    return 0
                }
            }

            class ComplexComparator : SimpleComparator() {
                override fun compare(p0: String?, p1: String?): Int {
                    return 0
                }
            }
            
            fun foo() {
                val bar = ComplexComparator()
                bar.reversed()
            }
        """.trimIndent()
        val findings = ForbiddenMethodCall(
            TestConfig(METHODS to listOf("java.util.Comparator.reversed"))
        ).compileAndLintWithContext(env, code)
        assertThat(findings).hasSize(1)
    }

    @Test
    fun `should report functions with lambda params`() {
        val code = """
            package org.example
            
            fun bar(b: (String) -> String) = Unit
            
            fun foo() {
                bar { "" }
            }
        """.trimIndent()
        val findings = ForbiddenMethodCall(
            TestConfig(METHODS to listOf("org.example.bar((kotlin.String) -> kotlin.String)"))
        ).compileAndLintWithContext(env, code)
        assertThat(findings).hasSize(1)
    }

    @Test
    fun `should report extension functions`() {
        val code = """
            package org.example
            
            fun String.bar() = Unit
            
            fun foo() {
                "".bar()
            }
        """.trimIndent()
        val findings = ForbiddenMethodCall(
            TestConfig(METHODS to listOf("org.example.bar(kotlin.String)"))
        ).compileAndLintWithContext(env, code)
        assertThat(findings).hasSize(1)
    }

    @Nested
    inner class `work with generics` {
        val code = """
            package org.example
            
            fun <T, U> bar(a: T, b: U, c: String) = Unit
            
            fun foo() {
                bar(1, "", "")
            }
        """.trimIndent()

        @Test
        fun `raise the issue`() {
            val findings = ForbiddenMethodCall(
                TestConfig(METHODS to listOf("org.example.bar(T, U, kotlin.String)"))
            ).compileAndLintWithContext(env, code)
            assertThat(findings).hasSize(1)
        }

        @Test
        fun `It doesn't raise any issue because the generics don't match`() {
            val findings = ForbiddenMethodCall(
                TestConfig(METHODS to listOf("org.example.bar(U, T, kotlin.String)"))
            ).compileAndLintWithContext(env, code)
            assertThat(findings).isEmpty()
        }
    }

    @Nested
    inner class `work with generic extensions` {
        val code = """
            package org.example
            
            fun <R> R.bar(a: String) = Unit
            
            fun foo() {
                1.bar("")
            }
        """.trimIndent()

        @Test
        fun `raise the issue`() {
            val findings = ForbiddenMethodCall(
                TestConfig(METHODS to listOf("org.example.bar(R, kotlin.String)"))
            ).compileAndLintWithContext(env, code)
            assertThat(findings).hasSize(1)
        }

        @Test
        fun `It doesn't raise any issue because the type doesn't match`() {
            val findings = ForbiddenMethodCall(
                TestConfig(METHODS to listOf("org.example.bar(kotlin.Int, kotlin.String)"))
            ).compileAndLintWithContext(env, code)
            assertThat(findings).isEmpty()
        }
    }

    @Nested
    inner class `Should distinguish between runCatching - #4448` {
        val code = """
            package org.example
            
            class A {
                fun foo() {
                    kotlin.runCatching {}
                    runCatching {}
                }
            }
        """.trimIndent()

        @Test
        fun `forbid the one without receiver`() {
            val findings = ForbiddenMethodCall(
                TestConfig(METHODS to listOf("kotlin.runCatching(() -> R)"))
            ).compileAndLintWithContext(env, code)
            assertThat(findings)
                .hasSize(1)
                .hasStartSourceLocation(5, 16)
        }

        @Test
        fun `forbid the one with receiver`() {
            val findings = ForbiddenMethodCall(
                TestConfig(METHODS to listOf("kotlin.runCatching(T, (T) -> R)"))
            ).compileAndLintWithContext(env, code)
            assertThat(findings)
                .hasSize(1)
                .hasStartSourceLocation(6, 9)
        }
    }

    @Nested
    inner class `Java getter calls` {

        @Test
        fun `should report Java getter call`() {
            val code = """
                import java.util.Calendar
                
                fun main() {
                    val calendar = Calendar.getInstance()
                    val day = calendar.getFirstDayOfWeek()
                }
            """.trimIndent()
            val findings = ForbiddenMethodCall(
                TestConfig(METHODS to listOf("java.util.Calendar.getFirstDayOfWeek"))
            ).compileAndLintWithContext(env, code)
            assertThat(findings).hasSize(1)
        }

        @Test
        fun `should report property getters call`() {
            val code = """
                import java.util.Calendar
                
                fun main() {
                    val calendar = Calendar.getInstance()
                    val day = calendar.firstDayOfWeek
                }
            """.trimIndent()
            val findings = ForbiddenMethodCall(
                TestConfig(METHODS to listOf("java.util.Calendar.getFirstDayOfWeek"))
            ).compileAndLintWithContext(env, code)
            assertThat(findings).hasSize(1)
        }
    }

    @Test
    fun `should report property setters call`() {
        val code = """
            import java.util.Calendar
            
            fun main() {
                val calendar = Calendar.getInstance()
                calendar.firstDayOfWeek = 1
            }
        """.trimIndent()
        val findings = ForbiddenMethodCall(
            TestConfig(METHODS to listOf("java.util.Calendar.setFirstDayOfWeek"))
        ).compileAndLintWithContext(env, code)
        assertThat(findings).hasSize(1)
    }

    @Test
    fun `should report reference call`() {
        val code = """
            import java.util.Calendar
            
            fun main() {
                val calendar = Calendar.getInstance()
                calendar.let(calendar::compareTo)
            }
        """.trimIndent()
        val findings = ForbiddenMethodCall(
            TestConfig(METHODS to listOf("java.util.Calendar.compareTo"))
        ).compileAndLintWithContext(env, code)
        assertThat(findings).hasSize(1)
    }

    @Nested
    inner class `Forbid constructors` {
        @Nested
        inner class NameOnly {
            @Test
            fun `empty constructor`() {
                val code = """
                    import java.util.Date
                    
                    val a = Date()
                """.trimIndent()
                val findings = ForbiddenMethodCall(
                    TestConfig(METHODS to listOf("java.util.Date.<init>"))
                ).compileAndLintWithContext(env, code)
                assertThat(findings).hasSize(1)
            }

            @Test
            fun `no-empty constructor`() {
                val code = """
                    import java.util.Date
                    
                    val a = Date(2022, 8 ,7)
                """.trimIndent()
                val findings = ForbiddenMethodCall(
                    TestConfig(METHODS to listOf("java.util.Date.<init>"))
                ).compileAndLintWithContext(env, code)
                assertThat(findings).hasSize(1)
            }
        }

        @Nested
        inner class WithParameters {
            @Test
            fun `empty constructor`() {
                val code = """
                    import java.util.Date
                    
                    val a = Date()
                """.trimIndent()
                val findings = ForbiddenMethodCall(
                    TestConfig(METHODS to listOf("java.util.Date.<init>()"))
                ).compileAndLintWithContext(env, code)
                assertThat(findings).hasSize(1)
            }

            @Test
            fun `no-empty constructor`() {
                val code = """
                    import java.util.Date
                    
                    val a = Date(2022, 8 ,7)
                """.trimIndent()
                val findings = ForbiddenMethodCall(
                    TestConfig(METHODS to listOf("java.util.Date.<init>(kotlin.Int, kotlin.Int, kotlin.Int)"))
                ).compileAndLintWithContext(env, code)
                assertThat(findings).hasSize(1)
            }

            @Test
            fun `should report BigDecimal double constructor usages by default`() {
                val code = """
                    import java.math.BigDecimal

                    val x = BigDecimal(3.14)
                """.trimIndent()
                val findings = ForbiddenMethodCall(TestConfig()).compileAndLintWithContext(env, code)
                assertThat(findings).hasSize(1)
            }

            @Test
            fun `should report nothing when using BigDecimal string constructor`() {
                val code = """
                    import java.math.BigDecimal

                    val x = BigDecimal("3.14")
                """.trimIndent()
                val findings = ForbiddenMethodCall(TestConfig()).compileAndLintWithContext(env, code)
                assertThat(findings).isEmpty()
            }

            @Test
            fun `should report nothing when using BigDecimal int constructor`() {
                val code = """
                    import java.math.BigDecimal

                    val x = BigDecimal(3)
                """.trimIndent()
                val findings = ForbiddenMethodCall(TestConfig()).compileAndLintWithContext(env, code)
                assertThat(findings).isEmpty()
            }
        }
    }
}
