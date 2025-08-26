@file:Suppress("ClassName")

package dev.detekt.rules.bugs

import dev.detekt.test.assertj.assertThat
import dev.detekt.test.lintWithContext
import dev.detekt.test.utils.KotlinCoreEnvironmentTest
import dev.detekt.test.utils.KotlinEnvironmentContainer
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource

@KotlinCoreEnvironmentTest
class MissingUseCallSpec(private val env: KotlinEnvironmentContainer) {
    private val subject = MissingUseCall()

    @ParameterizedTest
    @ValueSource(strings = ["java.io.Closeable", "java.lang.AutoCloseable"])
    fun `does not report when _use_ is used`(clazz: String) {
        val code = """
            ${myClosable(clazz)}

            fun test() {
                MyCloseable(0).use { /*no-op*/ }
            }
        """.trimIndent()
        val findings = subject.lintWithContext(env, code)
        assertThat(findings).isEmpty()
    }

    @ParameterizedTest
    @ValueSource(strings = ["java.io.Closeable", "java.lang.AutoCloseable"])
    fun `does report when _use_ is not used`(clazz: String) {
        val code = """
            fun test() {
                val myCloseable = MyCloseable(0)
            }

            ${myClosable(clazz)}
        """.trimIndent()
        val findings = subject.lintWithContext(env, code)
        assertThat(findings).singleElement()
            .hasStartSourceLocation(2, 23)
    }

    @Test
    fun `does not report when _use_ used with _Closeable_ returned from if else expression in single place`() {
        val code = """
            ${myClosable()}

            fun test() {
                MyCloseable(
                    if (System.currentTimeMillis() % 2 == 0L) 0 else 1
                ).use { 
                    /* no-op */
                }
            }
        """.trimIndent()
        val findings = subject.lintWithContext(env, code)
        assertThat(findings).isEmpty()
    }

    @Test
    fun `does not report when _use_ is not used in _Closeable_ as param modifying function`() {
        val code = """
            ${myClosable()}

            fun actOnClosable(bar: MyCloseable): MyCloseable { 
                bar.doStuff()
                return bar
            }
        """.trimIndent()
        val findings = subject.lintWithContext(env, code)
        assertThat(findings).isEmpty()
    }

    @Test
    fun `does not report when _use_ is not used in _Closeable_ as receiver modifying function`() {
        val code = """
            ${myClosable()}

            fun MyCloseable.actOnClosable(): MyCloseable { 
                this.doStuff()
                return this
            }
        """.trimIndent()
        val findings = subject.lintWithContext(env, code)
        assertThat(findings).isEmpty()
    }

    @Test
    fun `does report when _use_ is not used in outer closeable taking closable param`() {
        val code = """
            import java.io.Closeable
            import java.io.BufferedReader
            import java.io.FileReader

            val lines = BufferedReader(
                FileReader("some_file.txt")
            )
        """.trimIndent()
        val findings = subject.lintWithContext(env, code)
        assertThat(findings).singleElement()
            .hasMessage(
                "BufferedReader doesn't call `use` to access the `Closeable`"
            )
    }

    @Test
    fun `does not report when _use_ is used in outer closeable taking closable param`() {
        val code = """
            import java.io.Closeable
            import java.io.BufferedReader
            import java.io.FileReader

            fun test() {
                BufferedReader(
                    FileReader("some_file.txt")
                ).use { val lines = it.lines() }
            }
        """.trimIndent()
        val findings = subject.lintWithContext(env, code)
        assertThat(findings).isEmpty()
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
        val findings = subject.lintWithContext(env, code)
        assertThat(findings).singleElement()
            .hasMessage("MyCloseable doesn't call `use` to access the `Closeable`")
    }

    @Test
    fun `does not report inner use of stream without _use_ with some parameter`() {
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
        val findings = subject.lintWithContext(env, code)
        assertThat(findings).isEmpty()
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
        val findings = subject.lintWithContext(env, code)
        assertThat(findings).isEmpty()
    }

    @Test
    fun `does not report nested call when _use_ is used with computation inside _use_`() {
        val code = """
            import java.io.Closeable
            import java.io.BufferedReader
            import java.io.FileReader
            import java.util.stream.Collectors

            val lines1 = FileReader("some_file.txt").use { fileReader -> BufferedReader(fileReader).use { it.lines().collect(Collectors.toList()) } }
            val lines2 = FileReader("some_file.txt").use { fileReader -> fileReader.buffered().lines().collect(Collectors.toList()) }
            val linesStream = FileReader("some_file.txt").use { fileReader -> fileReader.buffered().lines() }
        """.trimIndent()
        val findings = subject.lintWithContext(env, code)
        assertThat(findings).isEmpty()
    }

    @Test
    fun `does not report use on chains`() {
        val code = """
            import java.io.Closeable
            import java.io.BufferedReader
            import java.io.FileReader
            import java.util.stream.Collectors

            val lines1 = BufferedReader(FileReader("some_file.txt")).use { it.lines().collect(Collectors.toList()) }
            val lines2 = FileReader("some_file.txt").buffered().lines().collect(Collectors.toList())
            val linesStream = FileReader("some_file.txt").buffered().lines()
        """.trimIndent()
        val findings = subject.lintWithContext(env, code)
        assertThat(findings).isEmpty()
    }

    @Test
    fun `does not report with custom class with same name is used`() {
        val code = """
            class Closeable {
                fun close() {
                    // closing the class
                }
            }

            class AutoCloseable {
                fun close() {
                    // closing the class
                }
            }

            fun test() {
                val myCloseable1 = Closeable()
                val myCloseable2 = AutoCloseable()
            }
        """.trimIndent()
        val findings = subject.lintWithContext(env, code)
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
        val findings = subject.lintWithContext(env, code)
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
        val findings = subject.lintWithContext(env, code)
        assertThat(findings).isEmpty()
    }

    @Test
    fun `does not report _Closeable_ anonymous object is used with _use_ with full qualifier`() {
        val code = """
            fun test() {
                val myCloseable = java.io.Closeable { /* no-op */ }.use { /* no-op */ }
            }
        """.trimIndent()
        val findings = subject.lintWithContext(env, code)
        assertThat(findings).isEmpty()
    }

    @Test
    fun `does report _Closeable_ anonymous object is used without _use_ with full qualifier`() {
        val code = """
            fun test() {
                val myCloseable = java.io.Closeable { /* no-op */ }
            }
        """.trimIndent()
        val findings = subject.lintWithContext(env, code)
        assertThat(findings).hasSize(1)
    }

    @Test
    fun `does not report _Closeable_ anonymous object with full qualifier is used with chain after _use_`() {
        val code = """
            fun test() {
                val myCloseable = java.io.Closeable { /* no-op */ }.use { mutableListOf<Int>() }.also { it.add(0) }
            }
        """.trimIndent()
        val findings = subject.lintWithContext(env, code)
        assertThat(findings).isEmpty()
    }

    @Test
    fun `does not report _Closeable_ anonymous object with full qualifier is used with arithmetic after _use_`() {
        val code = """
            fun test() {
                val myCloseable = java.io.Closeable { /* no-op */ }.use { 1 } + 2
            }
        """.trimIndent()
        val findings = subject.lintWithContext(env, code)
        assertThat(findings).isEmpty()
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
        val findings = subject.lintWithContext(env, code)
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
        val findings = subject.lintWithContext(env, code)
        assertThat(findings).isEmpty()
    }

    @Test
    fun `does not report _Closeable_ child class anonymous object is used with _use_`() {
        val code = """
            ${myClosable(isOpen = true)}

            fun test() {
                object : MyCloseable(0) {}.use { /* no-op */ }
            }
        """.trimIndent()
        val findings = subject.lintWithContext(env, code)
        assertThat(findings).isEmpty()
    }

    @Test
    fun `does report _Closeable_ child class anonymous object is used without _use_`() {
        val code = """
            ${myClosable(isOpen = true)}

            fun test() {
                object : MyCloseable(0) {}
            }
        """.trimIndent()
        val findings = subject.lintWithContext(env, code)
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
        val findings = subject.lintWithContext(env, code)
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
        val findings = subject.lintWithContext(env, code)
        assertThat(findings).isEmpty()
    }

    @Nested
    inner class `Given if else expression` {
        @Test
        fun `does not report when _use_ is used on _Closeable_ returned from multiline if else expression`() {
            val code = """
                ${myClosable()}

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
            val findings = subject.lintWithContext(env, code)
            assertThat(findings).isEmpty()
        }

        @Test
        fun `does not report when _use_ is used on _Closeable_ inside parenthesis returned from multiline if else expression`() {
            val code = """
                ${myClosable()}

                fun test() {
                    // with parenthesis in Closeables
                    if (System.currentTimeMillis() % 2 == 0L) {
                        (MyCloseable(0))
                    } else {
                        (MyCloseable(1))
                    }.use { 
                        /* no-op */
                    }

                    // with parenthesis in if else
                    (if (System.currentTimeMillis() % 2 == 0L) {
                        MyCloseable(0)
                    } else {
                        (MyCloseable(1))
                    }).use { 
                        /* no-op */
                    }
                }
            """.trimIndent()
            val findings = subject.lintWithContext(env, code)
            assertThat(findings).isEmpty()
        }

        @Test
        fun `does not report when _use_ is used on _Closeable_ inside braces returned from multiline if expression and else returning null`() {
            val code = """
                ${myClosable()}

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
            val findings = subject.lintWithContext(env, code)
            assertThat(findings).isEmpty()
        }

        @Test
        fun `does not report when _use_ is used on _Closeable_ returned from single line if else expression`() {
            val code = """
                ${myClosable()}

                fun test() {
                    (if (System.currentTimeMillis() % 2 == 0L) MyCloseable(0) else MyCloseable(1)).use { 
                        /* no-op */
                    }
                }
            """.trimIndent()
            val findings = subject.lintWithContext(env, code)
            assertThat(findings).isEmpty()
        }

        @Test
        fun `does not report when _use_ is used on _Closeable_ returned from single line if expression and else returning null`() {
            val code = """
                ${myClosable()}

                fun test() {
                    (if (System.currentTimeMillis() % 2 == 0L) MyCloseable(0) else null)?.use { 
                        /* no-op */
                    }
                }
            """.trimIndent()
            val findings = subject.lintWithContext(env, code)
            assertThat(findings).isEmpty()
        }

        @Test
        fun `does not report when _use_ is used on _Closeable_ inside parenthesis returned from single line if else expression`() {
            val code = """
                ${myClosable()}

                fun test() {
                    // with parenthesis in Closeables
                    (if (System.currentTimeMillis() % 2 == 0L) (MyCloseable(0)) else (MyCloseable(1))).use { 
                        /* no-op */
                    }
                }
            """.trimIndent()
            val findings = subject.lintWithContext(env, code)
            assertThat(findings).isEmpty()
        }

        @Test
        fun `does not report when _use_ is used in next line on _Closeable_ from if else expression`() {
            val code = """
                ${myClosable()}

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
            val findings = subject.lintWithContext(env, code)
            assertThat(findings).isEmpty()
        }

        @Test
        fun `does not report when _use_ is used after few lines on _Closeable_ from if else expression`() {
            val code = """
                ${myClosable()}

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
            val findings = subject.lintWithContext(env, code)
            assertThat(findings).isEmpty()
        }

        @Test
        fun `does not report when _use_ is used in next line on _Closeable_ from if expression and else returning null`() {
            val code = """
                ${myClosable()}

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
            val findings = subject.lintWithContext(env, code)
            assertThat(findings).isEmpty()
        }

        @Test
        fun `does report when _use_ is used not immediately after if else`() {
            val code = """
                import kotlin.random.Random

                ${myClosable()}

                fun test() {
                    val closable = if (System.currentTimeMillis() % 2 == 0L) { MyCloseable(0) } else { MyCloseable(1) }
                    if (Random(0L).nextBoolean()) { closable.use { /* no-op */ } }
                }
            """.trimIndent()
            val findings = subject.lintWithContext(env, code)
            assertThat(findings).hasSize(2)
        }

        @Test
        fun `does report when _Closeable_ is used inside if expression in multiline if else`() {
            val code = """
                ${myClosable()}

                fun test() {
                    val closable = if (System.currentTimeMillis() % 2 == 0L) { 
                        MyCloseable(0).also { 
                            // closeable is used
                        }
                    } else {
                        null
                    }
                    closable.use { /* no-op */ }
                }
            """.trimIndent()
            val findings = subject.lintWithContext(env, code)
            assertThat(findings).hasSize(1)
        }

        @Test
        fun `does report when _Closeable_ is used inside else expression inside multiline if else`() {
            val code = """
                ${myClosable()}

                fun test() {
                    val closable = if (System.currentTimeMillis() % 2 == 0L) { 
                        null
                    } else {
                        MyCloseable(1).also { 
                            // closeable is used
                        }
                    }
                    closable.use { /* no-op */ }
                }
            """.trimIndent()
            val findings = subject.lintWithContext(env, code)
            assertThat(findings).hasSize(1)
        }

        @Test
        fun `does report when _Closeable_ is used inside else expression inside single line if else`() {
            val code = """
                ${myClosable()}

                fun test() {
                    val closable = if (System.currentTimeMillis() % 2 == 0L) null else MyCloseable(1).also { /* closeable is used */ }
                    closable.use { /* no-op */ }
                }
            """.trimIndent()
            val findings = subject.lintWithContext(env, code)
            assertThat(findings).hasSize(1)
        }
    }

    @Nested
    inner class `Given function returning _Closeable_` {
        @Test
        fun `does report function returning _Closeable_ use it without _use_`() {
            val code = """
                ${myClosable()}

                fun functionThatReturnsClosable() = MyCloseable(0)

                fun test() {
                    functionThatReturnsClosable().doStuff()
                }
            """.trimIndent()
            val findings = subject.lintWithContext(env, code)
            assertThat(findings).hasSize(1)
        }

        @Test
        fun `does not report function returning _Closeable_ use it with _use_`() {
            val code = """
                import java.io.Closeable

                ${myClosable()}

                fun functionThatReturnsClosable1() = MyCloseable(0)

                fun functionThatReturnsClosable2(): MyCloseable {
                    return MyCloseable(0)
                }

                fun functionThatReturnsClosable3(): Closeable {
                    return MyCloseable(0)
                }
            """.trimIndent()
            val findings = subject.lintWithContext(env, code)
            assertThat(findings).isEmpty()
        }

        @Test
        fun `does report local function accessing _Closeable_ without _use_ inside outer function returning _Closeable_`() {
            val code = """
                ${myClosable()}

                fun functionThatReturnsClosable1(): MyCloseable {
                    fun localFun() {
                        MyCloseable(0)
                    }
                    return MyCloseable(0)
                }
            """.trimIndent()
            val findings = subject.lintWithContext(env, code)
            assertThat(findings).hasSize(1)
        }

        @Test
        fun `does report use of _Closeable_ without _use_ inside lambda inside function returning _Closeable_`() {
            val code = """
                ${myClosable()}

                fun functionThatReturnsClosable1(): MyCloseable {
                    val r = Runnable { MyCloseable(0) }
                    return MyCloseable(0)
                }
            """.trimIndent()
            val findings = subject.lintWithContext(env, code)
            assertThat(findings).hasSize(1)
        }

        @Test
        fun `does report use of _Closeable_ without _use_ inside object inside function returning _Closeable_`() {
            val code = """
                ${myClosable()}

                fun functionThatReturnsClosable1(): MyCloseable {
                    val r = object : Runnable { 
                        override fun run() {
                            MyCloseable(0)
                        }
                    }
                    return MyCloseable(0)
                }
            """.trimIndent()
            val findings = subject.lintWithContext(env, code)
            assertThat(findings).hasSize(1)
        }

        @Test
        fun `does report use of _Closeable_ without _use_ inside init inside function returning _Closeable_`() {
            val code = """
                ${myClosable()}

                fun functionThatReturnsClosable1(): MyCloseable {
                    val r = object : Runnable { 
                        override fun run() {
                            /* no-op */
                        }

                        init {
                            MyCloseable(0)
                        }
                    }
                    return MyCloseable(0)
                }
            """.trimIndent()
            val findings = subject.lintWithContext(env, code)
            assertThat(findings).hasSize(1)
        }

        @Test
        fun `does report use of _Closeable_ without _use_ inside class inside function returning _Closeable_`() {
            val code = """
                ${myClosable()}

                fun functionThatReturnsClosable1(): MyCloseable {
                    val r = object : Runnable { 
                        override fun run() {
                            /* no-op */
                        }

                        val closeable = MyCloseable(0)
                    }
                    return MyCloseable(0)
                }
            """.trimIndent()
            val findings = subject.lintWithContext(env, code)
            assertThat(findings).hasSize(1)
        }
    }
}

private fun myClosable(clazz: String = "java.io.Closeable", isOpen: Boolean = false): String =
    """
        ${if (isOpen) "open " else ""} class MyCloseable(private val i: Int) : $clazz {
            override fun close() { /* no-op */ }

            fun doStuff() { /* no-op */ }
        }
    """.trimIndent().replaceIndent("    ".repeat(3)).trim()
