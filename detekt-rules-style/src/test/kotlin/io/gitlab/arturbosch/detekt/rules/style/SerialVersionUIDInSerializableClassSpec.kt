package io.gitlab.arturbosch.detekt.rules.style

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.test.compileAndLint
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
        assertThat(subject.compileAndLint(code)).hasSize(1)
    }

    @Test
    fun `reports class with wrong datatype`() {
        val code = """
            import java.io.Serializable

            class C : Serializable {
                companion object {
                    const val serialVersionUID = 1
                }
            }
        """.trimIndent()
        assertThat(subject.compileAndLint(code)).hasSize(1)
    }

    @Test
    fun `reports class with wrong explicitly defined datatype`() {
        val code = """
            import java.io.Serializable

            class C : Serializable {
                companion object {
                    const val serialVersionUID: Int = 1
                }
            }
        """.trimIndent()
        assertThat(subject.compileAndLint(code)).hasSize(1)
    }

    @Test
    fun `reports class with wrong naming and without const modifier`() {
        val code = """
            import java.io.Serializable

            class C : Serializable {
                companion object {
                    const val serialVersionUUID = 1L
                }

                object NestedIncorrectSerialVersionUID : Serializable {
                    val serialVersionUUID = 1L
                }
            }
        """.trimIndent()
        assertThat(subject.compileAndLint(code)).hasSize(2)
    }

    @Test
    fun `does not report a unserializable class`() {
        val code = "class NoSerializableClass"
        assertThat(subject.compileAndLint(code)).isEmpty()
    }

    @Test
    fun `does not report an interface that implements Serializable`() {
        val code = """
            import java.io.Serializable

            interface I : Serializable
        """.trimIndent()
        assertThat(subject.compileAndLint(code)).isEmpty()
    }

    @Test
    fun `does not report UID constant with positive value`() {
        val code = """
            import java.io.Serializable

            class C : Serializable {
                companion object {
                    const val serialVersionUID = 1L
                }
            }
        """.trimIndent()
        assertThat(subject.compileAndLint(code)).isEmpty()
    }

    @Test
    fun `does not report UID constant with negative value`() {
        val code = """
            import java.io.Serializable

            class C : Serializable {
                companion object {
                    const val serialVersionUID = -1L
                }
            }
        """.trimIndent()
        assertThat(subject.compileAndLint(code)).isEmpty()
    }

    @Test
    fun `does not report UID constant with explicit Long type`() {
        val code = """
            import java.io.Serializable

            class C : Serializable {
                companion object {
                    const val serialVersionUID: Long = 1
                }
            }
        """.trimIndent()
        assertThat(subject.compileAndLint(code)).isEmpty()
    }
}
