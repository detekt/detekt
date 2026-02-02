package dev.detekt.rules.potentialbugs

import dev.detekt.api.Config
import dev.detekt.test.junit.KotlinCoreEnvironmentTest
import dev.detekt.test.lintWithContext
import dev.detekt.test.utils.KotlinEnvironmentContainer
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

@KotlinCoreEnvironmentTest
class ImplicitDefaultLocaleSpec(private val env: KotlinEnvironmentContainer) {
    private val subject = ImplicitDefaultLocale(Config.empty)

    @Test
    fun `reports String_format call with template but without explicit locale`() {
        val code = """
            fun x() {
                String.format("%d", 1)
            }
        """.trimIndent()
        assertThat(subject.lintWithContext(env, code)).hasSize(1)
    }

    @Test
    fun `does not report String_format call with explicit locale`() {
        val code = """
            import java.util.Locale
            fun x() {
                String.format(Locale.US, "%d", 1)
            }
        """.trimIndent()
        assertThat(subject.lintWithContext(env, code)).isEmpty()
    }

    @Test
    fun `does not report custom String_format call`() {
        val code = """
            fun String.Companion.format(format: String, value: Int) = format + value.toString()

            fun x() {
                String.format("%d", 1)
            }
        """.trimIndent()
        assertThat(subject.lintWithContext(env, code)).isEmpty()
    }

    @Test
    fun `reports format extension call with template but without explicit locale`() {
        val code = """
            fun x() {
                "%d".format(1)
            }
        """.trimIndent()
        assertThat(subject.lintWithContext(env, code)).hasSize(1)
    }

    @Test
    fun `does not report format extension call with explicit locale`() {
        val code = """
            import java.util.Locale
            fun x() {
                "%d".format(Locale.US, 1)
            }
        """.trimIndent()
        assertThat(subject.lintWithContext(env, code)).isEmpty()
    }

    @Test
    fun `does not report for custom format extension call`() {
        val code = """
            fun String.format(value: Int): String {
                return value.toString()
            }
            fun x() {
                "%d".format(1)
            }
        """.trimIndent()
        assertThat(subject.lintWithContext(env, code)).isEmpty()
    }

    @Test
    fun `does not report for locale-independent hexadecimal format specifier %x`() {
        val code = """
            fun x() {
                "0x%08x".format(255)
            }
        """.trimIndent()
        assertThat(subject.lintWithContext(env, code)).isEmpty()
    }

    @Test
    fun `does not report for locale-independent hexadecimal format specifier %X`() {
        val code = """
            fun x() {
                "0x%08X".format(255)
            }
        """.trimIndent()
        assertThat(subject.lintWithContext(env, code)).isEmpty()
    }

    @Test
    fun `does not report for locale-independent octal format specifier %o`() {
        val code = """
            fun x() {
                "%o".format(64)
            }
        """.trimIndent()
        assertThat(subject.lintWithContext(env, code)).isEmpty()
    }

    @Test
    fun `does not report for locale-independent hexadecimal floating-point %a`() {
        val code = """
            fun x() {
                "%a".format(1.0)
            }
        """.trimIndent()
        assertThat(subject.lintWithContext(env, code)).isEmpty()
    }

    @Test
    fun `does not report for locale-independent boolean format specifier %b`() {
        val code = """
            fun x() {
                "%b".format(true)
            }
        """.trimIndent()
        assertThat(subject.lintWithContext(env, code)).isEmpty()
    }

    @Test
    fun `does not report for locale-independent hash code format specifier %h`() {
        val code = """
            fun x() {
                "%h".format("test")
            }
        """.trimIndent()
        assertThat(subject.lintWithContext(env, code)).isEmpty()
    }

    @Test
    fun `does not report for locale-independent string format specifier %s`() {
        val code = """
            fun x() {
                "%s".format("test")
            }
        """.trimIndent()
        assertThat(subject.lintWithContext(env, code)).isEmpty()
    }

    @Test
    fun `does not report for locale-independent character format specifier %c`() {
        val code = """
            fun x() {
                "%c".format('A')
            }
        """.trimIndent()
        assertThat(subject.lintWithContext(env, code)).isEmpty()
    }

    @Test
    fun `does not report for locale-independent line separator %n`() {
        val code = """
            fun x() {
                "line1%nline2".format()
            }
        """.trimIndent()
        assertThat(subject.lintWithContext(env, code)).isEmpty()
    }

    @Test
    fun `does not report for literal percent %%`() {
        val code = """
            fun x() {
                "100%%".format()
            }
        """.trimIndent()
        assertThat(subject.lintWithContext(env, code)).isEmpty()
    }

    @Test
    fun `does not report for combined locale-independent specifiers`() {
        val code = """
            fun x() {
                "hex: 0x%08X, octal: %o, bool: %b".format(255, 64, true)
            }
        """.trimIndent()
        assertThat(subject.lintWithContext(env, code)).isEmpty()
    }

    @Test
    fun `does not report String_format with locale-independent specifiers`() {
        val code = """
            fun x() {
                String.format("0x%08X", 255)
            }
        """.trimIndent()
        assertThat(subject.lintWithContext(env, code)).isEmpty()
    }

    @Test
    fun `reports for locale-dependent decimal format specifier %d`() {
        val code = """
            fun x() {
                "%d".format(1)
            }
        """.trimIndent()
        assertThat(subject.lintWithContext(env, code)).hasSize(1)
    }

    @Test
    fun `reports for locale-dependent floating-point format specifier %f`() {
        val code = """
            fun x() {
                "%f".format(1.5)
            }
        """.trimIndent()
        assertThat(subject.lintWithContext(env, code)).hasSize(1)
    }

    @Test
    fun `reports for locale-dependent scientific notation %e`() {
        val code = """
            fun x() {
                "%e".format(1000.0)
            }
        """.trimIndent()
        assertThat(subject.lintWithContext(env, code)).hasSize(1)
    }

    @Test
    fun `reports for locale-dependent uppercase string %S`() {
        val code = """
            fun x() {
                "%S".format("test")
            }
        """.trimIndent()
        assertThat(subject.lintWithContext(env, code)).hasSize(1)
    }

    @Test
    fun `reports for locale-dependent uppercase character %C`() {
        val code = """
            fun x() {
                "%C".format('a')
            }
        """.trimIndent()
        assertThat(subject.lintWithContext(env, code)).hasSize(1)
    }

    @Test
    fun `reports for mixed locale-independent and locale-dependent specifiers`() {
        val code = """
            fun x() {
                "hex: 0x%X, decimal: %d".format(255, 100)
            }
        """.trimIndent()
        assertThat(subject.lintWithContext(env, code)).hasSize(1)
    }

    @Test
    fun `reports for date-time format specifiers`() {
        val code = """
            import java.util.Date
            fun x() {
                "%tY".format(Date())
            }
        """.trimIndent()
        assertThat(subject.lintWithContext(env, code)).hasSize(1)
    }

    @Test
    fun `does not report for format string without any specifiers`() {
        val code = """
            fun x() {
                "no specifiers here".format()
            }
        """.trimIndent()
        assertThat(subject.lintWithContext(env, code)).isEmpty()
    }

    @Test
    fun `reports for dynamic format string that cannot be analyzed`() {
        val code = """
            fun x(template: String) {
                template.format(1)
            }
        """.trimIndent()
        assertThat(subject.lintWithContext(env, code)).hasSize(1)
    }

    @Test
    fun `reports for String_format with dynamic format string`() {
        val code = """
            fun x(template: String) {
                String.format(template, 1)
            }
        """.trimIndent()
        assertThat(subject.lintWithContext(env, code)).hasSize(1)
    }

    @Test
    fun `does not report for format string with interpolation`() {
        val code = """
            fun x(prefix: String) {
                "${'$'}prefix: %s".format("test")
            }
        """.trimIndent()
        assertThat(subject.lintWithContext(env, code)).hasSize(1)
    }

    @Test
    fun `does not report for non-format method calls`() {
        val code = """
            fun x() {
                "test".toString()
            }
        """.trimIndent()
        assertThat(subject.lintWithContext(env, code)).isEmpty()
    }

    @Test
    fun `does not report for uppercase hexadecimal with argument index %1$X`() {
        val code = """
            fun x() {
                "hex: %1${'$'}08X".format(255)
            }
        """.trimIndent()
        assertThat(subject.lintWithContext(env, code)).isEmpty()
    }

    @Test
    fun `reports for String_format with interpolated format string as first argument`() {
        val code = """
            fun x(prefix: String) {
                String.format("${'$'}prefix: %d", 1)
            }
        """.trimIndent()
        assertThat(subject.lintWithContext(env, code)).hasSize(1)
    }

    @Test
    fun `reports for format extension with receiver that has interpolation`() {
        val code = """
            fun x(prefix: String) {
                "${'$'}prefix: %d".format(1)
            }
        """.trimIndent()
        assertThat(subject.lintWithContext(env, code)).hasSize(1)
    }

    @Test
    fun `does not report for String_format with locale-independent specifier as first string arg`() {
        val code = """
            fun x() {
                String.format("%x", 255)
            }
        """.trimIndent()
        assertThat(subject.lintWithContext(env, code)).isEmpty()
    }

    @Test
    fun `reports when format string has no interpolation but locale-dependent specifier`() {
        val code = """
            fun x() {
                String.format("%g", 1.5)
            }
        """.trimIndent()
        assertThat(subject.lintWithContext(env, code)).hasSize(1)
    }

    @Test
    fun `does not report for uppercase hexadecimal floating-point %A`() {
        val code = """
            fun x() {
                "%A".format(1.0)
            }
        """.trimIndent()
        assertThat(subject.lintWithContext(env, code)).isEmpty()
    }

    @Test
    fun `does not report for uppercase boolean format specifier %B`() {
        val code = """
            fun x() {
                "%B".format(true)
            }
        """.trimIndent()
        assertThat(subject.lintWithContext(env, code)).isEmpty()
    }

    @Test
    fun `does not report for uppercase hash code format specifier %H`() {
        val code = """
            fun x() {
                "%H".format("test")
            }
        """.trimIndent()
        assertThat(subject.lintWithContext(env, code)).isEmpty()
    }

    @Test
    fun `does not report for format specifiers with width and flags`() {
        val code = """
            fun x() {
                "%-10s %08x".format("test", 255)
            }
        """.trimIndent()
        assertThat(subject.lintWithContext(env, code)).isEmpty()
    }

    @Test
    fun `does not report for format specifiers with precision`() {
        val code = """
            fun x() {
                "%.5s".format("testing")
            }
        """.trimIndent()
        assertThat(subject.lintWithContext(env, code)).isEmpty()
    }

    @Test
    fun `reports for uppercase general format %G`() {
        val code = """
            fun x() {
                "%G".format(1.5)
            }
        """.trimIndent()
        assertThat(subject.lintWithContext(env, code)).hasSize(1)
    }

    @Test
    fun `reports for uppercase scientific notation %E`() {
        val code = """
            fun x() {
                "%E".format(1000.0)
            }
        """.trimIndent()
        assertThat(subject.lintWithContext(env, code)).hasSize(1)
    }

    @Test
    fun `does not report for extension format with multiple locale-independent specifiers`() {
        val code = """
            fun x() {
                "%s %s %s".format("a", "b", "c")
            }
        """.trimIndent()
        assertThat(subject.lintWithContext(env, code)).isEmpty()
    }

    @Test
    fun `reports when one of multiple specifiers is locale-dependent`() {
        val code = """
            fun x() {
                "%s %d %s".format("a", 1, "b")
            }
        """.trimIndent()
        assertThat(subject.lintWithContext(env, code)).hasSize(1)
    }

    @Test
    fun `does not report for String_format with multiple string arguments all locale-independent`() {
        val code = """
            fun x() {
                String.format("%s: %x", "value", 255)
            }
        """.trimIndent()
        assertThat(subject.lintWithContext(env, code)).isEmpty()
    }

    @Test
    fun `reports for format call with no arguments but locale-dependent specifier`() {
        val code = """
            fun x() {
                "%d".format()
            }
        """.trimIndent()
        assertThat(subject.lintWithContext(env, code)).hasSize(1)
    }

    @Test
    fun `reports when receiver is concatenated string literals`() {
        val code = """
            fun x() {
                ("prefix" + "%x").format(255)
            }
        """.trimIndent()
        assertThat(subject.lintWithContext(env, code)).hasSize(1)
    }

    @Test
    fun `does not report for format with argument index and locale-independent specifier`() {
        val code = """
            fun x() {
                "%2${'$'}s %1${'$'}x".format(255, "test")
            }
        """.trimIndent()
        assertThat(subject.lintWithContext(env, code)).isEmpty()
    }

    @Test
    fun `reports for format with argument index and locale-dependent specifier`() {
        val code = """
            fun x() {
                "%2${'$'}d %1${'$'}s".format("test", 255)
            }
        """.trimIndent()
        assertThat(subject.lintWithContext(env, code)).hasSize(1)
    }
}
