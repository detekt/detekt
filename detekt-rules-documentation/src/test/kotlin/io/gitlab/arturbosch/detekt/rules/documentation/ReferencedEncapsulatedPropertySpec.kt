package io.gitlab.arturbosch.detekt.rules.documentation

import io.gitlab.arturbosch.detekt.test.compileAndLint
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class ReferencedEncapsulatedPropertySpec {
    val subject = ReferencedEncapsulatedProperty()

    @Nested
    inner class `ReferencedEncapsulatedProperty rule` {

        @Test
        fun `reports referenced encapsulated properties`() {
            val code = """ 
            /**
             * Comment
             * [prop1] - encapsulated property
             * [prop2] - public property
             */
            class Test {
                private val prop1 = 0 // report
                val prop2 = 0 // do not report
            }
            """.trimIndent()
            assertThat(subject.compileAndLint(code)).hasSize(1)
        }

        @Test
        fun `reports referenced encapsulated properties in private class`() {
            val code = """ 
            /**
             * Comment
             * [prop1] - encapsulated property
             * [prop2] - public property
             */
            private class Test {
                private val prop1 = 0 // report
                val prop2 = 0 // do not report
            }
            """.trimIndent()
            assertThat(subject.compileAndLint(code)).hasSize(1)
        }

        @Test
        fun `reports referenced encapsulated properties in nested objects`() {
            val code = """ 
            /**
             * Comment
             * [prop1] - encapsulated property
             * [A.prop2] - encapsulated property
             * [A.B.prop3] - encapsulated property
             * [A.C.prop4] - encapsulated property
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
            class Test {
                val prop1 = 0
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
