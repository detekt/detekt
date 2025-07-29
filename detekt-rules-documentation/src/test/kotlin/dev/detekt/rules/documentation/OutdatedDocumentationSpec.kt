package dev.detekt.rules.documentation

import dev.detekt.api.Config
import io.gitlab.arturbosch.detekt.test.TestConfig
import io.gitlab.arturbosch.detekt.test.lint
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class OutdatedDocumentationSpec {
    val subject = OutdatedDocumentation(Config.empty)

    @Nested
    inner class General {
        @Test
        fun `should not report when doc is missing`() {
            val withoutDoc = """
                class MyClass(someParam: String, val someProp: String)
            """.trimIndent()
            assertThat(subject.lint(withoutDoc)).isEmpty()
        }

        @Test
        fun `should not report when doc does not contain any property or param tags`() {
            val docWithoutParamAndPropertyTags = """
                /**
                 * Some class description without referring to tags or properties
                 */
                class MyClass(someParam: String, val someProp: String)
            """.trimIndent()
            assertThat(subject.lint(docWithoutParamAndPropertyTags)).isEmpty()
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
            assertThat(subject.lint(correctParam)).isEmpty()
        }

        @Test
        fun `should report when doc mismatch class param name`() {
            val incorrectParamName = """
                /**
                 * @param someParam Description of param
                 */
                class MyClass(otherParam: String)
            """.trimIndent()
            assertThat(subject.lint(incorrectParamName)).hasSize(1)
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
            assertThat(subject.lint(incorrectListOfParams)).hasSize(1)
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
            assertThat(subject.lint(incorrectParamOrder)).hasSize(1)
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
            assertThat(subject.lint(correctParamAndProp)).isEmpty()
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
            assertThat(subject.lint(correctParamIncorrectProp)).hasSize(1)
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
            assertThat(subject.lint(incorrectParamCorrectProp)).hasSize(1)
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
            assertThat(subject.lint(incorrectConstructorDoc)).hasSize(1)
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
            assertThat(subject.lint(propertyAsParam)).hasSize(1)
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
            assertThat(subject.lint(incorrectDeclarationsOrder)).hasSize(1)
        }

        @Test
        fun `should not report when only public property is documented in internal constructor`() {
            val incorrectDeclarationsOrder = """
                /**
                 * Doc
                 * @property b desc
                 */
                class A internal constructor(val b: String)
            """.trimIndent()
            assertThat(subject.lint(incorrectDeclarationsOrder)).isEmpty()
        }

        @Test
        fun `should not report when only public property is documented in private constructor`() {
            val incorrectDeclarationsOrder = """
                /**
                 * Doc
                 * @property b desc
                 */
                class A private constructor(val b: String)
            """.trimIndent()
            assertThat(subject.lint(incorrectDeclarationsOrder)).isEmpty()
        }

        @Test
        fun `should not report when only public param is documented`() {
            val incorrectDeclarationsOrder = """
                /**
                 * Doc
                 * @param b desc
                 */
                class A internal constructor(val b: String)
            """.trimIndent()
            assertThat(
                OutdatedDocumentation(
                    TestConfig("allowParamOnConstructorProperties" to "true")
                ).lint(incorrectDeclarationsOrder)
            ).isEmpty()
        }

        @Test
        fun `should report when all public property is not documented`() {
            val incorrectDeclarationsOrder = """
                /**
                 * Doc
                 * @property a desc
                 */
                class A internal constructor(val a: String, val b: String)
            """.trimIndent()
            assertThat(subject.lint(incorrectDeclarationsOrder)).hasSize(1)
        }

        @Test
        fun `should report when all public property doc mismatch class property list order`() {
            val incorrectDeclarationsOrder = """
                /**
                 * Doc
                 * @property b desc
                 * @property a desc
                 */
                class A internal constructor(val a: String, val b: String)
            """.trimIndent()
            assertThat(subject.lint(incorrectDeclarationsOrder)).hasSize(1)
        }

        @Test
        fun `should report when only public property and param is documented with missing param`() {
            val incorrectDeclarationsOrder = """
                /**
                 * Doc
                 * @property a desc
                 * @param b desc
                 */
                class A internal constructor(
                    val a: String,
                    b: Int,
                    c: Int,
                )
            """.trimIndent()
            assertThat(subject.lint(incorrectDeclarationsOrder)).hasSize(1)
        }

        @Test
        fun `should report when only param is not documented for non internal or private constructor`() {
            val incorrectDeclarationsOrder = """
                /**
                 * Doc
                 * @property a desc
                 */
                class A(
                    val a: String,
                    b: Int,
                    c: Int,
                )
            """.trimIndent()
            assertThat(subject.lint(incorrectDeclarationsOrder)).hasSize(1)
        }

        @Test
        fun `should report when param which is private property is documented as property`() {
            val code = """
                /**
                 * Doc
                 * @property a desc
                 */
                class A(
                    private val a: String,
                )
            """.trimIndent()
            assertThat(subject.lint(code)).hasSize(1)
        }

        @Test
        fun `should not report internal or protected property is documented`() {
            val code = """
                /**
                 * Doc
                 * @property a desc
                 * @property b desc
                 */
                open class A(
                    internal val a: String,
                    protected val b: String,
                )
            """.trimIndent()
            assertThat(subject.lint(code)).isEmpty()
        }

        @Test
        fun `should not report when param which is private property is documented as param`() {
            val code = """
                /**
                 * Doc
                 * @param a desc
                 */
                class A(
                    private val a: String,
                )
            """.trimIndent()
            assertThat(subject.lint(code)).isEmpty()
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
            assertThat(subject.lint(correctTypeParam)).isEmpty()
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
            assertThat(subject.lint(correctTypeParam)).isEmpty()
        }

        @Test
        fun `should report when doc misses type param`() {
            val missingTypeParam = """
                /**
                 * @param someParam Description of param
                 */
                class MyClass<T>(someParam: String)
            """.trimIndent()
            assertThat(subject.lint(missingTypeParam)).hasSize(1)
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
            assertThat(subject.lint(incorrectTypeParamName)).hasSize(1)
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
            assertThat(subject.lint(incorrectTypeParamList)).hasSize(1)
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
            assertThat(subject.lint(correctDoc)).isEmpty()
        }

        @Test
        fun `should report when doc mismatch function param name`() {
            val incorrectParamName = """
                /**
                 * @param someParam Description of param
                 */
                fun myFun(otherParam: String) {}
            """.trimIndent()
            assertThat(subject.lint(incorrectParamName)).hasSize(1)
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
            assertThat(subject.lint(correctTypeParam)).isEmpty()
        }

        @Test
        fun `should report when doc misses type param`() {
            val missingTypeParam = """
                /**
                 * @param someParam Description of param
                 */
                fun <T> myFun(someParam: String) {}
            """.trimIndent()
            assertThat(subject.lint(missingTypeParam)).hasSize(1)
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
            assertThat(subject.lint(incorrectTypeParamName)).hasSize(1)
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
            assertThat(subject.lint(incorrectTypeParamList)).hasSize(1)
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
            assertThat(subject.lint(incorrectTypeParamsOrder)).hasSize(1)
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
            assertThat(subject.lint(correctClassWithFunction)).isEmpty()
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
            assertThat(subject.lint(incorrectClassWithTwoIncorrectFunctions)).hasSize(4)
        }
    }

    @Nested
    inner class `configuration matchTypeParameters` {
        private val configuredSubject =
            OutdatedDocumentation(TestConfig("matchTypeParameters" to "false"))

        @Test
        fun `should not report when class type parameters mismatch and configuration is off`() {
            val incorrectClassTypeParams = """
                /**
                 * @param someParam Description of param
                 */
                class MyClass<T, S>(someParam: String)
            """.trimIndent()
            assertThat(configuredSubject.lint(incorrectClassTypeParams)).isEmpty()
        }

        @Test
        fun `should not report when function type parameters mismatch and configuration is off`() {
            val incorrectFunctionTypeParams = """
                /**
                 * @param someParam Description of param
                 */
                fun <T, S> myFun(someParam: String) {}
            """.trimIndent()
            assertThat(configuredSubject.lint(incorrectFunctionTypeParams)).isEmpty()
        }
    }

    @Nested
    inner class `configuration matchDeclarationsOrder` {
        private val configuredSubject =
            OutdatedDocumentation(TestConfig("matchDeclarationsOrder" to "false"))

        @Test
        fun `should not report when declarations order mismatch and configuration is off`() {
            val incorrectDeclarationsOrder = """
                /**
                 * @param someParam Description of param
                 * @param otherParam Description of param
                 */
                class MyClass(otherParam: String, someParam: String)
            """.trimIndent()
            assertThat(configuredSubject.lint(incorrectDeclarationsOrder)).isEmpty()
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
            assertThat(configuredSubject.lint(incorrectDeclarationsOrderWithType)).isEmpty()
        }
    }

    @Nested
    inner class `configuration allowParamOnConstructorProperties` {
        private val configuredSubject = OutdatedDocumentation(
            TestConfig("allowParamOnConstructorProperties" to "true")
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
            assertThat(configuredSubject.lint(propertyAsParam)).isEmpty()
        }

        @Test
        fun `should not report when internal or protected property is documented as param`() {
            val propertyAsParam = """
                /**
                 * @param a Description of param
                 * @param b Description of property
                 */
                open class MyClass(internal val a: String, protected val b: String)
            """.trimIndent()
            assertThat(configuredSubject.lint(propertyAsParam)).isEmpty()
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
            assertThat(configuredSubject.lint(propertyAsParam)).isEmpty()
        }
    }

    @Nested
    inner class `configuration matchDeclarationsOrder and allowParamOnConstructorProperties` {
        private val configuredSubject = OutdatedDocumentation(
            TestConfig(
                "matchDeclarationsOrder" to "false",
                "allowParamOnConstructorProperties" to "true",
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
            assertThat(configuredSubject.lint(propertyAsParam)).isEmpty()
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
            assertThat(configuredSubject.lint(propertyAsParam)).isEmpty()
        }

        @Test
        fun `should report when param which is private property is documented as property`() {
            val code = """
                /**
                 * Doc
                 * @property a desc
                 */
                class A(
                    private val a: String,
                )
            """.trimIndent()
            assertThat(subject.lint(code)).hasSize(1)
        }
    }
}
