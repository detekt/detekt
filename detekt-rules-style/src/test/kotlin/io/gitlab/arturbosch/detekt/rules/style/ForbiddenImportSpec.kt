package io.gitlab.arturbosch.detekt.rules.style

import dev.detekt.api.Config
import dev.detekt.api.ValueWithReason
import dev.detekt.test.TestConfig
import dev.detekt.test.assertj.assertThat
import dev.detekt.test.lint
import dev.detekt.test.toConfig
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
        val findings = ForbiddenImport(Config.empty).lint(code, compile = false)
        assertThat(findings).isEmpty()
    }

    @Test
    fun `should report nothing when imports are blank`() {
        val findings = ForbiddenImport(TestConfig(IMPORTS to listOf("  "))).lint(code, compile = false)
        assertThat(findings).isEmpty()
    }

    @Test
    fun `should report nothing when imports do not match`() {
        val findings = ForbiddenImport(TestConfig(IMPORTS to listOf("org.*"))).lint(code, compile = false)
        assertThat(findings).isEmpty()
    }

    @Test
    @DisplayName("should report kotlin.* when imports are kotlin.*")
    fun reportKotlinWildcardImports() {
        val findings = ForbiddenImport(TestConfig(IMPORTS to listOf("kotlin.*"))).lint(code, compile = false)
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
        val config = TestConfig(IMPORTS to listOf(ValueWithReason("kotlin.*", "I'm just joking!").toConfig()))
        val findings = ForbiddenImport(config).lint(code, compile = false)
        assertThat(findings).satisfiesExactlyInAnyOrder(
            { assertThat(it).hasMessage("The import `kotlin.jvm.JvmField` has been forbidden: I'm just joking!") },
            { assertThat(it).hasMessage("The import `kotlin.SinceKotlin` has been forbidden: I'm just joking!") },
        )
    }

    @Test
    @DisplayName("should report kotlin.SinceKotlin when specified via fully qualified name")
    fun reportKotlinSinceKotlinWhenFqdnSpecified() {
        val findings = ForbiddenImport(TestConfig(IMPORTS to listOf("kotlin.SinceKotlin"))).lint(code, compile = false)
        assertThat(findings)
            .hasSize(1)
    }

    @Test
    @DisplayName("should report kotlin.SinceKotlin and kotlin.jvm.JvmField when specified via fully qualified names")
    fun reportMultipleConfiguredImportsCommaSeparated() {
        val findings = ForbiddenImport(TestConfig(IMPORTS to listOf("kotlin.SinceKotlin", "kotlin.jvm.JvmField")))
            .lint(code, compile = false)
        assertThat(findings).hasSize(2)
    }

    @Test
    @DisplayName(
        "should report kotlin.SinceKotlin and kotlin.jvm.JvmField when specified via fully qualified names list"
    )
    fun reportMultipleConfiguredImportsInList() {
        val findings = ForbiddenImport(TestConfig(IMPORTS to listOf("kotlin.SinceKotlin", "kotlin.jvm.JvmField")))
            .lint(code, compile = false)
        assertThat(findings).hasSize(2)
    }

    @Test
    @DisplayName("should report kotlin.SinceKotlin when specified via kotlin.Since*")
    fun reportsKotlinSinceKotlinWhenSpecifiedWithWildcard() {
        val findings = ForbiddenImport(TestConfig(IMPORTS to listOf("kotlin.Since*")))
            .lint(code, compile = false)
        assertThat(findings).hasSize(1)
    }

    @Test
    @DisplayName("should report all of com.example.R.string, net.example.R.dimen, and net.example.R.dimension")
    fun preAndPostWildcard() {
        val findings = ForbiddenImport(TestConfig(IMPORTS to listOf("*.R.*"))).lint(code, compile = false)
        assertThat(findings).hasSize(3)
    }

    @Test
    @DisplayName("should report net.example.R.dimen but not net.example.R.dimension")
    fun doNotReportSubstringOfFqdn() {
        val findings =
            ForbiddenImport(TestConfig(IMPORTS to listOf("net.example.R.dimen"))).lint(code, compile = false)
        assertThat(findings).hasSize(1)
    }

    @Test
    fun `should not report import when it does not match any pattern`() {
        val findings =
            ForbiddenImport(TestConfig(FORBIDDEN_PATTERNS to "nets.*R")).lint(code, compile = false)
        assertThat(findings).isEmpty()
    }

    @Test
    fun `should report import when it matches the forbidden pattern`() {
        val findings = ForbiddenImport(TestConfig(FORBIDDEN_PATTERNS to "net.*R|com.*expiremental"))
            .lint(code, compile = false)
        assertThat(findings).satisfiesExactlyInAnyOrder(
            { assertThat(it).hasMessage("The import `net.example.R.dimen` has been forbidden in the detekt config.") },
            { assertThat(it).hasMessage("The import `net.example.R.dimension` has been forbidden in the detekt config.") },
        )
    }
}
