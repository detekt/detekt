package io.github.detekt.tooling.api

import io.github.detekt.tooling.dsl.ExtensionsSpecBuilder
import io.github.detekt.tooling.internal.PluginsHolder
import org.assertj.core.api.Assertions.assertThatCode

import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe
import java.nio.file.Paths

internal class PluginsSpec : Spek({

    describe("either plugins loaded from Paths or a ClassLoader are supported") {

        it("throws when both sources are supplied via dsl") {
            assertThatCode {
                ExtensionsSpecBuilder().apply {
                    fromPaths { listOf() }
                    fromClassloader { javaClass.classLoader }
                }.build()
            }.isInstanceOf(IllegalArgumentException::class.java)

            assertThatCode {
                ExtensionsSpecBuilder().apply {
                    fromClassloader { javaClass.classLoader }
                    fromPaths { listOf() }
                }.build()
            }.isInstanceOf(IllegalArgumentException::class.java)
        }

        it("throws when both sources are supplied via internal helper class") {
            assertThatCode { PluginsHolder(listOf(), javaClass.classLoader) }
                .isInstanceOf(IllegalArgumentException::class.java)
        }

        context("plugins from paths must exist") {
            assertThatCode { PluginsHolder(listOf(Paths.get("/does/not/exist")), null) }
                .isInstanceOf(IllegalArgumentException::class.java)
        }
    }
})
