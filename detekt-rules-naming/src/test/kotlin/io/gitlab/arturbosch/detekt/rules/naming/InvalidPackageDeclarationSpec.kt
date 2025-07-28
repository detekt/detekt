package io.gitlab.arturbosch.detekt.rules.naming

import dev.detekt.test.utils.compileForTest
import dev.detekt.api.Config
import dev.detekt.test.TestConfig
import dev.detekt.test.assertThat
import dev.detekt.test.lint
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import kotlin.io.path.Path

private const val ROOT_PACKAGE = "rootPackage"
private const val REQUIRE_ROOT_PACKAGE = "requireRootInDeclaration"

class InvalidPackageDeclarationSpec {

    @Test
    fun `should pass if package declaration is correct`() {
        val ktFile = compileForTest(Path("src/test/resources/InvalidPackageDeclarationSpec/src/foo/bar/correct.kt"))
        val findings = InvalidPackageDeclaration(Config.empty).lint(ktFile)

        assertThat(findings).isEmpty()
    }

    @Test
    fun `should report if package declaration does not match source location`() {
        val ktFile = compileForTest(Path("src/test/resources/InvalidPackageDeclarationSpec/src/bar/incorrect.kt"))
        val findings = InvalidPackageDeclaration(Config.empty).lint(ktFile)

        assertThat(findings).hasSize(1)
        assertThat(findings).hasTextLocations(0 to 11)
    }

    @Nested
    inner class `with root package specified` {

        val config = TestConfig(ROOT_PACKAGE to "com.example")

        @Test
        fun `should pass if file is located within the root package`() {
            val ktFile = compileForTest(Path("src/test/resources/InvalidPackageDeclarationSpec/src/File.kt"))
            val findings = InvalidPackageDeclaration(config).lint(ktFile)

            assertThat(findings).isEmpty()
        }

        @Test
        fun `should pass if file is located relative to root package`() {
            val ktFile = compileForTest(Path("src/test/resources/InvalidPackageDeclarationSpec/src/foo/bar/File.kt"))
            val findings = InvalidPackageDeclaration(config).lint(ktFile)

            assertThat(findings).isEmpty()
        }

        @Test
        fun `should pass if file is located in directory corresponding to package declaration`() {
            val ktFile =
                compileForTest(
                    Path("src/test/resources/InvalidPackageDeclarationSpec/src/com/example/foo/bar/File.kt")
                )
            val findings = InvalidPackageDeclaration(config).lint(ktFile)

            assertThat(findings).isEmpty()
        }

        @Test
        fun `should report if package declaration does not match`() {
            val ktFile =
                compileForTest(
                    Path("src/test/resources/InvalidPackageDeclarationSpec/src/foo/bar/MismatchedDeclaration.kt")
                )
            val findings = InvalidPackageDeclaration(config).lint(ktFile)

            assertThat(findings).hasSize(1)
        }

        @Test
        fun `should report if file path matches root package but package declaration differs`() {
            val ktFile =
                compileForTest(Path("src/test/resources/InvalidPackageDeclarationSpec/src/com/example/File.kt"))
            val findings = InvalidPackageDeclaration(config).lint(ktFile)

            assertThat(findings).hasSize(1)
        }
    }

    @Nested
    inner class `with root package required` {

        val config = TestConfig(ROOT_PACKAGE to "com.example", REQUIRE_ROOT_PACKAGE to true)

        @Test
        fun `should pass if declaration starts with root package`() {
            val ktFileWithRelativePath =
                compileForTest(Path("src/test/resources/InvalidPackageDeclarationSpec/src/foo/bar/File.kt"))
            val findingsForRelativePath = InvalidPackageDeclaration(config).lint(ktFileWithRelativePath)

            assertThat(findingsForRelativePath).isEmpty()

            val ktFileWithFullPath =
                compileForTest(
                    Path("src/test/resources/InvalidPackageDeclarationSpec/src/com/example/foo/bar/File.kt")
                )
            val findingsForFullPath = InvalidPackageDeclaration(config).lint(ktFileWithFullPath)

            assertThat(findingsForFullPath).isEmpty()
        }

        @Test
        fun `should report if root package is missing`() {
            val ktFile =
                compileForTest(
                    Path("src/test/resources/InvalidPackageDeclarationSpec/src/foo/bar/rootPackageMissing.kt")
                )
            val findings = InvalidPackageDeclaration(config).lint(ktFile)

            assertThat(findings).hasSize(1)
        }

        @Test
        fun `should report if declaration only shares a prefix with root package`() {
            val ktFile =
                compileForTest(Path("src/test/resources/InvalidPackageDeclarationSpec/src/com/example_extra/File.kt"))
            val findings = InvalidPackageDeclaration(config).lint(ktFile)

            assertThat(findings).hasSize(1)
        }
    }
}
