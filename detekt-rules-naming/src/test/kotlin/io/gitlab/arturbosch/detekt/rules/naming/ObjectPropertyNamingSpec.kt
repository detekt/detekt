package io.gitlab.arturbosch.detekt.rules.naming

import dev.detekt.api.Config
import dev.detekt.test.TestConfig
import dev.detekt.test.assertThat
import dev.detekt.test.lint
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

private const val CONSTANT_PATTERN = "constantPattern"
private const val PROPERTY_PATTERN = "propertyPattern"
private const val PRIVATE_PROPERTY_PATTERN = "privatePropertyPattern"

class ObjectPropertyNamingSpec {

    @Nested
    inner class `constants in object declarations` {

        val subject = ObjectPropertyNaming(Config.empty)

        @Test
        fun `should not detect public constants complying to the naming rules`() {
            val code = """
                object O {
                    ${PublicConst.negative}
                }
            """.trimIndent()
            assertThat(subject.lint(code)).isEmpty()
        }

        @Test
        fun `should detect public constants not complying to the naming rules`() {
            val code = """
                object O {
                    ${PublicConst.positive}
                }
            """.trimIndent()
            assertThat(subject.lint(code)).hasSize(1)
        }

        @Test
        fun `should not detect private constants complying to the naming rules`() {
            val code = """
                object O {
                    ${PrivateConst.negative}
                }
            """.trimIndent()
            assertThat(subject.lint(code)).isEmpty()
        }

        @Test
        fun `should detect private constants not complying to the naming rules`() {
            val code = """
                object O {
                    ${PrivateConst.positive}
                }
            """.trimIndent()
            assertThat(subject.lint(code)).hasSize(1)
        }

        @Test
        fun `should report constants not complying to the naming rules at the right position`() {
            val code = """
                object O {
                    ${PublicConst.positive}
                }
            """.trimIndent()
            assertThat(subject.lint(code)).hasStartSourceLocation(2, 15)
        }
    }

    @Nested
    inner class `constants in companion object` {

        val subject = ObjectPropertyNaming(Config.empty)

        @Test
        fun `should not detect public constants complying to the naming rules`() {
            val code = """
                class C {
                    companion object {
                        ${PublicConst.negative}
                    }
                }
            """.trimIndent()
            assertThat(subject.lint(code)).isEmpty()
        }

        @Test
        fun `should detect public constants not complying to the naming rules`() {
            val code = """
                class C {
                    companion object {
                        ${PublicConst.positive}
                    }
                }
            """.trimIndent()
            assertThat(subject.lint(code)).hasSize(1)
        }

        @Test
        fun `should not detect private constants complying to the naming rules`() {
            val code = """
                class C {
                    companion object {
                        ${PrivateConst.negative}
                    }
                }
            """.trimIndent()
            assertThat(subject.lint(code)).isEmpty()
        }

        @Test
        fun `should detect private constants not complying to the naming rules`() {
            val code = """
                class C {
                    companion object {
                        ${PrivateConst.positive}
                    }
                }
            """.trimIndent()
            assertThat(subject.lint(code)).hasSize(1)
        }

        @Test
        fun `should report constants not complying to the naming rules at the right position`() {
            val code = """
                class C {
                    companion object {
                        ${PublicConst.positive}
                    }
                }
            """.trimIndent()
            assertThat(subject.lint(code)).hasStartSourceLocation(3, 19)
        }
    }

    @Nested
    inner class `variables in objects` {

        val subject = ObjectPropertyNaming(Config.empty)

        @Test
        fun `should not detect public variables complying to the naming rules`() {
            val code = """
                object O {
                    ${PublicVal.negative}
                }
            """.trimIndent()
            assertThat(subject.lint(code)).isEmpty()
        }

        @Test
        fun `should detect public variables not complying to the naming rules`() {
            val code = """
                object O {
                    ${PublicVal.positive}
                }
            """.trimIndent()
            assertThat(subject.lint(code)).hasSize(1)
        }

        @Test
        fun `should not detect private variables complying to the naming rules`() {
            val code = """
                object O {
                    ${PrivateVal.negative}
                }
            """.trimIndent()
            assertThat(subject.lint(code)).isEmpty()
        }

        @Test
        fun `should detect private variables not complying to the naming rules`() {
            val code = """
                object O {
                    private val __NAME = "Artur"
                }
            """.trimIndent()
            assertThat(subject.lint(code)).hasSize(1)
        }
    }

    @Nested
    inner class `variables and constants in objects with custom config` {

        private val config = TestConfig(
            CONSTANT_PATTERN to "_[A-Za-z]*",
            PRIVATE_PROPERTY_PATTERN to ".*",
        )
        private val subject = ObjectPropertyNaming(config)

        @Test
        fun `should not detect constants in object with underscores`() {
            val code = """
                object O {
                    const val _NAME = "Artur"
                    const val _name = "Artur"
                }
            """.trimIndent()
            assertThat(subject.lint(code)).isEmpty()
        }

        @Test
        fun `should not detect private properties in object`() {
            val code = """
                object O {
                    private val __NAME = "Artur"
                    private val _1234 = "Artur"
                }
            """.trimIndent()
            assertThat(subject.lint(code)).isEmpty()
        }
    }

    @Nested
    inner class `local properties` {
        @Test
        fun `should not detect local properties`() {
            val config = TestConfig(PROPERTY_PATTERN to "valid")
            val subject = ObjectPropertyNaming(config)

            val code = """
                object O {
                    fun foo() {
                        val somethingElse = 1
                    }
                }
            """.trimIndent()

            assertThat(subject.lint(code)).isEmpty()
        }
    }

    @Nested
    inner class `top level properties` {
        @Test
        fun `should not detect top level properties`() {
            val subject = ObjectPropertyNaming(Config.empty)

            val code = """
                val _invalidNaming = 1
            """.trimIndent()

            assertThat(subject.lint(code)).isEmpty()
        }
    }

    @Test
    fun `should not detect class properties`() {
        val subject = ObjectPropertyNaming(Config.empty)
        val code = """
            class O {
                val _invalidNaming = 1
            }
        """.trimIndent()
        assertThat(subject.lint(code)).isEmpty()
    }

    @Test
    fun `should not detect properties of class in object declaration`() {
        val subject = ObjectPropertyNaming(Config.empty)
        val code = """
            object A {
                class O {
                    val _invalidNaming = 1
                }
            }
        """.trimIndent()
        assertThat(subject.lint(code)).isEmpty()
    }
}

@Suppress("AbstractClassCanBeConcreteClass")
abstract class NamingSnippet(private val isPrivate: Boolean, private val isConst: Boolean) {

    val negative = """
        ${visibility()}${const()}val MY_NAME_8 = "Artur"
        ${visibility()}${const()}val MYNAME = "Artur"
        ${visibility()}${const()}val MyNAME = "Artur"
        ${visibility()}${const()}val name = "Artur"
        ${visibility()}${const()}val nAme = "Artur"
        ${visibility()}${const()}val serialVersionUID = 42L
    """.trimIndent()
    val positive = """${visibility()}${const()}val _nAme = "Artur""""

    private fun visibility() = if (isPrivate) "private " else ""
    private fun const() = if (isConst) "const " else ""
}

object PrivateConst : NamingSnippet(true, true)
object PublicConst : NamingSnippet(false, true)
object PrivateVal : NamingSnippet(true, false)
object PublicVal : NamingSnippet(false, false)
