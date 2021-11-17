package io.gitlab.arturbosch.detekt.core.suppressors

import io.github.detekt.test.utils.compileContentForTest
import io.gitlab.arturbosch.detekt.rules.setupKotlinEnvironment
import io.gitlab.arturbosch.detekt.test.getContextForPaths
import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.kotlin.cli.jvm.compiler.KotlinCoreEnvironment
import org.jetbrains.kotlin.psi.KtClass
import org.jetbrains.kotlin.psi.KtFunction
import org.jetbrains.kotlin.psi.KtParameter
import org.jetbrains.kotlin.psi.psiUtil.findDescendantOfType
import org.jetbrains.kotlin.psi.psiUtil.findFunctionByName
import org.jetbrains.kotlin.resolve.BindingContext
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

class FunctionSuppressorSpec : Spek({
    setupKotlinEnvironment()
    val env: KotlinCoreEnvironment by memoized()

    describe("FunctionSuppressorFactory") {
        it("Factory returns null if ignoreFunction is not set") {
            val suppressor = functionSuppressorFactory(
                buildConfigAware(/* empty */),
                BindingContext.EMPTY,
            )

            assertThat(suppressor).isNull()
        }

        it("Factory returns null if ignoreFunction is set to empty") {
            val suppressor = functionSuppressorFactory(
                buildConfigAware("ignoreFunction" to emptyList<String>()),
                BindingContext.EMPTY,
            )

            assertThat(suppressor).isNull()
        }

        it("Factory returns not null if ignoreFunction is set to a not empty list") {
            val suppressor = functionSuppressorFactory(
                buildConfigAware("ignoreFunction" to listOf("toString")),
                BindingContext.EMPTY,
            )

            assertThat(suppressor).isNotNull()
        }
    }

    describe("FunctionSuppressor") {
        it("If KtElement is null it returns false") {
            val suppressor = buildFunctionSuppressor(listOf("toString"), BindingContext.EMPTY)

            assertThat(suppressor.shouldSuppress(buildFinding(element = null))).isFalse()
        }

        context("If the function is suppressed") {
            val root by memoized {
                compileContentForTest(
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
            }
            val binding by memoized { env.getContextForPaths(listOf(root)) }

            it("If reports root it returns false") {
                val suppressor = buildFunctionSuppressor(listOf("toString"), binding)

                assertThat(suppressor.shouldSuppress(buildFinding(element = root))).isFalse()
            }

            it("If reports class it returns false") {
                val suppressor = buildFunctionSuppressor(listOf("toString"), binding)
                val ktClass = root.findChildByClass(KtClass::class.java)!!

                assertThat(suppressor.shouldSuppress(buildFinding(element = ktClass))).isFalse()
            }

            it("If reports function in class it returns true") {
                val suppressor = buildFunctionSuppressor(listOf("toString"), binding)
                val ktFunction = root.findChildByClass(KtClass::class.java)!!
                    .findFunctionByName("toString")!!

                assertThat(suppressor.shouldSuppress(buildFinding(element = ktFunction))).isTrue()
            }

            it("If reports parameter in function in class it returns true") {
                val suppressor = buildFunctionSuppressor(listOf("toString"), binding)
                val ktParameter = root.findChildByClass(KtClass::class.java)!!
                    .findFunctionByName("toString")!!
                    .findDescendantOfType<KtParameter>()!!

                assertThat(suppressor.shouldSuppress(buildFinding(element = ktParameter))).isTrue()
            }

            it("If reports function in function it returns true") {
                val suppressor = buildFunctionSuppressor(listOf("toString"), binding)
                val ktFunction = root.findChildByClass(KtClass::class.java)!!
                    .findFunctionByName("toString")!!
                    .children
                    .mapNotNull { it.findDescendantOfType<KtFunction>() }
                    .first()

                assertThat(suppressor.shouldSuppress(buildFinding(element = ktFunction))).isTrue()
            }

            it("If reports parameter function in function it returns true") {
                val suppressor = buildFunctionSuppressor(listOf("toString"), binding)
                val ktFunction = root.findChildByClass(KtClass::class.java)!!
                    .findFunctionByName("toString")!!
                    .children
                    .mapNotNull { it.findDescendantOfType<KtFunction>() }
                    .first()
                    .findDescendantOfType<KtParameter>()!!

                assertThat(suppressor.shouldSuppress(buildFinding(element = ktFunction))).isTrue()
            }

            it("If reports parameter function in function it returns true 2") {
                val suppressor = buildFunctionSuppressor(listOf("hello"), binding)
                val ktFunction = root.findChildByClass(KtClass::class.java)!!
                    .findFunctionByName("toString")!!
                    .children
                    .mapNotNull { it.findDescendantOfType<KtFunction>() }
                    .first()
                    .findDescendantOfType<KtParameter>()!!

                assertThat(suppressor.shouldSuppress(buildFinding(element = ktFunction))).isTrue()
            }

            it("If reports top level function it returns true") {
                val suppressor = buildFunctionSuppressor(listOf("toString"), binding)
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
            val binding by memoized { env.getContextForPaths(listOf(root)) }

            it("If reports root it returns false") {
                val suppressor = buildFunctionSuppressor(listOf("toString"), binding)
                assertThat(suppressor.shouldSuppress(buildFinding(element = root))).isFalse()
            }

            it("If reports class it returns false") {
                val suppressor = buildFunctionSuppressor(listOf("toString"), binding)
                val ktClass = root.findChildByClass(KtClass::class.java)!!

                assertThat(suppressor.shouldSuppress(buildFinding(element = ktClass))).isFalse()
            }

            it("If reports function in class it returns false") {
                val suppressor = buildFunctionSuppressor(listOf("toString"), binding)
                val ktFunction = root.findChildByClass(KtClass::class.java)!!
                    .findFunctionByName("compare")!!

                assertThat(suppressor.shouldSuppress(buildFinding(element = ktFunction))).isFalse()
            }

            it("If reports parameter in function in class it returns false") {
                val suppressor = buildFunctionSuppressor(listOf("toString"), binding)
                val ktParameter = root.findChildByClass(KtClass::class.java)!!
                    .findFunctionByName("compare")!!
                    .findDescendantOfType<KtParameter>()!!

                assertThat(suppressor.shouldSuppress(buildFinding(element = ktParameter))).isFalse()
            }

            it("If reports top level function it returns false") {
                val suppressor = buildFunctionSuppressor(listOf("toString"), binding)
                val ktFunction = root.findChildByClass(KtFunction::class.java)!!

                assertThat(suppressor.shouldSuppress(buildFinding(element = ktFunction))).isFalse()
            }
        }
    }
})

private fun buildFunctionSuppressor(ignoreFunction: List<String>, bindingContext: BindingContext): Suppressor {
    return functionSuppressorFactory(buildConfigAware("ignoreFunction" to ignoreFunction), bindingContext)!!
}
