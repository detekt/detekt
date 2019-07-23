package io.gitlab.arturbosch.detekt.rules.style

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.test.lint
import org.assertj.core.api.Assertions.assertThat
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

class SpacingBetweenPackageAndImportsSpec : Spek({
    val subject by memoized { SpacingBetweenPackageAndImports(Config.empty) }

    describe("SpacingBetweenPackageAndImports rule") {

        it("has no blank lines violation") {
            val code = "package test\n\nimport a.b\n\nclass A {}"
            assertThat(subject.lint(code)).hasSize(0)
        }

        it("has a package and import declaration") {
            val code = "package test\n\nimport a.b"
            assertThat(subject.lint(code)).hasSize(0)
        }

        it("has no import declaration") {
            val code = "package test\n\nclass A {}"
            assertThat(subject.lint(code)).hasSize(0)
        }

        it("has no package declaration") {
            val code = "import a.b\n\nclass A {}"
            assertThat(subject.lint(code)).hasSize(0)
        }

        it("has no package and import declaration") {
            val code = "class A {}"
            assertThat(subject.lint(code)).hasSize(0)
        }

        it("has a comment declaration") {
            val code = "import a.b\n\n// a comment"
            assertThat(subject.lint(code)).hasSize(0)
        }

        it("is an empty kt file") {
            assertThat(subject.lint("")).hasSize(0)
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

        it("should be valid") {
            val code = """
				package com.my.package

				import android.util.Log
				import java.util.concurrent.TimeUnit

				class MyClass { }
				"""
            assertThat(subject.lint(code)).hasSize(0)
        }

        it("has no class") {
            val code = """
				package com.my.package

				import android.util.Log
				import java.util.concurrent.TimeUnit
				"""
            assertThat(subject.lint(code)).hasSize(0)
        }
    }
})
