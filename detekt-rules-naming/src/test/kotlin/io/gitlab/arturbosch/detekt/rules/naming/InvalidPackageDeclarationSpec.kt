package io.gitlab.arturbosch.detekt.rules.naming

import io.github.detekt.test.utils.compileContentForTest
import io.gitlab.arturbosch.detekt.test.TestConfig
import io.gitlab.arturbosch.detekt.test.assertThat
import io.gitlab.arturbosch.detekt.test.lint
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import java.nio.file.FileSystems
import kotlin.io.path.Path

private const val ROOT_PACKAGE = "rootPackage"
private const val REQUIRE_ROOT_PACKAGE = "requireRootInDeclaration"

class InvalidPackageDeclarationSpec {

    @Test
    fun `should pass if package declaration is correct`() {
        val source = """
            package foo.bar

            class C
        """.trimIndent()

        val ktFile = compileContentForTest(source, createPath("project/src/foo/bar/File.kt"))
        val findings = InvalidPackageDeclaration().lint(ktFile)

        assertThat(findings).isEmpty()
    }

    @Test
    fun `should ignore the issue by alias suppression`() {
        val source = """
            @file:Suppress("PackageDirectoryMismatch")
            package foo

            class C
        """.trimIndent()

        val ktFile = compileContentForTest(source, createPath("project/src/bar/File.kt"))
        val findings = InvalidPackageDeclaration().lint(ktFile)

        assertThat(findings).isEmpty()
    }

    @Test
    fun `should report if package declaration does not match source location`() {
        val source = "package foo\n\nclass C"

        val ktFile = compileContentForTest(source, createPath("project/src/bar/File.kt"))
        val findings = InvalidPackageDeclaration().lint(ktFile)

        assertThat(findings).hasSize(1)
        assertThat(findings).hasTextLocations(0 to 11)
    }

    @Nested
    inner class `with root package specified` {

        val config = TestConfig(mapOf(ROOT_PACKAGE to "com.example"))

        @Test
        fun `should pass if file is located within the root package`() {
            val source = """
                package com.example

                class C
            """.trimIndent()

            val ktFile = compileContentForTest(source, createPath("src/File.kt"))
            val findings = InvalidPackageDeclaration(config).lint(ktFile)

            assertThat(findings).isEmpty()
        }

        @Test
        fun `should pass if file is located relative to root package`() {
            val source = """
                package com.example.foo.bar

                class C
            """.trimIndent()

            val ktFile = compileContentForTest(source, createPath("src/foo/bar/File.kt"))
            val findings = InvalidPackageDeclaration(config).lint(ktFile)

            assertThat(findings).isEmpty()
        }

        @Test
        fun `should pass if file is located in directory corresponding to package declaration`() {
            val source = """
                package com.example.foo.bar

                class C
            """.trimIndent()

            val ktFile = compileContentForTest(source, createPath("src/com/example/foo/bar/File.kt"))
            val findings = InvalidPackageDeclaration(config).lint(ktFile)

            assertThat(findings).isEmpty()
        }

        @Test
        fun `should report if package declaration does not match`() {
            val source = """
                package com.example.foo.baz

                class C
            """.trimIndent()

            val ktFile = compileContentForTest(source, createPath("src/foo/bar/File.kt"))
            val findings = InvalidPackageDeclaration(config).lint(ktFile)

            assertThat(findings).hasSize(1)
        }

        @Test
        fun `should report if file path matches root package but package declaration differs`() {
            val source = """
                package io.foo.bar

                class C
            """.trimIndent()

            val ktFile = compileContentForTest(source, createPath("src/com/example/File.kt"))
            val findings = InvalidPackageDeclaration(config).lint(ktFile)

            assertThat(findings).hasSize(1)
        }
    }

    @Nested
    inner class `with root package required` {

        val config = TestConfig(mapOf(ROOT_PACKAGE to "com.example", REQUIRE_ROOT_PACKAGE to true))

        @Test
        fun `should pass if declaration starts with root package`() {
            val source = """
                package com.example.foo.bar

                class C
            """.trimIndent()

            val ktFileWithRelativePath = compileContentForTest(source, createPath("src/foo/bar/File.kt"))
            val findingsForRelativePath = InvalidPackageDeclaration(config).lint(ktFileWithRelativePath)

            assertThat(findingsForRelativePath).isEmpty()

            val ktFileWithFullPath = compileContentForTest(source, createPath("src/com/example/foo/bar/File.kt"))
            val findingsForFullPath = InvalidPackageDeclaration(config).lint(ktFileWithFullPath)

            assertThat(findingsForFullPath).isEmpty()
        }

        @Test
        fun `should report if root package is missing`() {
            val source = """
                package foo.bar

                class C
            """.trimIndent()

            val ktFile = compileContentForTest(source, createPath("src/foo/bar/File.kt"))
            val findings = InvalidPackageDeclaration(config).lint(ktFile)

            assertThat(findings).hasSize(1)
        }
    }
}

private fun createPath(universalPath: String): String {
    val pathSegments = universalPath.split('/')
    val aRootPath = FileSystems.getDefault().rootDirectories.first()
    val path = Path(aRootPath.toString(), *pathSegments.toTypedArray())
    return path.toString()
}
