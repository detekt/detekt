package dev.detekt.rules.emptyblocks

import dev.detekt.api.Config
import dev.detekt.test.assertj.assertThat
import dev.detekt.test.lint
import org.jetbrains.kotlin.config.LanguageFeature
import org.jetbrains.kotlin.config.LanguageVersionSettingsImpl
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test

class EmptyClassBlockSpec {

    private val subject = EmptyClassBlock(Config.empty)

    @Test
    fun `reports the empty class body`() {
        val code = "class SomeClass {}"
        assertThat(subject.lint(code)).hasSize(1)
    }

    @Test
    fun `does not report class with comments in the body`() {
        val code = """
            class SomeClass {
                // Some comment to explain what this class is supposed to do
            }
        """.trimIndent()
        assertThat(subject.lint(code)).isEmpty()
    }

    @Test
    fun `does not report class with multiline comments in the body`() {
        val code = """
            class SomeClass {
                /*
                Some comment to explain what this class is supposed to do
                */
            }
        """.trimIndent()
        assertThat(subject.lint(code)).isEmpty()
    }

    @Test
    fun `reports the empty nested class body`() {
        val code = """
            class SomeClass {
                class EmptyClass {}
            }
        """.trimIndent()
        assertThat(subject.lint(code)).hasSize(1)
    }

    @Test
    fun `reports the empty object body`() {
        val code = "object SomeObject {}"
        val findings = subject.lint(code)
        assertThat(findings).singleElement()
            .hasTextLocation(18 to 20)
    }

    @Test
    fun `does not report the object if it is of an anonymous class`() {
        val code = """
            open class Open
            
            fun f() {
                 object : Open() {}
            }
        """.trimIndent()
        assertThat(subject.lint(code)).isEmpty()
    }

    @Test
    @Disabled("False positive with the preview feature `companion block`, https://github.com/detekt/detekt/issues/9744")
    fun `does not report a class whose body only contains a companion block`() {
        val code = """
            data class A(
                val s: String
            ) {
                companion {
                    @JvmField
                    val Empty = A("")
                }
            }
        """.trimIndent()
        assertThat(
            subject.lint(code, languageVersionSettings = companionBlocksAndExtensionsEnabled)
        ).isEmpty()
    }
}

private val companionBlocksAndExtensionsEnabled = LanguageVersionSettingsImpl(
    languageVersion = LanguageVersionSettingsImpl.DEFAULT.languageVersion,
    apiVersion = LanguageVersionSettingsImpl.DEFAULT.apiVersion,
    specificFeatures = mapOf(LanguageFeature.CompanionBlocksAndExtensions to LanguageFeature.State.ENABLED),
)
