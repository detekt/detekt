package io.gitlab.arturbosch.detekt.rules.documentation

import io.gitlab.arturbosch.detekt.test.TestConfig
import io.gitlab.arturbosch.detekt.test.compileAndLint
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class OutdatedDocumentationSpec {
    val subject = OutdatedDocumentation()

    @Nested
    inner class General {
        @Test
        fun `should not report when doc is missing`() {
            val withoutDoc = """
            class MyClass(someParam: String, val someProp: String)
            """.trimIndent()
            assertThat(subject.compileAndLint(withoutDoc)).isEmpty()
        }

        @Test
        fun `should not report when doc does not contain any property or param tags`() {
            val docWithoutParamAndPropertyTags = """
            /**
             * Some class description without referring to tags or properties
             */
            class MyClass(someParam: String, val someProp: String)
            """.trimIndent()
            assertThat(subject.compileAndLint(docWithoutParamAndPropertyTags)).isEmpty()
        }
    }

    @Nested
    inner class `class` {
        @Test
        fun `should not report when doc match class params`() {
            val correctParam = """
            /**
             * @param someParam Description of param
             */
            class MyClass(someParam: String)
            """.trimIndent()
            assertThat(subject.compileAndLint(correctParam)).isEmpty()
        }

        @Test
        fun `should report when doc mismatch class param name`() {
            val incorrectParamName = """
            /**
             * @param someParam Description of param
             */
            class MyClass(otherParam: String)
            """.trimIndent()
            assertThat(subject.compileAndLint(incorrectParamName)).hasSize(1)
        }

        @Test
        fun `should report when doc mismatch class param list`() {
            val incorrectListOfParams = """
            /**
             * @param someParam Description of param
             * @param someSecondParam Description of param
             */
            class MyClass(someParam: String)
            """.trimIndent()
            assertThat(subject.compileAndLint(incorrectListOfParams)).hasSize(1)
        }

        @Test
        fun `should report when doc mismatch class param list order`() {
            val incorrectParamOrder = """
            /**
             * @param someParam Description of param
             * @param otherParam Description of param
             */
            class MyClass(otherParam: String, someParam: String)
            """.trimIndent()
            assertThat(subject.compileAndLint(incorrectParamOrder)).hasSize(1)
        }

        @Test
        fun `should not report when doc match class params and props`() {
            val correctParamAndProp = """
            /**
             * @param someParam Description of param
             * @property someProp Description of property
             */
            class MyClass(someParam: String, val someProp: String)
            """.trimIndent()
            assertThat(subject.compileAndLint(correctParamAndProp)).isEmpty()
        }

        @Test
        fun `should report when doc match class params but mismatch props`() {
            val correctParamIncorrectProp = """
            /**
             * @param someParam Description of param
             * @property someProp Description of property
             */
            class MyClass(someParam: String, val otherProp: String)
            """.trimIndent()
            assertThat(subject.compileAndLint(correctParamIncorrectProp)).hasSize(1)
        }

        @Test
        fun `should report when doc mismatch class params and match props`() {
            val incorrectParamCorrectProp = """
            /**
             * @param someParam Description of param
             * @property someProp Description of property
             */
            class MyClass(otherParam: String, val someProp: String)
            """.trimIndent()
            assertThat(subject.compileAndLint(incorrectParamCorrectProp)).hasSize(1)
        }

        @Test
        fun `should report when doc for constructor is incorrect`() {
            val incorrectConstructorDoc = """
            class MyClass {
                /**
                 * @param someParam
                 */
                constructor(otherParam: String)
            }
            """.trimIndent()
            assertThat(subject.compileAndLint(incorrectConstructorDoc)).hasSize(1)
        }

        @Test
        fun `should report when property is documented as param`() {
            val propertyAsParam = """
                /**
                 * @property someParam Description of param
                 * @param someProp Description of property
                 */
                class MyClass(someParam: String, val someProp: String)
            """.trimIndent()
            assertThat(subject.compileAndLint(propertyAsParam)).hasSize(1)
        }

        @Test
        fun `should report when declarations order is incorrect`() {
            val incorrectDeclarationsOrder = """
            /**
             * @property someProp Description of property
             * @param someParam Description of param
             */
            class MyClass(someParam: String, val someProp: String)
            """.trimIndent()
            assertThat(subject.compileAndLint(incorrectDeclarationsOrder)).hasSize(1)
        }
    }

    @Nested
    inner class `class with type params` {

        @Test
        fun `should not report when doc match class params`() {
            val correctTypeParam = """
            /**
             * @param T Description of type param
             * @param someParam Description of param
             */
            class MyClass<T>(someParam: String)
            """.trimIndent()
            assertThat(subject.compileAndLint(correctTypeParam)).isEmpty()
        }

        @Test
        fun `should not report when doc match class params and no primary constructor`() {
            val correctTypeParam = """
            /**
             * Some description
             * @param T Description of type param
             */
            class MyClass<T>
            """.trimIndent()
            assertThat(subject.compileAndLint(correctTypeParam)).isEmpty()
        }

        @Test
        fun `should report when doc misses type param`() {
            val missingTypeParam = """
            /**
             * @param someParam Description of param
             */
            class MyClass<T>(someParam: String)
            """.trimIndent()
            assertThat(subject.compileAndLint(missingTypeParam)).hasSize(1)
        }

        @Test
        fun `should report when doc mismatch type param name`() {
            val incorrectTypeParamName = """
            /**
             * @param S Description of type param
             * @param someParam Description of param
             */
            class MyClass<T>(someParam: String)
            """.trimIndent()
            assertThat(subject.compileAndLint(incorrectTypeParamName)).hasSize(1)
        }

        @Test
        fun `should report when doc mismatch type param list`() {
            val incorrectTypeParamList = """
            /**
             * @param T Description of type param
             * @param someParam Description of param
             */
            class MyClass<T, S>(someParam: String)
            """.trimIndent()
            assertThat(subject.compileAndLint(incorrectTypeParamList)).hasSize(1)
        }
    }

    @Nested
    inner class Function {

        @Test
        fun `should not report when doc match function params`() {
            val correctDoc = """
            /**
             * @param someParam Description of param
             */
            fun myFun(someParam: String) {}
            """.trimIndent()
            assertThat(subject.compileAndLint(correctDoc)).isEmpty()
        }

        @Test
        fun `should report when doc mismatch function param name`() {
            val incorrectParamName = """
            /**
             * @param someParam Description of param
             */
            fun myFun(otherParam: String) {}
            """.trimIndent()
            assertThat(subject.compileAndLint(incorrectParamName)).hasSize(1)
        }
    }

    @Nested
    inner class `function with type params` {

        @Test
        fun `should not report when doc match function params`() {
            val correctTypeParam = """
            /**
             * @param T Description of type param
             * @param someParam Description of param
             */
            fun <T> myFun(someParam: String) {}
            """.trimIndent()
            assertThat(subject.compileAndLint(correctTypeParam)).isEmpty()
        }

        @Test
        fun `should report when doc misses type param`() {
            val missingTypeParam = """
            /**
             * @param someParam Description of param
             */
            fun <T> myFun(someParam: String) {}
            """.trimIndent()
            assertThat(subject.compileAndLint(missingTypeParam)).hasSize(1)
        }

        @Test
        fun `should report when doc mismatch type param name`() {
            val incorrectTypeParamName = """
            /**
             * @param S Description of type param
             * @param someParam Description of param
             */
            fun <T> myFun(someParam: String) {}
            """.trimIndent()
            assertThat(subject.compileAndLint(incorrectTypeParamName)).hasSize(1)
        }

        @Test
        fun `should report when doc mismatch type param list`() {
            val incorrectTypeParamList = """
            /**
             * @param T Description of type param
             * @param someParam Description of param
             */
            fun <T, S> myFun(someParam: String) {}
            """.trimIndent()
            assertThat(subject.compileAndLint(incorrectTypeParamList)).hasSize(1)
        }

        @Test
        fun `should report when not all type params are first declarations of doc`() {
            val incorrectTypeParamsOrder = """
                /**
                 * @param T Description of type param
                 * @param someParam Description of param
                 * @param S Description of type param
                 */
                fun <T, S> myFun(someParam: String) {}
            """.trimIndent()
            assertThat(subject.compileAndLint(incorrectTypeParamsOrder)).hasSize(1)
        }
    }

    @Nested
    inner class `advanced scenarios` {

        @Test
        fun `should not report when doc match all signatures`() {
            val correctClassWithFunction = """
            /**
             * @param someParam Description of param
             */
            class MyClass(someParam: String) {
                /**
                 * @param someParam Description of param 
                 */
                fun myFun(someParam: String) {}
            }
            """.trimIndent()
            assertThat(subject.compileAndLint(correctClassWithFunction)).isEmpty()
        }

        @Test
        fun `should report for every class and function with incorrect doc`() {
            val incorrectClassWithTwoIncorrectFunctions = """
            /**
             * @param someParam Description of param
             */
            class MyClass(val someProp: String) {
                /**
                 * @param someParam Description of param 
                 */
                fun myFun(someParam: String, someSecondParam: String) {}
                /**
                 * @param someParam Description of param 
                 */
                fun myOtherFun(otherParam: String) {}
                /**
                 * @param someParam Description of param 
                 */
                class MyNestedClass(otherParam: String)
            }
            """.trimIndent()
            assertThat(subject.compileAndLint(incorrectClassWithTwoIncorrectFunctions)).hasSize(4)
        }
    }

    @Nested
    inner class `configuration matchTypeParameters` {
        val configuredSubject =
            OutdatedDocumentation(TestConfig(mapOf("matchTypeParameters" to "false")))

        @Test
        fun `should not report when class type parameters mismatch and configuration is off`() {
            val incorrectClassTypeParams = """
            /**
             * @param someParam Description of param
             */
            class MyClass<T, S>(someParam: String)
            """.trimIndent()
            assertThat(configuredSubject.compileAndLint(incorrectClassTypeParams)).isEmpty()
        }

        @Test
        fun `should not report when function type parameters mismatch and configuration is off`() {
            val incorrectFunctionTypeParams = """
            /**
             * @param someParam Description of param
             */
            fun <T, S> myFun(someParam: String) {}
            """.trimIndent()
            assertThat(configuredSubject.compileAndLint(incorrectFunctionTypeParams)).isEmpty()
        }
    }

    @Nested
    inner class `configuration matchDeclarationsOrder` {
        val configuredSubject =
            OutdatedDocumentation(TestConfig(mapOf("matchDeclarationsOrder" to "false")))

        @Test
        fun `should not report when declarations order mismatch and configuration is off`() {
            val incorrectDeclarationsOrder = """
            /**
             * @param someParam Description of param
             * @param otherParam Description of param
             */
            class MyClass(otherParam: String, someParam: String)
            """.trimIndent()
            assertThat(configuredSubject.compileAndLint(incorrectDeclarationsOrder)).isEmpty()
        }

        @Test
        fun `should not report when declarations with types order mismatch and configuration is off`() {
            val incorrectDeclarationsOrderWithType = """
            /**
             * @param S Description of type param
             * @param someParam Description of param
             * @param otherParam Description of param
             * @param T Description of param
             */
            fun <T, S> myFun(otherParam: String, someParam: String) {}
            """.trimIndent()
            assertThat(configuredSubject.compileAndLint(incorrectDeclarationsOrderWithType)).isEmpty()
        }
    }

    @Nested
    inner class `configuration allowParamOnConstructorProperties` {
        val configuredSubject =
            OutdatedDocumentation(TestConfig(mapOf("allowParamOnConstructorProperties" to "true")))

        @Test
        fun `should not report when property is documented as param`() {
            val propertyAsParam = """
                /**
                 * @param someParam Description of param
                 * @param someProp Description of property
                 */
                class MyClass(someParam: String, val someProp: String)
            """.trimIndent()
            assertThat(configuredSubject.compileAndLint(propertyAsParam)).isEmpty()
        }

        @Test
        fun `should not report when property is documented as property`() {
            val propertyAsParam = """
                /**
                 * @param someParam Description of param
                 * @property someProp Description of property
                 */
                class MyClass(someParam: String, val someProp: String)
            """.trimIndent()
            assertThat(configuredSubject.compileAndLint(propertyAsParam)).isEmpty()
        }
    }

    @Nested
    inner class `configuration matchDeclarationsOrder and allowParamOnConstructorProperties` {
        val configuredSubject =
            OutdatedDocumentation(
                TestConfig(
                    mapOf(
                        "matchDeclarationsOrder" to "false",
                        "allowParamOnConstructorProperties" to "true"
                    )
                )
            )

        @Test
        fun `should not report when property is documented as param`() {
            val propertyAsParam = """
                /**
                 * @param someParam Description of param
                 * @param someProp Description of property
                 */
                class MyClass(someParam: String, val someProp: String)
            """.trimIndent()
            assertThat(configuredSubject.compileAndLint(propertyAsParam)).isEmpty()
        }

        @Test
        fun `should not report when property is documented as property`() {
            val propertyAsParam = """
                /**
                 * @param someParam Description of param
                 * @property someProp Description of property
                 */
                class MyClass(someParam: String, val someProp: String)
            """.trimIndent()
            assertThat(configuredSubject.compileAndLint(propertyAsParam)).isEmpty()
        }
    }
}
