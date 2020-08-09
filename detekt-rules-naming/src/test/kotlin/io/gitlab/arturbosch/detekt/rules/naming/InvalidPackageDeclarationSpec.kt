package io.gitlab.arturbosch.detekt.rules.naming

import io.github.detekt.test.utils.compileContentForTest
import io.gitlab.arturbosch.detekt.rules.naming.InvalidPackageDeclaration.Companion.ROOT_PACKAGE
import io.gitlab.arturbosch.detekt.test.TestConfig
import io.gitlab.arturbosch.detekt.test.assertThat
import io.gitlab.arturbosch.detekt.test.lint
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe
import java.nio.file.FileSystems
import java.nio.file.Paths

internal class InvalidPackageDeclarationSpec : Spek({

    describe("InvalidPackageDeclaration rule") {

        it("should pass if package declaration is correct") {
            val source = """
                package foo.bar

                class C
            """

            val ktFile = compileContentForTest(source, createPath("project/src/foo/bar/File.kt"))
            val findings = InvalidPackageDeclaration().lint(ktFile)

            assertThat(findings).isEmpty()
        }

        it("should report if package declaration is missing") {
            val ktFile = compileContentForTest("class C")

            val findings = InvalidPackageDeclaration().lint(ktFile)

            assertThat(findings).hasSize(1)
        }

        it("should report if package declaration does not match source location") {
            val source = """
                package foo

                class C
            """.trimIndent()

            val ktFile = compileContentForTest(source, createPath("project/src/bar/File.kt"))
            val findings = InvalidPackageDeclaration().lint(ktFile)

            assertThat(findings).hasSize(1)
            assertThat(findings).hasTextLocations(0 to 11)
        }

        describe("with root package specified") {

            val config by memoized { TestConfig(mapOf(ROOT_PACKAGE to "com.example")) }

            it("should pass if file is located within the root package") {
                val source = """
                    package com.example

                    class C
                """

                val ktFile = compileContentForTest(source, createPath("src/File.kt"))
                val findings = InvalidPackageDeclaration(config).lint(ktFile)

                assertThat(findings).isEmpty()
            }

            it("should pass if file is located relative to root package") {
                val source = """
                    package com.example.foo.bar

                    class C
                """

                val ktFile = compileContentForTest(source, createPath("src/foo/bar/File.kt"))
                val findings = InvalidPackageDeclaration(config).lint(ktFile)

                assertThat(findings).isEmpty()
            }

            it("should pass if file is located in directory corresponding to package declaration") {
                val source = """
                    package com.example.foo.bar

                    class C
                """

                val ktFile = compileContentForTest(source, createPath("src/com/example/foo/bar/File.kt"))
                val findings = InvalidPackageDeclaration(config).lint(ktFile)

                assertThat(findings).isEmpty()
            }

            it("should report if package declaration does not match") {
                val source = """
                    package com.example.foo.baz

                    class C
                """

                val ktFile = compileContentForTest(source, createPath("src/foo/bar/File.kt"))
                val findings = InvalidPackageDeclaration(config).lint(ktFile)

                assertThat(findings).hasSize(1)
            }
            it("should report if file path matches root package but package declaration differs") {
                val source = """
                    package io.foo.bar

                    class C
                """

                val ktFile = compileContentForTest(source, createPath("src/com/example/File.kt"))
                val findings = InvalidPackageDeclaration(config).lint(ktFile)

                assertThat(findings).hasSize(1)
            }
        }
    }
})

private fun createPath(universalPath: String): String {
    val pathSegments = universalPath.split('/')
    val aRootPath = FileSystems.getDefault().rootDirectories.first()
    val path = Paths.get(aRootPath.toString(), *pathSegments.toTypedArray())
    return path.toString()
}
