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

class AnnotationSuppressorSpec : Spek({
    setupKotlinEnvironment()
    val env: KotlinCoreEnvironment by memoized()

    describe("AnnotationSuppressorFactory") {
        it("Factory returns null if ignoreAnnotated is not set") {
            val suppressor = annotationSuppressorFactory(buildConfigAware(/* empty */), BindingContext.EMPTY)

            assertThat(suppressor).isNull()
        }

        it("Factory returns null if ignoreAnnotated is set to empty") {
            val suppressor = annotationSuppressorFactory(
                buildConfigAware("ignoreAnnotated" to emptyList<String>()),
                BindingContext.EMPTY,
            )

            assertThat(suppressor).isNull()
        }

        it("Factory returns not null if ignoreAnnotated is set to a not empty list") {
            val suppressor = annotationSuppressorFactory(
                buildConfigAware("ignoreAnnotated" to listOf("Composable")),
                BindingContext.EMPTY,
            )

            assertThat(suppressor).isNotNull()
        }
    }

    describe("AnnotationSuppressor") {
        val suppressor by memoized {
            annotationSuppressorFactory(
                buildConfigAware("ignoreAnnotated" to listOf("Composable")),
                BindingContext.EMPTY,
            )!!
        }

        it("If KtElement is null it returns false") {
            assertThat(suppressor.shouldSuppress(buildFinding(element = null))).isFalse()
        }

        context("If annotation is at file level") {
            val root by memoized {
                compileContentForTest(
                    """
                    @file:Composable

                    class OneClass {
                        fun function(parameter: String) {
                            val a = 0
                        }
                    }

                    fun topLevelFunction() = Unit
                    """.trimIndent()
                )
            }

            it("If reports root it returns true") {
                assertThat(suppressor.shouldSuppress(buildFinding(element = root))).isTrue()
            }

            it("If reports class it returns true") {
                val ktClass = root.findChildByClass(KtClass::class.java)!!

                assertThat(suppressor.shouldSuppress(buildFinding(element = ktClass))).isTrue()
            }

            it("If reports function in class it returns true") {
                val ktFunction = root.findChildByClass(KtClass::class.java)!!
                    .findFunctionByName("function")!!

                assertThat(suppressor.shouldSuppress(buildFinding(element = ktFunction))).isTrue()
            }

            it("If reports parameter in function in class it returns true") {
                val ktParameter = root.findChildByClass(KtClass::class.java)!!
                    .findFunctionByName("function")!!
                    .findDescendantOfType<KtParameter>()!!

                assertThat(suppressor.shouldSuppress(buildFinding(element = ktParameter))).isTrue()
            }

            it("If reports top level function it returns true") {
                val ktFunction = root.findChildByClass(KtFunction::class.java)!!

                assertThat(suppressor.shouldSuppress(buildFinding(element = ktFunction))).isTrue()
            }
        }

        context("If annotation is at function level") {
            val root by memoized {
                compileContentForTest(
                    """
                    class OneClass {
                        @Composable
                        fun function(parameter: String) {
                            val a = 0
                        }
                    }

                    fun topLevelFunction() = Unit
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
                    .findFunctionByName("function")!!

                assertThat(suppressor.shouldSuppress(buildFinding(element = ktFunction))).isTrue()
            }

            it("If reports parameter in function in class it returns true") {
                val ktParameter = root.findChildByClass(KtClass::class.java)!!
                    .findFunctionByName("function")!!
                    .findDescendantOfType<KtParameter>()!!

                assertThat(suppressor.shouldSuppress(buildFinding(element = ktParameter))).isTrue()
            }

            it("If reports top level function it returns false") {
                val ktFunction = root.findChildByClass(KtFunction::class.java)!!

                assertThat(suppressor.shouldSuppress(buildFinding(element = ktFunction))).isFalse()
            }
        }

        context("If there is not annotations") {
            val root by memoized {
                compileContentForTest(
                    """
                    class OneClass {
                        fun function(parameter: String) {
                            val a = 0
                        }
                    }

                    fun topLevelFunction() = Unit
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
                    .findFunctionByName("function")!!

                assertThat(suppressor.shouldSuppress(buildFinding(element = ktFunction))).isFalse()
            }

            it("If reports parameter in function in class it returns false") {
                val ktParameter = root.findChildByClass(KtClass::class.java)!!
                    .findFunctionByName("function")!!
                    .findDescendantOfType<KtParameter>()!!

                assertThat(suppressor.shouldSuppress(buildFinding(element = ktParameter))).isFalse()
            }

            it("If reports top level function it returns false") {
                val ktFunction = root.findChildByClass(KtFunction::class.java)!!

                assertThat(suppressor.shouldSuppress(buildFinding(element = ktFunction))).isFalse()
            }
        }

        context("If there are other annotations") {
            val root by memoized {
                compileContentForTest(
                    """
                    @file:A

                    @B
                    class OneClass {
                        @Composable
                        fun function(@C parameter: String) {
                            @D
                            val a = 0
                        }
                    }

                    @E
                    fun topLevelFunction() = Unit
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
                    .findFunctionByName("function")!!

                assertThat(suppressor.shouldSuppress(buildFinding(element = ktFunction))).isTrue()
            }

            it("If reports parameter in function in class it returns true") {
                val ktParameter = root.findChildByClass(KtClass::class.java)!!
                    .findFunctionByName("function")!!
                    .findDescendantOfType<KtParameter>()!!

                assertThat(suppressor.shouldSuppress(buildFinding(element = ktParameter))).isTrue()
            }

            it("If reports top level function it returns false") {
                val ktFunction = root.findChildByClass(KtFunction::class.java)!!

                assertThat(suppressor.shouldSuppress(buildFinding(element = ktFunction))).isFalse()
            }
        }
    }

    describe("Full Qualified names") {
        val composableFiles by memoized {
            arrayOf(
                compileContentForTest(
                    """
                    package androidx.compose.runtime

                    annotation class Composable
                    """.trimIndent()
                ),
                compileContentForTest(
                    """
                    package foo.bar

                    annotation class Composable
                    """.trimIndent()
                ),
            )
        }

        context("general cases") {
            val root by memoized {
                compileContentForTest(
                    """
                    package foo.bar

                    import androidx.compose.runtime.Composable

                    @Composable
                    fun function() = Unit
                    """.trimIndent()
                )
            }
            val binding by memoized {
                env.getContextForPaths(listOf(root, *composableFiles))
            }

            it("Just name") {
                val suppressor = annotationSuppressorFactory(
                    buildConfigAware("ignoreAnnotated" to listOf("Composable")),
                    binding,
                )!!

                val ktFunction = root.findChildByClass(KtFunction::class.java)!!

                assertThat(suppressor.shouldSuppress(buildFinding(ktFunction))).isTrue()
            }

            it("Full qualified name name") {
                val suppressor = annotationSuppressorFactory(
                    buildConfigAware("ignoreAnnotated" to listOf("androidx.compose.runtime.Composable")),
                    binding,
                )!!

                val ktFunction = root.findChildByClass(KtFunction::class.java)!!

                assertThat(suppressor.shouldSuppress(buildFinding(ktFunction))).isTrue()
            }

            it("With glob doesn't match because * doesn't match .") {
                val suppressor = annotationSuppressorFactory(
                    buildConfigAware("ignoreAnnotated" to listOf("*.Composable")),
                    binding,
                )!!

                val ktFunction = root.findChildByClass(KtFunction::class.java)!!

                assertThat(suppressor.shouldSuppress(buildFinding(ktFunction))).isFalse()
            }

            it("With glob2") {
                val suppressor = annotationSuppressorFactory(
                    buildConfigAware("ignoreAnnotated" to listOf("**.Composable")),
                    binding,
                )!!

                val ktFunction = root.findChildByClass(KtFunction::class.java)!!

                assertThat(suppressor.shouldSuppress(buildFinding(ktFunction))).isTrue()
            }

            it("With glob3") {
                val suppressor = annotationSuppressorFactory(
                    buildConfigAware("ignoreAnnotated" to listOf("Compo*")),
                    binding,
                )!!

                val ktFunction = root.findChildByClass(KtFunction::class.java)!!

                assertThat(suppressor.shouldSuppress(buildFinding(ktFunction))).isTrue()
            }

            it("With glob4") {
                val suppressor = annotationSuppressorFactory(
                    buildConfigAware("ignoreAnnotated" to listOf("*")),
                    binding,
                )!!

                val ktFunction = root.findChildByClass(KtFunction::class.java)!!

                assertThat(suppressor.shouldSuppress(buildFinding(ktFunction))).isTrue()
            }
        }

        it("Doesn't mix annotations") {
            val root = compileContentForTest(
                """
                package foo.bar

                @Composable
                fun function() = Unit
                """.trimIndent()
            )

            val suppressor = annotationSuppressorFactory(
                buildConfigAware("ignoreAnnotated" to listOf("androidx.compose.runtime.Composable")),
                env.getContextForPaths(listOf(root, *composableFiles)),
            )!!

            val ktFunction = root.findChildByClass(KtFunction::class.java)!!

            assertThat(suppressor.shouldSuppress(buildFinding(ktFunction))).isFalse()
        }

        it("Works when no using imports") {
            val root = compileContentForTest(
                """
                package foo.bar

                @androidx.compose.runtime.Composable
                fun function() = Unit
                """.trimIndent()
            )

            val suppressor = annotationSuppressorFactory(
                buildConfigAware("ignoreAnnotated" to listOf("androidx.compose.runtime.Composable")),
                env.getContextForPaths(listOf(root, *composableFiles)),
            )!!

            val ktFunction = root.findChildByClass(KtFunction::class.java)!!

            assertThat(suppressor.shouldSuppress(buildFinding(ktFunction))).isTrue()
        }

        it("Works when using import alias") {
            val root = compileContentForTest(
                """
                package foo.bar

                import androidx.compose.runtime.Composable as Bar

                @Bar
                fun function() = Unit
                """.trimIndent()
            )

            val suppressor = annotationSuppressorFactory(
                buildConfigAware("ignoreAnnotated" to listOf("androidx.compose.runtime.Composable")),
                env.getContextForPaths(listOf(root, *composableFiles)),
            )!!

            val ktFunction = root.findChildByClass(KtFunction::class.java)!!

            assertThat(suppressor.shouldSuppress(buildFinding(ktFunction))).isTrue()
        }
    }
})
