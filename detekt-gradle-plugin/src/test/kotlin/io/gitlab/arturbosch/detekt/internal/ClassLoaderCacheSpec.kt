package io.gitlab.arturbosch.detekt.internal

import io.gitlab.arturbosch.detekt.gradle.TestFileCollection
import org.assertj.core.api.Assertions.assertThat
import org.gradle.api.internal.file.AbstractFileCollection
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe
import java.io.File
import java.util.concurrent.CompletableFuture
import java.util.concurrent.CountDownLatch
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import java.util.function.Supplier
import kotlin.random.Random

internal class ClassLoaderCacheSpec : Spek({

    describe("classpath changes") {

        it("passes for same files") {
            val changed = hasClasspathChanged(
                setOf(FixedDateFile("/a/b/c")),
                setOf(FixedDateFile("/a/b/c"))
            )

            assertThat(changed).isFalse()
        }

        it("reports for different file count") {
            val changed = hasClasspathChanged(
                setOf(DifferentDateFile("/a/b/c"), DifferentDateFile("/c/b/a")),
                setOf(DifferentDateFile("/a/b/c"))
            )

            assertThat(changed).isTrue()
        }

        it("reports different files") {
            val changed = hasClasspathChanged(
                setOf(DifferentDateFile("/c/b/a")),
                setOf(DifferentDateFile("/a/b/c"))
            )

            assertThat(changed).isTrue()
        }

        it("concurrent blocking file resolution does not deadlock") {
            val changed = hasClasspathChanged(
                setOf(DifferentDateFile("/a/b/c")),
                setOf(DifferentDateFile("/a/b/c"))
            )

            assertThat(changed).isTrue()
        }

        it("resolves files without synchronization") {
            val file1 = FixedDateFile("/a/b/c")
            val collection1 = CountdownFileCollection(file1)

            val file2 = FixedDateFile("/c/b/a")
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
})

private open class FixedDateFile(path: String) : File(path) {
    override fun compareTo(other: File?): Int {
        if (other == null) return 1
        return path.compareTo(other.path)
    }

    override fun lastModified(): Long = 12345L
}

private class DifferentDateFile(path: String) : FixedDateFile(path) {

    override fun lastModified(): Long {
        var nextDate = random.nextLong()
        while (cache.contains(nextDate)) {
            nextDate = random.nextLong()
        }
        cache.add(nextDate)
        return nextDate
    }

    companion object {
        private val random = Random(seed = 200)
        private val cache = HashSet<Long>()
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
