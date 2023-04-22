package io.gitlab.arturbosch.detekt.rules.bugs

import io.gitlab.arturbosch.detekt.rules.KotlinCoreEnvironmentTest
import io.gitlab.arturbosch.detekt.test.assertThat
import io.gitlab.arturbosch.detekt.test.compileAndLintWithContext
import org.jetbrains.kotlin.cli.jvm.compiler.KotlinCoreEnvironment
import org.junit.jupiter.api.Test

@KotlinCoreEnvironmentTest
class MissingUseCallSpec(private val env: KotlinCoreEnvironment) {
    private val subject = MissingUseCall()

    @Test
    fun `does not report when use is used`() {
        val code = """
            import java.io.Closeable

            class MyCloseable : Closeable {
                override fun close() {
                    // closing the closeable
                }
            }

            fun test() {
                MyCloseable().use { /*no-op*/ }
            }
        """.trimIndent()
        val findings = subject.compileAndLintWithContext(env, code)

        assertThat(findings).isEmpty()
    }

    @Test
    fun `does report when use is not used`() {
        val code = """
            import java.io.Closeable

            class MyCloseable : Closeable {
                override fun close() {
                    // closing the closeable
                }
            }

            fun test() {
                val myCloseable = MyCloseable()
            }
        """.trimIndent()
        val findings = subject.compileAndLintWithContext(env, code)

        assertThat(findings).hasSize(1)
    }

    @Test
    fun `does report when _AutoCloseable_ is used without _use_`() {
        val code = """
            import java.lang.AutoCloseable

            class MyCloseable : AutoCloseable {
                override fun close() {
                    // closing the closeable
                }
            }

            fun test() {
                val myCloseable = MyCloseable()
            }
        """.trimIndent()
        val findings = subject.compileAndLintWithContext(env, code)

        assertThat(findings).hasSize(1)
    }

    @Test
    fun `does not report when _AutoCloseable_ is used with _use_`() {
        val code = """
            import java.lang.AutoCloseable

            class MyCloseable : AutoCloseable {
                override fun close() {
                    // closing the closeable
                }
            }

            fun test() {
                val myCloseable = MyCloseable().use { /*no-op*/ }
            }
        """.trimIndent()
        val findings = subject.compileAndLintWithContext(env, code)

        assertThat(findings).isEmpty()
    }

    @Test
    fun `does report when _use_ is not immediately used with _Closeable_ returned from if else expression`() {
        val code = """
            import java.io.Closeable

            class MyCloseable(private val i: Int) : Closeable {
                override fun close() {
                    println("Closing ${'$'}i")
                }
            }

            fun test() {
                (if (System.currentTimeMillis() % 2 == 0L) MyCloseable(0) else MyCloseable(1)).use { 
                    /*no-op*/
                }
            }
        """.trimIndent()
        val findings = subject.compileAndLintWithContext(env, code)

        assertThat(findings).hasSize(2)
    }

    @Test
    fun `does report when _use_ is not used immediately with _Closeable_ returned from multiline if else`() {
        val code = """
            import java.io.Closeable

            class MyCloseable(private val i: Int) : Closeable {
                override fun close() {
                    println("Closing ${'$'}i")
                }
            }

            fun test() {
                if (System.currentTimeMillis() % 2 == 0L) {
                    MyCloseable(0) 
                } else {
                    MyCloseable(1)
                }.use { 
                    /*no-op*/
                }
            }
        """.trimIndent()
        val findings = subject.compileAndLintWithContext(env, code)

        assertThat(findings).hasSize(2)
    }

    @Test
    fun `does not report when _use_ used with _Closeable_ returned from if else expression in single place`() {
        val code = """
            import java.io.Closeable

            class MyCloseable(private val i: Int) : Closeable {
                override fun close() {
                    println("Closing ${'$'}i")
                }
            }

            fun test() {
                MyCloseable(
                    if (System.currentTimeMillis() % 2 == 0L) 0 else 1
                ).use { 
                    /*no-op*/
                }
            }
        """.trimIndent()
        val findings = subject.compileAndLintWithContext(env, code)

        assertThat(findings).isEmpty()
    }

    @Test
    fun `does not report when _use_ is not used in _Closeable_ as param modifying function`() {
        val code = """
            import java.io.Closeable

            class MyCloseable(private val i: Int) : Closeable {
                override fun close() {
                    println("Closing ${'$'}i")
                }

                fun makeSound() {
                    println("🐶 woof")
                }
            }

            fun actOnClosable(bar: MyCloseable): MyCloseable { 
                bar.makeSound()
                return bar
            }
        """.trimIndent()
        val findings = subject.compileAndLintWithContext(env, code)

        assertThat(findings).isEmpty()
    }

    @Test
    fun `does not report when _use_ is not used in _Closeable_ as receiver modifying function`() {
        val code = """
            import java.io.Closeable

            class MyCloseable(private val i: Int) : Closeable {
                override fun close() {
                    println("Closing ${'$'}i")
                }

                fun makeSound() {
                    println("🐱 woof")
                }
            }

            fun MyCloseable.actOnClosable(): MyCloseable { 
                this.makeSound()
                return this
            }
        """.trimIndent()
        val findings = subject.compileAndLintWithContext(env, code)

        assertThat(findings).isEmpty()
    }

    @Test
    fun `does report nested violations`() {
        val code = """
            import java.io.Closeable
            import java.io.BufferedReader
            import java.io.FileReader

            val lines = BufferedReader(
                FileReader("some_file.txt")
            )
        """.trimIndent()
        val findings = subject.compileAndLintWithContext(env, code)

        assertThat(findings).hasSize(2)
    }

    @Test
    fun `does report outer violations`() {
        val code = """
            import java.io.Closeable
            import java.io.BufferedReader
            import java.io.FileReader

            fun getSomeValueButGenerateException() = 0

            class MyCloseable(private val fileReader: FileReader, private val i: Int) : Closeable {
                override fun close() {
                    println("Closing ${'$'}i")
                }
            }


            val whatever = FileReader("some_file.txt").use { fileReader ->
                MyCloseable(
                    fileReader,
                    getSomeValueButGenerateException()
                )
            }
        """.trimIndent()
        val findings = subject.compileAndLintWithContext(env, code)

        assertThat(findings).hasSize(2)
    }

    @Test
    fun `does report inner violations`() {
        val code = """
            import java.io.Closeable
            import java.io.BufferedReader
            import java.io.FileReader

            fun getSomeValueButGenerateException() = 0

            class MyCloseable(private val fileReader: FileReader, private val i: Int) : Closeable {
                override fun close() {
                    println("Closing ${'$'}i")
                }
            }

            val whatever = MyCloseable(
                FileReader("some_file.txt"),
                getSomeValueButGenerateException()
            ).use { /*no-op*/ }
        """.trimIndent()
        val findings = subject.compileAndLintWithContext(env, code)

        assertThat(findings).hasSize(1)
    }

    @Test
    fun `does not report nested call when _use_ is used`() {
        val code = """
            import java.io.Closeable
            import java.io.BufferedReader
            import java.io.FileReader

            fun getSomeValueButGenerateException() = 0
            fun whatever(closeable: MyCloseable) = 0

            class MyCloseable(private val fileReader: FileReader, private val i: Int) : Closeable {
                override fun close() {
                    println("Closing ${'$'}i")
                }
            }

            val whatever = FileReader("somefile.txt").use { fileReader ->
                MyCloseable(
                    fileReader,
                    getSomeValueButGenerateException()
                ).use { myClosable -> whatever(myClosable) }
            }
        """.trimIndent()
        val findings = subject.compileAndLintWithContext(env, code)

        assertThat(findings).isEmpty()
    }

    @Test
    fun `does not report with custom _Closeable_ class is used`() {
        val code = """
            class Closeable {
                fun close() {
                    // closing the closeable
                }
            }

            fun test() {
                val myCloseable = Closeable()
            }
        """.trimIndent()
        val findings = subject.compileAndLintWithContext(env, code)

        assertThat(findings).isEmpty()
    }

    @Test
    fun `does not report with custom _AutoCloseable_ class is used`() {
        val code = """
            class AutoCloseable {
                fun close() {
                    // closing the closeable
                }
            }

            fun test() {
                val myCloseable = AutoCloseable()
            }
        """.trimIndent()
        val findings = subject.compileAndLintWithContext(env, code)

        assertThat(findings).isEmpty()
    }

    @Test
    fun `does report _Closeable_ lambda is used without _use_`() {
        val code = """
            import java.io.Closeable

            fun test() {
                val myCloseable = Closeable { /*no-op*/ }
            }
        """.trimIndent()
        val findings = subject.compileAndLintWithContext(env, code)

        assertThat(findings).hasSize(1)
    }

    @Test
    fun `does not report _Closeable_ lambda is used with _use_`() {
        val code = """
            import java.io.Closeable

            fun test() {
                val myCloseable = Closeable { /*no-op*/ }.use { /*no-op*/ }
            }
        """.trimIndent()
        val findings = subject.compileAndLintWithContext(env, code)

        assertThat(findings).isEmpty()
    }

    @Test
    fun `does not report _Closeable_ anonymous object is used with _use_ with full qualifier`() {
        val code = """
            import java.io.Closeable

            fun test() {
                val myCloseable = java.io.Closeable { /*no-op*/ }.use { /*no-op*/ }
            }
        """.trimIndent()
        val findings = subject.compileAndLintWithContext(env, code)

        assertThat(findings).isEmpty()
    }

    @Test
    fun `does not report _Closeable_ anonymous object with full qualifier is used with chain after _use_`() {
        val code = """
            import java.io.Closeable

            fun test() {
                val myCloseable = java.io.Closeable { /*no-op*/ }.use { mutableListOf<Int>() }.also { it.add(0) }
            }
        """.trimIndent()
        val findings = subject.compileAndLintWithContext(env, code)

        assertThat(findings).isEmpty()
    }

    @Test
    fun `does not report _Closeable_ anonymous object with full qualifier is used with arithmetic after _use_`() {
        val code = """
            import java.io.Closeable

            fun test() {
                val myCloseable = java.io.Closeable { /*no-op*/ }.use { 1 } + 2
            }
        """.trimIndent()
        val findings = subject.compileAndLintWithContext(env, code)

        assertThat(findings).isEmpty()
    }

    @Test
    fun `does report _Closeable_ anonymous object is used without _use_ with full qualifier`() {
        val code = """
            import java.io.Closeable

            fun test() {
                val myCloseable = java.io.Closeable { /*no-op*/ }
            }
        """.trimIndent()
        val findings = subject.compileAndLintWithContext(env, code)

        assertThat(findings).hasSize(1)
    }

    @Test
    fun `does report _Closeable_ anonymous object is used without _use_`() {
        val code = """
            import java.io.Closeable

            @Suppress("ObjectLiteralToLambda")
            val myCloseable = object : Closeable {
                override fun close() {
                    /*no-op*/
                }
            }
        """.trimIndent()
        val findings = subject.compileAndLintWithContext(env, code)

        assertThat(findings).hasSize(1)
    }

    @Test
    fun `does not report _Closeable_ anonymous object is used with _use_`() {
        val code = """
            import java.io.Closeable

            @Suppress("ObjectLiteralToLambda")
            val myCloseable = object : Closeable {
                override fun close() {
                    /*no-op*/
                }
            }.use { /*no-op*/ }
        """.trimIndent()
        val findings = subject.compileAndLintWithContext(env, code)

        assertThat(findings).isEmpty()
    }

    @Test
    fun `does not report _Closeable_ child class anonymous object is used with _use_`() {
        val code = """
            import java.io.Closeable

            open class MyCloseable : Closeable {
                override fun close() {
                    // closing the closeable
                }
            }

            fun test() {
                @Suppress("ObjectLiteralToLambda")
                val myCloseable = object : MyCloseable() {
                    override fun close() {
                        /*no-op*/
                    }
                }.use { /*no-op*/ }
            }
        """.trimIndent()
        val findings = subject.compileAndLintWithContext(env, code)

        assertThat(findings).isEmpty()
    }

    @Test
    fun `does report _Closeable_ child class anonymous object is used with out _use_`() {
        val code = """
            import java.io.Closeable

            open class MyCloseable : Closeable {
                override fun close() {
                    // closing the closeable
                }
            }

            fun test() {
                @Suppress("ObjectLiteralToLambda")
                val myCloseable = object : MyCloseable() {
                    override fun close() {
                        /*no-op*/
                    }
                }
            }
        """.trimIndent()
        val findings = subject.compileAndLintWithContext(env, code)

        assertThat(findings).hasSize(1)
    }

    @Test
    fun `does report function returning _Closeable_ use it with out _use_`() {
        val code = """
            import java.io.Closeable

            open class MyCloseable : Closeable {
                override fun close() {
                    // closing the closeable
                }

                fun doStuff() {

                }
            }

            fun functionThatReturnsClosable() = MyCloseable()

            fun test() {
                functionThatReturnsClosable().doStuff()
            }
        """.trimIndent()
        val findings = subject.compileAndLintWithContext(env, code)

        assertThat(findings).hasSize(2)
    }

    @Test
    fun `does not report function returning _Closeable_ use it with _use_`() {
        val code = """
            import java.io.Closeable

            open class MyCloseable : Closeable {
                override fun close() {
                    // closing the closeable
                }

                fun doStuff() {

                }
            }

            fun functionThatReturnsClosable() = MyCloseable()

            fun test() {
                functionThatReturnsClosable().use { it.doStuff() }
            }
        """.trimIndent()
        val findings = subject.compileAndLintWithContext(env, code)

        assertThat(findings).hasSize(1)
    }
}
