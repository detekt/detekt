package io.gitlab.arturbosch.detekt.rules.bugs

import io.gitlab.arturbosch.detekt.test.TestConfig
import io.gitlab.arturbosch.detekt.test.compileAndLint
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatExceptionOfType
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import java.util.regex.PatternSyntaxException

class LateinitUsageSpec {

    val code = """
        import kotlin.SinceKotlin
        
        class SomeRandomTest {
            lateinit var v1: String
            @SinceKotlin("1.0.0") lateinit var v2: String
        }
    """.trimIndent()

    @Test
    fun `should report lateinit usages`() {
        val findings = LateinitUsage().compileAndLint(code)
        assertThat(findings).hasSize(2)
    }

    @Test
    @DisplayName("should only report lateinit property with no @SinceKotlin annotation")
    fun `should only report lateinit property with no SinceKotlin annotation`() {
        val findings =
            LateinitUsage(TestConfig(EXCLUDE_ANNOTATED_PROPERTIES to listOf("SinceKotlin"))).compileAndLint(
                code
            )
        assertThat(findings).hasSize(1)
    }

    @Test
    @DisplayName("should only report lateinit properties not matching kotlin.*")
    fun `should only report lateinit properties not matching any kotlin annotation`() {
        val findings =
            LateinitUsage(TestConfig(EXCLUDE_ANNOTATED_PROPERTIES to listOf("kotlin.*"))).compileAndLint(code)
        assertThat(findings).hasSize(1)
    }

    @Test
    fun `should only report lateinit properties matching kotlin_SinceKotlin`() {
        val config = TestConfig(EXCLUDE_ANNOTATED_PROPERTIES to listOf("kotlin.SinceKotlin"))
        val findings = LateinitUsage(config).compileAndLint(code)
        assertThat(findings).hasSize(1)
    }

    @Test
    fun `should only report lateinit property with no @SinceKotlin annotation with config string`() {
        val findings =
            LateinitUsage(TestConfig(EXCLUDE_ANNOTATED_PROPERTIES to "SinceKotlin")).compileAndLint(code)
        assertThat(findings).hasSize(1)
    }

    @Test
    fun `should only report lateinit property with no @SinceKotlin annotation containing whitespaces with config string`() {
        val findings =
            LateinitUsage(TestConfig(EXCLUDE_ANNOTATED_PROPERTIES to " SinceKotlin ")).compileAndLint(code)
        assertThat(findings).hasSize(1)
    }

    @Test
    fun `should report lateinit properties not matching the exclude pattern`() {
        val findings =
            LateinitUsage(TestConfig(EXCLUDE_ANNOTATED_PROPERTIES to "IgnoreThis")).compileAndLint(code)
        assertThat(findings).hasSize(2)
    }

    @Test
    fun `should report lateinit properties when ignoreOnClassesPattern does not match`() {
        val findings =
            LateinitUsage(TestConfig(IGNORE_ON_CLASSES_PATTERN to "[\\w]+Test1234")).compileAndLint(code)
        assertThat(findings).hasSize(2)
    }

    @Test
    fun `should not report lateinit properties when ignoreOnClassesPattern does match`() {
        val findings =
            LateinitUsage(TestConfig(IGNORE_ON_CLASSES_PATTERN to "[\\w]+Test")).compileAndLint(code)
        assertThat(findings).isEmpty()
    }

    @Test
    fun `should fail when enabled with faulty regex pattern`() {
        assertThatExceptionOfType(PatternSyntaxException::class.java).isThrownBy {
            LateinitUsage(TestConfig(IGNORE_ON_CLASSES_PATTERN to "*Test")).compileAndLint(code)
        }
    }

    @Test
    fun `should not fail when disabled with faulty regex pattern`() {
        val findings = LateinitUsage(
            TestConfig("active" to "false", IGNORE_ON_CLASSES_PATTERN to "*Test")
        ).compileAndLint(code)
        assertThat(findings).isEmpty()
    }
}

private const val EXCLUDE_ANNOTATED_PROPERTIES = "excludeAnnotatedProperties"
private const val IGNORE_ON_CLASSES_PATTERN = "ignoreOnClassesPattern"
