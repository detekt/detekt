package io.gitlab.arturbosch.detekt.core

import io.gitlab.arturbosch.detekt.api.internal.PathFilters
import org.assertj.core.api.Assertions.assertThat
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

/**
 * @author Artur Bosch
 */
class KtTreeCompilerSpec : Spek({

    fun fixture(vararg filters: String): KtTreeCompiler =
        KtTreeCompiler(settings = ProcessingSettings(path,
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
    }
})
