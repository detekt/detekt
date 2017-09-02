package io.gitlab.arturbosch.detekt.rules.style

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.test.lint
import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.spek.api.dsl.given
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.subject.SubjectSpek
import org.jetbrains.spek.subject.dsl.SubjectProviderDsl

class PackageDeclarationStyleSpec : SubjectSpek<PackageDeclarationStyle>({
	subject { PackageDeclarationStyle(Config.empty) }

	given("several package and import declarations") {

		it("has no blank lines violation") {
			val code = "package test\n\nimport a.b\n\nclass A {}"
			assertCodeWithoutViolation(code)
		}

		it("has a package and import declaration") {
			val code = "package test\n\nimport a.b"
			assertCodeWithoutViolation(code)
		}

		it("has no import declaration") {
			val code = "package test\n\nclass A {}"
			assertCodeWithoutViolation(code)
		}

		it("has no package declaration") {
			val code = "import a.b\n\nclass A {}"
			assertCodeWithoutViolation(code)
		}

		it("has no package and import declaration") {
			val code = "class A {}"
			assertCodeWithoutViolation(code)
		}

		it("has a comment declaration") {
			val code = "import a.b\n\n// a comment"
			assertCodeWithoutViolation(code)
		}

		it("is an empty kt file") {
			assertCodeWithoutViolation("")
		}

		it("has code on new line") {
			val code = "package test\nimport a.b\nclass A {}"
			assertCodeViolation(code, 2)
		}

		it("has code with spaces") {
			val code = "package test; import a.b; class A {}"
			assertCodeViolation(code, 2)
		}

		it("has package declarations in same line") {
			val code = "package test;import a.b;class A {}"
			assertCodeViolation(code, 2)
		}
	}
})

private fun SubjectProviderDsl<PackageDeclarationStyle>.assertCodeViolation(code: String, size: Int) {
	assertThat(subject.lint(code)).hasSize(size)
}

private fun SubjectProviderDsl<PackageDeclarationStyle>.assertCodeWithoutViolation(code: String) {
	assertThat(subject.lint(code)).hasSize(0)
}
