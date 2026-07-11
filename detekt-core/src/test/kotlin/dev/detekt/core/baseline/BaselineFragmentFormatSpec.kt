package dev.detekt.core.baseline

import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatIllegalArgumentException
import org.assertj.core.api.Assertions.assertThatIllegalStateException
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import org.junit.jupiter.api.parallel.ResourceLock
import org.junit.jupiter.api.parallel.Resources
import java.nio.file.Path
import java.util.concurrent.Callable
import java.util.concurrent.CountDownLatch
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import kotlin.io.path.createDirectories
import kotlin.io.path.createFile
import kotlin.io.path.listDirectoryEntries
import kotlin.io.path.name
import kotlin.io.path.walk
import kotlin.io.path.writeText

@ResourceLock(Resources.SYSTEM_PROPERTIES)
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
    fun `rejects a whitespace-only ID`() {
        fragmentsDirectory.createDirectories()
        fragmentsDirectory.resolve("invalid.xml").writeText("<ID>   </ID>\n")

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
    fun `preserves carriage returns in canonical XML and its hash path`() {
        val id = "one\rtwo"
        val hash = "db8eed34a90a4e3edbb620b45c73f2047983dc901e746fa343ca66440be27544"
        val format = BaselineFragmentFormat()

        format.write(fragmentsDirectory, DefaultBaseline(emptySet(), setOf(id)))

        val fragment = fragmentsDirectory.resolve("${hash.take(2)}/$hash.xml")
        assertThat(fragment).hasContent("<ID>one&#13;two</ID>\n")
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
    fun `reader waits for a lock held by another process`() {
        val expected = setOf("one", "two")
        BaselineFragmentFormat().write(fragmentsDirectory, DefaultBaseline(emptySet(), expected))
        val lockFile = baselineFragmentLockFile(fragmentsDirectory)
        val lockHolderSource = tempDir.resolve("LockHolder.java")
        lockHolderSource.writeText(
            """
                import java.nio.channels.FileChannel;
                import java.nio.file.Path;
                import static java.nio.file.StandardOpenOption.CREATE;
                import static java.nio.file.StandardOpenOption.WRITE;

                class LockHolder {
                    public static void main(String[] args) throws Exception {
                        try (var channel = FileChannel.open(Path.of(args[0]), CREATE, WRITE);
                             var ignored = channel.lock()) {
                            System.out.println("LOCKED");
                            System.out.flush();
                            System.in.read();
                        }
                    }
                }
            """.trimIndent()
        )
        val process = ProcessBuilder(
            Path.of(System.getProperty("java.home"), "bin", "java").toString(),
            lockHolderSource.toString(),
            lockFile.toString(),
        ).redirectErrorStream(true).start()
        val executor = Executors.newFixedThreadPool(2)

        try {
            val childReady = executor.submit(Callable { process.inputStream.bufferedReader().readLine() })
            assertThat(childReady.get(10, TimeUnit.SECONDS)).isEqualTo("LOCKED")
            val beforeFileLock = CountDownLatch(1)
            val reader = executor.submit<Set<String>> {
                BaselineFragmentFormat(beforeFileLock = { beforeFileLock.countDown() })
                    .read(fragmentsDirectory)
                    .currentIssues
            }
            assertThat(beforeFileLock.await(10, TimeUnit.SECONDS)).isTrue()
            assertThat(reader).isNotDone()

            process.outputStream.write(1)
            process.outputStream.close()

            assertThat(reader.get(10, TimeUnit.SECONDS)).isEqualTo(expected)
            assertThat(process.waitFor(10, TimeUnit.SECONDS)).isTrue()
            assertThat(process.exitValue()).isZero()
        } finally {
            process.destroyForcibly()
            executor.shutdownNow()
        }
    }

    @Test
    fun `retains a process lock for a waiting user and releases it after both finish`() {
        val firstRegistered = CountDownLatch(1)
        val releaseFirst = CountDownLatch(1)
        val secondRegistered = CountDownLatch(1)
        val executor = Executors.newFixedThreadPool(2)

        try {
            val first = executor.submit {
                BaselineFragmentFormat(
                    afterProcessLockRegistered = {
                        firstRegistered.countDown()
                        releaseFirst.await()
                    }
                ).write(fragmentsDirectory, DefaultBaseline(emptySet(), setOf("one")))
            }
            assertThat(firstRegistered.await(10, TimeUnit.SECONDS)).isTrue()
            val second = executor.submit {
                BaselineFragmentFormat(afterProcessLockRegistered = { secondRegistered.countDown() })
                    .write(fragmentsDirectory, DefaultBaseline(emptySet(), setOf("two")))
            }
            assertThat(secondRegistered.await(10, TimeUnit.SECONDS)).isTrue()
            assertThat(baselineFragmentProcessLockUsers(fragmentsDirectory)).isEqualTo(2)

            releaseFirst.countDown()
            first.get(10, TimeUnit.SECONDS)
            second.get(10, TimeUnit.SECONDS)

            assertThat(baselineFragmentProcessLockUsers(fragmentsDirectory)).isZero()
        } finally {
            releaseFirst.countDown()
            executor.shutdownNow()
        }
    }

    @Test
    fun `uses a configured lock directory`() {
        val configuredLockDirectory = tempDir.resolve("locks")
        val previous = System.setProperty(
            BaselineFragmentFormat.LOCK_DIRECTORY_PROPERTY,
            configuredLockDirectory.toString(),
        )

        try {
            BaselineFragmentFormat().write(fragmentsDirectory, DefaultBaseline(emptySet(), setOf("one")))

            assertThat(configuredLockDirectory.listDirectoryEntries("*.lock")).hasSize(1)
        } finally {
            if (previous == null) {
                System.clearProperty(BaselineFragmentFormat.LOCK_DIRECTORY_PROPERTY)
            } else {
                System.setProperty(BaselineFragmentFormat.LOCK_DIRECTORY_PROPERTY, previous)
            }
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

    @Test
    fun `removes stale temporary files when writing fragments`() {
        val staleTemporaryFile = fragmentsDirectory.resolve("nested/orphan.tmp")
        staleTemporaryFile.parent.createDirectories()
        staleTemporaryFile.writeText("stale")

        BaselineFragmentFormat().write(
            fragmentsDirectory,
            DefaultBaseline(emptySet(), setOf("one")),
        )

        assertThat(staleTemporaryFile).doesNotExist()
    }
}
