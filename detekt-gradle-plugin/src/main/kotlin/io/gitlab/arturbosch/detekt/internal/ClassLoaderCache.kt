package io.gitlab.arturbosch.detekt.internal

import org.codehaus.groovy.runtime.DefaultGroovyMethodsSupport
import org.gradle.api.file.FileCollection
import java.net.URLClassLoader

object ClassLoaderCache {

    private var cached: URLClassLoader? = null
    private var lastSeenClasspath: FileCollection? = null

    fun getOrCreate(classpath: FileCollection): URLClassLoader {
        val lastClasspath = lastSeenClasspath
        if (lastClasspath == null) {
            cache(classpath)
        } else {
            synchronized(ClassLoaderCache) {
                if (!lastClasspath.minus(classpath).isEmpty) {
                    DefaultGroovyMethodsSupport.closeQuietly(cached)
                    cache(classpath)
                }
            }
        }
        return cached ?: throw IllegalStateException("Detekt classloader expected.")
    }

    private fun cache(classpath: FileCollection) {
        lastSeenClasspath = classpath
        cached = URLClassLoader(
            classpath.map { it.toURI().toURL() }.toTypedArray(),
            null /* isolate detekt environment */
        )
    }
}
