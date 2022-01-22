package io.gitlab.arturbosch.detekt.core.tooling

import io.github.detekt.test.utils.createTempFileForTest
import io.github.detekt.test.utils.resourceAsPath
import io.gitlab.arturbosch.detekt.core.createNullLoggingSpec
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import java.nio.file.Files

internal class DefaultConfigProviderSpec {
    @Nested
    inner class `defaultConfigProvider without plugins` {
        val spec = createNullLoggingSpec {}

        @Test
        fun `gets`() {
            val config = DefaultConfigProvider().apply { init(spec) }.get()

            assertThat(config.parentPath).isNull()
            assertThat(config.subConfig("build").valueOrNull<Int>("maxIssues")).isEqualTo(0)
            assertThat(config.valueOrNull<Any>("sample")).isNull()
        }

        @Test
        fun `copies`() {
            val path = createTempFileForTest("test", "test")
            DefaultConfigProvider().apply { init(spec) }.copy(path)

            assertThat(path)
                .hasSameTextualContentAs(resourceAsPath("default-detekt-config.yml"))
        }
    }

    @Nested
    inner class `defaultConfigProvider with plugins` {
        val spec = createNullLoggingSpec {
            extensions {
                fromPaths { listOf(resourceAsPath("sample-rule-set.jar")) }
            }
        }

        @Test
        fun `gets`() {
            val config = DefaultConfigProvider().apply { init(spec) }.get()

            assertThat(config.parentPath).isNull()
            assertThat(config.subConfig("build").valueOrNull<Int>("maxIssues")).isEqualTo(0)
            assertThat(config.valueOrNull<Any>("sample")).isNotNull()
        }

        @Test
        fun `copies`() {
            val path = createTempFileForTest("test", "test")
            DefaultConfigProvider().apply { init(spec) }.copy(path)

            val actual = String(Files.readAllBytes(path), Charsets.UTF_8)
            val expected = String(Files.readAllBytes(resourceAsPath("default-detekt-config.yml")), Charsets.UTF_8) +
                """
                    |
                    |sample:
                    |  TooManyFunctions:
                    |    active: true
                    |  TooManyFunctionsTwo:
                    |    active: true
                    |
                """.trimMargin()

            assertThat(actual).isEqualTo(expected)
        }
    }
}
