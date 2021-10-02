package io.gitlab.arturbosch.detekt.core.suppressors

import io.github.detekt.test.utils.compileContentForTest
import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.kotlin.psi.KtClass
import org.jetbrains.kotlin.psi.KtFunction
import org.jetbrains.kotlin.psi.KtParameter
import org.jetbrains.kotlin.psi.psiUtil.findDescendantOfType
import org.jetbrains.kotlin.psi.psiUtil.findFunctionByName
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

class FunctionSuppressorSpec : Spek({

    describe("FunctionSuppressorFactory") {
        it("Factory returns null if ignoreFunction is not set") {
            val suppressor = functionSuppressorFactory(buildConfigAware(/* empty */))

            assertThat(suppressor).isNull()
        }

        it("Factory returns null if ignoreFunction is set to empty") {
            val suppressor = functionSuppressorFactory(
                buildConfigAware("ignoreFunction" to emptyList<String>())
            )

            assertThat(suppressor).isNull()
        }

        it("Factory returns not null if ignoreFunction is set to a not empty list") {
            val suppressor = functionSuppressorFactory(
                buildConfigAware("ignoreFunction" to listOf("toString"))
            )

            assertThat(suppressor).isNotNull()
        }
    }

    describe("FunctionSuppressor") {
        val suppressor by memoized {
            functionSuppressorFactory(buildConfigAware("ignoreFunction" to listOf("toString")))!!
        }

        it("If KtElement is null it returns false") {
            assertThat(suppressor.shouldSuppress(buildFinding(element = null))).isFalse()
        }

        context("If the function is suppressed") {
            val root by memoized {
                compileContentForTest(
                    """
                    class OneClass {
                        fun toString(parameter: String): String {
                            return ""
                        }
                    }

                    fun toString() = Unit
                    """.trimIndent()
                )
            }

            it("If reports root it returns false") {
                assertThat(suppressor.shouldSuppress(buildFinding(element = root))).isFalse()
            }

            it("If reports class it returns false") {
                val ktClass = root.findChildByClass(KtClass::class.java)!!

                assertThat(suppressor.shouldSuppress(buildFinding(element = ktClass))).isFalse()
            }

            it("If reports function in class it returns true") {
                val ktFunction = root.findChildByClass(KtClass::class.java)!!
                    .findFunctionByName("toString")!!

                assertThat(suppressor.shouldSuppress(buildFinding(element = ktFunction))).isTrue()
            }

            it("If reports parameter in function in class it returns true") {
                val ktParameter = root.findChildByClass(KtClass::class.java)!!
                    .findFunctionByName("toString")!!
                    .findDescendantOfType<KtParameter>()!!

                assertThat(suppressor.shouldSuppress(buildFinding(element = ktParameter))).isTrue()
            }

            it("If reports top level function it returns true") {
                val ktFunction = root.findChildByClass(KtFunction::class.java)!!

                assertThat(suppressor.shouldSuppress(buildFinding(element = ktFunction))).isTrue()
            }
        }

        context("If the function is not suppressed") {
            val root by memoized {
                compileContentForTest(
                    """
                    class OneClass {
                        fun compare(parameter: String): String {
                            return ""
                        }
                    }

                    fun compare() = Unit
                    """.trimIndent()
                )
            }

            it("If reports root it returns false") {
                assertThat(suppressor.shouldSuppress(buildFinding(element = root))).isFalse()
            }

            it("If reports class it returns false") {
                val ktClass = root.findChildByClass(KtClass::class.java)!!

                assertThat(suppressor.shouldSuppress(buildFinding(element = ktClass))).isFalse()
            }

            it("If reports function in class it returns false") {
                val ktFunction = root.findChildByClass(KtClass::class.java)!!
                    .findFunctionByName("compare")!!

                assertThat(suppressor.shouldSuppress(buildFinding(element = ktFunction))).isFalse()
            }

            it("If reports parameter in function in class it returns false") {
                val ktParameter = root.findChildByClass(KtClass::class.java)!!
                    .findFunctionByName("compare")!!
                    .findDescendantOfType<KtParameter>()!!

                assertThat(suppressor.shouldSuppress(buildFinding(element = ktParameter))).isFalse()
            }

            it("If reports top level function it returns false") {
                val ktFunction = root.findChildByClass(KtFunction::class.java)!!

                assertThat(suppressor.shouldSuppress(buildFinding(element = ktFunction))).isFalse()
            }
        }
    }
})
