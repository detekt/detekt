package io.gitlab.arturbosch.detekt.rules.documentation

import io.gitlab.arturbosch.detekt.test.compileAndLint
import org.assertj.core.api.Assertions.assertThat
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

class UndocumentedPublicPropertySpec : Spek({
    val subject by memoized { UndocumentedPublicProperty() }

    describe("UndocumentedPublicProperty rule") {

        it("reports undocumented public property") {
            val code = "val a = 1"
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
                    * Comment
                    */
                    val a = 1
                }
            """
            assertThat(subject.compileAndLint(code)).isEmpty()
        }

        it("does not report undocumented, public and overridden property in class") {
            val code = """
                interface I {
                    /**
                     * Comment
                     */
                    val a: Int
                }
                
                class Test : I {
                    override val a = 1
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
                * @property a int1
                * [b] int2
                * @property [c] int3
                * @param d int4
                */
                class Test(val a: Int, val b: Int, val c: Int, val d: Int)
            """
            assertThat(subject.compileAndLint(code)).isEmpty()
        }

        describe("public properties in nested classes") {

            it("reports undocumented public properties in nested classes") {
                val code = """
                class Outer { 
                    class Inner {
                        val i = 0
                        
                        class InnerInner {
                            val ii = 0
                        }
                    }
                }
            """
                assertThat(subject.compileAndLint(code)).hasSize(2)
            }

            it("reports undocumented public properties in inner classes") {
                val code = """
                class Outer {
                    inner class Inner {
                        val i = 0
                    }
                }
            """
                assertThat(subject.compileAndLint(code)).hasSize(1)
            }

            it("reports undocumented public properties inside objects") {
                val code = """
                object Outer {
                    class Inner {
                        val i = 0
                    }
                }
            """
                assertThat(subject.compileAndLint(code)).hasSize(1)
            }

            it("does not report undocumented and non-public properties in nested classes") {
                val code = """
                internal class Outer {
                    class Inner {
                        val i = 0
                    }
                }
            """
                assertThat(subject.compileAndLint(code)).isEmpty()
            }

            it("does not report undocumented and non-public properties in inner classes") {
                val code = """
                internal class Outer {
                    inner class Inner {
                        val i = 0
                    }
                }
            """
                assertThat(subject.compileAndLint(code)).isEmpty()
            }
        }

        describe("public properties in primary constructors inside nested classes") {

            it("reports undocumented public properties in nested classes") {
                val code = """
                class Outer(val a: Int) {
                    class Inner(val b: Int) {
                        class InnerInner(val c: Int)
                    }
                }
            """
                assertThat(subject.compileAndLint(code)).hasSize(3)
            }

            it("reports undocumented public properties in inner classes") {
                val code = """
                class Outer(val a: Int) {
                    inner class Inner(val b: Int)
                }
            """
                assertThat(subject.compileAndLint(code)).hasSize(2)
            }

            it("reports undocumented public properties inside objects") {
                val code = """
                object Outer {
                    class Inner(val a: Int)
                }
            """
                assertThat(subject.compileAndLint(code)).hasSize(1)
            }

            it("does not report undocumented and non-public properties in nested classes") {
                val code = """
                internal class Outer(val a: Int) {
                    class Inner(val b: Int)
                }
            """
                assertThat(subject.compileAndLint(code)).isEmpty()
            }

            it("does not report undocumented and non-public properties in inner classes") {
                val code = """
                internal class Outer(val a: Int) {
                    inner class Inner(val b: Int)
                }
            """
                assertThat(subject.compileAndLint(code)).isEmpty()
            }
        }
    }
})
