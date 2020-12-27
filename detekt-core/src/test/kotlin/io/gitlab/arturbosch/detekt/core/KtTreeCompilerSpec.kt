package io.gitlab.arturbosch.detekt.core

import io.github.detekt.test.utils.NullPrintStream
import io.github.detekt.test.utils.resourceAsPath
import io.gitlab.arturbosch.detekt.core.tooling.withSettings
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatIllegalArgumentException
import org.jetbrains.kotlin.psi.KtFile
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe
import java.nio.file.Paths

@ExperimentalCoroutinesApi
class KtTreeCompilerSpec : Spek({

    describe("tree compiler functionality") {

        it("should compile all files") {
            runBlocking {
                val ktFiles = fixture { compile(path) }
                assertThat(ktFiles)
                    .describedAs("It should compile at least three files, but did ${ktFiles.size}")
                    .hasSizeGreaterThanOrEqualTo(3)
            }
        }

        it("should filter the file 'Default.kt'") {
            runBlocking {
                val ktFiles = fixture("**/Default.kt", assertIgnoreMessage = true) { compile(path) }
                val ktFile = ktFiles.find { it.name == "Default.kt" }
                assertThat(ktFile).describedAs("It should have no Default.kt file").isNull()
            }
        }

        it("should work with two or more filters") {
            runBlocking {
                val ktFiles = fixture(
                    "**/Default.kt",
                    "**/*Test*",
                    "**/*Complex*",
                    "**/*KotlinScript*"
                ) { compile(path) }
                assertThat(ktFiles).isEmpty()
            }
        }

        it("should also compile regular files") {
            runBlocking {
                assertThat(fixture { compile(path.resolve("Default.kt")) }).hasSize(1)
            }
        }

        it("throws an exception if given file does not exist") {
            val invalidPath = "NOTHERE"
            assertThatIllegalArgumentException()
                .isThrownBy { runBlocking { fixture { compile(Paths.get(invalidPath)) } } }
                .withMessage("Given path $invalidPath does not exist!")
        }

        it("does not compile a folder with a css file") {
            runBlocking {
                val cssPath = resourceAsPath("css")
                val ktFiles = fixture { compile(cssPath) }
                assertThat(ktFiles).isEmpty()
            }
        }

        it("does not compile a css file") {
            runBlocking {
                val cssPath = resourceAsPath("css").resolve("test.css")
                val ktFiles = fixture { compile(cssPath) }
                assertThat(ktFiles).isEmpty()
            }
        }
    }
})

private fun fixture(
    vararg filters: String,
    assertIgnoreMessage: Boolean = false,
    block: suspend KtTreeCompiler.() -> Flow<KtFile>
): List<KtFile> {
    val channel = if (assertIgnoreMessage) StringBuilder() else NullPrintStream()
    val spec = createNullLoggingSpec {
        project {
            inputPaths = listOf(path)
            excludes = filters.toList()
        }
        logging {
            debug = assertIgnoreMessage
            outputChannel = channel
        }
    }
    val result = spec.withSettings {
        runBlocking { block(KtTreeCompiler(this@withSettings, spec.projectSpec)).toList() }
    }

    if (assertIgnoreMessage) {
        assertThat(channel.toString()).contains("Ignoring file ")
    }

    return result
}
