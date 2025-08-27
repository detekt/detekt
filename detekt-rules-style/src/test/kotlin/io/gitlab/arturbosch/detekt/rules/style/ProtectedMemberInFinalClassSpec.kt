package io.gitlab.arturbosch.detekt.rules.style

import dev.detekt.api.Config
import dev.detekt.test.assertj.assertThat
import dev.detekt.test.lint
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class ProtectedMemberInFinalClassSpec {
    val subject = ProtectedMemberInFinalClass(Config.empty)

    @Nested
    inner class `check all variants of protected visibility modifier in final class` {

        @Test
        fun `reports a protected field in a final class`() {
            val code = """
                class Foo {
                    protected var i1 = 0
                }
            """.trimIndent()
            val findings = subject.lint(code)
            assertThat(findings).singleElement()
                .hasStartSourceLocation(2, 5)
        }

        @Test
        fun `reports a protected constructor in a final class`() {
            val code = """
                class Foo {
                    var i1: Int = 0
                    protected constructor(i1: Int) : super() {
                        this.i1 = i1
                    }
                }
            """.trimIndent()
            val findings = subject.lint(code)
            assertThat(findings).singleElement()
                .hasStartSourceLocation(3, 5)
        }

        @Test
        fun `reports a protected function in a final class`() {
            val code = """
                class Foo {
                    protected fun function() {}
                }
            """.trimIndent()
            val findings = subject.lint(code)
            assertThat(findings).singleElement()
                .hasStartSourceLocation(2, 5)
        }

        @Test
        fun `reports an inner class with a protected field in a final class`() {
            val code = """
                class Foo {
                    inner class InnerClass2 {
                        protected val i = 0
                    }
                }
            """.trimIndent()
            val findings = subject.lint(code)
            assertThat(findings).singleElement()
                .hasStartSourceLocation(3, 9)
        }

        @Test
        fun `reports a protected inner class with a protected field in a final class`() {
            val code = """
                class Fee {
                    protected inner class YetAnotherInnerClass {
                        protected val i = 0
                    }
                }
            """.trimIndent()
            val findings = subject.lint(code)
            assertThat(findings).satisfiesExactlyInAnyOrder(
                { assertThat(it).hasStartSourceLocation(2, 5) },
                { assertThat(it).hasStartSourceLocation(3, 9) },
            )
        }

        @Test
        fun `reports a protected companion object in a final class`() {
            val code = """
                class Foo {
                    protected companion object {
                        protected class A {
                            protected var x = 0
                        }
                    }
                }
            """.trimIndent()
            val findings = subject.lint(code)
            assertThat(findings).satisfiesExactlyInAnyOrder(
                { assertThat(it).hasStartSourceLocation(2, 5) },
                { assertThat(it).hasStartSourceLocation(2, 5) },
                { assertThat(it).hasStartSourceLocation(4, 13) },
            )
        }

        @Test
        fun `reports a protected companion object in an nested class`() {
            val code = """
                abstract class Foo {
                    protected companion object {
                        protected class A {
                            protected var x = 0
                        }
                    }
                }
            """.trimIndent()
            val findings = subject.lint(code)
            assertThat(findings).singleElement()
                .hasStartSourceLocation(4, 13)
        }

        @Test
        fun `reports a protected field object in a final inner class`() {
            val code = """
                open class OpenClass {
                    inner class InnerClass {
                        protected val i = 0
                    }
                }
            """.trimIndent()
            val findings = subject.lint(code)
            assertThat(findings).singleElement()
                .hasStartSourceLocation(3, 9)
        }

        @Test
        fun `reports a protected primary constructor in a final class`() {
            val code = """
                class FinalClassWithProtectedConstructor protected constructor()
            """.trimIndent()
            val findings = subject.lint(code)
            assertThat(findings).singleElement()
                .hasStartSourceLocation(1, 42)
        }

        @Test
        fun `reports a protected method named finalize if id does not match JVM signuatre in a final class`() {
            val code = """
                class MyFinalizable {
                     protected fun finalize(parameter: String) { // note parameters are not empty
                     
                     }               
                }
            """.trimIndent()
            val findings = subject.lint(code)
            assertThat(findings).singleElement()
                .hasStartSourceLocation(2, 6)
        }

        @Test
        fun `reports a protected property named finalize in a final class`() {
            val code = """
                class MyFinalizable {
                     protected val finalize get() = "hello world"         
                }
            """.trimIndent()
            val findings = subject.lint(code)
            assertThat(findings).singleElement()
                .hasStartSourceLocation(2, 6)
        }
    }

    @Nested
    inner class `check valid occurrences of protected that should not be reported` {

        @Test
        fun `does not report non-protected members in final class`() {
            val code = """
                abstract class BaseClass
                class Foo : BaseClass() {
                    private val i = 0
                }
            """.trimIndent()
            assertThat(subject.lint(code)).isEmpty()
        }

        @Test
        fun `does not report overridden fields`() {
            val code = """
                abstract class BaseClass {
                    protected abstract val abstractProp : Int
                }
                class Foo : BaseClass() {
                    // should not report protected = private visibility
                    protected override val abstractProp = 0
                }
            """.trimIndent()
            assertThat(subject.lint(code)).isEmpty()
        }

        @Test
        fun `does not report overridden functions`() {
            val code = """
                abstract class BaseClass {
                    protected abstract fun abstractFunction()
                }
                class Foo : BaseClass() {
                    // should not report protected = private visibility
                    protected override fun abstractFunction() {
                    }
                }
            """.trimIndent()
            assertThat(subject.lint(code)).isEmpty()
        }

        @Test
        fun `does not report protected definitions in abstract class`() {
            val code = """
                abstract class BaseClass {
                    protected abstract val abstractProp: Int
                    protected abstract fun abstractFunction()
                
                    protected object InnerObject
                }
            """.trimIndent()
            assertThat(subject.lint(code)).isEmpty()
        }

        @Test
        fun `does not report protected definitions in sealed class`() {
            val code = """
                sealed class SealedClass {
                    protected fun a() {}
                }
            """.trimIndent()
            assertThat(subject.lint(code)).isEmpty()
        }

        @Test
        fun `does not report protected definitions in enum class`() {
            val code = """
                enum class EnumClass {
                    ;
                    protected fun foo() {}
                }
            """.trimIndent()
            assertThat(subject.lint(code)).isEmpty()
        }

        @Test
        fun `does not report protected definitions of finalize method`() {
            val code = """
                class MyFinalizable {
                     protected fun finalize() {
                     
                     }               
                }
            """.trimIndent()
            assertThat(subject.lint(code)).isEmpty()
        }
    }
}
