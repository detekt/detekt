package dev.detekt.rules.style

import dev.detekt.api.Config
import dev.detekt.test.assertj.assertThat
import dev.detekt.test.junit.KotlinCoreEnvironmentTest
import dev.detekt.test.lintWithContext
import dev.detekt.test.utils.KotlinEnvironmentContainer
import org.jetbrains.kotlin.config.LanguageFeature
import org.jetbrains.kotlin.config.LanguageVersionSettingsImpl
import org.junit.jupiter.api.Test

@KotlinCoreEnvironmentTest
class CollapseAnnotationUseSiteTargetsSpec(val env: KotlinEnvironmentContainer) {

    @Test
    fun `reports every applicable target on a primary constructor property`() {
        val code = """
            @Target(
                AnnotationTarget.VALUE_PARAMETER,
                AnnotationTarget.PROPERTY,
                AnnotationTarget.FIELD,
                AnnotationTarget.PROPERTY_GETTER,
            )
            annotation class Marker

            class Example(
                @param:Marker
                @property:Marker
                @field:Marker
                @get:Marker
                val value: String,
            )
        """.trimIndent()

        val findings = CollapseAnnotationUseSiteTargets(Config.empty).lintWithContext(env, code)

        assertThat(findings).singleElement()
            .hasTextLocation("@param:Marker")
            .hasMessage(
                "Annotation `Marker` manually targets every applicable property site. " +
                    "Replace these annotations with `@all:Marker`.",
            )
    }

    @Test
    fun `includes the setter parameter for a mutable constructor property`() {
        val code = """
            @Target(
                AnnotationTarget.VALUE_PARAMETER,
                AnnotationTarget.PROPERTY,
                AnnotationTarget.FIELD,
                AnnotationTarget.PROPERTY_GETTER,
            )
            annotation class Marker

            class Example(
                @param:Marker
                @property:Marker
                @field:Marker
                @get:Marker
                @setparam:Marker
                var value: String,
            )
        """.trimIndent()

        val findings = CollapseAnnotationUseSiteTargets(Config.empty).lintWithContext(env, code)

        assertThat(findings).hasSize(1)
    }

    @Test
    fun `reports every applicable target on a member property`() {
        val code = """
            @Target(
                AnnotationTarget.VALUE_PARAMETER,
                AnnotationTarget.PROPERTY,
                AnnotationTarget.FIELD,
                AnnotationTarget.PROPERTY_GETTER,
            )
            annotation class Marker

            class Example {
                @property:Marker
                @field:Marker
                @get:Marker
                val value: String = "value"
            }
        """.trimIndent()

        val findings = CollapseAnnotationUseSiteTargets(Config.empty).lintWithContext(env, code)

        assertThat(findings).hasSize(1)
    }

    @Test
    fun `does not require a field for an abstract property`() {
        val code = """
            @Target(
                AnnotationTarget.PROPERTY,
                AnnotationTarget.FIELD,
                AnnotationTarget.PROPERTY_GETTER,
            )
            annotation class Marker

            interface Example {
                @property:Marker
                @get:Marker
                val value: String
            }
        """.trimIndent()

        val findings = CollapseAnnotationUseSiteTargets(Config.empty).lintWithContext(env, code)

        assertThat(findings).hasSize(1)
    }

    @Test
    fun `does not include the receiver of an extension property`() {
        val code = """
            @Target(
                AnnotationTarget.PROPERTY,
                AnnotationTarget.FIELD,
                AnnotationTarget.PROPERTY_GETTER,
                AnnotationTarget.VALUE_PARAMETER,
            )
            annotation class Marker

            @property:Marker
            @get:Marker
            val String.firstCharacter: Char
                get() = first()
        """.trimIndent()

        val findings = CollapseAnnotationUseSiteTargets(Config.empty).lintWithContext(env, code)

        assertThat(findings).hasSize(1)
    }

    @Test
    fun `reports grouped annotation syntax`() {
        val code = """
            @Target(AnnotationTarget.FIELD, AnnotationTarget.PROPERTY_GETTER)
            annotation class Marker

            class Example {
                @field:[Marker]
                @get:Marker
                val value: String = "value"
            }
        """.trimIndent()

        val findings = CollapseAnnotationUseSiteTargets(Config.empty).lintWithContext(env, code)

        assertThat(findings).hasSize(1)
    }

    @Test
    fun `uses the targets declared by the annotation`() {
        val code = """
            @Target(AnnotationTarget.FIELD, AnnotationTarget.PROPERTY_GETTER)
            annotation class Marker

            class Example {
                @field:Marker
                @get:Marker
                val value: String = "value"
            }
        """.trimIndent()

        val findings = CollapseAnnotationUseSiteTargets(Config.empty).lintWithContext(env, code)

        assertThat(findings).hasSize(1)
    }

    @Test
    fun `does not report an annotation with a single applicable property target`() {
        val code = """
            @Target(AnnotationTarget.PROPERTY)
            annotation class Marker

            class Example {
                @property:Marker
                val value: String = "value"
            }
        """.trimIndent()

        val findings = CollapseAnnotationUseSiteTargets(Config.empty).lintWithContext(env, code)

        assertThat(findings).isEmpty()
    }

    @Test
    fun `uses the default annotation targets`() {
        val code = """
            annotation class Marker

            class Example(
                @param:Marker
                @property:Marker
                @field:Marker
                @get:Marker
                val value: String,
            )
        """.trimIndent()

        val findings = CollapseAnnotationUseSiteTargets(Config.empty).lintWithContext(env, code)

        assertThat(findings).hasSize(1)
    }

    @Test
    fun `does not report when an applicable target is missing`() {
        val code = """
            @Target(
                AnnotationTarget.VALUE_PARAMETER,
                AnnotationTarget.PROPERTY,
                AnnotationTarget.FIELD,
                AnnotationTarget.PROPERTY_GETTER,
            )
            annotation class Marker

            class Example(
                @param:Marker
                @property:Marker
                @field:Marker
                val value: String,
            )
        """.trimIndent()

        val findings = CollapseAnnotationUseSiteTargets(Config.empty).lintWithContext(env, code)

        assertThat(findings).isEmpty()
    }

    @Test
    fun `does not combine annotation applications with different arguments`() {
        val code = """
            @Target(
                AnnotationTarget.VALUE_PARAMETER,
                AnnotationTarget.PROPERTY,
                AnnotationTarget.FIELD,
                AnnotationTarget.PROPERTY_GETTER,
            )
            annotation class Marker(val value: Int)

            class Example(
                @param:Marker(1)
                @property:Marker(1)
                @field:Marker(1)
                @get:Marker(2)
                val value: String,
            )
        """.trimIndent()

        val findings = CollapseAnnotationUseSiteTargets(Config.empty).lintWithContext(env, code)

        assertThat(findings).isEmpty()
    }

    @Test
    fun `does not combine syntactically different arguments`() {
        val code = """
            @Target(
                AnnotationTarget.VALUE_PARAMETER,
                AnnotationTarget.PROPERTY,
                AnnotationTarget.FIELD,
                AnnotationTarget.PROPERTY_GETTER,
            )
            annotation class Marker(val value: Int)

            class Example(
                @param:Marker(1)
                @property:Marker(value = 1)
                @field:Marker(1)
                @get:Marker(1)
                val value: String,
            )
        """.trimIndent()

        val findings = CollapseAnnotationUseSiteTargets(Config.empty).lintWithContext(env, code)

        assertThat(findings).isEmpty()
    }

    @Test
    fun `does not inspect regular constructor parameters`() {
        val code = """
            @Target(AnnotationTarget.VALUE_PARAMETER)
            annotation class Marker

            class Example(@param:Marker value: String)
        """.trimIndent()

        val findings = CollapseAnnotationUseSiteTargets(Config.empty).lintWithContext(env, code)

        assertThat(findings).isEmpty()
    }

    @Test
    fun `does not report an annotation already using all`() {
        val code = """
            @Target(
                AnnotationTarget.VALUE_PARAMETER,
                AnnotationTarget.PROPERTY,
                AnnotationTarget.FIELD,
                AnnotationTarget.PROPERTY_GETTER,
            )
            annotation class Marker

            class Example(@all:Marker val value: String)
        """.trimIndent()

        val findings = CollapseAnnotationUseSiteTargets(Config.empty).lintWithContext(env, code)

        assertThat(findings).isEmpty()
    }

    @Test
    fun `does not report delegated properties because all cannot target them`() {
        val code = """
            @Target(AnnotationTarget.PROPERTY, AnnotationTarget.PROPERTY_GETTER)
            annotation class Marker

            class Example {
                @property:Marker
                @get:Marker
                val value: String by lazy { "value" }
            }
        """.trimIndent()

        val findings = CollapseAnnotationUseSiteTargets(Config.empty).lintWithContext(env, code)

        assertThat(findings).isEmpty()
    }

    @Test
    fun `does not report an explicit setter target because all does not propagate to it`() {
        val code = """
            @Target(
                AnnotationTarget.PROPERTY,
                AnnotationTarget.FIELD,
                AnnotationTarget.PROPERTY_GETTER,
                AnnotationTarget.PROPERTY_SETTER,
            )
            annotation class Marker

            class Example {
                @property:Marker
                @field:Marker
                @get:Marker
                @set:Marker
                var value: String = "value"
            }
        """.trimIndent()

        val findings = CollapseAnnotationUseSiteTargets(Config.empty).lintWithContext(env, code)

        assertThat(findings).isEmpty()
    }

    @Test
    fun `does not report properties in JVM records`() {
        val code = """
            @Target(
                AnnotationTarget.VALUE_PARAMETER,
                AnnotationTarget.PROPERTY,
                AnnotationTarget.FIELD,
                AnnotationTarget.PROPERTY_GETTER,
            )
            annotation class Marker

            @JvmRecord
            data class Example(
                @param:Marker
                @property:Marker
                @field:Marker
                @get:Marker
                val value: String,
            )
        """.trimIndent()

        val findings = CollapseAnnotationUseSiteTargets(Config.empty).lintWithContext(env, code)

        assertThat(findings).isEmpty()
    }

    @Test
    fun `does not report when the all use-site target language feature is disabled`() {
        val code = """
            @Target(AnnotationTarget.FIELD, AnnotationTarget.PROPERTY_GETTER)
            annotation class Marker

            class Example {
                @field:Marker
                @get:Marker
                val value: String = "value"
            }
        """.trimIndent()

        val findings = CollapseAnnotationUseSiteTargets(Config.empty).lintWithContext(
            environment = env,
            content = code,
            languageVersionSettings = allUseSiteTargetDisabled,
        )

        assertThat(findings).isEmpty()
    }
}

private val allUseSiteTargetDisabled = LanguageVersionSettingsImpl(
    languageVersion = LanguageVersionSettingsImpl.DEFAULT.languageVersion,
    apiVersion = LanguageVersionSettingsImpl.DEFAULT.apiVersion,
    specificFeatures = mapOf(LanguageFeature.AnnotationAllUseSiteTarget to LanguageFeature.State.DISABLED),
)
