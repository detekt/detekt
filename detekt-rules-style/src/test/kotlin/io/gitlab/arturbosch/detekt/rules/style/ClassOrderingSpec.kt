package io.gitlab.arturbosch.detekt.rules.style

import io.github.detekt.test.utils.compileForTest
import io.github.detekt.test.utils.readResourceContent
import io.github.detekt.test.utils.resourceAsPath
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.test.TestConfig
import io.gitlab.arturbosch.detekt.test.compileAndLint
import io.gitlab.arturbosch.detekt.test.lint
import org.assertj.core.api.Assertions.assertThat
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

class ClassOrderingSpec : Spek({
    val subject by memoized { ClassOrdering(Config.empty) }

    describe("ClassOrdering rule") {

        it("does not report when class contents are in expected order with property first") {
            val code = """
                class InOrder(private val x: String) {
                    val y = x

                    init {
                        check(x == "yes")
                    }

                    constructor(z: Int): this(z.toString())

                    fun returnX() = x

                    companion object {
                        const val IMPORTANT_VALUE = 3
                    }
                }
            """

            assertThat(subject.compileAndLint(code)).isEmpty()
        }

        it("does not report when class contents are in expected order with class initializer first") {
            val code = """
                class InOrder(private val x: String) {
                    init {
                        check(x == "yes")
                    }

                    val y = x

                    constructor(z: Int): this(z.toString())

                    fun returnX() = x

                    companion object {
                        const val IMPORTANT_VALUE = 3
                    }
                }
            """

            assertThat(subject.compileAndLint(code)).isEmpty()
        }

        it("reports when class initializer block is out of order") {
            val code = """
                class OutOfOrder(private val x: String) {
                    val y = x

                    constructor(z: Int): this(z.toString())

                    init {
                        check(x == "yes")
                    }

                    fun returnX() = x

                    companion object {
                        const val IMPORTANT_VALUE = 3
                    }
                }
            """

            val findings = subject.compileAndLint(code)
            assertThat(findings).hasSize(1)
            assertThat(findings[0].message).isEqualTo(
                "initializer blocks should be declared before secondary constructors."
            )
        }

        it("reports when secondary constructor is out of order") {
            val code = """
                class OutOfOrder(private val x: String) {
                    constructor(z: Int): this(z.toString())

                    val y = x

                    init {
                        check(x == "yes")
                    }

                    fun returnX() = x

                    companion object {
                        const val IMPORTANT_VALUE = 3
                    }
                }
            """

            val findings = subject.compileAndLint(code)
            assertThat(findings).hasSize(2)
            assertThat(findings[0].message).isEqualTo(
                "property `y` should be declared before secondary constructors."
            )
            assertThat(findings[1].message).isEqualTo(
                "initializer blocks should be declared before secondary constructors."
            )
        }

        it("reports when method is out of order") {
            val code = """
                class OutOfOrder(private val x: String) {
                    fun returnX() = x

                    val y = x

                    init {
                        check(x == "yes")
                    }

                    constructor(z: Int): this(z.toString())

                    companion object {
                        const val IMPORTANT_VALUE = 3
                    }
                }
            """

            val findings = subject.compileAndLint(code)
            assertThat(findings).hasSize(3)
            assertThat(findings[0].message)
                .isEqualTo("property `y` should be declared before method declarations.")
            assertThat(findings[1].message)
                .isEqualTo("initializer blocks should be declared before method declarations.")
            assertThat(findings[2].message)
                .isEqualTo("secondary constructor should be declared before method declarations.")
        }

        it("reports when companion object is out of order") {
            val code = """
                class OutOfOrder(private val x: String) {
                    val y = x

                    init {
                        check(x == "yes")
                    }

                    constructor(z: Int): this(z.toString())

                    companion object {
                        const val IMPORTANT_VALUE = 3
                    }

                    fun returnX() = x
                }
            """

            val findings = subject.compileAndLint(code)
            assertThat(findings).hasSize(1)
            assertThat(findings[0].message).isEqualTo("method `returnX()` should be declared before companion object.")
        }

        it("does not report nested class order") {
            val code = """
                class OutOfOrder(private val x: String) {
                    val y = x

                    init {
                        check(x == "yes")
                    }

                    constructor(z: Int): this(z.toString())

                    class Nested {
                        fun foo() = 2
                    }

                    fun returnX() = x
                }
            """

            assertThat(subject.compileAndLint(code)).hasSize(0)
        }

        it("does not report anonymous object order") {
            val code = """
                class OutOfOrder(private val x: String) {
                    val y = x

                    init {
                        check(x == "yes")
                    }

                    constructor(z: Int): this(z.toString())

                    object AnonymousObject {
                        fun foo() = 2
                    }

                    fun returnX() = x
                }
            """

            assertThat(subject.compileAndLint(code)).hasSize(0)
        }

        it("report all issues with interleaving nested class") {
            val code = """
                class MultipleMisorders(private val x: String) {
                    companion object {
                        const val IMPORTANT_VALUE = 3
                    }

                    class Nested { }

                    fun returnX() = x

                    class Nested2 { }

                    constructor(z: Int): this(z.toString())

                    class Nested3 { }
                    
                    val y = x
                }
            """

            val findings = subject.compileAndLint(code)
            assertThat(findings).hasSize(3)
            assertThat(findings[0].message)
                .isEqualTo("method `returnX()` should be declared before companion object.")
            assertThat(findings[1].message)
                .isEqualTo("secondary constructor should be declared before companion object.")
            assertThat(findings[2].message)
                .isEqualTo("property `y` should be declared before companion object.")
        }

        it("does report all issues in a class with multiple misorderings") {
            val code = """
                class MultipleMisorders(private val x: String) {
                    companion object {
                        const val IMPORTANT_VALUE = 3
                    }

                    fun returnX() = x

                    constructor(z: Int): this(z.toString())
                    
                    val y = x
                }
            """

            val findings = subject.compileAndLint(code)
            assertThat(findings).hasSize(3)
            assertThat(findings[0].message)
                .isEqualTo("method `returnX()` should be declared before companion object.")
            assertThat(findings[1].message)
                .isEqualTo("secondary constructor should be declared before companion object.")
            assertThat(findings[2].message)
                .isEqualTo("property `y` should be declared before companion object.")
        }

        it("auto corrects all violations") {
            val ktFile = compileForTest(resourceAsPath("ClassOrderingAutoCorrectible.kt"))

            // Perform autocorrection
            val findings = ClassOrdering(TestConfig(Config.AUTO_CORRECT_KEY to true)).lint(ktFile)
            assertThat(findings).hasSize(4)

            // Verify autocorrection output
            val actualContent = ktFile.text
            val expectedContent = readResourceContent("ClassOrderingAutoCorrected.kt")
            assertThat(actualContent).isEqualToIgnoringNewLines(expectedContent)

            // Ensure autocorrected code has no violation
            assertThat(subject.lint(ktFile)).isEmpty()
        }
    }
})
