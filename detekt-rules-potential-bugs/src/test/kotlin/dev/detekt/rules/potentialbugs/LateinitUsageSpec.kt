package dev.detekt.rules.potentialbugs

import dev.detekt.api.Config
import dev.detekt.test.TestConfig
import dev.detekt.test.lint
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatExceptionOfType
import org.junit.jupiter.api.Test
import java.util.regex.PatternSyntaxException

class LateinitUsageSpec {

    val code = """
        import kotlin.SinceKotlin
        
        class SomeRandomTest {
            lateinit var v1: String
            @SinceKotlin("1.0.0") private lateinit var v2: String
        }
    """.trimIndent()

    @Test
    fun `should report lateinit usages`() {
        val findings = LateinitUsage(Config.empty).lint(code)
        assertThat(findings).hasSize(2)
    }

    @Test
    fun `should report lateinit properties when ignoreOnClassesPattern does not match`() {
        val findings =
            LateinitUsage(TestConfig(IGNORE_ON_CLASSES_PATTERN to "[\\w]+Test1234")).lint(code)
        assertThat(findings).hasSize(2)
    }

    @Test
    fun `should not report lateinit properties when ignoreOnClassesPattern does match`() {
        val findings =
            LateinitUsage(TestConfig(IGNORE_ON_CLASSES_PATTERN to "[\\w]+Test")).lint(code)
        assertThat(findings).isEmpty()
    }

    @Test
    fun `should fail when enabled with faulty regex pattern`() {
        assertThatExceptionOfType(PatternSyntaxException::class.java).isThrownBy {
            LateinitUsage(TestConfig(IGNORE_ON_CLASSES_PATTERN to "*Test")).lint(code)
        }
    }
}

private const val IGNORE_ON_CLASSES_PATTERN = "ignoreOnClassesPattern"
