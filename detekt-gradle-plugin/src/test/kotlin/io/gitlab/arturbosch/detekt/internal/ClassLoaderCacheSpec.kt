package io.gitlab.arturbosch.detekt.internal

import io.gitlab.arturbosch.detekt.gradle.TestFileCollection
import org.assertj.core.api.Assertions.assertThat
import org.gradle.api.internal.file.AbstractFileCollection
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import java.io.File
import java.util.concurrent.CompletableFuture
import java.util.concurrent.CountDownLatch
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import java.util.function.Supplier

class ClassLoaderCacheSpec {

    @Nested
    inner class ClasspathChanges {

        @Test
        fun `same classloader is returned for the same files`() {
            val cache = DefaultClassLoaderCache()
            val initialClassLoader = cache.getOrCreate(TestFileCollection(File("a/b/c")))
            val secondClassLoader = cache.getOrCreate(TestFileCollection(File("a/b/c")))

            assertThat(initialClassLoader === secondClassLoader).isTrue()
        }

        @Test
        fun `different classloaders are returned for different files`() {
            val cache = DefaultClassLoaderCache()
            val firstClassLoader = cache.getOrCreate(TestFileCollection(File("a/b/c")))
            val secondClassLoader = cache.getOrCreate(TestFileCollection(File("c/b/a")))

            assertThat(firstClassLoader === secondClassLoader).isFalse()
        }

        @Test
        fun `same classloader for the same files in different order`() {
            val cache = DefaultClassLoaderCache()
            val firstClassLoader = cache.getOrCreate(TestFileCollection(File("a/b/c"), File("d/e/f")))
            val secondClassLoader = cache.getOrCreate(TestFileCollection(File("d/e/f"), File("a/b/c")))

            assertThat(firstClassLoader === secondClassLoader).isTrue()
        }

        @Test
        fun `resolves files without synchronization`() {
            val file1 = File("/a/b/c")
            val collection1 = CountdownFileCollection(file1)

            val file2 = File("/c/b/a")
            val collection2 = TestFileCollection(file2)

            val cache = DefaultClassLoaderCache()
            val executor = Executors.newSingleThreadExecutor()
            val latch = CountDownLatch(1)
            try {
                val supplier = Supplier {
                    latch.countDown()
                    cache.getOrCreate(collection1)
                }
                val task = CompletableFuture.supplyAsync(supplier, executor)
                @Suppress("UsePropertyAccessSyntax")
                assertThat(latch.await(10L, TimeUnit.SECONDS)).isTrue()
                // Will call `getOrCreate` next - wait a moment to be sure
                Thread.sleep(2000L)
                val classpath2 = cache.getOrCreate(collection2)
                collection1.latch.countDown()
                val classpath1 = task.join()
                assertThat(classpath1.urLs).isEqualTo(arrayOf(file1.toURI().toURL()))
                assertThat(classpath2.urLs).isEqualTo(arrayOf(file2.toURI().toURL()))
            } finally {
                val remaining = executor.shutdownNow()
                assertThat(remaining).isEmpty()
            }
        }
    }
}

private class CountdownFileCollection(private vararg val files: File) : AbstractFileCollection() {

    val latch = CountDownLatch(1)

    override fun getFiles(): MutableSet<File> {
        @Suppress("UsePropertyAccessSyntax")
        assertThat(latch.await(10L, TimeUnit.SECONDS)).isTrue()
        return files.toMutableSet()
    }

    override fun getDisplayName(): String = "CountdownFileCollection"
}
