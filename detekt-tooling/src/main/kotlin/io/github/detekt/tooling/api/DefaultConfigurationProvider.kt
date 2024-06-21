package io.github.detekt.tooling.api

import io.github.detekt.tooling.api.spec.ExtensionsSpec
import io.gitlab.arturbosch.detekt.api.Config
import java.nio.file.Path
import java.util.ServiceLoader

interface DefaultConfigurationProvider {

    fun init(extensionsSpec: ExtensionsSpec)

    fun get(): Config

    fun copy(targetLocation: Path)

    companion object {

        fun load(
            extensionsSpec: ExtensionsSpec,
            classLoader: ClassLoader = DefaultConfigurationProvider::class.java.classLoader,
        ): DefaultConfigurationProvider =
            ServiceLoader.load(DefaultConfigurationProvider::class.java, classLoader)
                .first()
                .apply { init(extensionsSpec) }
    }
}
