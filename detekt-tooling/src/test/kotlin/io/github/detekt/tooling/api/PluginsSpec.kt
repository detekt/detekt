package io.github.detekt.tooling.api

import io.github.detekt.tooling.dsl.ExtensionsSpecBuilder
import io.github.detekt.tooling.internal.PluginsHolder
import org.assertj.core.api.Assertions.assertThatCode
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import java.nio.file.Paths

class PluginsSpec {

    @Nested
    inner class `either plugins loaded from Paths or a ClassLoader are supported` {

        @Test
        fun `throws when both sources are supplied via dsl`() {
            assertThatCode {
                ExtensionsSpecBuilder().apply {
                    fromPaths { emptyList() }
                    fromClassloader { javaClass.classLoader }
                }.build()
            }.isInstanceOf(IllegalArgumentException::class.java)

            assertThatCode {
                ExtensionsSpecBuilder().apply {
                    fromClassloader { javaClass.classLoader }
                    fromPaths { emptyList() }
                }.build()
            }.isInstanceOf(IllegalArgumentException::class.java)
        }

        @Test
        fun `throws when both sources are supplied via internal helper class`() {
            assertThatCode { PluginsHolder(emptyList(), javaClass.classLoader) }
                .isInstanceOf(IllegalArgumentException::class.java)
        }

        @Test
        fun `plugins from paths must exist`() {
            assertThatCode { PluginsHolder(listOf(Paths.get("/does/not/exist")), null) }
                .isInstanceOf(IllegalArgumentException::class.java)
        }
    }
}
