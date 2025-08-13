package io.gitlab.arturbosch.detekt.rules.style

import dev.detekt.api.ValueWithReason
import dev.detekt.test.TestConfig
import dev.detekt.test.assertThat
import dev.detekt.test.lintWithContext
import dev.detekt.test.toConfig
import dev.detekt.test.utils.KotlinCoreEnvironmentTest
import dev.detekt.test.utils.KotlinEnvironmentContainer
import org.intellij.lang.annotations.Language
import org.junit.jupiter.api.Test

private const val MARKER_CLASSES = "markerClasses"

@Language("kotlin")
private const val ANNOTAION_DECLARATIONS = """
    package annotations

    @RequiresOptIn(message = "This API is forbidden.")
    @Retention(AnnotationRetention.BINARY)
    @Target(AnnotationTarget.CLASS, AnnotationTarget.FUNCTION)
    annotation class ForbiddenApi

    @RequiresOptIn(message = "This API is not allowed.")
    @Retention(AnnotationRetention.BINARY)
    @Target(AnnotationTarget.CLASS, AnnotationTarget.FUNCTION)
    annotation class DoNotUseApi

    @RequiresOptIn(message = "This API is ok to be used.")
    @Retention(AnnotationRetention.BINARY)
    @Target(AnnotationTarget.CLASS, AnnotationTarget.FUNCTION)
    annotation class AllowedApi
"""

@KotlinCoreEnvironmentTest
class ForbiddenOptInSpec(val env: KotlinEnvironmentContainer) {
    private val optInConfig = TestConfig(
        MARKER_CLASSES to listOf(
            ValueWithReason("ForbiddenApi").toConfig(),
            ValueWithReason("DoNotUseApi", "Do not use!").toConfig(),
        )
    )

    @Test
    fun `should report single forbidden opt-in without a stating a reason`() {
        val code = """
            import annotations.*

            @OptIn(ForbiddenApi::class)
            fun main() {}
        """.trimIndent()
        val findings = ForbiddenOptIn(optInConfig).lintWithContext(env, code, ANNOTAION_DECLARATIONS)

        assertThat(findings).singleElement()
            .hasSourceLocation(3, 1)
            .hasMessage("The opt-in `ForbiddenApi` has been forbidden in the detekt config.")
    }

    @Test
    fun `should report single forbidden opt-in showing the reason`() {
        val code = """
            import annotations.*

            @OptIn(DoNotUseApi::class)
            fun main() {}
        """.trimIndent()
        val findings = ForbiddenOptIn(optInConfig).lintWithContext(env, code, ANNOTAION_DECLARATIONS)

        assertThat(findings).singleElement()
            .hasSourceLocation(3, 1)
            .hasMessage("The opt-in `DoNotUseApi` has been forbidden: Do not use!")
    }

    @Test
    fun `should report multiple forbidden opt-ins`() {
        val code = """
            import annotations.*

            @OptIn(DoNotUseApi::class, AllowedApi::class, ForbiddenApi::class)
            fun main() {}
        """.trimIndent()
        val findings = ForbiddenOptIn(optInConfig).lintWithContext(env, code, ANNOTAION_DECLARATIONS)

        assertThat(findings)
            .extracting("message")
            .containsExactlyInAnyOrder(
                "The opt-in `DoNotUseApi` has been forbidden: Do not use!",
                "The opt-in `ForbiddenApi` has been forbidden in the detekt config."
            )
    }
}
