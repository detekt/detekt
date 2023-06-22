package io.gitlab.arturbosch.detekt.rules.complexity

import io.gitlab.arturbosch.detekt.test.TestConfig
import io.gitlab.arturbosch.detekt.test.compileAndLint
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

private val defaultAllowedDefinitions = "allowedDefinitions" to 3
private val staticDeclarationsConfig = TestConfig(defaultAllowedDefinitions, "includeStaticDeclarations" to true)
private val privateDeclarationsConfig = TestConfig(defaultAllowedDefinitions, "includePrivateDeclarations" to true)
private val ignoreOverloadedConfig = TestConfig(defaultAllowedDefinitions, "ignoreOverloaded" to true)

class ComplexInterfaceSpec {

    private val subject = ComplexInterface(TestConfig(defaultAllowedDefinitions))

    @Nested
    inner class `ComplexInterface rule positives` {

        @Nested
        inner class `interface members` {
            val code = """
                interface I {
                    fun f1()
                    fun f2()
                    val i1: Int
                    fun fImpl() {}
                }
            """.trimIndent()

            @Test
            fun `reports complex interface`() {
                assertThat(subject.compileAndLint(code)).hasSize(1)
            }

            @Test
            fun `reports complex interface with includeStaticDeclarations config`() {
                val rule = ComplexInterface(staticDeclarationsConfig)
                assertThat(rule.compileAndLint(code)).hasSize(1)
            }
        }

        @Nested
        inner class `nested interface members` {
            val code = """
                class I {
                    interface Nested {
                        fun f1()
                        fun f2()
                        val i1: Int
                        fun fImpl() {}
                    }
                }
            """.trimIndent()

            @Test
            fun `reports complex interface`() {
                assertThat(subject.compileAndLint(code)).hasSize(1)
            }

            @Test
            fun `reports complex interface with includeStaticDeclarations config`() {
                val rule = ComplexInterface(staticDeclarationsConfig)
                assertThat(rule.compileAndLint(code)).hasSize(1)
            }
        }

        @Nested
        inner class `interface with static declarations` {
            val code = """
                interface I {
                    fun f1()
                    companion object {
                        fun sf() = 0
                        const val c = 0
                        val v = 0
                    }
                }
            """.trimIndent()

            @Test
            fun `does not report static declarations per default`() {
                assertThat(subject.compileAndLint(code)).isEmpty()
            }

            @Test
            fun `reports complex interface with includeStaticDeclarations config`() {
                val rule = ComplexInterface(staticDeclarationsConfig)
                assertThat(rule.compileAndLint(code)).hasSize(1)
            }
        }

        @Nested
        inner class `private functions` {
            val code = """
                interface I {
                    fun f1()
                    fun f2()
                    val i1: Int
                    private fun fImpl() {}
                }
            """.trimIndent()

            @Test
            fun `does not report complex interface`() {
                assertThat(subject.compileAndLint(code)).isEmpty()
            }

            @Test
            fun `does report complex interface with includePrivateDeclarations config`() {
                val rule = ComplexInterface(privateDeclarationsConfig)
                assertThat(rule.compileAndLint(code)).hasSize(1)
            }
        }

        @Nested
        inner class `private members` {
            val code = """
                interface I {
                    fun f1()
                    fun f2()
                    private val i1: Int
                        get() = 42
                    fun fImpl() {}
                }
            """.trimIndent()

            @Test
            fun `does not report complex interface`() {
                assertThat(subject.compileAndLint(code)).isEmpty()
            }

            @Test
            fun `does report complex interface with includePrivateDeclarations config`() {
                val rule = ComplexInterface(privateDeclarationsConfig)
                assertThat(rule.compileAndLint(code)).hasSize(1)
            }
        }

        @Nested
        inner class `overloaded methods` {
            val code = """
                interface I {
                    fun f1()
                    fun f1(i: Int)
                    val i1: Int
                    fun fImpl() {}
                }
            """.trimIndent()

            @Test
            fun `reports complex interface with overloaded methods`() {
                assertThat(subject.compileAndLint(code)).hasSize(1)
            }

            @Test
            fun `does not report simple interface with ignoreOverloaded`() {
                val rule = ComplexInterface(ignoreOverloadedConfig)
                assertThat(rule.compileAndLint(code)).isEmpty()
            }

            @Test
            fun `reports complex interface with extension methods with a different receiver`() {
                val interfaceWithExtension = """
                    interface I {
                        fun f1()
                        fun String.f1(i: Int)
                        val i1: Int
                        fun fImpl() {}
                    }
                """.trimIndent()
                val rule = ComplexInterface(ignoreOverloadedConfig)
                assertThat(rule.compileAndLint(interfaceWithExtension)).hasSize(1)
            }

            @Test
            fun `does not report simple interface with extension methods with the same receiver`() {
                val interfaceWithOverloadedExtensions = """
                    interface I {
                        fun String.f1()
                        fun String.f1(i: Int)
                        val i1: Int
                        fun fImpl() {}
                    }
                """.trimIndent()
                val rule = ComplexInterface(ignoreOverloadedConfig)
                assertThat(rule.compileAndLint(interfaceWithOverloadedExtensions)).isEmpty()
            }
        }
    }

    @Nested
    inner class `ComplexInterface rule negatives` {

        @Test
        fun `does not report a simple interface `() {
            val code = """
                interface I {
                    fun f()
                    fun fImpl() {
                        val x = 0 // should not report
                    }
                
                    val i: Int
                    // a comment shouldn't be detected
                }
            """.trimIndent()
            assertThat(subject.compileAndLint(code)).isEmpty()
        }

        @Test
        fun `does not report a simple interface with a companion object`() {
            val code = """
                interface I {
                    fun f()
                
                    companion object {
                        fun sf() = 0
                        const val c = 0
                    }
                }
            """.trimIndent()
            assertThat(subject.compileAndLint(code)).isEmpty()
        }

        @Test
        fun `does not report an empty interface`() {
            val code = "interface Empty"
            assertThat(subject.compileAndLint(code)).isEmpty()
        }

        @Test
        fun `does not report an interface that has exactly the allowed definitions`() {
            val code = """
                interface MyInterface{
                    fun func1()
                    fun func2()
                    fun func3()
                }
            """.trimIndent()
            assertThat(subject.compileAndLint(code)).isEmpty()
        }
    }
}
