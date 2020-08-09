package io.gitlab.arturbosch.detekt.rules.naming

import io.gitlab.arturbosch.detekt.test.TestConfig
import io.gitlab.arturbosch.detekt.test.assertThat
import io.gitlab.arturbosch.detekt.test.compileAndLint
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

class ObjectPropertyNamingSpec : Spek({

    describe("constants in object declarations") {

        val subject by memoized { ObjectPropertyNaming() }

        it("should not detect public constants complying to the naming rules") {
            val code = """
                object O {
                    ${PublicConst.negative}
                }
            """
            assertThat(subject.compileAndLint(code)).isEmpty()
        }

        it("should detect public constants not complying to the naming rules") {
            val code = """
                object O {
                    ${PublicConst.positive}
                }
            """
            assertThat(subject.compileAndLint(code)).hasSize(1)
        }

        it("should not detect private constants complying to the naming rules") {
            val code = """
                object O {
                    ${PrivateConst.negative}
                }
            """
            assertThat(subject.compileAndLint(code)).isEmpty()
        }

        it("should detect private constants not complying to the naming rules") {
            val code = """
                object O {
                    ${PrivateConst.positive}
                }
            """
            assertThat(subject.compileAndLint(code)).hasSize(1)
        }

        it("should report constants not complying to the naming rules at the right position") {
            val code = """
                object O {
                    ${PublicConst.positive}
                }
            """
            assertThat(subject.compileAndLint(code)).hasSourceLocation(2, 15)
        }
    }

    describe("constants in companion object") {

        val subject by memoized { ObjectPropertyNaming() }

        it("should not detect public constants complying to the naming rules") {
            val code = """
                class C {
                    companion object {
                        ${PublicConst.negative}
                    }
                }
            """
            assertThat(subject.compileAndLint(code)).isEmpty()
        }

        it("should detect public constants not complying to the naming rules") {
            val code = """
                class C {
                    companion object {
                        ${PublicConst.positive}
                    }
                }
            """
            assertThat(subject.compileAndLint(code)).hasSize(1)
        }

        it("should not detect private constants complying to the naming rules") {
            val code = """
                class C {
                    companion object {
                        ${PrivateConst.negative}
                    }
                }
            """
            assertThat(subject.compileAndLint(code)).isEmpty()
        }

        it("should detect private constants not complying to the naming rules") {
            val code = """
                class C {
                    companion object {
                        ${PrivateConst.positive}
                    }
                }
            """
            assertThat(subject.compileAndLint(code)).hasSize(1)
        }

        it("should report constants not complying to the naming rules at the right position") {
            val code = """
                class C {
                    companion object {
                        ${PublicConst.positive}
                    }
                }
            """
            assertThat(subject.compileAndLint(code)).hasSourceLocation(3, 19)
        }
    }

    describe("variables in objects") {

        val subject by memoized { ObjectPropertyNaming() }

        it("should not detect public variables complying to the naming rules") {
            val code = """
                object O {
                    ${PublicVal.negative}
                }
            """
            assertThat(subject.compileAndLint(code)).isEmpty()
        }

        it("should detect public variables not complying to the naming rules") {
            val code = """
                object O {
                    ${PublicVal.positive}
                }
            """
            assertThat(subject.compileAndLint(code)).hasSize(1)
        }

        it("should not detect private variables complying to the naming rules") {
            val code = """
                object O {
                    ${PrivateVal.negative}
                }
            """
            assertThat(subject.compileAndLint(code)).isEmpty()
        }

        it("should detect private variables not complying to the naming rules") {
            val code = """
                object O {
                    private val __NAME = "Artur"
                }
            """
            assertThat(subject.compileAndLint(code)).hasSize(1)
        }
    }

    describe("variables and constants in objects with custom config") {

        val config by memoized {
            TestConfig(mapOf(
                ObjectPropertyNaming.CONSTANT_PATTERN to "_[A-Za-z]*",
                ObjectPropertyNaming.PRIVATE_PROPERTY_PATTERN to ".*"
            ))
        }
        val subject by memoized { ObjectPropertyNaming(config) }

        it("should not detect constants in object with underscores") {
            val code = """
                object O {
                    const val _NAME = "Artur"
                    const val _name = "Artur"
                }
            """
            assertThat(subject.compileAndLint(code)).isEmpty()
        }

        it("should not detect private properties in object") {
            val code = """
                object O {
                    private val __NAME = "Artur"
                    private val _1234 = "Artur"
                }
            """
            assertThat(subject.compileAndLint(code)).isEmpty()
        }
    }

    describe("local properties") {
        it("should not detect local properties") {
            val config = TestConfig(mapOf(
                ObjectPropertyNaming.PROPERTY_PATTERN to "valid"
            ))
            val subject = ObjectPropertyNaming(config)

            val code = """
                object O {
                    fun foo() {
                        val somethingElse = 1
                    }
                }
            """

            assertThat(subject.compileAndLint(code)).isEmpty()
        }
    }
})

@Suppress("UnnecessaryAbstractClass")
abstract class NamingSnippet(private val isPrivate: Boolean, private val isConst: Boolean) {

    val negative = """
                    ${visibility()}${const()}val MY_NAME_8 = "Artur"
                    ${visibility()}${const()}val MYNAME = "Artur"
                    ${visibility()}${const()}val MyNAME = "Artur"
                    ${visibility()}${const()}val name = "Artur"
                    ${visibility()}${const()}val nAme = "Artur"
                    ${visibility()}${const()}val serialVersionUID = 42L"""
    val positive = """${visibility()}${const()}val _nAme = "Artur""""

    private fun visibility() = if (isPrivate) "private " else ""
    private fun const() = if (isConst) "const " else ""
}

object PrivateConst : NamingSnippet(true, true)
object PublicConst : NamingSnippet(false, true)
object PrivateVal : NamingSnippet(true, false)
object PublicVal : NamingSnippet(false, false)
