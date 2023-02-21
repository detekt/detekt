package io.gitlab.arturbosch.detekt.rules.documentation

import io.gitlab.arturbosch.detekt.test.assertThat
import io.gitlab.arturbosch.detekt.test.compileAndLint
import org.junit.jupiter.api.Test

class KDocReferencesNonPublicPropertySpec {
    val subject = KDocReferencesNonPublicProperty()

    @Test
    fun `reports referenced non-public properties`() {
        val code = """
            /**
             * Comment
             * [prop1] - non-public property
             * [prop2] - public property
             */
            class Test {
                private val nonReferencedProp = 0
                private val prop1 = 0
                val prop2 = 0
            }
        """.trimIndent()
        assertThat(subject.compileAndLint(code)).hasSize(1)
    }

    @Test
    fun `does not report referenced non-public properties in primary constructor`() {
        val code = """
            /**
             * Comment
             * [prop1] - non-public property
             * [prop2] - public property
             */
            class Test(
                private val nonReferencedProp: Int = 0,
                private val prop1: Int = 0,
                val prop2: Int = 0,
            )
        """.trimIndent()
        assertThat(subject.compileAndLint(code)).isEmpty()
    }

    @Test
    fun `reports referenced non-public properties in private class`() {
        val code = """
            /**
             * Comment
             * [prop1] - non-public property
             * [prop2] - public property
             */
            private class Test {
                private val nonReferencedProp = 0
                private val prop1 = 0
                val prop2 = 0
            }
        """.trimIndent()
        assertThat(subject.compileAndLint(code)).hasSize(1)
    }

    @Test
    fun `reports referenced non-public properties in nested objects`() {
        val code = """
            /**
             * Comment
             * [prop1] - non-public property
             * [A.prop2] - non-public property
             * [A.B.prop3] - non-public property
             * [A.C.prop4] - non-public property
             */
            class Test {
                private val prop1 = 0
            
                object A {
                    private val nonReferencedProp = 0
                    private val prop2 = 0
            
                    private object B {
                        val prop3 = 0
                    }
                    object C {
                        private val prop4 = 0
                    }
                }
            }
        """.trimIndent()
        assertThat(subject.compileAndLint(code)).hasSize(4)
    }

    @Test
    fun `does not report properties with no KDoc`() {
        val code = """
            class Test {
                private val prop1 = 0
                val prop2 = 0
            }
        """.trimIndent()
        assertThat(subject.compileAndLint(code)).isEmpty()
    }

    @Test
    fun `does not report properties with empty comments`() {
        val code = """
            /**
             */
            class Test {
                private val prop1 = 0
                val prop2 = 0
            }
        """.trimIndent()
        assertThat(subject.compileAndLint(code)).isEmpty()
    }

    @Test
    fun `does not report properties not enclosed in a class`() {
        val code = """
            /**
             * [prop1]
             * [prop2]
             */
            private val prop1 = 0
            val prop2 = 0
        """.trimIndent()
        assertThat(subject.compileAndLint(code)).isEmpty()
    }

    @Test
    fun `does not report referenced public properties in nested objects`() {
        val code = """
            /**
             * Comment
             * [prop1] - public property
             * [A.B.prop2] - public property
             * [C.prop3] - public property
             */
            open class Test {
                protected val prop1 = 0
                object A {
                    object B {
                        val nonReferencedProp = 0
                        val prop2 = 0
                    }
                }
                object C {
                    val prop3 = 0
                }
            }
        """.trimIndent()
        assertThat(subject.compileAndLint(code)).isEmpty()
    }

    @Test
    fun `does not report local variable with the same name as a public property`() {
        val code = """
            /**
             * [prop] - public property
             */
            class Test {
                val prop: String = ""
            
                fun foo(): Int {
                    val prop = 0
                    return prop
                }
            
                companion object {
                    fun foo(): Int {
                        val prop = 0
                        return prop
                    }
                }
            }
        """.trimIndent()
        assertThat(subject.compileAndLint(code)).isEmpty()
    }

    @Test
    fun `does not report local variable with the same name as a public constructor property`() {
        val code = """
            /**
             * [prop] - public property
             */
            class Test(val prop: String) {
                fun foo(): Int {
                    val prop = 0
                    return prop
                }
            
                companion object {
                    fun foo(): Int {
                        val prop = 0
                        return prop
                    }
                }
            }
        """.trimIndent()
        assertThat(subject.compileAndLint(code)).isEmpty()
    }
}
