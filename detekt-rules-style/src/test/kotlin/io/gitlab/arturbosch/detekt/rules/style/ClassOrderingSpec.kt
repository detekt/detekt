package io.gitlab.arturbosch.detekt.rules.style

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.test.compileAndLint
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
            """.trimIndent()

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
            """.trimIndent()

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
            """.trimIndent()

            assertThat(subject.compileAndLint(code)).hasSize(1)
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
            """.trimIndent()

            assertThat(subject.compileAndLint(code)).hasSize(1)
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
            """.trimIndent()

            assertThat(subject.compileAndLint(code)).hasSize(1)
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
            """.trimIndent()

            assertThat(subject.compileAndLint(code)).hasSize(1)
        }
    }
})
