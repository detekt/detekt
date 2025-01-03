package io.gitlab.arturbosch.detekt.rules.style

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.SourceLocation
import io.gitlab.arturbosch.detekt.test.assertThat
import io.gitlab.arturbosch.detekt.test.lint
import org.assertj.core.api.Assertions.assertThat
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
        assertThat(findings)
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
        assertThat(findings).singleElement().hasMessage(WRONG_SERIAL_VERSION_UID_MESSAGE)
        assertThat(findings)
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
        assertThat(findings).singleElement().hasMessage(WRONG_SERIAL_VERSION_UID_MESSAGE)
        assertThat(findings)
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
        assertThat(findings).singleElement().hasMessage(WRONG_SERIAL_VERSION_UID_MESSAGE)
        assertThat(findings)
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
        assertThat(findings)
            .hasSize(2)
            .hasStartSourceLocations(SourceLocation(3, 7), SourceLocation(8, 12))
            .hasEndSourceLocations(SourceLocation(3, 8), SourceLocation(8, 43))
        assertThat(findings.map { it.message }).containsOnly(
            "The class C implements the `Serializable` interface and should thus define " +
                "a `serialVersionUID`.",
            "The object NestedIncorrectSerialVersionUID implements the `Serializable` interface and should thus " +
                "define a `serialVersionUID`."
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
        assertThat(findings)
            .hasSize(2)
            .hasStartSourceLocations(SourceLocation(5, 21), SourceLocation(8, 17))
            .hasEndSourceLocations(SourceLocation(5, 37), SourceLocation(8, 33))
        assertThat(findings.map { it.message }).containsOnly(
            WRONG_SERIAL_VERSION_UID_MESSAGE,
            WRONG_SERIAL_VERSION_UID_MESSAGE
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
        assertThat(findings)
            .hasSize(2)
            .hasStartSourceLocations(SourceLocation(6, 25), SourceLocation(11, 21))
            .hasEndSourceLocations(SourceLocation(6, 41), SourceLocation(11, 37))
        assertThat(findings.map { it.message }).containsOnly(
            WRONG_SERIAL_VERSION_UID_MESSAGE,
            WRONG_SERIAL_VERSION_UID_MESSAGE
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
        assertThat(findings)
            .hasSize(2)
            .hasStartSourceLocations(SourceLocation(3, 7), SourceLocation(4, 11))
            .hasEndSourceLocations(SourceLocation(3, 8), SourceLocation(4, 12))
        assertThat(findings.map { it.message }).containsOnly(
            "The class A implements the `Serializable` interface and should thus define a `serialVersionUID`.",
            "The class B implements the `Serializable` interface and should thus define a `serialVersionUID`."
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
