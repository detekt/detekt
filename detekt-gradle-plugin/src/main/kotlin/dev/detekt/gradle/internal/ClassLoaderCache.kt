package dev.detekt.gradle.internal

import java.io.File
import java.net.URLClassLoader
import java.security.MessageDigest
import java.util.Locale
import java.util.concurrent.ConcurrentHashMap

internal fun interface ClassLoaderCache {

    fun getOrCreate(classpath: Set<File>): URLClassLoader
}

internal class DefaultClassLoaderCache : ClassLoaderCache {
    private val classpathFilesHashWithLoaders = ConcurrentHashMap<String, URLClassLoader>()

    override fun getOrCreate(classpath: Set<File>): URLClassLoader {
        val classpathHashCode = sha1(classpath.sorted())
        return classpathFilesHashWithLoaders.getOrPut(classpathHashCode) {
            URLClassLoader(
                classpath.map { it.toURI().toURL() }.toTypedArray(),
                null // isolate detekt environment
            )
        }
    }
}

internal object GlobalClassLoaderCache : ClassLoaderCache by DefaultClassLoaderCache()

private fun sha1(paths: Collection<File>): String {
    val digest = MessageDigest.getInstance("SHA-1")
    val buffer = ByteArray(8192)

    paths
        .flatMap { file ->
            if (file.isDirectory) {
                file.walkTopDown().filter { it.isFile }.sortedBy { it.absolutePath }
            } else if (file.exists()) {
                sequenceOf(file)
            } else {
                emptySequence()
            }
        }
        .forEach { file ->
            file.inputStream().use { input ->
                var bytesRead = input.read(buffer)
                while (bytesRead != -1) {
                    digest.update(buffer, 0, bytesRead)
                    bytesRead = input.read(buffer)
                }
            }
        }
    return digest.digest().joinToString("") { "%02x".format(Locale.ROOT, it) }
}
