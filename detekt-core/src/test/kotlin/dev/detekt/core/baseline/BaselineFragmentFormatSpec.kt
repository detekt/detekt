package dev.detekt.core.baseline

import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatIllegalArgumentException
import org.assertj.core.api.Assertions.assertThatIllegalStateException
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import java.nio.file.Path
import java.util.concurrent.CountDownLatch
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import kotlin.io.path.createDirectories
import kotlin.io.path.createFile
import kotlin.io.path.listDirectoryEntries
import kotlin.io.path.name
import kotlin.io.path.walk
import kotlin.io.path.writeText

class BaselineFragmentFormatSpec {

    private val oneHash = "eaf9bfb60bc3201b682726631958d1ad05bfcc3382d08a3963823f00aa56eeb1"

    @TempDir
    lateinit var tempDir: Path

    private val fragmentsDirectory
        get() = tempDir.resolve("baseline.d")

    @Test
    fun `writes one deterministic hash-addressed file for each unique ID`() {
        val baseline = DefaultBaseline(emptySet(), setOf("one", "one"))

        BaselineFragmentFormat().write(fragmentsDirectory, baseline)

        val expected = fragmentsDirectory.resolve("${oneHash.substring(0, 2)}/$oneHash.xml")
        assertThat(expected).hasContent("<ID>one</ID>\n")
        assertThat(fragmentsDirectory.resolve(oneHash.substring(0, 2)).listDirectoryEntries()).containsExactly(expected)
    }

    @Test
    fun `rejects a fragment whose path does not match its content hash`() {
        fragmentsDirectory.resolve("aa").createDirectories()
        fragmentsDirectory.resolve("aa/incorrect.xml").writeText("<ID>one</ID>\n")

        assertThatIllegalArgumentException()
            .isThrownBy { BaselineFragmentFormat().read(fragmentsDirectory) }
            .withMessageContaining("does not match")
    }

    @Test
    fun `removes stale fragments during explicit creation`() {
        fragmentsDirectory.resolve("stale").createDirectories()
        fragmentsDirectory.resolve("stale/old.xml").writeText("<ID>old</ID>\n")

        BaselineFragmentFormat().write(fragmentsDirectory, DefaultBaseline(emptySet(), setOf("one")))

        assertThat(fragmentsDirectory.resolve("stale/old.xml")).doesNotExist()
        assertThat(BaselineFragmentFormat().read(fragmentsDirectory).currentIssues).containsExactly("one")
    }

    @Test
    fun `rejects fragments with more than one ID`() {
        fragmentsDirectory.createDirectories()
        val fragment = fragmentsDirectory.resolve("invalid.xml")
        fragment.writeText("<ID>one</ID><ID>two</ID>\n")

        assertThatIllegalStateException()
            .isThrownBy { BaselineFragmentFormat().read(fragmentsDirectory) }
            .withMessageContaining("exactly one")
    }

    @Test
    fun `rejects attributes on an ID element`() {
        fragmentsDirectory.createDirectories()
        fragmentsDirectory.resolve("invalid.xml").writeText("<ID source=\"manual\">one</ID>\n")

        assertThatIllegalStateException()
            .isThrownBy { BaselineFragmentFormat().read(fragmentsDirectory) }
            .withMessageContaining("exactly one")
    }

    @Test
    fun `rejects nested elements in an ID element`() {
        fragmentsDirectory.createDirectories()
        fragmentsDirectory.resolve("invalid.xml").writeText("<ID>one<nested/>two</ID>\n")

        assertThatIllegalStateException()
            .isThrownBy { BaselineFragmentFormat().read(fragmentsDirectory) }
            .withMessageContaining("exactly one")
    }

    @Test
    fun `escapes IDs and verifies the hash from their decoded value`() {
        val id = "one & <two>"
        val format = BaselineFragmentFormat()

        format.write(fragmentsDirectory, DefaultBaseline(emptySet(), setOf(id)))

        val fragment = fragmentsDirectory.walk().single { it.toString().endsWith(".xml") }
        assertThat(fragment).hasContent("<ID>one &amp; &lt;two&gt;</ID>\n")
        assertThat(format.read(fragmentsDirectory).currentIssues).containsExactly(id)
    }

    @Test
    fun `rejects document type declarations`() {
        fragmentsDirectory.createDirectories()
        val fragment = fragmentsDirectory.resolve("invalid.xml")
        fragment.writeText("<!DOCTYPE ID [<!ENTITY xxe SYSTEM \"file:///etc/passwd\">]><ID>&xxe;</ID>\n")

        assertThatIllegalStateException()
            .isThrownBy { BaselineFragmentFormat().read(fragmentsDirectory) }
    }

    @Test
    fun `writes an empty directory for an empty baseline`() {
        fragmentsDirectory.createDirectories()
        fragmentsDirectory.resolve("old.xml").createFile()

        BaselineFragmentFormat().write(fragmentsDirectory, DefaultBaseline(emptySet(), emptySet()))

        assertThat(fragmentsDirectory.toFile().walkTopDown().filter { it.extension == "xml" }.toList()).isEmpty()
    }

    @Test
    fun `reading fragments does not change the managed directory`() {
        val format = BaselineFragmentFormat()
        format.write(fragmentsDirectory, DefaultBaseline(emptySet(), setOf("one")))
        val entriesBeforeRead = fragmentsDirectory.walk().map { it.toString() }.toList()
        val parentEntriesBeforeRead = tempDir.listDirectoryEntries()

        format.read(fragmentsDirectory)

        assertThat(
            fragmentsDirectory.walk().map { it.toString() }.toList()
        ).containsExactlyElementsOf(entriesBeforeRead)
        assertThat(tempDir.listDirectoryEntries()).containsExactlyElementsOf(parentEntriesBeforeRead)
    }

    @Test
    fun `concurrent readers observe a complete writer snapshot`() {
        val format = BaselineFragmentFormat()
        val oldIds = (1..50).mapTo(mutableSetOf()) { "old-$it" }
        val newIds = (1..50).mapTo(mutableSetOf()) { "new-$it" }
        format.write(fragmentsDirectory, DefaultBaseline(emptySet(), oldIds))
        val start = CountDownLatch(1)
        val executor = Executors.newFixedThreadPool(2)

        try {
            val writer = executor.submit {
                start.await()
                format.write(fragmentsDirectory, DefaultBaseline(emptySet(), newIds))
            }
            val reader = executor.submit<List<Set<String>>> {
                start.await()
                List(20) { format.read(fragmentsDirectory).currentIssues }
            }
            start.countDown()

            writer.get(10, TimeUnit.SECONDS)
            assertThat(reader.get(10, TimeUnit.SECONDS)).allSatisfy { snapshot ->
                assertThat(snapshot == oldIds || snapshot == newIds).isTrue()
            }
        } finally {
            executor.shutdownNow()
        }
    }

    @Test
    fun `does not leave temporary files after writing fragments`() {
        BaselineFragmentFormat().write(
            fragmentsDirectory,
            DefaultBaseline(emptySet(), setOf("one", "two")),
        )

        assertThat(fragmentsDirectory.walk().filter { it.name.endsWith(".tmp") }.toList()).isEmpty()
    }
}
