package io.gitlab.arturbosch.detekt.internal

import org.gradle.api.file.FileCollection
import java.net.URLClassLoader
import java.util.concurrent.ConcurrentHashMap

interface ClassLoaderCache {

    fun invalidate(classpath: FileCollection)
    fun getOrCreate(classpath: FileCollection): URLClassLoader
}

internal class DefaultClassLoaderCache : ClassLoaderCache {

    private val classpathFilesHashWithLoaders = ConcurrentHashMap<Int, URLClassLoader>()

    private fun getHashCode(classpath: FileCollection): Int {
        val classpathFiles = classpath.files
        return HashSet(classpathFiles).hashCode()
    }

    override fun invalidate(classpath: FileCollection) {
        classpathFilesHashWithLoaders.remove(getHashCode(classpath))
    }

    override fun getOrCreate(classpath: FileCollection): URLClassLoader {
        val classpathFiles = classpath.files
        val classpathHashCode = getHashCode(classpath)
        return classpathFilesHashWithLoaders.getOrPut(classpathHashCode) {
            URLClassLoader(
                classpathFiles.map { it.toURI().toURL() }.toTypedArray(),
                null // isolate detekt environment
            )
        }
    }
}

object GlobalClassLoaderCache : ClassLoaderCache by DefaultClassLoaderCache()
