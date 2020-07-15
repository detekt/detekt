package io.gitlab.arturbosch.detekt.core.settings

import io.github.detekt.tooling.api.spec.ExtensionsSpec
import org.jetbrains.kotlin.utils.closeQuietly
import java.io.Closeable
import java.net.URLClassLoader
import java.nio.file.Files

interface ClassloaderAware {

    val pluginLoader: ClassLoader

    fun closeLoaderIfNeeded()
}

class ExtensionFacade(
    private val extensionsSpec: ExtensionsSpec
) : AutoCloseable, Closeable, ClassloaderAware {

    init {
        extensionsSpec.plugins?.paths?.forEach {
            require(Files.exists(it)) { "Given plugin ‘$it’ does not exist." }
            require(it.toString().endsWith("jar")) { "Given plugin ‘$it’ is not a JAR." }
        }
    }

    /**
     * Shared class loader used to load services from plugin jars.
     */
    override val pluginLoader: ClassLoader by lazy {
        val plugins = extensionsSpec.plugins
        when {
            plugins?.loader != null -> checkNotNull(plugins.loader)
            plugins?.paths != null -> {
                val pluginUrls = checkNotNull(plugins.paths)
                    .map { it.toUri().toURL() }
                    .toTypedArray()
                URLClassLoader(pluginUrls, javaClass.classLoader)
            }
            else -> javaClass.classLoader
        }
    }

    override fun close() {
        closeLoaderIfNeeded()
    }

    override fun closeLoaderIfNeeded() {
        if (extensionsSpec.plugins?.paths != null) {
            // we created a classloader and need to close it
            closeQuietly(pluginLoader as? URLClassLoader)
        }
    }
}
