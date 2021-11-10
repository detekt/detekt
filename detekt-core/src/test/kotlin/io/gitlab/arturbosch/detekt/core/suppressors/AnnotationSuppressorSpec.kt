package io.gitlab.arturbosch.detekt.core.suppressors

import io.github.detekt.psi.FilePath
import io.github.detekt.test.utils.compileContentForTest
import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.ConfigAware
import io.gitlab.arturbosch.detekt.api.Debt
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Finding
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Location
import io.gitlab.arturbosch.detekt.api.RuleId
import io.gitlab.arturbosch.detekt.api.Severity
import io.gitlab.arturbosch.detekt.api.SourceLocation
import io.gitlab.arturbosch.detekt.api.TextLocation
import io.gitlab.arturbosch.detekt.test.TestConfig
import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.kotlin.psi.KtClass
import org.jetbrains.kotlin.psi.KtElement
import org.jetbrains.kotlin.psi.KtFunction
import org.jetbrains.kotlin.psi.KtParameter
import org.jetbrains.kotlin.psi.psiUtil.findDescendantOfType
import org.jetbrains.kotlin.psi.psiUtil.findFunctionByName
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe
import java.nio.file.Paths

class AnnotationSuppressorSpec : Spek({

    describe("AnnotationSuppressorFactory") {
        it("Factory returns null if ignoreAnnotated is not set") {
            val suppressor = annotationSuppressorFactory(buildConfigAware(/* empty */))

            assertThat(suppressor).isNull()
        }

        it("Factory returns null if ignoreAnnotated is set to empty") {
            val suppressor = annotationSuppressorFactory(
                buildConfigAware("ignoreAnnotated" to emptyList<String>())
            )

            assertThat(suppressor).isNull()
        }

        it("Factory returns not null if ignoreAnnotated is set to a not empty list") {
            val suppressor = annotationSuppressorFactory(
                buildConfigAware("ignoreAnnotated" to listOf("Composable"))
            )

            assertThat(suppressor).isNotNull()
        }
    }

    describe("AnnotationSuppressor") {
        val suppressor by memoized {
            annotationSuppressorFactory(buildConfigAware("ignoreAnnotated" to listOf("Composable")))!!
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
})

private fun buildFinding(element: KtElement?): Finding = CodeSmell(
    issue = Issue("RuleName", Severity.CodeSmell, "", Debt.FIVE_MINS),
    entity = element?.let { Entity.from(element) } ?: buildEmptyEntity(),
    message = "",
)

private fun buildEmptyEntity(): Entity = Entity(
    name = "",
    signature = "",
    location = Location(SourceLocation(0, 0), TextLocation(0, 0), FilePath.fromAbsolute(Paths.get("/"))),
    ktElement = null,
)

private fun buildConfigAware(
    vararg pairs: Pair<String, Any>
) = object : ConfigAware {
    override val ruleId: RuleId = "ruleId"
    override val ruleSetConfig: Config = TestConfig(*pairs)
}
