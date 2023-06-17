@file:Suppress("ClassName")

package io.gitlab.arturbosch.detekt.rules.bugs

import io.gitlab.arturbosch.detekt.rules.KotlinCoreEnvironmentTest
import io.gitlab.arturbosch.detekt.test.assertThat
import io.gitlab.arturbosch.detekt.test.compileAndLintWithContext
import org.jetbrains.kotlin.cli.jvm.compiler.KotlinCoreEnvironment
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

@KotlinCoreEnvironmentTest
class MissingUseCallSpec(private val env: KotlinCoreEnvironment) {
    private val subject = MissingUseCall()

    @Test
    fun `does not report when _use_ is used`() {
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
    fun `does report when _use_ is not used`() {
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
        assertThat(findings[0]).hasSourceLocation(10, 23)
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
                    /* no-op */
                }
            }
        """.trimIndent()
        val findings = subject.compileAndLintWithContext(env, code)
        assertThat(findings).isEmpty()
    }

    @Test
    fun `does report when _use_ is used later on the chain`() {
        val code = """
            import java.io.Closeable

            class MyCloseable(private val i: Int) : Closeable {
                override fun close() {
                    println("Closing ${'$'}i")
                }
            }

            fun test() {
                MyCloseable(0).also { 
                    println("closed is accessed here")
                }.use { 
                    /* no-op */
                }

                MyCloseable(0).apply { 
                    println("closed is accessed here")
                }.use { 
                    /* no-op */
                }
            }
        """.trimIndent()
        val findings = subject.compileAndLintWithContext(env, code)
        assertThat(findings).hasSize(2)
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
                    println("🐱 meow")
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
            ).use { /* no-op */ }
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

            val whatever = FileReader("some_file.txt").use { fileReader ->
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
    fun `does not report with custom class with same name is used`() {
        val code = """
            class Closeable {
                fun close() {
                    // closing the closeable
                }
            }

            class AutoCloseable {
                fun close() {
                    // closing the closeable
                }
            }

            fun test() {
                val myCloseable1 = Closeable()
                val myCloseable2 = AutoCloseable()
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
                val myCloseable = Closeable { /* no-op */ }
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
                val myCloseable = Closeable { /* no-op */ }.use { /* no-op */ }
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
                val myCloseable = java.io.Closeable { /* no-op */ }.use { /* no-op */ }
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
                val myCloseable = java.io.Closeable { /* no-op */ }.use { mutableListOf<Int>() }.also { it.add(0) }
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
                val myCloseable = java.io.Closeable { /* no-op */ }.use { 1 } + 2
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
                val myCloseable = java.io.Closeable { /* no-op */ }
            }
        """.trimIndent()
        val findings = subject.compileAndLintWithContext(env, code)
        assertThat(findings).hasSize(1)
    }

    @Test
    fun `does report _Closeable_ anonymous object is used without _use_`() {
        val code = """
            import java.io.Closeable

            val myCloseable = object : Closeable {
                override fun close() {
                    /* no-op */
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

            val myCloseable = object : Closeable {
                override fun close() {
                    /* no-op */
                }
            }.use { /* no-op */ }
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
                val myCloseable = object : MyCloseable() {
                    override fun close() {
                        /* no-op */
                    }
                }.use { /* no-op */ }
            }
        """.trimIndent()
        val findings = subject.compileAndLintWithContext(env, code)
        assertThat(findings).isEmpty()
    }

    @Test
    fun `does report _Closeable_ child class anonymous object is used without _use_`() {
        val code = """
            import java.io.Closeable

            open class MyCloseable : Closeable {
                override fun close() {
                    // closing the closeable
                }
            }

            fun test() {
                val myCloseable = object : MyCloseable() {
                    override fun close() {
                        /* no-op */
                    }
                }
            }
        """.trimIndent()
        val findings = subject.compileAndLintWithContext(env, code)
        assertThat(findings).hasSize(1)
    }

    @Test
    fun `does not report function returning nullable _Closeable_ used with _use_`() {
        val code = """
            import java.io.Closeable
            fun foo(): Closeable? = null
            fun test() { 
                foo()?.use { /* no-op */ }
                foo()!!.use { /* no-op */ }
            }
        """.trimIndent()
        val findings = subject.compileAndLintWithContext(env, code)
        assertThat(findings).isEmpty()
    }

    @Test
    fun `does not report nullable instance function returning _Closeable_ used with _use_`() {
        val code = """
            import java.io.Closeable
            class Bar {
                fun foo(): Closeable? = null
            }
            fun test(bar: Bar?) {
                bar?.foo()?.use { /* no-op */ }
                bar?.foo()!!.use { /* no-op */ }
            }
        """.trimIndent()
        val findings = subject.compileAndLintWithContext(env, code)
        assertThat(findings).isEmpty()
    }

    @Nested
    inner class `Given if else expression` {
        @Test
        fun `does not report when _use_ is used on _Closeable_ returned from multiline if else expression`() {
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
                        /* no-op */
                    }
                }
            """.trimIndent()
            val findings = subject.compileAndLintWithContext(env, code)
            assertThat(findings).isEmpty()
        }

        @Test
        fun `does not report when _use_ is used on _Closeable_ inside braces returned from multiline if else expression`() {
            val code = """
                import java.io.Closeable

                class MyCloseable(private val i: Int) : Closeable {
                    override fun close() {
                        println("Closing ${'$'}i")
                    }
                }

                fun test() {
                    // with braces in Closeables
                    if (System.currentTimeMillis() % 2 == 0L) {
                        (MyCloseable(0)) 
                    } else {
                        (MyCloseable(1))
                    }.use { 
                        /* no-op */
                    }

                    // with braces in if else
                    (if (System.currentTimeMillis() % 2 == 0L) {
                        (MyCloseable(0)) 
                    } else {
                        (MyCloseable(1))
                    }).use { 
                        /* no-op */
                    }
                }
            """.trimIndent()
            val findings = subject.compileAndLintWithContext(env, code)
            assertThat(findings).isEmpty()
        }

        @Test
        fun `does not report when _use_ is used on _Closeable_ inside braces returned from multiline if expression and else returning null`() {
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
                        null
                    }?.use { 
                        /* no-op */
                    }
                }
            """.trimIndent()
            val findings = subject.compileAndLintWithContext(env, code)
            assertThat(findings).isEmpty()
        }

        @Test
        fun `does not report when _use_ is used on _Closeable_ returned from single line if else expression`() {
            val code = """
                import java.io.Closeable

                class MyCloseable(private val i: Int) : Closeable {
                    override fun close() {
                        println("Closing ${'$'}i")
                    }
                }

                fun test() {
                    (if (System.currentTimeMillis() % 2 == 0L) MyCloseable(0) else MyCloseable(1)).use { 
                        /* no-op */
                    }
                }
            """.trimIndent()
            val findings = subject.compileAndLintWithContext(env, code)
            assertThat(findings).isEmpty()
        }

        @Test
        fun `does not report when _use_ is used on _Closeable_ returned from single line if expression and else returning null`() {
            val code = """
                import java.io.Closeable

                class MyCloseable(private val i: Int) : Closeable {
                    override fun close() {
                        println("Closing ${'$'}i")
                    }
                }

                fun test() {
                    (if (System.currentTimeMillis() % 2 == 0L) MyCloseable(0) else null)?.use { 
                        /* no-op */
                    }
                }
            """.trimIndent()
            val findings = subject.compileAndLintWithContext(env, code)
            assertThat(findings).isEmpty()
        }

        @Test
        fun `does not report when _use_ is used on _Closeable_ inside braces returned from single line if else expression`() {
            val code = """
                import java.io.Closeable

                class MyCloseable(private val i: Int) : Closeable {
                    override fun close() {
                        println("Closing ${'$'}i")
                    }
                }

                fun test() {
                    // with braces in Closeables
                    (if (System.currentTimeMillis() % 2 == 0L) (MyCloseable(0)) else (MyCloseable(1))).use { 
                        /* no-op */
                    }
                }
            """.trimIndent()
            val findings = subject.compileAndLintWithContext(env, code)
            assertThat(findings).isEmpty()
        }

        @Test
        fun `does not report when _use_ is used in next line on _Closeable_ from if else expression`() {
            val code = """
                import java.io.Closeable

                class MyCloseable(private val i: Int) : Closeable {
                    override fun close() {
                        println("Closing ${'$'}i")
                    }
                }

                fun test() {
                    val closable1 = if (System.currentTimeMillis() % 2 == 0L) { 
                        MyCloseable(0) 
                    } else { 
                        MyCloseable(1)
                    }
                    closable1.use { /* no-op */ }

                    val closable2 = if (System.currentTimeMillis() % 2 == 0L) { MyCloseable(0) } else { MyCloseable(1) }
                    closable2.use { /* no-op */ }

                    val closeable3 = if (System.currentTimeMillis() % 2 == 0L) MyCloseable(0) else MyCloseable(1)
                    closeable3.use { /* no-op */ }
                }
            """.trimIndent()
            val findings = subject.compileAndLintWithContext(env, code)
            assertThat(findings).isEmpty()
        }

        @Test
        fun `does not report when _use_ is used after few lines on _Closeable_ from if else expression`() {
            val code = """
                import java.io.Closeable

                class MyCloseable(private val i: Int) : Closeable {
                    override fun close() {
                        println("Closing ${'$'}i")
                    }
                }

                fun test() {
                    val closable = if (System.currentTimeMillis() % 2 == 0L) { 
                        MyCloseable(0)
                    } else { 
                        MyCloseable(1)
                    }
                    println("Processing code")
                    closable.use { /* no-op */ }
                }
            """.trimIndent()
            val findings = subject.compileAndLintWithContext(env, code)
            assertThat(findings).isEmpty()
        }

        @Test
        fun `does not report when _use_ is used in next line on _Closeable_ from if expression and else returning null`() {
            val code = """
                import java.io.Closeable

                class MyCloseable(private val i: Int) : Closeable {
                    override fun close() {
                        println("Closing ${'$'}i")
                    }
                }

                fun test() {
                    val closable1 = if (System.currentTimeMillis() % 2 == 0L) { 
                        MyCloseable(0) 
                    } else { 
                        null
                    }
                    closable1?.use { /* no-op */ }

                    val closable2 = if (System.currentTimeMillis() % 2 == 0L) { MyCloseable(0) } else { null }
                    closable2?.use { /* no-op */ }

                    val closeable3 = if (System.currentTimeMillis() % 2 == 0L) MyCloseable(0) else null
                    closeable3?.use { /* no-op */ }
                }
            """.trimIndent()
            val findings = subject.compileAndLintWithContext(env, code)
            assertThat(findings).isEmpty()
        }

        @Test
        fun `does report when _use_ is used not immediately after if else`() {
            val code = """
                import java.io.Closeable
                import kotlin.random.Random

                class MyCloseable(private val i: Int) : Closeable {
                    override fun close() {
                        println("Closing ${'$'}i")
                    }
                }

                fun test() {
                    val closable = if (System.currentTimeMillis() % 2 == 0L) { MyCloseable(0) } else { MyCloseable(1) }
                    if (Random(0L).nextBoolean()) { closable.use { /* no-op */ } }
                }
            """.trimIndent()
            val findings = subject.compileAndLintWithContext(env, code)
            assertThat(findings).hasSize(2)
        }

        @Test
        fun `does report when _Closeable_ is used inside if expression in multiline if else`() {
            val code = """
                import java.io.Closeable

                class MyCloseable(private val i: Int) : Closeable {
                    override fun close() {
                        println("Closing ${'$'}i")
                    }
                }

                fun test() {
                    val closable = if (System.currentTimeMillis() % 2 == 0L) { 
                        MyCloseable(0).also { 
                            // closeable is used
                        }
                    } else {
                        MyCloseable(1)
                    }
                    closable.use { /* no-op */ }
                }
            """.trimIndent()
            val findings = subject.compileAndLintWithContext(env, code)
            assertThat(findings).hasSize(2)
        }

        @Test
        fun `does report when _Closeable_ is used inside else expression inside multiline if else`() {
            val code = """
                import java.io.Closeable

                class MyCloseable(private val i: Int) : Closeable {
                    override fun close() {
                        println("Closing ${'$'}i")
                    }
                }

                fun test() {
                    val closable = if (System.currentTimeMillis() % 2 == 0L) { 
                        MyCloseable(0)
                    } else {
                        MyCloseable(1).also { 
                            // closeable is used
                        }
                    }
                    closable.use { /* no-op */ }
                }
            """.trimIndent()
            val findings = subject.compileAndLintWithContext(env, code)
            assertThat(findings).hasSize(2)
        }

        @Test
        fun `does report when _Closeable_ is used inside else expression inside single line if else`() {
            val code = """
                import java.io.Closeable

                class MyCloseable(private val i: Int) : Closeable {
                    override fun close() {
                        println("Closing ${'$'}i")
                    }
                }

                fun test() {
                    val closable = if (System.currentTimeMillis() % 2 == 0L) MyCloseable(0) else MyCloseable(1).also { /* closeable is used */ }
                    closable.use { /* no-op */ }
                }
            """.trimIndent()
            val findings = subject.compileAndLintWithContext(env, code)
            assertThat(findings).hasSize(2)
        }
    }

    @Nested
    inner class `Given function returning _Closeable_` {
        @Test
        fun `does report function returning _Closeable_ use it without _use_`() {
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
            assertThat(findings).hasSize(1)
        }

        @Test
        fun `does not report function returning _Closeable_ use it with _use_`() {
            val code = """
                import java.io.Closeable

                open class MyCloseable : Closeable {
                    override fun close() {
                        // closing the closeable
                    }
                }

                fun functionThatReturnsClosable1() = MyCloseable()

                fun functionThatReturnsClosable2(): MyCloseable {
                    return MyCloseable()
                }

                fun functionThatReturnsClosable3(): Closeable {
                    return MyCloseable()
                }
            """.trimIndent()
            val findings = subject.compileAndLintWithContext(env, code)
            assertThat(findings).isEmpty()
        }

        @Test
        fun `does report local function accessing _Closeable_ without _use_ inside outer function returning _Closeable_`() {
            val code = """
                import java.io.Closeable

                open class MyCloseable : Closeable {
                    override fun close() {
                        // closing the closeable
                    }
                }

                fun functionThatReturnsClosable1(): MyCloseable {
                    fun localFun() {
                        MyCloseable()
                    }
                    return MyCloseable()
                }
            """.trimIndent()
            val findings = subject.compileAndLintWithContext(env, code)
            assertThat(findings).hasSize(1)
        }

        @Test
        fun `does report use of _Closeable_ without _use_ inside lambda inside function returning _Closeable_`() {
            val code = """
                import java.io.Closeable

                open class MyCloseable : Closeable {
                    override fun close() {
                        // closing the closeable
                    }
                }

                fun functionThatReturnsClosable1(): MyCloseable {
                    val r = Runnable { MyCloseable() }
                    return MyCloseable()
                }
            """.trimIndent()
            val findings = subject.compileAndLintWithContext(env, code)
            assertThat(findings).hasSize(1)
        }

        @Test
        fun `does report use of _Closeable_ without _use_ inside object inside function returning _Closeable_`() {
            val code = """
                import java.io.Closeable

                open class MyCloseable : Closeable {
                    override fun close() {
                        // closing the closeable
                    }
                }

                fun functionThatReturnsClosable1(): MyCloseable {
                    val r = object : Runnable { 
                        override fun run() {
                            MyCloseable()
                        }
                    }
                    return MyCloseable()
                }
            """.trimIndent()
            val findings = subject.compileAndLintWithContext(env, code)
            assertThat(findings).hasSize(1)
        }

        @Test
        fun `does report use of _Closeable_ without _use_ inside init inside function returning _Closeable_`() {
            val code = """
                import java.io.Closeable

                open class MyCloseable : Closeable {
                    override fun close() {
                        // closing the closeable
                    }
                }

                fun functionThatReturnsClosable1(): MyCloseable {
                    val r = object : Runnable { 
                        override fun run() {
                            /* no-op */
                        }

                        init {
                            MyCloseable()
                        }
                    }
                    return MyCloseable()
                }
            """.trimIndent()
            val findings = subject.compileAndLintWithContext(env, code)
            assertThat(findings).hasSize(1)
        }

        @Test
        fun `does report use of _Closeable_ without _use_ inside class inside function returning _Closeable_`() {
            val code = """
                import java.io.Closeable

                open class MyCloseable : Closeable {
                    override fun close() {
                        // closing the closeable
                    }
                }

                fun functionThatReturnsClosable1(): MyCloseable {
                    val r = object : Runnable { 
                        override fun run() {
                            /* no-op */
                        }

                        val closeable = MyCloseable()
                    }
                    return MyCloseable()
                }
            """.trimIndent()
            val findings = subject.compileAndLintWithContext(env, code)
            assertThat(findings).hasSize(1)
        }
    }
}
