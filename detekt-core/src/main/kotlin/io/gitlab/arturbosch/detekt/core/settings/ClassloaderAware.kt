package io.gitlab.arturbosch.detekt.core.settings

import dev.detekt.tooling.api.spec.ExtensionsSpec
import org.jetbrains.kotlin.utils.closeQuietly
import java.io.Closeable
import java.net.URLClassLoader
import kotlin.io.path.exists
import kotlin.io.path.extension

interface ClassloaderAware {

    val pluginLoader: ClassLoader

    fun closeLoaderIfNeeded()
}

class ExtensionFacade(
    private val plugins: ExtensionsSpec.Plugins?,
) : AutoCloseable, Closeable, ClassloaderAware {

    init {
        plugins?.paths?.forEach {
            require(it.exists()) { "Given plugin ‘$it’ does not exist." }
            require(it.extension == "jar") { "Given plugin ‘$it’ is not a JAR." }
        }
    }

    /**
     * Shared class loader used to load services from plugin jars.
     */
    override val pluginLoader: ClassLoader by lazy {
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
        if (plugins?.paths != null) {
            // we created a classloader and need to close it
            closeQuietly(pluginLoader as? URLClassLoader)
        }
    }
}
