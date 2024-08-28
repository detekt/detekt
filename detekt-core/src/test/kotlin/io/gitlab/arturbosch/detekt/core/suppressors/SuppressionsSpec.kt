package io.gitlab.arturbosch.detekt.core.suppressors

import io.github.detekt.test.utils.compileContentForTest
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.RuleSet
import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.kotlin.psi.KtClass
import org.jetbrains.kotlin.psi.KtElement
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.psi.KtFunction
import org.jetbrains.kotlin.psi.KtParameter
import org.jetbrains.kotlin.psi.psiUtil.findDescendantOfType
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource

class SuppressionsSpec {

    private fun KtElement.isSuppressedBy(): Boolean =
        isSuppressedBy(SuppressionTestRule(), "RuleName", setOf("alias1", "alias2"), RuleSet.Id("RuleSetId"))

    @Nested
    inner class DifferentSuppressLocation {
        @Nested
        inner class AtFile {
            val file = compileContentForTest(
                """
                    @file:Suppress("RuleName")
                    
                    class OneClass {
                        fun function(parameter: String) {
                            val a = 0
                        }
                    }
                    
                    fun topLevelFunction() = Unit
                """.trimIndent()
            )

            @Test
            fun file() {
                assertThat(file.isSuppressedBy()).isTrue()
            }

            @Test
            fun `class`() {
                assertThat(file.getClass().isSuppressedBy()).isTrue()
            }

            @Test
            fun method() {
                assertThat(file.getMethod().isSuppressedBy()).isTrue()
            }

            @Test
            fun methodParameter() {
                assertThat(file.getMethodParameter().isSuppressedBy()).isTrue()
            }

            @Test
            fun function() {
                assertThat(file.getFunction().isSuppressedBy()).isTrue()
            }
        }

        @Nested
        inner class AtClass {
            val file = compileContentForTest(
                """
                    @Suppress("RuleName")
                    class OneClass {
                        fun function(parameter: String) {
                            val a = 0
                        }
                    }
                    
                    fun topLevelFunction() = Unit
                """.trimIndent()
            )

            @Test
            fun file() {
                assertThat(file.isSuppressedBy()).isFalse()
            }

            @Test
            fun `class`() {
                assertThat(file.getClass().isSuppressedBy()).isTrue()
            }

            @Test
            fun method() {
                assertThat(file.getMethod().isSuppressedBy()).isTrue()
            }

            @Test
            fun methodParameter() {
                assertThat(file.getMethodParameter().isSuppressedBy()).isTrue()
            }

            @Test
            fun function() {
                assertThat(file.getFunction().isSuppressedBy()).isFalse()
            }
        }

        @Nested
        inner class AtMethod {
            val file = compileContentForTest(
                """
                    class OneClass {
                        @Suppress("RuleName")
                        fun function(parameter: String) {
                            val a = 0
                        }
                    }
                    
                    fun topLevelFunction() = Unit
                """.trimIndent()
            )

            @Test
            fun file() {
                assertThat(file.isSuppressedBy()).isFalse()
            }

            @Test
            fun `class`() {
                assertThat(file.getClass().isSuppressedBy()).isFalse()
            }

            @Test
            fun method() {
                assertThat(file.getMethod().isSuppressedBy()).isTrue()
            }

            @Test
            fun methodParameter() {
                assertThat(file.getMethodParameter().isSuppressedBy()).isTrue()
            }

            @Test
            fun function() {
                assertThat(file.getFunction().isSuppressedBy()).isFalse()
            }
        }

        @Nested
        inner class AtMethodParameter {
            val file = compileContentForTest(
                """
                    class OneClass {
                        fun function(@Suppress("RuleName") parameter: String) {
                            val a = 0
                        }
                    }
                    
                    fun topLevelFunction() = Unit
                """.trimIndent()
            )

            @Test
            fun file() {
                assertThat(file.isSuppressedBy()).isFalse()
            }

            @Test
            fun `class`() {
                assertThat(file.getClass().isSuppressedBy()).isFalse()
            }

            @Test
            fun method() {
                assertThat(file.getMethod().isSuppressedBy()).isFalse()
            }

            @Test
            fun methodParameter() {
                assertThat(file.getMethodParameter().isSuppressedBy()).isTrue()
            }

            @Test
            fun function() {
                assertThat(file.getFunction().isSuppressedBy()).isFalse()
            }
        }

        @Nested
        inner class AtFunction {
            val file = compileContentForTest(
                """
                    class OneClass {
                        fun function(parameter: String) {
                            val a = 0
                        }
                    }
                    
                    @Suppress("RuleName")
                    fun topLevelFunction() = Unit
                """.trimIndent()
            )

            @Test
            fun file() {
                assertThat(file.isSuppressedBy()).isFalse()
            }

            @Test
            fun `class`() {
                assertThat(file.getClass().isSuppressedBy()).isFalse()
            }

            @Test
            fun method() {
                assertThat(file.getMethod().isSuppressedBy()).isFalse()
            }

            @Test
            fun methodParameter() {
                assertThat(file.getMethodParameter().isSuppressedBy()).isFalse()
            }

            @Test
            fun function() {
                assertThat(file.getFunction().isSuppressedBy()).isTrue()
            }
        }
    }

    @ParameterizedTest
    @ValueSource(strings = ["Suppress", "SuppressWarnings"])
    fun `works with both annotations`(annotation: String) {
        assertThat(compileContentForTest("""@file:$annotation("RuleName")""").isSuppressedBy()).isTrue()
    }

    @ParameterizedTest
    @ValueSource(
        strings = ["all", "All", "ALL", "RuleName", "RuleSetId", "RuleSetId.RuleName", "RuleSetId:RuleName", "alias1", "alias2"]
    )
    fun shouldSuppress(value: String) {
        assertThat(compileContentForTest("""@file:Suppress("$value")""").isSuppressedBy()).isTrue()
        assertThat(compileContentForTest("""@file:Suppress("detekt.$value")""").isSuppressedBy()).isTrue()
        assertThat(compileContentForTest("""@file:Suppress("detekt:$value")""").isSuppressedBy()).isTrue()
        assertThat(compileContentForTest("""@file:Suppress("Detekt.$value")""").isSuppressedBy()).isTrue()
    }

    @ParameterizedTest
    @ValueSource(
        strings = ["aLL", "Rulename", "RuleName2", "RuleName.RuleSetId", "RuleSetId.alias1", "RuleSetId:alias2"]
    )
    fun `should not suppress with @Suppress annotation with unexpected value`(value: String) {
        assertThat(compileContentForTest("""@file:Suppress("$value")""").isSuppressedBy()).isFalse()
        assertThat(compileContentForTest("""@file:Suppress("detekt.$value")""").isSuppressedBy()).isFalse()
        assertThat(compileContentForTest("""@file:Suppress("detekt:$value")""").isSuppressedBy()).isFalse()
        assertThat(compileContentForTest("""@file:Suppress("Detekt.$value")""").isSuppressedBy()).isFalse()
    }

    @Test
    fun checkAllAnnotations1() {
        val file = compileContentForTest(
            """
                @file:Suppress("Foo")
                @file:SuppressWarnings("RuleName")
            """.trimIndent()
        )
        assertThat(file.isSuppressedBy()).isTrue()
    }

    @Test
    fun checkAllAnnotations2() {
        val file = compileContentForTest(
            """
                @file:Suppress("RuleName")
                @file:SuppressWarnings("Foo")
            """.trimIndent()
        )
        assertThat(file.isSuppressedBy()).isTrue()
    }
}

private fun KtFile.getClass(): KtElement = findChildByClass(KtClass::class.java)!!

private fun KtFile.getMethod(): KtElement =
    findChildByClass(KtClass::class.java)!!
        .body!!
        .children
        .single { it is KtFunction }
        .let { it as KtFunction }
        .bodyBlockExpression!!

private fun KtFile.getMethodParameter(): KtElement =
    findChildByClass(KtClass::class.java)!!
        .body!!
        .children
        .single { it is KtFunction }
        .findDescendantOfType<KtParameter>()!!

private fun KtFile.getFunction(): KtElement = findChildByClass(KtFunction::class.java)!!

private class SuppressionTestRule : Rule(Config.empty, "")
