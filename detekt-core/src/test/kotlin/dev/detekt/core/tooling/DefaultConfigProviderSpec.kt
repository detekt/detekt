package dev.detekt.core.tooling

import dev.detekt.core.createNullLoggingSpec
import dev.detekt.test.utils.resourceAsPath
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import java.nio.file.Path
import kotlin.io.path.readText

class DefaultConfigProviderSpec {
    @Nested
    inner class `defaultConfigProvider without plugins` {
        private val extensionsSpec = createNullLoggingSpec {}.extensionsSpec

        @Test
        fun gets() {
            val config = DefaultConfigProvider().apply { init(extensionsSpec) }.get()

            assertThat(config.parentPath).isNull()
            assertThat(config.valueOrNull<Any>("sample")).isNull()
        }

        @Test
        fun copies(@TempDir tempDir: Path) {
            val path = tempDir.resolve("test.test")
            DefaultConfigProvider().apply { init(extensionsSpec) }.copy(path)

            assertThat(path)
                .hasSameTextualContentAs(resourceAsPath("default-detekt-config.yml"))
        }
    }

    @Nested
    inner class `defaultConfigProvider with plugins` {
        private val extensionsSpec = createNullLoggingSpec {
            extensions {
                fromPaths { listOf(resourceAsPath("sample-rule-set.jar")) }
            }
        }.extensionsSpec

        @Test
        fun gets() {
            val config = DefaultConfigProvider().apply { init(extensionsSpec) }.get()

            assertThat(config.parentPath).isNull()
            assertThat(config.valueOrNull<Any>("sample")).isNotNull()
        }

        @Test
        fun copies(@TempDir tempDir: Path) {
            val path = tempDir.resolve("test.test")
            DefaultConfigProvider().apply { init(extensionsSpec) }.copy(path)

            val actual = path.readText()
            val expected = resourceAsPath("default-detekt-config.yml").readText() +
                """
                    
                    sample:
                      TooManyFunctions:
                        active: true
                      TooManyFunctionsTwo:
                        active: true
                    
                """.trimIndent()

            assertThat(actual).isEqualTo(expected)
        }
    }
}
