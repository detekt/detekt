package io.gitlab.arturbosch.detekt.rules.style

import io.github.detekt.test.utils.compileContentForTest
import io.gitlab.arturbosch.detekt.api.internal.CompilerResources
import io.gitlab.arturbosch.detekt.test.assertThat
import io.gitlab.arturbosch.detekt.test.compileAndLint
import io.mockk.every
import io.mockk.mockk
import org.jetbrains.kotlin.config.AnalysisFlags
import org.jetbrains.kotlin.config.ExplicitApiMode
import org.jetbrains.kotlin.config.LanguageVersionSettings
import org.jetbrains.kotlin.resolve.calls.smartcasts.DataFlowValueFactoryImpl
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

class RedundantVisibilityModifierRuleSpec : Spek({
    val subject by memoized { RedundantVisibilityModifierRule() }

    describe("RedundantVisibilityModifier rule") {
        it("does not report overridden function of abstract class w/ public modifier") {
            val code = """
                abstract class A {
                    abstract protected fun A()
                }

                class Test : A() {
                    override public fun A() {}
                }
            """
            assertThat(subject.compileAndLint(code)).isEmpty()
        }

        it("does not report overridden function of abstract class w/o public modifier") {
            val code = """
                abstract class A {
                    abstract protected fun A()
                }

                class Test : A() {
                    override fun A() {}
                }
            """
            assertThat(subject.compileAndLint(code)).isEmpty()
        }

        it("does not report overridden function of interface") {
            val code = """
                interface A {
                    fun A()
                }

                class Test : A {
                    override public fun A() {}
                }
            """
            assertThat(subject.compileAndLint(code)).isEmpty()
        }

        it("should ignore the issue by alias suppression") {
            val code = """
                class Test{
                    @Suppress("RedundantVisibilityModifier")
                    public fun A() {}
                }
            """
            assertThat(subject.compileAndLint(code)).isEmpty()
        }

        it("reports public function in class") {
            val code = """
                class Test{
                    public fun A() {}
                }
            """
            assertThat(subject.compileAndLint(code)).hasSize(1)
        }

        it("does not report function in class w/o modifier") {
            val code = """
                class Test{
                    fun A() {}
                }
            """
            assertThat(subject.compileAndLint(code)).isEmpty()
        }

        it("reports public class") {
            val code = """
                public class Test(){
                    fun test(){}
                }
            """
            assertThat(subject.compileAndLint(code)).hasSize(1)
        }

        it("reports interface w/ public modifier") {
            val code = """
                public interface Test{
                    public fun test()
                }
            """
            assertThat(subject.compileAndLint(code)).hasSize(2)
        }

        it("reports field w/ public modifier") {
            val code = """
                class Test{
                    public val str : String = "test"
                }
            """
            assertThat(subject.compileAndLint(code)).hasSize(1)
        }

        it("does not report field w/o public modifier") {
            val code = """
                class Test{
                    val str : String = "test"
                }
            """
            assertThat(subject.compileAndLint(code)).isEmpty()
        }

        it("does not report overridden field w/o public modifier") {
            val code = """
                abstract class A {
                    abstract val test: String
                }

                class B : A() {
                    override val test: String = "valid"
                }
            """
            assertThat(subject.compileAndLint(code)).isEmpty()
        }

        it("does not report overridden field w/ public modifier") {
            val code = """
                abstract class A {
                    abstract val test: String
                }

                class B : A() {
                    override public val test: String = "valid"
                }
            """
            assertThat(subject.compileAndLint(code)).isEmpty()
        }

        it("reports internal modifier on nested class in private object") {
            val code = """
                private object A {

                    internal class InternalClass

                }
            """
            assertThat(subject.compileAndLint(code)).hasSize(1)
        }

        it("reports internal modifier on function declaration in private object") {
            val code = """
                private object A {

                    internal fun internalFunction() {}

                }
            """
            assertThat(subject.compileAndLint(code)).hasSize(1)
        }

        describe("Explicit API mode") {

            val code by memoized {
                compileContentForTest("""
                    public class A() {
                        fun f()
                    }"""
                )
            }
            val rule by memoized { RedundantVisibilityModifierRule() }

            fun mockCompilerResources(mode: ExplicitApiMode): CompilerResources {
                val languageVersionSettings = mockk<LanguageVersionSettings>()
                every { languageVersionSettings.getFlag(AnalysisFlags.explicitApiMode) } returns mode
                @Suppress("DEPRECATION")
                return CompilerResources(languageVersionSettings, DataFlowValueFactoryImpl(languageVersionSettings))
            }

            it("does not report public function in class if explicit API mode is set to strict") {
                rule.visitFile(code, compilerResources = mockCompilerResources(ExplicitApiMode.STRICT))
                assertThat(rule.findings).isEmpty()
            }

            it("reports public function in class if explicit API mode is disabled") {
                rule.visitFile(code, compilerResources = mockCompilerResources(ExplicitApiMode.DISABLED))
                assertThat(rule.findings).hasSize(1)
            }

            it("reports public function in class if compiler resources are not available") {
                rule.visitFile(code, compilerResources = null)
                assertThat(rule.findings).hasSize(1)
            }
        }
    }
})
