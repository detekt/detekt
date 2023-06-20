package io.gitlab.arturbosch.detekt.rules.bugs

import io.gitlab.arturbosch.detekt.test.TestConfig
import io.gitlab.arturbosch.detekt.test.compileAndLint
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatExceptionOfType
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
        val config = TestConfig(
            "active" to "false",
            IGNORE_ON_CLASSES_PATTERN to "*Test"
        )
        val findings = LateinitUsage(config).compileAndLint(code)
        assertThat(findings).isEmpty()
    }
}

private const val EXCLUDE_ANNOTATED_PROPERTIES = "excludeAnnotatedProperties"
private const val IGNORE_ON_CLASSES_PATTERN = "ignoreOnClassesPattern"
