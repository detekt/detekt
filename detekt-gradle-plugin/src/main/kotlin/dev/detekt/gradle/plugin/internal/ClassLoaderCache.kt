package dev.detekt.gradle.plugin.internal

import java.io.File
import java.net.URLClassLoader
import java.util.concurrent.ConcurrentHashMap

internal fun interface ClassLoaderCache {

    fun getOrCreate(classpath: Set<File>): URLClassLoader
}

internal class DefaultClassLoaderCache : ClassLoaderCache {

    private val classpathFilesHashWithLoaders = ConcurrentHashMap<Int, URLClassLoader>()

    override fun getOrCreate(classpath: Set<File>): URLClassLoader {
        val classpathHashCode = HashSet(classpath).hashCode()
        return classpathFilesHashWithLoaders.getOrPut(classpathHashCode) {
            URLClassLoader(
                classpath.map { it.toURI().toURL() }.toTypedArray(),
                null // isolate detekt environment
            )
        }
    }
}

internal object GlobalClassLoaderCache : ClassLoaderCache by DefaultClassLoaderCache()
