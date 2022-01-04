package io.github.detekt.tooling.api

import io.github.detekt.tooling.api.spec.ProcessingSpec
import io.gitlab.arturbosch.detekt.api.Config
import java.nio.file.Path
import java.util.ServiceLoader

interface DefaultConfigurationProvider {

    fun init(spec: ProcessingSpec)

    fun get(): Config

    fun copy(targetLocation: Path)

    companion object {

        fun load(
            spec: ProcessingSpec,
            classLoader: ClassLoader = DefaultConfigurationProvider::class.java.classLoader,
        ): DefaultConfigurationProvider =
            ServiceLoader.load(DefaultConfigurationProvider::class.java, classLoader).first().apply { init(spec) }
    }
}
