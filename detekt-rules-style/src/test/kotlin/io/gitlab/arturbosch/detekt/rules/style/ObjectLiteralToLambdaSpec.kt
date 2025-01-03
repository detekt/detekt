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

class ObjectLiteralToLambdaSpec {
    val subject = ObjectLiteralToLambda(Config.empty)

    @Nested
    @KotlinCoreEnvironmentTest
    inner class WithDefaultSources(val env: KotlinCoreEnvironment) {

        @Nested
        inner class `report convertible expression` {
            @Test
            fun `is property`() {
                val code = """
                    fun interface Sam {
                        fun foo()
                    }
                    val a = object : Sam {
                        override fun foo() {
                        }
                    }
                """.trimIndent()
                assertThat(subject.compileAndLintWithContext(env, code))
                    .hasSize(1)
                    .hasStartSourceLocations(SourceLocation(4, 9))
            }

            @Test
            fun `is in function`() {
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
                """.trimIndent()
                assertThat(subject.compileAndLintWithContext(env, code))
                    .hasSize(1)
                    .hasStartSourceLocations(SourceLocation(5, 5))
            }

            @Test
            fun `is in init`() {
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
                """.trimIndent()
                assertThat(subject.compileAndLintWithContext(env, code))
                    .hasSize(1)
                    .hasStartSourceLocations(SourceLocation(6, 9))
            }

            @Test
            fun `is generic`() {
                val code = """
                    fun interface Sam<T> {
                        fun foo(): T
                    }
                    val a = object : Sam<Int> {
                        override fun foo(): Int {
                            return 1
                        }
                    }
                """.trimIndent()
                assertThat(subject.compileAndLintWithContext(env, code))
                    .hasSize(1)
                    .hasStartSourceLocations(SourceLocation(4, 9))
            }

            @Test
            fun `has other default method`() {
                val code = """
                    fun interface Sam {
                        fun foo()
                        fun bar() {}
                    }
                    val a = object : Sam {
                        override fun foo() {
                        }
                    }
                """.trimIndent()
                assertThat(subject.compileAndLintWithContext(env, code))
                    .hasSize(1)
                    .hasStartSourceLocations(SourceLocation(5, 9))
            }

            @Test
            fun `nested declaration`() {
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
                """.trimIndent()
                assertThat(subject.compileAndLintWithContext(env, code))
                    .hasSize(1)
                    .hasStartSourceLocations(SourceLocation(7, 5))
            }

            @Test
            fun `expression body syntax`() {
                val code = """
                    fun interface Sam {
                        fun foo(): Int
                    }
                    val a = object : Sam {
                        override fun foo() = 3
                    }
                """.trimIndent()
                assertThat(subject.compileAndLintWithContext(env, code))
                    .hasSize(1)
                    .hasStartSourceLocations(SourceLocation(4, 9))
            }
        }

        @Nested
        inner class `is not correct implement` {
            @Test
            fun `is empty interface`() {
                val code = """
                    interface Sam
                    val a = object : Sam {}
                """.trimIndent()
                assertThat(subject.compileAndLintWithContext(env, code)).isEmpty()
            }

            @Test
            fun `is empty interface and has own function`() {
                val code = """
                    interface Sam
                    val a = object : Sam {
                        fun foo() {
                        }
                    }
                """.trimIndent()
                assertThat(subject.compileAndLintWithContext(env, code)).isEmpty()
            }

            @Test
            fun `is single property interface`() {
                val code = """
                    interface Sam {
                        val foo: Int
                    }
                    val a = object : Sam {
                        override val foo = 1
                    }
                """.trimIndent()
                assertThat(subject.compileAndLintWithContext(env, code)).isEmpty()
            }

            @Test
            fun `is empty interface and has own property`() {
                val code = """
                    interface Sam
                    val a = object : Sam {
                        val b = 1
                    }
                """.trimIndent()
                assertThat(subject.compileAndLintWithContext(env, code)).isEmpty()
            }

            @Test
            fun `is not fun interface`() {
                val code = """
                    interface Sam {
                        fun foo()
                    }
                    val a = object : Sam {
                        override fun foo() {
                        }
                    }
                """.trimIndent()
                assertThat(subject.compileAndLintWithContext(env, code)).isEmpty()
            }

            @Test
            fun `is not interface`() {
                val code = """
                    abstract class Something {
                        abstract fun foo()
                    }
                    val a = object : Something() {
                        override fun foo() {
                        }
                    }
                """.trimIndent()
                assertThat(subject.compileAndLintWithContext(env, code)).isEmpty()
            }

            @Test
            fun `has multi implement`() {
                val code = """
                    fun interface First {
                        fun foo()
                    }
                    interface Second
                    
                    val a: First = object : First, Second {
                        override fun foo() {
                        }
                    }
                """.trimIndent()
                assertThat(subject.compileAndLintWithContext(env, code)).isEmpty()
            }

            @Test
            fun `has complex implement`() {
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
                """.trimIndent()
                assertThat(subject.compileAndLintWithContext(env, code)).isEmpty()
            }
        }

        @Nested
        inner class `has impurities` {
            @Test
            fun `has more than one method`() {
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
                """.trimIndent()
                assertThat(subject.compileAndLintWithContext(env, code)).isEmpty()
            }

            @Test
            fun `has property`() {
                val code = """
                    fun interface Sam {
                        fun foo()
                    }
                    val a = object : Sam {
                        private var bar = 0
                        override fun foo() {
                        }
                    }
                """.trimIndent()
                assertThat(subject.compileAndLintWithContext(env, code)).isEmpty()
            }

            @Test
            fun `has init`() {
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
                """.trimIndent()
                assertThat(subject.compileAndLintWithContext(env, code)).isEmpty()
            }
        }

        @Nested
        inner class `java interface` {
            @Test
            fun `is convertible`() {
                val code = """
                    val a = object : Runnable {
                        override fun run(){
                        }
                    }
                """.trimIndent()
                assertThat(subject.compileAndLintWithContext(env, code))
                    .hasSize(1)
                    .hasStartSourceLocations(SourceLocation(1, 9))
            }

            @Test
            fun `is convertible Callable generic`() {
                val code = """
                    import java.util.concurrent.Callable
                    val a = object : Callable<Int> {
                        override fun call(): Int {
                            return 1
                        }
                    }
                """.trimIndent()
                assertThat(subject.compileAndLintWithContext(env, code))
                    .hasSize(1)
                    .hasStartSourceLocations(SourceLocation(2, 9))
            }

            @Test
            fun `empty interface`() {
                val code = """
                    import java.util.EventListener
                    val a = object : EventListener {
                        fun foo() {
                        }
                    }
                """.trimIndent()
                assertThat(subject.compileAndLintWithContext(env, code)).isEmpty()
            }

            @Test
            fun `is convertible Enumeration generic`() {
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
                """.trimIndent()
                assertThat(subject.compileAndLintWithContext(env, code)).isEmpty()
            }
        }

        @Nested
        inner class `object use itself` {
            @Test
            fun `call 'this'`() {
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
                """.trimIndent()
                assertThat(subject.compileAndLintWithContext(env, code)).isEmpty()
            }

            @Test
            fun `use 'this'`() {
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
                """.trimIndent()
                assertThat(subject.compileAndLintWithContext(env, code)).isEmpty()
            }

            @Test
            fun `use class method`() {
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
                """.trimIndent()
                assertThat(subject.compileAndLintWithContext(env, code)).isEmpty()
            }

            @Test
            fun `call 'this' inside nested object`() {
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
                """.trimIndent()
                assertThat(subject.compileAndLintWithContext(env, code))
                    .hasSize(1)
                    .hasStartSourceLocations(SourceLocation(6, 5))
            }

            @Test
            fun `call labeled 'this'`() {
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
                """.trimIndent()
                assertThat(subject.compileAndLintWithContext(env, code))
                    .hasSize(1)
                    .hasStartSourceLocations(SourceLocation(7, 9))
            }

            @Test
            fun `recursive call`() {
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
                """.trimIndent()
                assertThat(subject.compileAndLintWithContext(env, code)).isEmpty()
            }
        }

        @Nested
        inner class `Edge case` {
            // https://github.com/detekt/detekt/pull/3599#issuecomment-806389701
            @Test
            fun `Anonymous objects are always newly created, but lambdas are singletons, so they have the same reference`() {
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
                """.trimIndent()
                assertThat(subject.compileAndLintWithContext(env, code))
                    .hasSize(1)
                    .hasStartSourceLocations(SourceLocation(5, 19))
            }
        }
    }

    @Nested
    @KotlinCoreEnvironmentTest(additionalJavaSourcePaths = ["java"])
    inner class WithAdditionalJavaSources(val env: KotlinCoreEnvironment) {

        @Test
        fun `has other default methods`() {
            val code = """
                import com.example.fromjava.SamWithDefaultMethods
                
                fun main() {
                    val x = object : SamWithDefaultMethods {
                        override fun foo() {
                            println()
                        }
                    }
                }
            """.trimIndent()

            assertThat(subject.lintWithContext(env, code)).hasSize(1)
        }

        @Test
        fun `has only default methods`() {
            val code = """
                import com.example.fromjava.OnlyDefaultMethods
                
                fun main() {
                    val x = object : OnlyDefaultMethods {
                    }
                }
            """.trimIndent()
            assertThat(subject.lintWithContext(env, code)).isEmpty()
        }

        @Test
        fun `implements a default method`() {
            val code = """
                import com.example.fromjava.OnlyDefaultMethods
                
                fun main() {
                    val x = object : OnlyDefaultMethods {
                        override fun foo() {
                            println()
                        }
                    }
                }
            """.trimIndent()
            assertThat(subject.lintWithContext(env, code)).isEmpty()
        }
    }
}
