package io.gitlab.arturbosch.detekt.core.tooling

import io.github.detekt.test.utils.createTempFileForTest
import io.github.detekt.test.utils.resourceAsPath
import io.gitlab.arturbosch.detekt.core.createNullLoggingSpec
import org.assertj.core.api.Assertions.assertThat
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe
import java.nio.file.Files

internal class DefaultConfigProviderSpec : Spek({
    describe("defaultConfigProvider without plugins") {
        val spec by memoized {
            createNullLoggingSpec {}
        }

        it("gets") {
            val config = DefaultConfigProvider().apply { init(spec) }.get()

            assertThat(config.parentPath).isNull()
            assertThat(config.subConfig("build").valueOrNull<Int>("maxIssues")).isEqualTo(0)
            assertThat(config.valueOrNull<Any>("sample")).isNull()
        }

        it("copies") {
            val path = createTempFileForTest("test", "test")
            DefaultConfigProvider().apply { init(spec) }.copy(path)

            assertThat(path)
                .hasSameTextualContentAs(resourceAsPath("default-detekt-config.yml"))
        }
    }

    describe("defaultConfigProvider without plugins") {
        val spec by memoized {
            createNullLoggingSpec {
                extensions {
                    fromPaths { listOf(resourceAsPath("sample-rule-set.jar")) }
                }
            }
        }

        it("gets") {
            val config = DefaultConfigProvider().apply { init(spec) }.get()

            assertThat(config.parentPath).isNull()
            assertThat(config.subConfig("build").valueOrNull<Int>("maxIssues")).isEqualTo(0)
            assertThat(config.valueOrNull<Any>("sample")).isNotNull()
        }

        it("copies") {
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
})
