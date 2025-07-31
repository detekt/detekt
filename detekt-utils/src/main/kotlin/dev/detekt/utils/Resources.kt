package dev.detekt.utils

import java.io.InputStream
import java.net.URL

fun URL.openSafeStream(): InputStream =
    openConnection()
        /*
         * Due to https://bugs.openjdk.java.net/browse/JDK-6947916 and https://bugs.openjdk.java.net/browse/JDK-8155607,
         * it is necessary to disallow caches to maintain stability on JDK 8 and 11 (and possibly more).
         * Otherwise, simultaneous invocations of detekt in the same VM can fail spuriously. A similar bug is referenced in
         * https://github.com/detekt/detekt/issues/3396. The performance regression is likely unnoticeable.
         * Due to https://github.com/detekt/detekt/issues/4332 it is included for all JDKs.
         */
        .apply { useCaches = false }
        .getInputStream()

fun <T> Class<T>.getSafeResourceAsStream(name: String): InputStream? = getResource(name)?.openSafeStream()

fun ClassLoader.getSafeResourceAsStream(name: String): InputStream? = getResource(name)?.openSafeStream()
