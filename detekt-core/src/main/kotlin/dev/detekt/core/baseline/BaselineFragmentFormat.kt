package dev.detekt.core.baseline

import org.xml.sax.Attributes
import org.xml.sax.SAXException
import org.xml.sax.helpers.DefaultHandler
import java.nio.channels.FileChannel
import java.nio.charset.StandardCharsets.UTF_8
import java.nio.file.AtomicMoveNotSupportedException
import java.nio.file.Files
import java.nio.file.LinkOption.NOFOLLOW_LINKS
import java.nio.file.Path
import java.nio.file.StandardCopyOption.ATOMIC_MOVE
import java.nio.file.StandardCopyOption.REPLACE_EXISTING
import java.nio.file.StandardOpenOption.CREATE
import java.nio.file.StandardOpenOption.WRITE
import java.security.MessageDigest
import java.util.Locale
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.locks.ReentrantLock
import javax.xml.XMLConstants
import javax.xml.parsers.SAXParserFactory
import kotlin.io.path.createDirectories
import kotlin.io.path.deleteIfExists
import kotlin.io.path.extension
import kotlin.io.path.inputStream
import kotlin.io.path.isDirectory
import kotlin.io.path.name
import kotlin.io.path.outputStream
import kotlin.io.path.relativeTo
import kotlin.io.path.walk

internal class BaselineFragmentFormat {

    fun read(directory: Path): DefaultBaseline {
        require(directory.isDirectory()) { "Baseline fragment path must be a directory: $directory" }
        return withDirectoryLock(directory) {
            val currentIssues = directory.fragmentFiles()
                .map { path ->
                    val id = readFragment(path)
                    require(path.relativeTo(directory) == relativePath(id)) {
                        "Baseline fragment path does not match its content hash: $path"
                    }
                    id
                }
                .toList()
            require(currentIssues.size == currentIssues.toSet().size) {
                "Baseline fragment directory contains duplicate IDs: $directory"
            }
            DefaultBaseline(emptySet(), currentIssues.toSet())
        }
    }

    fun write(directory: Path, baseline: DefaultBaseline) {
        val fragments = baseline.currentIssues
            .associateByCollisionChecked(::relativePath)
            .mapKeys { (path, _) -> directory.resolve(path) }

        directory.createDirectories()
        withDirectoryLock(directory) {
            deleteTemporaryFiles(directory)
            fragments.forEach { (target, id) -> writeAtomically(target, canonicalElement(id) + "\n") }
            directory.fragmentFiles()
                .filterNot(fragments::containsKey)
                .forEach(Path::deleteIfExists)
            deleteTemporaryFiles(directory)
            deleteEmptyShardDirectories(directory)
        }
    }

    private fun readFragment(path: Path): String =
        try {
            path.inputStream().use { input ->
                val handler = FragmentHandler()
                secureSaxParser().parse(input, handler)
                handler.id()
            }
        } catch (error: SAXException) {
            throw IllegalStateException("Baseline fragment '$path' must contain exactly one valid ID element.", error)
        }

    private fun secureSaxParser() =
        SAXParserFactory.newInstance()
            .apply {
                setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true)
                setFeature("http://apache.org/xml/features/disallow-doctype-decl", true)
                setFeature("http://xml.org/sax/features/external-general-entities", false)
                setFeature("http://xml.org/sax/features/external-parameter-entities", false)
                setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false)
            }
            .newSAXParser()

    private fun Path.fragmentFiles(): Sequence<Path> =
        walk().filter { path ->
            Files.isRegularFile(path, NOFOLLOW_LINKS) && path.extension == XML_EXTENSION
        }

    private fun writeAtomically(target: Path, content: String) {
        requireNotNull(target.parent).createDirectories()
        val temporary = Files.createTempFile(target.parent, ".${target.name}.", TEMPORARY_SUFFIX)
        temporary.outputStream().bufferedWriter(UTF_8).use { it.write(content) }
        try {
            Files.move(temporary, target, ATOMIC_MOVE, REPLACE_EXISTING)
        } catch (_: AtomicMoveNotSupportedException) {
            Files.move(temporary, target, REPLACE_EXISTING)
        } finally {
            temporary.deleteIfExists()
        }
    }

    private fun deleteTemporaryFiles(directory: Path) {
        directory.walk()
            .filter { path -> Files.isRegularFile(path, NOFOLLOW_LINKS) && path.name.endsWith(TEMPORARY_SUFFIX) }
            .forEach(Path::deleteIfExists)
    }

    private fun <T> withDirectoryLock(directory: Path, action: () -> T): T {
        val lockIdentity = runCatching { directory.toRealPath() }
            .getOrElse { directory.toAbsolutePath().normalize() }
        val processLock = processLocks.computeIfAbsent(lockIdentity) { ReentrantLock() }
        processLock.lock()
        try {
            val lockFile = lockFileFor(lockIdentity)
            return FileChannel.open(lockFile, CREATE, WRITE).use { channel ->
                channel.lock().use { action() }
            }
        } finally {
            processLock.unlock()
        }
    }

    private fun lockFileFor(lockIdentity: Path): Path {
        // A user-scoped directory keeps coordination files outside checkouts without exposing a shared tmp namespace.
        val lockDirectory = Path.of(System.getProperty("user.home"), ".detekt", LOCK_DIRECTORY_NAME).createDirectories()
        val digest = MessageDigest.getInstance("SHA-256")
            .digest(lockIdentity.toString().toByteArray(UTF_8))
            .joinToString("") { byte -> "%02x".format(Locale.ROOT, byte) }
        return lockDirectory.resolve("$digest.lock")
    }

    private fun deleteEmptyShardDirectories(directory: Path) {
        directory.walk()
            .filter { it != directory && it.isDirectory() }
            .sortedByDescending { it.nameCount }
            .forEach { path ->
                if (!path.walk().drop(1).iterator().hasNext()) path.deleteIfExists()
            }
    }

    private class FragmentHandler : DefaultHandler() {
        private var depth = 0
        private var idCount = 0
        private val content = StringBuilder()

        override fun startElement(uri: String, localName: String, qName: String, attributes: Attributes) {
            depth++
            if (attributes.length != 0) {
                throw SAXException("A baseline fragment ID element must not have attributes.")
            }
            if (depth != 1 || qName != ID || idCount != 0) {
                throw SAXException("A baseline fragment must contain exactly one ID element.")
            }
            idCount++
        }

        override fun characters(ch: CharArray, start: Int, length: Int) {
            if (depth == 1) content.append(ch, start, length)
        }

        override fun endElement(uri: String, localName: String, qName: String) {
            if (depth != 1 || qName != ID) {
                throw SAXException("A baseline fragment must contain exactly one ID element.")
            }
            depth--
        }

        fun id(): String {
            if (idCount != 1 || depth != 0 || content.isBlank()) {
                throw SAXException("A baseline fragment must contain exactly one non-empty ID element.")
            }
            return content.toString()
        }
    }

    private companion object {
        val processLocks = ConcurrentHashMap<Path, ReentrantLock>()
        const val XML_EXTENSION = "xml"
        const val LOCK_DIRECTORY_NAME = "detekt-baseline-locks"
        const val TEMPORARY_SUFFIX = ".tmp"
    }
}

private fun canonicalElement(id: String): String = "<ID>${id.escapeXml()}</ID>"

private fun String.escapeXml(): String =
    buildString(length) {
        this@escapeXml.forEach { character ->
            append(
                when (character) {
                    '&' -> "&amp;"
                    '<' -> "&lt;"
                    '>' -> "&gt;"
                    else -> character
                }
            )
        }
    }

private fun relativePath(id: String): Path {
    val digest = MessageDigest.getInstance("SHA-256")
        .digest(canonicalElement(id).toByteArray(UTF_8))
        .joinToString("") { byte -> "%02x".format(Locale.ROOT, byte) }
    return Path.of(digest.take(SHARD_LENGTH), "$digest.xml")
}

private fun Set<String>.associateByCollisionChecked(path: (String) -> Path): Map<Path, String> =
    buildMap {
        this@associateByCollisionChecked.forEach { id ->
            val target = path(id)
            val previous = put(target, id)
            check(previous == null || previous == id) { "Baseline fragment SHA-256 collision: $target" }
        }
    }

private const val SHARD_LENGTH = 2
