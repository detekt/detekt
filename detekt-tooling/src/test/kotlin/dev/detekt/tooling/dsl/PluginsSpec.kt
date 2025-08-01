package dev.detekt.tooling.dsl

import dev.detekt.tooling.internal.PluginsHolder
import org.assertj.core.api.Assertions.assertThatCode
import org.junit.jupiter.api.Test
import kotlin.io.path.Path

class PluginsSpec {

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
        assertThatCode { PluginsHolder(listOf(Path("/does/not/exist")), null) }
            .isInstanceOf(IllegalArgumentException::class.java)
    }
}
