package io.gitlab.arturbosch.detekt.internal

import org.assertj.core.api.Assertions.assertThat
import org.gradle.api.internal.file.AbstractFileCollection
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe
import java.io.File
import kotlin.random.Random

internal class ClassLoaderCacheSpec : Spek({

    describe("classpath changes") {

        it("passes for same files") {
            val changed = ClassLoaderCache.hasClasspathChanged(
                TestFileCollection(FixedDateFile("/a/b/c")),
                TestFileCollection(FixedDateFile("/a/b/c"))
            )

            assertThat(changed).isFalse()
        }

        it("reports for different file count") {
            val changed = ClassLoaderCache.hasClasspathChanged(
                TestFileCollection(DifferentDateFile("/a/b/c"), DifferentDateFile("/c/b/a")),
                TestFileCollection(DifferentDateFile("/a/b/c"))
            )

            assertThat(changed).isTrue()
        }

        it("reports different files") {
            val changed = ClassLoaderCache.hasClasspathChanged(
                TestFileCollection(DifferentDateFile("/c/b/a")),
                TestFileCollection(DifferentDateFile("/a/b/c"))
            )

            assertThat(changed).isTrue()
        }

        it("reports same files with different modify date") {
            val changed = ClassLoaderCache.hasClasspathChanged(
                TestFileCollection(DifferentDateFile("/a/b/c")),
                TestFileCollection(DifferentDateFile("/a/b/c"))
            )

            assertThat(changed).isTrue()
        }
    }
})

private class TestFileCollection(private vararg val files: File) : AbstractFileCollection() {

    override fun getFiles(): MutableSet<File> = files.toMutableSet()
    override fun getDisplayName(): String = "FileCol"
}

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
