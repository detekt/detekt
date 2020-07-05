package io.github.detekt.tooling.api

import io.gitlab.arturbosch.detekt.api.Config
import java.nio.file.Path
import java.util.ServiceLoader

interface DefaultConfigurationProvider {

    fun get(): Config

    fun copy(targetLocation: Path)

    companion object {

        fun load(
            classLoader: ClassLoader = DefaultConfigurationProvider::class.java.classLoader
        ): DefaultConfigurationProvider =
            ServiceLoader.load(DefaultConfigurationProvider::class.java, classLoader).first()
    }
}
