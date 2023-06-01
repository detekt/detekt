package io.gitlab.arturbosch.detekt.core

import io.github.detekt.test.utils.resourceAsPath
import io.gitlab.arturbosch.detekt.core.tooling.withSettings
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatIllegalArgumentException
import org.jetbrains.kotlin.psi.KtFile
import org.junit.jupiter.api.Test
import kotlin.io.path.Path

class KtTreeCompilerSpec {

    @Test
    fun `should compile all files`() {
        val (ktFiles, _) = fixture { compile(path) }
        assertThat(ktFiles.size)
            .describedAs("It should compile at least three files, but did ${ktFiles.size}")
            .isGreaterThanOrEqualTo(3)
    }

    @Test
    fun `should filter the file 'Default_kt'`() {
        val (ktFiles, output) = fixture("**/Default.kt", loggingDebug = true) { compile(path) }
        val ktFile = ktFiles.find { it.name == "Default.kt" }
        assertThat(ktFile).describedAs("It should have no Default.kt file").isNull()

        assertThat(output).contains("Ignoring file ")
    }

    @Test
    fun `should work with two or more filters`() {
        val (ktFiles, _) = fixture(
            "**/Default.kt",
            "**/*Test*",
            "**/*Complex*",
            "**/*KotlinScript*"
        ) { compile(path) }
        assertThat(ktFiles).isEmpty()
    }

    @Test
    fun `should also compile regular files`() {
        val (ktFiles, _) = fixture { compile(path.resolve("Default.kt")) }
        assertThat(ktFiles.size).isEqualTo(1)
    }

    @Test
    fun `throws an exception if given file does not exist`() {
        val invalidPath = "NOTHERE"
        assertThatIllegalArgumentException()
            .isThrownBy { fixture { compile(Path(invalidPath)) } }
            .withMessage("Given path $invalidPath does not exist!")
    }

    @Test
    fun `does not compile a folder with a css file`() {
        val cssPath = resourceAsPath("css")
        val (ktFiles, output) = fixture { compile(cssPath) }
        assertThat(ktFiles).isEmpty()
        assertThat(output).isEmpty()
    }

    @Test
    fun `does not compile a css file`() {
        val cssPath = resourceAsPath("css").resolve("test.css")
        val (ktFiles, output) = fixture { compile(cssPath) }
        assertThat(ktFiles).isEmpty()
        assertThat(output).isEmpty()
    }

    @Test
    fun `does not compile a css file but show log if debug is enabled`() {
        val cssPath = resourceAsPath("css").resolve("test.css")
        val (ktFiles, output) = fixture(loggingDebug = true) { compile(cssPath) }
        assertThat(ktFiles).isEmpty()
        assertThat(output).contains("Ignoring a file detekt cannot handle: ")
    }
}

internal inline fun fixture(
    vararg filters: String,
    loggingDebug: Boolean = false,
    crossinline block: KtTreeCompiler.() -> List<KtFile>
): Pair<List<KtFile>, String> {
    val channel = StringBuilder()
    val spec = createNullLoggingSpec {
        project {
            inputPaths = listOf(path)
            excludes = filters.toList()
        }
        logging {
            debug = loggingDebug
            outputChannel = channel
        }
    }
    val result = spec.withSettings { block(KtTreeCompiler(this, spec.projectSpec)) }

    return result to channel.toString()
}
