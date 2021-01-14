package io.gitlab.arturbosch.detekt.internal

import org.codehaus.groovy.runtime.DefaultGroovyMethodsSupport
import org.gradle.api.file.FileCollection
import java.io.File
import java.net.URLClassLoader

interface ClassLoaderCache {

    fun getOrCreate(classpath: FileCollection): URLClassLoader
}

internal class DefaultClassLoaderCache : ClassLoaderCache {

    private var loaderAndClasspathFiles: Pair<URLClassLoader, Set<File>>? = null

    override fun getOrCreate(classpath: FileCollection): URLClassLoader {
        val classpathFiles = classpath.files
        synchronized(this) {
            val lastLoader = loaderAndClasspathFiles?.first
            val lastClasspathFiles = loaderAndClasspathFiles?.second

            if (lastClasspathFiles == null) {
                cache(classpathFiles)
            } else if (hasClasspathChanged(lastClasspathFiles, classpathFiles)) {
                DefaultGroovyMethodsSupport.closeQuietly(lastLoader)
                cache(classpathFiles)
            }

            return loaderAndClasspathFiles?.first ?: error("Cached or newly created detekt classloader expected.")
        }
    }

    private fun cache(classpathFiles: Set<File>) {
        loaderAndClasspathFiles = URLClassLoader(
            classpathFiles.map { it.toURI().toURL() }.toTypedArray(),
            null /* isolate detekt environment */
        ) to classpathFiles
    }
}

object GlobalClassLoaderCache : ClassLoaderCache by DefaultClassLoaderCache()

internal fun hasClasspathChanged(lastClasspathFiles: Set<File>, currentClasspathFiles: Set<File>): Boolean {
    if (lastClasspathFiles.size != currentClasspathFiles.size) {
        return true
    }
    return lastClasspathFiles.sorted()
        .zip(currentClasspathFiles.sorted())
        .any { (last, current) -> last != current || last.lastModified() != current.lastModified() }
}
