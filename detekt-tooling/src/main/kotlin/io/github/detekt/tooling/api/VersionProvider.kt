package io.github.detekt.tooling.api

import java.util.ServiceLoader

interface VersionProvider {

    fun current(): String

    companion object {

        fun load(
            classLoader: ClassLoader = VersionProvider::class.java.classLoader,
        ): VersionProvider =
            ServiceLoader.load(VersionProvider::class.java, classLoader).first()
    }
}
