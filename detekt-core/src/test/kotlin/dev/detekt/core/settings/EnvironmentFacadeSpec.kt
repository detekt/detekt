package dev.detekt.core.settings

import dev.detekt.core.ProcessingSettings
import dev.detekt.core.createProcessingSettings
import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.kotlin.analysis.api.analyze
import org.jetbrains.kotlin.analysis.api.components.KaDiagnosticCheckerFilter
import org.jetbrains.kotlin.analysis.api.diagnostics.KaSeverity
import org.jetbrains.org.objectweb.asm.ClassWriter
import org.jetbrains.org.objectweb.asm.Opcodes
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import java.nio.file.Path
import java.util.jar.JarEntry
import java.util.jar.JarOutputStream
import kotlin.io.path.outputStream
import kotlin.io.path.writeText

class EnvironmentFacadeSpec {

    @Test
    fun `resolves a class added to a classpath jar that an earlier run read`(@TempDir dir: Path) {
        val jar = dir.resolve("lib.jar")
        val source = dir.resolve("Use.kt")
        val keepalive = System.getProperty(KEEPALIVE)
        // As in a Gradle daemon where the Kotlin Gradle plugin or Android Lint has set it:
        // the application environment, and what it read of the jars, outlives each run.
        System.setProperty(KEEPALIVE, "true")
        try {
            writeJar(jar, "lib/Old")
            source.writeText(
                """
                    import lib.Old

                    class Use(val old: Old)
                """.trimIndent()
            )
            assertThat(compilerErrors(source, jar)).isEmpty()

            writeJar(jar, "lib/Old", "lib/New")
            source.writeText(
                """
                    import lib.New
                    import lib.Old

                    class Use(val old: Old, val new: New)
                """.trimIndent()
            )
            assertThat(compilerErrors(source, jar)).isEmpty()
        } finally {
            if (keepalive == null) System.clearProperty(KEEPALIVE) else System.setProperty(KEEPALIVE, keepalive)
        }
    }

    @Test
    fun `resolves classpath classes in a session still open when another one closes`(@TempDir dir: Path) {
        val jar = dir.resolve("lib.jar")
        writeJar(jar, "lib/Old")
        val first = dir.resolve("First.kt")
        first.writeText(
            """
                import lib.Old

                class First(val old: Old)
            """.trimIndent()
        )
        val second = dir.resolve("Second.kt")
        second.writeText(
            """
                import lib.Old

                class Second(val old: Old)
            """.trimIndent()
        )
        createProcessingSettings(second) { compiler { classpath = listOf(jar) } }.use { secondSettings ->
            assertThat(compilerErrors(first, jar)).isEmpty()
            assertThat(compilerErrors(secondSettings)).isEmpty()
        }
    }
}

private const val KEEPALIVE = "kotlin.environment.keepalive"

private fun compilerErrors(source: Path, jar: Path): List<String> =
    createProcessingSettings(source) { compiler { classpath = listOf(jar) } }.use(::compilerErrors)

private fun compilerErrors(settings: ProcessingSettings): List<String> =
    settings.ktFiles.flatMap { file ->
        analyze(file) {
            file.collectDiagnostics(KaDiagnosticCheckerFilter.ONLY_COMMON_CHECKERS)
                .filter { it.severity == KaSeverity.ERROR }
                .map { it.defaultMessage }
        }
    }

private fun writeJar(jar: Path, vararg classNames: String) {
    JarOutputStream(jar.outputStream()).use { out ->
        classNames.forEach { name ->
            out.putNextEntry(JarEntry("$name.class"))
            out.write(emptyClass(name))
            out.closeEntry()
        }
    }
}

private fun emptyClass(name: String): ByteArray =
    ClassWriter(0).apply {
        visit(Opcodes.V1_8, Opcodes.ACC_PUBLIC, name, null, "java/lang/Object", null)
        visitEnd()
    }.toByteArray()
