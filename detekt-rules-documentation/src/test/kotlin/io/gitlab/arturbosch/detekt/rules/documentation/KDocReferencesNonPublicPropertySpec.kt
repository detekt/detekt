package io.gitlab.arturbosch.detekt.rules.documentation

import io.gitlab.arturbosch.detekt.test.compileAndLint
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class KDocReferencesNonPublicPropertySpec {
    val subject = KDocReferencesNonPublicProperty()

    inner class `KDocReferencesNonPublicProperty rule` {

        @Test
        fun `reports referenced non-public properties`() {
            val code = """ 
            /**
             * Comment
             * [prop1] - non-public property
             * [prop2] - public property
             */
            class Test {
                private val prop1 = 0
                val prop2 = 0
            }
            """.trimIndent()
            assertThat(subject.compileAndLint(code)).hasSize(1)
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
    }
}
