package io.github.detekt.tooling.api

import io.github.detekt.tooling.api.spec.ProcessingSpec
import java.util.ServiceLoader

/**
 * Specifies on how to retrieve detekt instances to run analyses.
 */
interface DetektProvider {

    /**
     * Configure a [Detekt] instance based on given [ProcessingSpec].
     */
    fun get(processingSpec: ProcessingSpec): Detekt

    companion object {

        /**
         * Looks for an provider on the classpath which is able to load [Detekt] instances.
         */
        fun load(
            classLoader: ClassLoader = DetektProvider::class.java.classLoader
        ): DetektProvider =
            ServiceLoader.load(DetektProvider::class.java, classLoader).first()
    }
}
