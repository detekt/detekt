package io.gitlab.arturbosch.detekt.internal

import org.codehaus.groovy.runtime.DefaultGroovyMethodsSupport
import org.gradle.api.file.FileCollection
import java.net.URLClassLoader

interface ClassLoaderCache {

    fun getOrCreate(classpath: FileCollection): URLClassLoader
}

object GlobalClassLoaderCache : ClassLoaderCache {

    private var loaderAndClasspath: Pair<URLClassLoader, FileCollection>? = null

    override fun getOrCreate(classpath: FileCollection): URLClassLoader = synchronized(this) {
        val lastLoader = loaderAndClasspath?.first
        val lastClasspath = loaderAndClasspath?.second

        if (lastClasspath == null) {
            cache(classpath)
        } else if (hasClasspathChanged(lastClasspath, classpath)) {
            DefaultGroovyMethodsSupport.closeQuietly(lastLoader)
            cache(classpath)
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

internal fun hasClasspathChanged(lastClasspath: FileCollection, currentClasspath: FileCollection): Boolean {
    if (lastClasspath.files.size != currentClasspath.files.size) {
        return true
    }
    return lastClasspath.sorted()
        .zip(currentClasspath.sorted())
        .any { (last, current) -> last != current || last.lastModified() != current.lastModified() }
}
