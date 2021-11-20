package io.github.detekt.tooling.internal

import java.io.InputStream
import java.net.URL

fun URL.openSafeStream(): InputStream {
    return openConnection()
        /*
         * Due to https://bugs.openjdk.java.net/browse/JDK-6947916 and https://bugs.openjdk.java.net/browse/JDK-8155607,
         * it is necessary to disallow caches to maintain stability on JDK 8. Otherwise, simultaneous invocations of
         * Detekt in the same VM can fail spuriously. A similar bug is referenced in
         * https://github.com/detekt/detekt/issues/3396. The performance regression is likely unnoticeable.
         */
        .apply { if (System.getProperty("java.specification.version") == "1.8") useCaches = false }
        .getInputStream()
}

fun <T> Class<T>.getSafeResourceAsStream(name: String): InputStream? {
    return getResource(name)?.openSafeStream()
}
