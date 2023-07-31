package io.gitlab.arturbosch.detekt.core.suppressors

import io.github.detekt.test.utils.compileContentForTest
import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.kotlin.psi.KtClass
import org.jetbrains.kotlin.psi.KtFunction
import org.jetbrains.kotlin.psi.KtParameter
import org.jetbrains.kotlin.psi.psiUtil.findDescendantOfType
import org.jetbrains.kotlin.psi.psiUtil.findFunctionByName
import org.jetbrains.kotlin.resolve.BindingContext
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class FunctionSuppressorSpec {

    @Nested
    inner class FunctionSuppressorFactory {
        @Test
        fun `Factory returns null if ignoreFunction is not set`() {
            val suppressor = functionSuppressorFactory(
                buildConfigAware(),
                BindingContext.EMPTY,
            )

            assertThat(suppressor).isNull()
        }

        @Test
        fun `Factory returns null if ignoreFunction is set to empty`() {
            val suppressor = functionSuppressorFactory(
                buildConfigAware("ignoreFunction" to emptyList<String>()),
                BindingContext.EMPTY,
            )

            assertThat(suppressor).isNull()
        }

        @Test
        fun `Factory returns not null if ignoreFunction is set to a not empty list`() {
            val suppressor = functionSuppressorFactory(
                buildConfigAware("ignoreFunction" to listOf("toString")),
                BindingContext.EMPTY,
            )

            assertThat(suppressor).isNotNull()
        }
    }

    @Nested
    inner class FunctionSuppressor {
        @Test
        fun `If KtElement is null it returns false`() {
            val suppressor = buildFunctionSuppressor(listOf("toString"), BindingContext.EMPTY)

            assertThat(suppressor.shouldSuppress(buildFinding(element = null))).isFalse()
        }

        @Nested
        inner class `If the function is suppressed` {
            val root = compileContentForTest(
                """
                    class OneClass {
                        fun toString(parameter: String): String {
                            fun hello(name: String) {
                                println("Hello " + name)
                            }
                            hello("World")
                            return ""
                        }
                    }
                    
                    fun toString() = Unit
                """.trimIndent()
            )

            @Test
            fun `If reports root it returns false`() {
                val suppressor = buildFunctionSuppressor(listOf("toString"), BindingContext.EMPTY)

                assertThat(suppressor.shouldSuppress(buildFinding(element = root))).isFalse()
            }

            @Test
            fun `If reports class it returns false`() {
                val suppressor = buildFunctionSuppressor(listOf("toString"), BindingContext.EMPTY)
                val ktClass = root.findChildByClass(KtClass::class.java)!!

                assertThat(suppressor.shouldSuppress(buildFinding(element = ktClass))).isFalse()
            }

            @Test
            fun `If reports function in class it returns true`() {
                val suppressor = buildFunctionSuppressor(listOf("toString"), BindingContext.EMPTY)
                val ktFunction = root.findChildByClass(KtClass::class.java)!!
                    .findFunctionByName("toString")!!

                assertThat(suppressor.shouldSuppress(buildFinding(element = ktFunction))).isTrue()
            }

            @Test
            fun `If reports parameter in function in class it returns true`() {
                val suppressor = buildFunctionSuppressor(listOf("toString"), BindingContext.EMPTY)
                val ktParameter = root.findChildByClass(KtClass::class.java)!!
                    .findFunctionByName("toString")!!
                    .findDescendantOfType<KtParameter>()!!

                assertThat(suppressor.shouldSuppress(buildFinding(element = ktParameter))).isTrue()
            }

            @Test
            fun `If reports function in function it returns true`() {
                val suppressor = buildFunctionSuppressor(listOf("toString"), BindingContext.EMPTY)
                val ktFunction = root.findChildByClass(KtClass::class.java)!!
                    .findFunctionByName("toString")!!
                    .children
                    .mapNotNull { it.findDescendantOfType<KtFunction>() }
                    .first()

                assertThat(suppressor.shouldSuppress(buildFinding(element = ktFunction))).isTrue()
            }

            @Test
            fun `If reports parameter function in function it returns true`() {
                val suppressor = buildFunctionSuppressor(listOf("toString"), BindingContext.EMPTY)
                val ktFunction = root.findChildByClass(KtClass::class.java)!!
                    .findFunctionByName("toString")!!
                    .children
                    .mapNotNull { it.findDescendantOfType<KtFunction>() }
                    .first()
                    .findDescendantOfType<KtParameter>()!!

                assertThat(suppressor.shouldSuppress(buildFinding(element = ktFunction))).isTrue()
            }

            @Test
            fun `If reports parameter function in function it returns true 2`() {
                val suppressor = buildFunctionSuppressor(listOf("hello"), BindingContext.EMPTY)
                val ktFunction = root.findChildByClass(KtClass::class.java)!!
                    .findFunctionByName("toString")!!
                    .children
                    .mapNotNull { it.findDescendantOfType<KtFunction>() }
                    .first()
                    .findDescendantOfType<KtParameter>()!!

                assertThat(suppressor.shouldSuppress(buildFinding(element = ktFunction))).isTrue()
            }

            @Test
            fun `If reports top level function it returns true`() {
                val suppressor = buildFunctionSuppressor(listOf("toString"), BindingContext.EMPTY)
                val ktFunction = root.findChildByClass(KtFunction::class.java)!!

                assertThat(suppressor.shouldSuppress(buildFinding(element = ktFunction))).isTrue()
            }
        }

        @Nested
        inner class `If the function is not suppressed` {
            val root = compileContentForTest(
                """
                    class OneClass {
                        fun compare(parameter: String): String {
                            return ""
                        }
                    }
                    
                    fun compare() = Unit
                """.trimIndent()
            )

            @Test
            fun `If reports root it returns false`() {
                val suppressor = buildFunctionSuppressor(listOf("toString"), BindingContext.EMPTY)
                assertThat(suppressor.shouldSuppress(buildFinding(element = root))).isFalse()
            }

            @Test
            fun `If reports class it returns false`() {
                val suppressor = buildFunctionSuppressor(listOf("toString"), BindingContext.EMPTY)
                val ktClass = root.findChildByClass(KtClass::class.java)!!

                assertThat(suppressor.shouldSuppress(buildFinding(element = ktClass))).isFalse()
            }

            @Test
            fun `If reports function in class it returns false`() {
                val suppressor = buildFunctionSuppressor(listOf("toString"), BindingContext.EMPTY)
                val ktFunction = root.findChildByClass(KtClass::class.java)!!
                    .findFunctionByName("compare")!!

                assertThat(suppressor.shouldSuppress(buildFinding(element = ktFunction))).isFalse()
            }

            @Test
            fun `If reports parameter in function in class it returns false`() {
                val suppressor = buildFunctionSuppressor(listOf("toString"), BindingContext.EMPTY)
                val ktParameter = root.findChildByClass(KtClass::class.java)!!
                    .findFunctionByName("compare")!!
                    .findDescendantOfType<KtParameter>()!!

                assertThat(suppressor.shouldSuppress(buildFinding(element = ktParameter))).isFalse()
            }

            @Test
            fun `If reports top level function it returns false`() {
                val suppressor = buildFunctionSuppressor(listOf("toString"), BindingContext.EMPTY)
                val ktFunction = root.findChildByClass(KtFunction::class.java)!!

                assertThat(suppressor.shouldSuppress(buildFinding(element = ktFunction))).isFalse()
            }
        }
    }
}

private fun buildFunctionSuppressor(ignoreFunction: List<String>, bindingContext: BindingContext): Suppressor {
    return functionSuppressorFactory(buildConfigAware("ignoreFunction" to ignoreFunction), bindingContext)!!
}
