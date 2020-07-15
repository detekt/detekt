package io.gitlab.arturbosch.detekt.rules.style

import io.github.detekt.test.utils.compileContentForTest
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.test.compileAndLint
import io.gitlab.arturbosch.detekt.test.lint
import org.assertj.core.api.Assertions.assertThat
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

class SpacingBetweenPackageAndImportsSpec : Spek({
    val subject by memoized { SpacingBetweenPackageAndImports(Config.empty) }

    describe("SpacingBetweenPackageAndImports rule") {

        it("has no blank lines violation") {
            val code = "package test\n\nimport a.b\n\nclass A {}"
            assertThat(subject.lint(code)).isEmpty()
        }

        it("has a package and import declaration") {
            val code = "package test\n\nimport a.b"
            assertThat(subject.lint(code)).isEmpty()
        }

        it("has no import declaration") {
            val code = "package test\n\nclass A {}"
            assertThat(subject.lint(code)).isEmpty()
        }

        it("has no package declaration") {
            val code = "import a.b\n\nclass A {}"
            assertThat(subject.lint(code)).isEmpty()
        }

        it("has no package and import declaration") {
            val code = "class A {}"
            assertThat(subject.lint(code)).isEmpty()
        }

        it("has a comment declaration") {
            val code = "import a.b\n\n// a comment"
            assertThat(subject.lint(code)).isEmpty()
        }

        it("is an empty kt file") {
            assertThat(subject.lint("")).isEmpty()
        }

        describe("Kotlin scripts") {

            it("has no package declaration in script") {
                val code = "import a.b\n\nprint(1)"
                val ktsFile = compileContentForTest(code, "Test.kts")
                assertThat(subject.lint(ktsFile)).isEmpty()
            }

            it("has no package and import declaration in script") {
                val code = "print(1)"
                val ktsFile = compileContentForTest(code, "Test.kts")
                assertThat(subject.lint(ktsFile)).isEmpty()
            }

            it("has import declarations separated by new line in script") {
                val code = "import a.b\n\nimport a.c\n\nprint(1)"
                val ktsFile = compileContentForTest(code, "Test.kts")
                assertThat(subject.lint(ktsFile)).isEmpty()
            }
        }

        it("has code on new line") {
            val code = "package test\nimport a.b\nclass A {}"
            assertThat(subject.lint(code)).hasSize(2)
        }

        it("has code with spaces") {
            val code = "package test; import a.b; class A {}"
            assertThat(subject.lint(code)).hasSize(2)
        }

        it("has two many blank lines") {
            val code = "package test\n\n\nimport a.b\n\n\nclass A {}"
            assertThat(subject.lint(code)).hasSize(2)
        }

        it("has package declarations in same line") {
            val code = "package test;import a.b;class A {}"
            assertThat(subject.lint(code)).hasSize(2)
        }

        it("has multiple imports in file") {
            val code = """
                package com.my

                import kotlin.collections.List
                import kotlin.collections.Set

                class A { }
                """
            assertThat(subject.compileAndLint(code)).isEmpty()
        }

        it("has no class") {
            val code = """
                package com.my

                import kotlin.collections.List
                import kotlin.collections.Set
                """
            assertThat(subject.compileAndLint(code)).isEmpty()
        }
    }
})
