package io.github.detekt.tooling.api

import io.github.detekt.tooling.api.spec.ProcessingSpec
import java.util.ServiceLoader

/**
 * Specifies on how to retrieve detekt instances to run analyses.
 */
interface DetektProvider {

    /**
     * Is used to choose the highest priority provider if more than one are found on the classpath.
     *
     * Can be useful to stub/mock detekt instances for tests.
     */
    val priority: Int get() = -1

    /**
     * Configure a [Detekt] instance based on given [ProcessingSpec].
     */
    fun get(processingSpec: ProcessingSpec): Detekt

    companion object {

        /**
         * Looks for a provider on the classpath which is able to load [Detekt] instances.
         */
        fun load(
            classLoader: ClassLoader = DetektProvider::class.java.classLoader,
        ): DetektProvider =
            ServiceLoader.load(DetektProvider::class.java, classLoader)
                .maxByOrNull { it.priority }
                ?: error("No implementation of DetektProvider found.")
    }
}
