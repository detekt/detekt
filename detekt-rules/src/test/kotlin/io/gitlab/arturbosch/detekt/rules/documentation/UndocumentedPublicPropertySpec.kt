package io.gitlab.arturbosch.detekt.rules.documentation

import io.gitlab.arturbosch.detekt.test.compileAndLint
import org.assertj.core.api.Assertions.assertThat
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

class UndocumentedPublicPropertySpec : Spek({
    val subject by memoized { UndocumentedPublicProperty() }

    describe("UndocumentedPublicProperty rule") {

        it("reports undocumented public property") {
            val code = """
                val a = 1
            """
            assertThat(subject.compileAndLint(code)).hasSize(1)
        }

        it("reports undocumented public properties in companion object") {
            val code = """
                class Test {
                    companion object {
                        val a = 1
                        public val b = 1
                    }
                }
            """
            assertThat(subject.compileAndLint(code)).hasSize(2)
        }

        it("reports undocumented public properties in a primary constructor") {
            val code = "class Test(val a: Int)"
            assertThat(subject.compileAndLint(code)).hasSize(1)
        }

        it("reports undocumented public property in a primary constructor") {
            val code = "/* comment */ class Test(val a: Int)"
            assertThat(subject.compileAndLint(code)).hasSize(1)
        }

        it("does not report documented public property") {
            val code = """
                /**
                 * Comment
                 */
                val a = 1
            """
            assertThat(subject.compileAndLint(code)).isEmpty()
        }

        it("does not report documented public property in class") {
            val code = """
                class Test {
                    /**
                    *
                    */
                    val a = 1
                }
            """
            assertThat(subject.compileAndLint(code)).isEmpty()
        }

        it("does not report undocumented internal and private property") {
            val code = """
                class Test {
                    internal val a = 1
                    private val b = 1
                }
            """
            assertThat(subject.compileAndLint(code)).isEmpty()
        }

        it("does not report local variables") {
            val code = """
                fun commented(x: Int) {
                    var a = x
                }
            """
            assertThat(subject.compileAndLint(code)).isEmpty()
        }

        it("does not report public properties in internal class") {
            val code = """
                internal class NoComments {
                    public val a = 1
                    val b = 1
                }
            """
            assertThat(subject.compileAndLint(code)).isEmpty()
        }

        it("does not report public properties in private class") {
            val code = """
                private class NoComments {
                    public val a = 1
                    val b = 1
                }
            """
            assertThat(subject.compileAndLint(code)).isEmpty()
        }

        it("does not report properties in a secondary constructor") {
            val code = """
                class Test() {
                    constructor(a: Int) : this()
                }
            """
            assertThat(subject.compileAndLint(code)).isEmpty()
        }

        it("does not report undocumented non-public properties in a primary constructor") {
            val code = """
                class Test1(internal val a: Int)
                class Test2(b: Int)
            """
            assertThat(subject.compileAndLint(code)).isEmpty()
        }

        it("does not report undocumented public properties in a primary constructor for an internal class") {
            val code = "internal class Test(val a: Int)"
            assertThat(subject.compileAndLint(code)).isEmpty()
        }

        it("does not report documented public properties in a primary constructor") {
            val code = """
                /**
                * @a int1
                * [b] int2 
                */
                class Test(val a: Int, val b: Int)
            """
            assertThat(subject.compileAndLint(code)).isEmpty()
        }
    }
})
