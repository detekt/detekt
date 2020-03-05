package io.gitlab.arturbosch.detekt.internal

import org.codehaus.groovy.runtime.DefaultGroovyMethodsSupport
import org.gradle.api.file.FileCollection
import java.net.URLClassLoader

object ClassLoaderCache {

    private var loaderAndClasspath: Pair<URLClassLoader, FileCollection>? = null

    fun getOrCreate(classpath: FileCollection): URLClassLoader = synchronized(ClassLoaderCache) {
        val lastLoader = loaderAndClasspath?.first
        val lastClasspath = loaderAndClasspath?.second

        if (lastClasspath == null) {
            cache(classpath)
        } else {
            if (!lastClasspath.minus(classpath).isEmpty) {
                DefaultGroovyMethodsSupport.closeQuietly(lastLoader)
                cache(classpath)
            }
        }

        return loaderAndClasspath?.first ?: error("Cached or newly created detekt classloader expected.")
    }

    private fun cache(classpath: FileCollection) {
        loaderAndClasspath = URLClassLoader(
            classpath.map { it.toURI().toURL() }.toTypedArray(),
            null /* isolate detekt environment */
        ) to classpath
    }
}
