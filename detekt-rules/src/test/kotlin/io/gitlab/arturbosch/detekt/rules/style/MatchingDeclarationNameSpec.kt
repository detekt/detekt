package io.gitlab.arturbosch.detekt.rules.style

import io.gitlab.arturbosch.detekt.test.assertThat
import io.gitlab.arturbosch.detekt.test.compileContentForTest
import io.gitlab.arturbosch.detekt.test.lint
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.given
import org.jetbrains.spek.api.dsl.it

/**
 * @author Artur Bosch
 */
internal class MatchingDeclarationNameSpec : Spek({

	given("compliant test cases") {

		it("should pass for object declaration") {
			val ktFile = compileContentForTest("object O")
			ktFile.name = "O.kt"
			val findings = MatchingDeclarationName().lint(ktFile)
			assertThat(findings).isEmpty()
		}

		it("should pass for class declaration") {
			val ktFile = compileContentForTest("class C")
			ktFile.name = "C.kt"
			val findings = MatchingDeclarationName().lint(ktFile)
			assertThat(findings).isEmpty()
		}

		it("should pass for multiple declaration") {
			val ktFile = compileContentForTest("""
				class C
				object O
				fun a() = 5
			""")
			ktFile.name = "MultiDeclarations.kt"
			val findings = MatchingDeclarationName().lint(ktFile)
			assertThat(findings).isEmpty()
		}

		it("should pass for class declaration with utility functions") {
			val ktFile = compileContentForTest("""
				class C
				fun a() = 5
				fun C.b() = 5
			""")
			ktFile.name = "C.kt"
			val findings = MatchingDeclarationName().lint(ktFile)
			assertThat(findings).isEmpty()
		}
	}

	given("non-compliant test cases") {

		it("should not pass for object declaration") {
			val ktFile = compileContentForTest("object O")
			ktFile.name = "Objects.kt"
			val findings = MatchingDeclarationName().lint(ktFile)
			assertThat(findings).hasSize(1)
		}

		it("should not pass for class declaration") {
			val ktFile = compileContentForTest("class C")
			ktFile.name = "Classes.kt"
			val findings = MatchingDeclarationName().lint(ktFile)
			assertThat(findings).hasSize(1)
		}

		it("should not pass for class declaration with utility functions") {
			val ktFile = compileContentForTest("""
				class C
				fun a() = 5
				fun C.b() = 5
			""")
			ktFile.name = "ClassUtils.kt"
			val findings = MatchingDeclarationName().lint(ktFile)
			assertThat(findings).hasSize(1)
		}
	}
})
