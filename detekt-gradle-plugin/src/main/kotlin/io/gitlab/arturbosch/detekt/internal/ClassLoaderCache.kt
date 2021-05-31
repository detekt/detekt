package io.gitlab.arturbosch.detekt.internal

import org.gradle.api.file.FileCollection
import java.net.URLClassLoader
import java.util.concurrent.ConcurrentHashMap

fun interface ClassLoaderCache {

    fun getOrCreate(classpath: FileCollection): URLClassLoader
}

internal class DefaultClassLoaderCache : ClassLoaderCache {

    private val classpathFilesHashWithLoaders = ConcurrentHashMap<Int, URLClassLoader>()

    override fun getOrCreate(classpath: FileCollection): URLClassLoader {
        val classpathFiles = classpath.files
        val classpathHashCode = HashSet(classpathFiles).hashCode()
        return classpathFilesHashWithLoaders.getOrPut(classpathHashCode) {
            URLClassLoader(
                classpathFiles.map { it.toURI().toURL() }.toTypedArray(),
                null /* isolate detekt environment */
            )
        }
    }
}

object GlobalClassLoaderCache : ClassLoaderCache by DefaultClassLoaderCache()
