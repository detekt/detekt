package dev.detekt.gradle.internal

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.io.File
import java.util.concurrent.CompletableFuture
import java.util.concurrent.CountDownLatch
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import java.util.function.Supplier

class ClassLoaderCacheSpec {

    @Test
    fun `same classloader is returned for the same files`() {
        val cache = DefaultClassLoaderCache()
        val initialClassLoader = cache.getOrCreate(setOf(File("a/b/c")))
        val secondClassLoader = cache.getOrCreate(setOf(File("a/b/c")))

        assertThat(initialClassLoader).isSameAs(secondClassLoader)
    }

    @Test
    fun `different classloaders are returned for different files`() {
        val cache = DefaultClassLoaderCache()
        val firstClassLoader = cache.getOrCreate(setOf(File("a/b/c")))
        val secondClassLoader = cache.getOrCreate(setOf(File("c/b/a")))

        assertThat(firstClassLoader).isNotSameAs(secondClassLoader)
    }

    @Test
    fun `same classloader for the same files in different order`() {
        val cache = DefaultClassLoaderCache()
        val firstClassLoader = cache.getOrCreate(setOf(File("a/b/c"), File("d/e/f")))
        val secondClassLoader = cache.getOrCreate(setOf(File("d/e/f"), File("a/b/c")))

        assertThat(firstClassLoader).isSameAs(secondClassLoader)
    }

    @Test
    fun `resolves files without synchronization`() {
        val file1 = File("/a/b/c")
        val collection1 = setOf(file1)
        val latch1 = CountDownLatch(1)

        val file2 = File("/c/b/a")
        val collection2 = setOf(file2)
        val latch2 = CountDownLatch(1)

        val cache = DefaultClassLoaderCache()
        val executor = Executors.newSingleThreadExecutor()

        try {
            val supplier = Supplier {
                latch2.countDown()
                assertThat(latch1.await(10L, TimeUnit.SECONDS)).isTrue()
                cache.getOrCreate(collection1)
            }
            val task = CompletableFuture.supplyAsync(supplier, executor)
            assertThat(latch2.await(10L, TimeUnit.SECONDS)).isTrue()
            // Will call `getOrCreate` next - wait a moment to be sure
            Thread.sleep(2000L)
            val classpath2 = cache.getOrCreate(collection2)
            latch1.countDown()
            val classpath1 = task.join()
            assertThat(classpath1.urLs).isEqualTo(arrayOf(file1.toURI().toURL()))
            assertThat(classpath2.urLs).isEqualTo(arrayOf(file2.toURI().toURL()))
        } finally {
            val remaining = executor.shutdownNow()
            assertThat(remaining).isEmpty()
        }
    }
}
