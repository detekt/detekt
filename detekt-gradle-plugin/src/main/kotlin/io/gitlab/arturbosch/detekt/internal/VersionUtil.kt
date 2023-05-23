package io.gitlab.arturbosch.detekt.internal

import java.io.InputStream
import java.net.URL
import java.util.Properties

fun loadDetektVersion(
    classLoader: ClassLoader,
    propertyFileName: String = "detekt-versions.properties",
    propertyName: String = "detektVersion"
): String {
    // Other Gradle plugins can also have a versions.properties.
    val distinctVersions = classLoader
        .getResources(propertyFileName)
        .toList()
        .mapNotNull { versions ->
            Properties().run {
                load(versions.openSafeStream())
                getProperty(propertyName)
            }
        }
        .distinct()
    return distinctVersions.singleOrNull() ?: error(
        "You're importing two detekt plugins which have different versions. " +
            "(${distinctVersions.joinToString()}) Make sure to align the versions."
    )
}

// Copy-paste from io.github.detekt.utils.openSafeStream in Resources.kt.
// Can't use that function, because gradle-plugin is minimising dependencies: see #4748.
private fun URL.openSafeStream(): InputStream {
    return openConnection()
        /*
         * Due to https://bugs.openjdk.java.net/browse/JDK-6947916 and https://bugs.openjdk.java.net/browse/JDK-8155607,
         * it is necessary to disallow caches to maintain stability on JDK 8 and 11 (and possibly more).
         * Otherwise, simultaneous invocations of detekt in the same VM can fail spuriously. A similar bug is referenced in
         * https://github.com/detekt/detekt/issues/3396. The performance regression is likely unnoticeable.
         * Due to https://github.com/detekt/detekt/issues/4332 it is included for all JDKs.
         */
        .apply { useCaches = false }
        .getInputStream()
}
