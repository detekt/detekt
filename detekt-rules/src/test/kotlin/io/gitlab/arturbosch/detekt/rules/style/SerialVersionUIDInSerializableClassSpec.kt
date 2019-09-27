package io.gitlab.arturbosch.detekt.rules.style

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.test.compileAndLint
import org.assertj.core.api.Assertions.assertThat
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

class SerialVersionUIDInSerializableClassSpec : Spek({
    val subject by memoized { SerialVersionUIDInSerializableClass(Config.empty) }

    describe("SerialVersionUIDInSerializableClass rule") {

        it("reports class with no serialVersionUID") {
            val code = """
                import java.io.Serializable

                class C : Serializable
            """
            assertThat(subject.compileAndLint(code)).hasSize(1)
        }

        it("reports class with wrong datatype") {
            val code = """
                import java.io.Serializable

                class C : Serializable {
                    companion object {
                        const val serialVersionUID = 1
                    }
                }
            """
            assertThat(subject.compileAndLint(code)).hasSize(1)
        }

        it("reports class with wrong explicitly defined datatype") {
            val code = """
                import java.io.Serializable

                class C : Serializable {
                    companion object {
                        const val serialVersionUID: Int = 1
                    }
                }
            """
            assertThat(subject.compileAndLint(code)).hasSize(1)
        }

        it("reports class with wrong naming and without const modifier") {
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
            """
            assertThat(subject.compileAndLint(code)).hasSize(2)
        }

        it("does not report a unserializable class") {
            val code = "class NoSerializableClass"
            assertThat(subject.compileAndLint(code)).isEmpty()
        }

        it("does not report an interface that implements Serializable") {
            val code = """
                import java.io.Serializable

                interface I : Serializable
            """
            assertThat(subject.compileAndLint(code)).isEmpty()
        }

        it("does not report UID constant with positive value") {
            val code = """
                import java.io.Serializable

                class C : Serializable {
                    companion object {
                        const val serialVersionUID = 1L
                    }
                }
            """
            assertThat(subject.compileAndLint(code)).isEmpty()
        }

        it("does not report UID constant with negative value") {
            val code = """
                import java.io.Serializable

                class C : Serializable {
                    companion object {
                        const val serialVersionUID = -1L
                    }
                }
            """
            assertThat(subject.compileAndLint(code)).isEmpty()
        }

        it("does not report UID constant with explicit Long type") {
            val code = """
                import java.io.Serializable

                class C : Serializable {
                    companion object {
                        const val serialVersionUID: Long = 1
                    }
                }
            """
            assertThat(subject.compileAndLint(code)).isEmpty()
        }
    }
})
