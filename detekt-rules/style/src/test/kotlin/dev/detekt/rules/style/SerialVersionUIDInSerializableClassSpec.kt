package dev.detekt.rules.style

import dev.detekt.api.Config
import dev.detekt.api.SourceLocation
import dev.detekt.test.assertj.assertThat
import dev.detekt.test.lint
import org.junit.jupiter.api.Test

class SerialVersionUIDInSerializableClassSpec {
    val subject = SerialVersionUIDInSerializableClass(Config.empty)

    @Test
    fun `reports class with no serialVersionUID`() {
        val code = """
            import java.io.Serializable
            
            class C : Serializable
        """.trimIndent()
        val findings = subject.lint(code)
        assertThat(findings).singleElement()
            .hasMessage(
                "The class C implements the `Serializable` interface and should thus define " +
                    "a `serialVersionUID`."
            )
        assertThat(findings).singleElement()
            .hasStartSourceLocation(3, 7)
            .hasEndSourceLocation(3, 8)
    }

    @Test
    fun `reports class with wrong datatype`() {
        val code = """
            import java.io.Serializable
            
            class C : Serializable {
                companion object {
                    private const val serialVersionUID = 1
                }
            }
        """.trimIndent()
        val findings = subject.lint(code)
        assertThat(findings).singleElement()
            .hasMessage(WRONG_SERIAL_VERSION_UID_MESSAGE)
            .hasStartSourceLocation(5, 27)
            .hasEndSourceLocation(5, 43)
    }

    @Test
    fun `reports class with wrong explicitly defined datatype`() {
        val code = """
            import java.io.Serializable
            
            class C : Serializable {
                companion object {
                    private const val serialVersionUID: Int = 1
                }
            }
        """.trimIndent()
        val findings = subject.lint(code)
        assertThat(findings).singleElement()
            .hasMessage(WRONG_SERIAL_VERSION_UID_MESSAGE)
            .hasStartSourceLocation(5, 27)
            .hasEndSourceLocation(5, 43)
    }

    @Test
    fun `reports class with wrong visibility`() {
        val code = """
            import java.io.Serializable

            class C : Serializable {
                companion object {
                    const val serialVersionUID: Long = 1L
                }
            }
        """.trimIndent()
        val findings = subject.lint(code)
        assertThat(findings).singleElement()
            .hasMessage(WRONG_SERIAL_VERSION_UID_MESSAGE)
            .hasStartSourceLocation(5, 19)
            .hasEndSourceLocation(5, 35)
    }

    @Test
    fun `reports class with wrong naming and without const modifier`() {
        val code = """
            import java.io.Serializable
            
            class C : Serializable {
                companion object {
                    private const val serialVersionUUID = 1L
                }
            
                object NestedIncorrectSerialVersionUID : Serializable {
                    private val serialVersionUUID = 1L
                }
            }
        """.trimIndent()
        val findings = subject.lint(code)
        assertThat(findings).satisfiesExactlyInAnyOrder(
            {
                assertThat(it)
                    .hasMessage(
                        "The class C implements the `Serializable` interface and should thus define " +
                            "a `serialVersionUID`.",
                    )
                    .hasStartSourceLocation(SourceLocation(3, 7))
                    .hasEndSourceLocation(SourceLocation(3, 8))
            },
            {
                assertThat(it)
                    .hasMessage(
                        "The object NestedIncorrectSerialVersionUID implements the `Serializable` interface and should thus " +
                            "define a `serialVersionUID`."
                    )
                    .hasStartSourceLocation(SourceLocation(8, 12))
                    .hasEndSourceLocation(SourceLocation(8, 43))
            },
        )
    }

    @Test
    fun `reports nested object without const modifier`() {
        val code = """
            import java.io.Serializable
            
            object A : Serializable {
                object B : Serializable {
                    private val serialVersionUID = 1L
                }
            
                private val serialVersionUID = 1L
            }
        """.trimIndent()
        val findings = subject.lint(code)
        assertThat(findings).satisfiesExactlyInAnyOrder(
            {
                assertThat(it)
                    .hasMessage(WRONG_SERIAL_VERSION_UID_MESSAGE)
                    .hasStartSourceLocation(SourceLocation(5, 21))
                    .hasEndSourceLocation(SourceLocation(5, 37))
            },
            {
                assertThat(it)
                    .hasMessage(WRONG_SERIAL_VERSION_UID_MESSAGE)
                    .hasStartSourceLocation(SourceLocation(8, 17))
                    .hasEndSourceLocation(SourceLocation(8, 33))
            },
        )
    }

    @Test
    fun `reports nested class without const modifier`() {
        val code = """
            import java.io.Serializable
            
            class A : Serializable {
                class B : Serializable {
                    companion object {
                        private val serialVersionUID = 1L
                    }
                }

                companion object {
                    private val serialVersionUID = 1L
                }
            }
        """.trimIndent()
        val findings = subject.lint(code)
        assertThat(findings).satisfiesExactlyInAnyOrder(
            {
                assertThat(it)
                    .hasMessage(WRONG_SERIAL_VERSION_UID_MESSAGE)
                    .hasStartSourceLocation(SourceLocation(6, 25))
                    .hasEndSourceLocation(SourceLocation(6, 41))
            },
            {
                assertThat(it)
                    .hasMessage(WRONG_SERIAL_VERSION_UID_MESSAGE)
                    .hasStartSourceLocation(SourceLocation(11, 21))
                    .hasEndSourceLocation(SourceLocation(11, 37))
            },
        )
    }

    @Test
    fun `reports nested class without serialVersionUID property`() {
        val code = """
            import java.io.Serializable
            
            class A : Serializable {
                class B : Serializable
            }
        """.trimIndent()
        val findings = subject.lint(code)
        assertThat(findings).satisfiesExactlyInAnyOrder(
            {
                assertThat(it)
                    .hasMessage(
                        "The class A implements the `Serializable` interface and should thus define a `serialVersionUID`."
                    )
                    .hasStartSourceLocation(SourceLocation(3, 7))
                    .hasEndSourceLocation(SourceLocation(3, 8))
            },
            {
                assertThat(it)
                    .hasMessage(
                        "The class B implements the `Serializable` interface and should thus define a `serialVersionUID`."
                    )
                    .hasStartSourceLocation(SourceLocation(4, 11))
                    .hasEndSourceLocation(SourceLocation(4, 12))
            },
        )
    }

    @Test
    fun `does not report a unserializable class`() {
        val code = "class NoSerializableClass"
        assertThat(subject.lint(code)).isEmpty()
    }

    @Test
    fun `does not report an interface that implements Serializable`() {
        val code = """
            import java.io.Serializable
            
            interface I : Serializable
        """.trimIndent()
        assertThat(subject.lint(code)).isEmpty()
    }

    @Test
    fun `does not report UID constant with positive value`() {
        val code = """
            import java.io.Serializable
            
            class C : Serializable {
                companion object {
                    private const val serialVersionUID = 1L
                }
            }
        """.trimIndent()
        assertThat(subject.lint(code)).isEmpty()
    }

    @Test
    fun `does not report UID constant with negative value`() {
        val code = """
            import java.io.Serializable
            
            class C : Serializable {
                companion object {
                    private const val serialVersionUID = -1L
                }
            }
        """.trimIndent()
        assertThat(subject.lint(code)).isEmpty()
    }

    @Test
    fun `does not report UID constant with explicit Long type`() {
        val code = """
            import java.io.Serializable
            
            class C : Serializable {
                companion object {
                    private const val serialVersionUID: Long = 1
                }
            }
        """.trimIndent()
        assertThat(subject.lint(code)).isEmpty()
    }

    @Test
    fun `does not report constant in private companion object`() {
        val code = """
            import java.io.Serializable
            
            class C : Serializable {
                private companion object {
                    const val serialVersionUID: Long = 1
                }
            }
        """.trimIndent()
        assertThat(subject.lint(code)).isEmpty()
    }

    companion object {
        private const val WRONG_SERIAL_VERSION_UID_MESSAGE =
            "The property `serialVersionUID` signature is not correct. `serialVersionUID` should be " +
                "`private` and `constant` and its type should be `Long`"
    }
}
