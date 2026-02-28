package dev.detekt.test.utils

import java.nio.file.Path
import kotlin.script.experimental.jvm.util.classpathFromClassloader

class KotlinEnvironmentContainer(val javaSourceRoots: List<Path>, val jvmClasspathRoots: List<Path>)

/**
 * Create a [KotlinEnvironmentContainer] used for test.
 *
 * @param additionalJavaSourceRootPaths the optional Java source roots list.
 */
fun createEnvironment(additionalJavaSourceRootPaths: List<Path> = emptyList()): KotlinEnvironmentContainer {
    val classLoader = Thread.currentThread().contextClassLoader
    val classpath = checkNotNull(classpathFromClassloader(classLoader)) { "We should always have a classpath" }
    return KotlinEnvironmentContainer(
        javaSourceRoots = additionalJavaSourceRootPaths,
        jvmClasspathRoots = classpath.map { it.toPath() },
    )
}
