package io.gitlab.arturbosch.detekt.core

import io.gitlab.arturbosch.detekt.test.yamlConfig
import java.nio.file.Path
import java.nio.file.Paths
import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.given
import org.jetbrains.spek.api.dsl.it

/**
 * @author Artur Bosch
 */
class TestPatternTest : Spek({

    given("a test pattern for paths") {

        fun splitSources(pattern: TestPattern, path: Path): Pair<List<Path>, List<Path>> =
                listOf(path).partition { pattern.matches(it.toString()) }

        val defaultPattern = createTestPattern(yamlConfig("patterns/test-pattern.yml"))

        it("should identify a kt file in test path as test source with test as the first directory") {
            val path = "./test/SomeFile.kt"
            val (testSources, mainSources) = splitSources(defaultPattern, Paths.get(path))

            assertThat(testSources).allMatch { it.toString().endsWith(path.toFile()) }
            assertThat(testSources).isNotEmpty()
            assertThat(mainSources).isEmpty()
        }

        it("should identify a kt file in test path as test source") {
            val path = "./path/test/SomeFile.kt"
            val (testSources, mainSources) = splitSources(defaultPattern, Paths.get(path))

            assertThat(testSources).allMatch { it.toString().endsWith(path.toFile()) }
            assertThat(testSources).isNotEmpty()
            assertThat(mainSources).isEmpty()
        }

        it("should identify kt Test file as test source") {
            val path = "./some/path/abcTest.kt"
            val (testSources, mainSources) = splitSources(defaultPattern, Paths.get(path))

            assertThat(testSources).allMatch { it.toString().endsWith(path.toFile()) }
            assertThat(testSources).isNotEmpty()
            assertThat(mainSources).isEmpty()
        }

        it("should not identify kt files in an absolute path containing test as test source") {
            val pattern = createTestPattern(yamlConfig("patterns/test-pattern.yml"), Paths.get("/path/test/detekt"))
            val path = "./some/path/SomeFile.kt"
            val (testSources, mainSources) = splitSources(pattern, Paths.get(path))

            assertThat(testSources).isEmpty()
            assertThat(mainSources).allMatch { it.toString().endsWith(path.toFile()) }
            assertThat(mainSources).isNotEmpty()
        }

        it("should not identify a non-test kt file as test source") {
            val path = "./some/path/abc.kt"
            val (testSources, mainSources) = splitSources(defaultPattern, Paths.get(path))

            assertThat(testSources).isEmpty()
            assertThat(mainSources).allMatch { it.toString().endsWith(path.toFile()) }
            assertThat(mainSources).isNotEmpty()
        }
    }
})

private fun String.toFile() = this.split('/').last()
