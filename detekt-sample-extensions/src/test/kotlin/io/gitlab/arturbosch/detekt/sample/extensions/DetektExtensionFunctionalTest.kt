@file:Suppress("ImportOrdering")

package io.gitlab.arturbosch.detekt.sample.extensions

import java.io.File
import org.assertj.core.api.Assertions.assertThat
import org.gradle.testkit.runner.GradleRunner
import org.junit.jupiter.api.Test

/**
 * The jar file created by ./gradlew jar task (located in build/libs)
 * will be copied to local file maven repo
 * and will be tested as a version 1.2.3 in this functional test.
 */
const val FAKE_JAR_VERSION = "1.2.3"

/**
 * Functional test for the Detekt extension.
 *
 * This class uses `GradleRunner` to execute the `detekt` task on sample Gradle project templates
 * located in `src/test/resources`. Each test case simulates a real-world scenario by setting up a
 * self-contained test project in a temporary directory and running a Gradle build against it. This
 * approach verifies that the custom Detekt rules are correctly packaged, loaded, and executed,
 * reporting the expected issues.
 *
 * During execution this test creates build/functional-test-* directories so it could be convenient
 * to 'cd' there during development and run `gradle detekt` directly to verify how the detekt
 * extension plugin works.
 */
@Suppress("MaxLineLength", "ArgumentListWrapping")
class DetektExtensionFunctionalTest {

    // Initializes test-wide properties before any tests are run.
    //
    // This block locates the single Detekt extension JAR file created by the `jar` task in the
    // `build/libs` directory. It verifies that exactly one such JAR exists to ensure a clean and
    // predictable test environment. The version is dynamically extracted from the JAR's filename and
    // stored in `realJarVersion`, making the test setup robust against version changes.
    //
    // N.B. This means current functional test depends on "gradlew jar" result that is taken into
    // account in the project's build.gradle.kts file.
    init {
        val libsDir = File("build/libs")
        val files =
            libsDir.listFiles { _, name ->
                name.startsWith("detekt-sample-extensions") && name.endsWith(".jar")
            }
        require(files != null && files.size == 1) {
            "Expected exactly one JAR file matching 'detekt-sample-extensions-*.jar' in '$libsDir'" +
                ", but found ${files?.size ?: 0}. Files: ${files?.joinToString(", ")}"
        }
    }

    @Test
    fun `detekt task with default config success if suppress issues`() {
        val testProjectDir =
            setupGradleProject("functional-test-project-success-if-suppress-issues-with-default-config")

        // Run both build and detekt tasks and verify they succeeded
        // 'build' task helps to verify that we're testing the real buildable project, not
        // but not some synthetic set of files which "looks like proj".
        GradleRunner.create()
            .withProjectDir(testProjectDir)
            .withArguments("detekt", "build")
            .build()
    }

    @Test
    fun `detekt task with default config fails and both custom rule and in-built rule are violated`() {
        val testProjectDir = setupGradleProject("functional-test-project-fail-with-default-config")

        // Run detekt task and verify it failed
        val result = GradleRunner.create()
            .withProjectDir(testProjectDir)
            .withArguments("detekt")
            .buildAndFail()

        // Assert custom extension rules were triggered
        assertThat(result.output).contains("TooManyFunctions.kt has 12 function declarations. Threshold is specified with 10. [TooManyFunctions]")
        assertThat(result.output).contains("TooManyFunctions.kt has 12 function declarations. Threshold is specified with 10. [TooManyFunctionsTwo]")

        // Assert in-built rule with another name were triggered
        // https://github.com/detekt/detekt/blob/main/detekt-rules-empty/src/main/kotlin/dev/detekt/rules/empty/EmptyRule.kt
        assertThat(result.output).contains("This empty block of code can be removed. [EmptyFunctionBlock]")
        // In-built rule with the same name as extension rule was triggered
        // https://github.com/detekt/detekt/blob/main/detekt-rules-complexity/src/main/kotlin/dev/detekt/rules/complexity/TooManyFunctions.kt
        assertThat(result.output).contains("Class 'TooManyFunctions' with '12' functions detected. The maximum allowed functions per class is set to '11' [TooManyFunctions]")

        // Assert amount of issues
        assertThat(result.output).contains("Analysis failed with 4 issues")

        // Assert detekt task failed the build
        assertThat(result.output).contains("Task :detekt FAILED")
        assertThat(result.output).contains("FAILURE: Build failed with an exception.")
    }

    @Test
    fun `detekt task with custom config fails and only custom rule is violated`() {
        val testProjectDir =
            setupGradleProject("functional-test-project-fail-with-custom-config-and-extension-rules-only")

        // Run detekt task and verify it failed
        val result = GradleRunner.create()
            .withProjectDir(testProjectDir)
            .withArguments("detekt")
            .buildAndFail()

        // Assert custom extension rules were triggered
        assertThat(result.output).contains("TooManyFunctions.kt has 12 function declarations. Threshold is specified with 5. [TooManyFunctionsTwo]")

        // Assert amount of issue
        assertThat(result.output).contains("Analysis failed with 1 issues")

        // Assert detekt task failed the build
        assertThat(result.output).contains("Task :detekt FAILED")
        assertThat(result.output).contains("FAILURE: Build failed with an exception.")
    }

    @Test
    fun `detekt task with custom config fails and both custom rule and in-built rule are violated`() {
        val testProjectDir =
            setupGradleProject("functional-test-project-fail-with-custom-config-and-both-extension-and-inbuilt-rules")

        // Run detekt task and verify it failed
        val result = GradleRunner.create()
            .withProjectDir(testProjectDir)
            .withArguments("detekt")
            .buildAndFail()

        // Assert custom extension rules were triggered
        assertThat(result.output).contains("TooManyFunctions.kt has 12 function declarations. Threshold is specified with 5. [TooManyFunctionsTwo]")

        // Assert in-built rule with another name were triggered
        // https://github.com/detekt/detekt/blob/main/detekt-rules-empty/src/main/kotlin/dev/detekt/rules/empty/EmptyRule.kt
        assertThat(result.output).contains("This empty block of code can be removed. [EmptyFunctionBlock]")
        // In-built rule with the same name as extension rule was triggered
        // https://github.com/detekt/detekt/blob/main/detekt-rules-complexity/src/main/kotlin/dev/detekt/rules/complexity/TooManyFunctions.kt
        assertThat(result.output).contains("Class 'TooManyFunctions' with '12' functions detected. The maximum allowed functions per class is set to '11' [TooManyFunctions]")

        // Assert amount of issue
        assertThat(result.output).contains("Analysis failed with 3 issues")

        // Assert detekt task failed the build
        assertThat(result.output).contains("Task :detekt FAILED")
        assertThat(result.output).contains("FAILURE: Build failed with an exception.")
    }

    /**
     * Sets up a self-contained Gradle project for a single functional test case.
     *
     * This function prepares a temporary Gradle project by:
     * 1. Creating a unique directory for the test run inside the `build` directory.
     * 2. Copying a template project from `src/test/resources/{testProject}`.
     * 3. Setting up a local Maven repository structure within the test project to use the same
     *    notation to specify dependency on detektPlugin in build.gradle.kts file as in real project.
     * 4. Copying the actual extension JAR (found by the `init` block) into this local Maven
     *    repository, renaming it to a fixed, predictable version (`fakeJarVersion`). This allows the
     *    test project's `build.gradle.kts` to resolve the dependency consistently.
     *
     * @param testProject The name of the test project directory under `src/test/resources` to use as
     *   a template.
     * @return The [File] object pointing to the root of the created temporary test project.
     */
    private fun setupGradleProject(testProject: String): File {
        val testProjectDir = File("build/$testProject")
        testProjectDir.mkdirs()
        File("src/test/resources/$testProject").copyRecursively(testProjectDir, true)
        File("src/test/resources/test-maven-repo")
            .copyRecursively(testProjectDir.resolve("test-maven-repo"), true)
        val testMavenRepoPath = "test-maven-repo/io/gitlab/arturbosch/detekt-sample-extension"
        File("build/libs/detekt-sample-extensions.jar")
            .copyTo(
                testProjectDir.resolve(
                    "$testMavenRepoPath/$FAKE_JAR_VERSION/detekt-sample-extension-$FAKE_JAR_VERSION.jar"
                ),
                overwrite = true
            )

        return testProjectDir
    }
}
