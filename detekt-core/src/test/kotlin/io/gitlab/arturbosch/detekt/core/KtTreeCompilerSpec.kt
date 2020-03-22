package io.gitlab.arturbosch.detekt.core

import io.gitlab.arturbosch.detekt.api.internal.PathFilters
import io.gitlab.arturbosch.detekt.test.createProcessingSettings
import io.gitlab.arturbosch.detekt.test.resource
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatIllegalArgumentException
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe
import java.nio.file.Paths

class KtTreeCompilerSpec : Spek({

    fun fixture(vararg filters: String): KtTreeCompiler =
        KtTreeCompiler(settings = createProcessingSettings(path,
            pathFilters = PathFilters.of(null, filters.joinToString(","))))

    describe("tree compiler functionality") {

        it("should compile all files") {
            val ktFiles = fixture().compile(path)
            assertThat(ktFiles.size)
                .describedAs("It should compile at least three files, but did ${ktFiles.size}")
                .isGreaterThanOrEqualTo(3)
        }

        it("should filter the file 'Default.kt'") {
            val ktFiles = fixture("**/Default.kt").compile(path)
            val ktFile = ktFiles.find { it.name == "Default.kt" }
            assertThat(ktFile).describedAs("It should have no Default.kt file").isNull()
        }

        it("should work with two or more filters") {
            val ktFiles = fixture(
                "**/Default.kt",
                "**/*Test*",
                "**/*Complex*",
                "**/*KotlinScript*"
            ).compile(path)
            assertThat(ktFiles).isEmpty()
        }

        it("should also compile regular files") {
            assertThat(fixture().compile(path.resolve("Default.kt")).size).isEqualTo(1)
        }

        it("throws an exception if given file does not exist") {
            val invalidPath = "NOTHERE"
            assertThatIllegalArgumentException()
                .isThrownBy { fixture().compile(Paths.get(invalidPath)) }
                .withMessage("Given path $invalidPath does not exist!")
        }

        it("does not compile a folder with a css file") {
            val cssPath = Paths.get(resource("css"))
            val ktFiles = fixture().compile(cssPath)
            assertThat(ktFiles).isEmpty()
        }

        it("does not compile a css file") {
            val cssPath = Paths.get(resource("css")).resolve("test.css")
            val ktFiles = fixture().compile(cssPath)
            assertThat(ktFiles).isEmpty()
        }
    }
})
