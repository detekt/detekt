package io.gitlab.arturbosch.detekt.core.suppressors

import io.github.detekt.test.utils.compileContentForTest
import io.gitlab.arturbosch.detekt.rules.KotlinCoreEnvironmentTest
import io.gitlab.arturbosch.detekt.test.getContextForPaths
import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.kotlin.cli.jvm.compiler.KotlinCoreEnvironment
import org.jetbrains.kotlin.psi.KtClass
import org.jetbrains.kotlin.psi.KtFunction
import org.jetbrains.kotlin.psi.KtParameter
import org.jetbrains.kotlin.psi.psiUtil.findDescendantOfType
import org.jetbrains.kotlin.psi.psiUtil.findFunctionByName
import org.jetbrains.kotlin.resolve.BindingContext
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

@KotlinCoreEnvironmentTest
class AnnotationSuppressorSpec(private val env: KotlinCoreEnvironment) {

    @Nested
    inner class `AnnotationSuppressorFactory` {
        @Test
        fun `Factory returns null if ignoreAnnotated is not set`() {
            val suppressor = annotationSuppressorFactory(buildConfigAware(/* empty */), BindingContext.EMPTY)

            assertThat(suppressor).isNull()
        }

        @Test
        fun `Factory returns null if ignoreAnnotated is set to empty`() {
            val suppressor = annotationSuppressorFactory(
                buildConfigAware("ignoreAnnotated" to emptyList<String>()),
                BindingContext.EMPTY,
            )

            assertThat(suppressor).isNull()
        }

        @Test
        fun `Factory returns not null if ignoreAnnotated is set to a not empty list`() {
            val suppressor = annotationSuppressorFactory(
                buildConfigAware("ignoreAnnotated" to listOf("Composable")),
                BindingContext.EMPTY,
            )

            assertThat(suppressor).isNotNull()
        }
    }

    @Nested
    inner class `AnnotationSuppressor` {
        val suppressor = annotationSuppressorFactory(
            buildConfigAware("ignoreAnnotated" to listOf("Composable")),
            BindingContext.EMPTY,
        )!!

        @Test
        fun `If KtElement is null it returns false`() {
            assertThat(suppressor.shouldSuppress(buildFinding(element = null))).isFalse()
        }

        @Nested
        inner class `If annotation is at file level` {
            val root = compileContentForTest(
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

            @Test
            fun `If reports root it returns true`() {
                assertThat(suppressor.shouldSuppress(buildFinding(element = root))).isTrue()
            }

            @Test
            fun `If reports class it returns true`() {
                val ktClass = root.findChildByClass(KtClass::class.java)!!

                assertThat(suppressor.shouldSuppress(buildFinding(element = ktClass))).isTrue()
            }

            @Test
            fun `If reports function in class it returns true`() {
                val ktFunction = root.findChildByClass(KtClass::class.java)!!
                    .findFunctionByName("function")!!

                assertThat(suppressor.shouldSuppress(buildFinding(element = ktFunction))).isTrue()
            }

            @Test
            fun `If reports parameter in function in class it returns true`() {
                val ktParameter = root.findChildByClass(KtClass::class.java)!!
                    .findFunctionByName("function")!!
                    .findDescendantOfType<KtParameter>()!!

                assertThat(suppressor.shouldSuppress(buildFinding(element = ktParameter))).isTrue()
            }

            @Test
            fun `If reports top level function it returns true`() {
                val ktFunction = root.findChildByClass(KtFunction::class.java)!!

                assertThat(suppressor.shouldSuppress(buildFinding(element = ktFunction))).isTrue()
            }
        }

        @Nested
        inner class `If annotation is at function level` {
            val root = compileContentForTest(
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

            @Test
            fun `If reports root it returns false`() {
                assertThat(suppressor.shouldSuppress(buildFinding(element = root))).isFalse()
            }

            @Test
            fun `If reports class it returns false`() {
                val ktClass = root.findChildByClass(KtClass::class.java)!!

                assertThat(suppressor.shouldSuppress(buildFinding(element = ktClass))).isFalse()
            }

            @Test
            fun `If reports function in class it returns true`() {
                val ktFunction = root.findChildByClass(KtClass::class.java)!!
                    .findFunctionByName("function")!!

                assertThat(suppressor.shouldSuppress(buildFinding(element = ktFunction))).isTrue()
            }

            @Test
            fun `If reports parameter in function in class it returns true`() {
                val ktParameter = root.findChildByClass(KtClass::class.java)!!
                    .findFunctionByName("function")!!
                    .findDescendantOfType<KtParameter>()!!

                assertThat(suppressor.shouldSuppress(buildFinding(element = ktParameter))).isTrue()
            }

            @Test
            fun `If reports top level function it returns false`() {
                val ktFunction = root.findChildByClass(KtFunction::class.java)!!

                assertThat(suppressor.shouldSuppress(buildFinding(element = ktFunction))).isFalse()
            }
        }

        @Nested
        inner class `If there is not annotations` {
            val root = compileContentForTest(
                """
                    class OneClass {
                        fun function(parameter: String) {
                            val a = 0
                        }
                    }

                    fun topLevelFunction() = Unit
                """.trimIndent()
            )

            @Test
            fun `If reports root it returns false`() {
                assertThat(suppressor.shouldSuppress(buildFinding(element = root))).isFalse()
            }

            @Test
            fun `If reports class it returns false`() {
                val ktClass = root.findChildByClass(KtClass::class.java)!!

                assertThat(suppressor.shouldSuppress(buildFinding(element = ktClass))).isFalse()
            }

            @Test
            fun `If reports function in class it returns false`() {
                val ktFunction = root.findChildByClass(KtClass::class.java)!!
                    .findFunctionByName("function")!!

                assertThat(suppressor.shouldSuppress(buildFinding(element = ktFunction))).isFalse()
            }

            @Test
            fun `If reports parameter in function in class it returns false`() {
                val ktParameter = root.findChildByClass(KtClass::class.java)!!
                    .findFunctionByName("function")!!
                    .findDescendantOfType<KtParameter>()!!

                assertThat(suppressor.shouldSuppress(buildFinding(element = ktParameter))).isFalse()
            }

            @Test
            fun `If reports top level function it returns false`() {
                val ktFunction = root.findChildByClass(KtFunction::class.java)!!

                assertThat(suppressor.shouldSuppress(buildFinding(element = ktFunction))).isFalse()
            }
        }

        @Nested
        inner class `If there are other annotations` {
            val root = compileContentForTest(
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

            @Test
            fun `If reports root it returns false`() {
                assertThat(suppressor.shouldSuppress(buildFinding(element = root))).isFalse()
            }

            @Test
            fun `If reports class it returns false`() {
                val ktClass = root.findChildByClass(KtClass::class.java)!!

                assertThat(suppressor.shouldSuppress(buildFinding(element = ktClass))).isFalse()
            }

            @Test
            fun `If reports function in class it returns true`() {
                val ktFunction = root.findChildByClass(KtClass::class.java)!!
                    .findFunctionByName("function")!!

                assertThat(suppressor.shouldSuppress(buildFinding(element = ktFunction))).isTrue()
            }

            @Test
            fun `If reports parameter in function in class it returns true`() {
                val ktParameter = root.findChildByClass(KtClass::class.java)!!
                    .findFunctionByName("function")!!
                    .findDescendantOfType<KtParameter>()!!

                assertThat(suppressor.shouldSuppress(buildFinding(element = ktParameter))).isTrue()
            }

            @Test
            fun `If reports top level function it returns false`() {
                val ktFunction = root.findChildByClass(KtFunction::class.java)!!

                assertThat(suppressor.shouldSuppress(buildFinding(element = ktFunction))).isFalse()
            }
        }
    }

    @Nested
    inner class `Full Qualified names` {
        val composableFiles = arrayOf(
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

        @Nested
        inner class `general cases` {
            val root = compileContentForTest(
                """
                    package foo.bar

                    import androidx.compose.runtime.Composable

                    @Composable
                    fun function() = Unit
                """.trimIndent()
            )

            val binding = env.getContextForPaths(listOf(root, *composableFiles))

            @Test
            fun `Just name`() {
                val suppressor = annotationSuppressorFactory(
                    buildConfigAware("ignoreAnnotated" to listOf("Composable")),
                    binding,
                )!!

                val ktFunction = root.findChildByClass(KtFunction::class.java)!!

                assertThat(suppressor.shouldSuppress(buildFinding(ktFunction))).isTrue()
            }

            @Test
            fun `Full qualified name name`() {
                val suppressor = annotationSuppressorFactory(
                    buildConfigAware("ignoreAnnotated" to listOf("androidx.compose.runtime.Composable")),
                    binding,
                )!!

                val ktFunction = root.findChildByClass(KtFunction::class.java)!!

                assertThat(suppressor.shouldSuppress(buildFinding(ktFunction))).isTrue()
            }

            @Test
            fun `With glob doesn't match because * doesn't match _`() {
                val suppressor = annotationSuppressorFactory(
                    buildConfigAware("ignoreAnnotated" to listOf("*.Composable")),
                    binding,
                )!!

                val ktFunction = root.findChildByClass(KtFunction::class.java)!!

                assertThat(suppressor.shouldSuppress(buildFinding(ktFunction))).isFalse()
            }

            @Test
            fun `With glob2`() {
                val suppressor = annotationSuppressorFactory(
                    buildConfigAware("ignoreAnnotated" to listOf("**.Composable")),
                    binding,
                )!!

                val ktFunction = root.findChildByClass(KtFunction::class.java)!!

                assertThat(suppressor.shouldSuppress(buildFinding(ktFunction))).isTrue()
            }

            @Test
            fun `With glob3`() {
                val suppressor = annotationSuppressorFactory(
                    buildConfigAware("ignoreAnnotated" to listOf("Compo*")),
                    binding,
                )!!

                val ktFunction = root.findChildByClass(KtFunction::class.java)!!

                assertThat(suppressor.shouldSuppress(buildFinding(ktFunction))).isTrue()
            }

            @Test
            fun `With glob4`() {
                val suppressor = annotationSuppressorFactory(
                    buildConfigAware("ignoreAnnotated" to listOf("*")),
                    binding,
                )!!

                val ktFunction = root.findChildByClass(KtFunction::class.java)!!

                assertThat(suppressor.shouldSuppress(buildFinding(ktFunction))).isTrue()
            }
        }

        @Test
        fun `Doesn't mix annotations`() {
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

        @Test
        fun `Works when no using imports`() {
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

        @Test
        fun `Works when using import alias`() {
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
}
