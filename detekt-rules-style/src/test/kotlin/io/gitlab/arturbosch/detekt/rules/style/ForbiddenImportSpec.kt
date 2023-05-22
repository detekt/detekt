package io.gitlab.arturbosch.detekt.rules.style

import io.gitlab.arturbosch.detekt.api.ValueFormat.GLOB
import io.gitlab.arturbosch.detekt.api.ValueWithReason
import io.gitlab.arturbosch.detekt.test.TestConfig
import io.gitlab.arturbosch.detekt.test.lint
import io.gitlab.arturbosch.detekt.test.toConfig
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

private const val IMPORTS = "imports"
private const val FORBIDDEN_PATTERNS = "forbiddenPatterns"

class ForbiddenImportSpec {
    val code = """
        package foo
        
        import kotlin.jvm.JvmField
        import kotlin.SinceKotlin
        
        import com.example.R.string
        import net.example.R.dimen
        import net.example.R.dimension
    """.trimIndent()

    @Test
    fun `should report nothing by default`() {
        val findings = ForbiddenImport().lint(code)
        assertThat(findings).isEmpty()
    }

    @Test
    fun `should report nothing when imports are blank`() {
        val findings = ForbiddenImport(TestConfig(IMPORTS to listOf("  "))).lint(code)
        assertThat(findings).isEmpty()
    }

    @Test
    fun `should report nothing when imports do not match`() {
        val config = TestConfig(
            IMPORTS to listOf(
                ValueWithReason(value = "something.very.Specific").toConfig(),
                ValueWithReason(value = "org.*", format = GLOB).toConfig()
            )
        )
        val findings = ForbiddenImport(config).lint(code)
        assertThat(findings).isEmpty()
    }

    @Test
    @DisplayName("should report kotlin.* when imports are kotlin.*")
    fun reportKotlinWildcardImports() {
        val config = TestConfig(
            IMPORTS to listOf(
                ValueWithReason(value = "kotlin.*", format = GLOB).toConfig()
            )
        )
        val findings = ForbiddenImport(config).lint(code)
        assertThat(findings)
            .extracting("message")
            .containsExactlyInAnyOrder(
                "The import `kotlin.jvm.JvmField` has been forbidden in the detekt config.",
                "The import `kotlin.SinceKotlin` has been forbidden in the detekt config.",
            )
    }

    @Test
    @DisplayName("should report kotlin.* when imports are kotlin.* with reasons")
    fun reportKotlinWildcardImports2() {
        val config = TestConfig(
            IMPORTS to listOf(
                ValueWithReason("kotlin.*", "I'm just joking!", format = GLOB).toConfig()
            )
        )
        val findings = ForbiddenImport(config).lint(code)
        assertThat(findings).hasSize(2)
        assertThat(findings[0].message)
            .isEqualTo("The import `kotlin.jvm.JvmField` has been forbidden: I'm just joking!")
        assertThat(findings[1].message)
            .isEqualTo("The import `kotlin.SinceKotlin` has been forbidden: I'm just joking!")
    }

    @Test
    @DisplayName("should report kotlin.SinceKotlin when specified via fully qualified name")
    fun reportKotlinSinceKotlinWhenFqdnSpecified() {
        val config = TestConfig(IMPORTS to listOf("kotlin.SinceKotlin"))
        val findings = ForbiddenImport(config).lint(code)
        assertThat(findings).hasSize(1)
    }

    @Test
    @DisplayName("should report kotlin.SinceKotlin and kotlin.jvm.JvmField when specified via fully qualified names")
    fun reportMultipleConfiguredImportsCommaSeparated() {
        val config = TestConfig(IMPORTS to listOf("kotlin.SinceKotlin", "kotlin.jvm.JvmField"))
        val findings = ForbiddenImport(config).lint(code)
        assertThat(findings).hasSize(2)
    }

    @Test
    @DisplayName(
        "should report kotlin.SinceKotlin and kotlin.jvm.JvmField when specified via fully qualified names list"
    )
    fun reportMultipleConfiguredImportsInList() {
        val config = TestConfig(IMPORTS to listOf("kotlin.SinceKotlin", "kotlin.jvm.JvmField"))
        val findings = ForbiddenImport(config).lint(code)
        assertThat(findings).hasSize(2)
    }

    @Test
    @DisplayName("should report kotlin.SinceKotlin when specified via kotlin.Since*")
    fun reportsKotlinSinceKotlinWhenSpecifiedWithWildcard() {
        val config = TestConfig(
            IMPORTS to listOf(
                ValueWithReason(value = "kotlin.Since*", format = GLOB).toConfig()
            )
        )
        val findings = ForbiddenImport(config).lint(code)
        assertThat(findings).hasSize(1)
    }

    @Test
    @DisplayName("should report all of com.example.R.string, net.example.R.dimen, and net.example.R.dimension")
    fun preAndPostWildcard() {
        val config = TestConfig(
            IMPORTS to listOf(
                ValueWithReason(value = "*.R.*", format = GLOB).toConfig()
            )
        )
        val findings = ForbiddenImport(config).lint(code)
        assertThat(findings).hasSize(3)
    }

    @Test
    @DisplayName("should report net.example.R.dimen but not net.example.R.dimension")
    fun doNotReportSubstringOfFqdn() {
        val config = TestConfig(IMPORTS to listOf("net.example.R.dimen"))
        val findings = ForbiddenImport(config).lint(code)
        assertThat(findings).hasSize(1)
    }

    @Test
    fun `should not report import when it does not match any pattern`() {
        val config = TestConfig(FORBIDDEN_PATTERNS to "nets.*R")
        val findings = ForbiddenImport(config).lint(code)
        assertThat(findings).isEmpty()
    }

    @Test
    fun `should report import when it matches the forbidden pattern`() {
        val config = TestConfig(FORBIDDEN_PATTERNS to "net.*R|com.*expiremental")
        val findings = ForbiddenImport(config).lint(code)
        assertThat(findings)
            .extracting("message")
            .containsExactlyInAnyOrder(
                "The import `net.example.R.dimen` has been forbidden in the detekt config.",
                "The import `net.example.R.dimension` has been forbidden in the detekt config."
            )
    }
}
