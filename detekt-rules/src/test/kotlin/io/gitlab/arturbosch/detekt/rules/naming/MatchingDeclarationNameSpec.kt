package io.gitlab.arturbosch.detekt.rules.naming

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

		it("should pass for interface declaration") {
			val ktFile = compileContentForTest("interface I")
			ktFile.name = "I.kt"
			val findings = MatchingDeclarationName().lint(ktFile)
			assertThat(findings).isEmpty()
		}

		it("should pass for enum declaration") {
			val ktFile = compileContentForTest("""
				enum class E {
					ONE, TWO, THREE
				}
			""")
			ktFile.name = "E.kt"
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

		it("should pass for private class declaration") {
			val ktFile = compileContentForTest("""
				private class C
				fun a() = 5
			""")
			ktFile.name = "b.kt"
			val findings = MatchingDeclarationName().lint(ktFile)
			assertThat(findings).isEmpty()
		}
	}

	given("non-compliant test cases") {

		it("should not pass for object declaration") {
			val ktFile = compileContentForTest("object O")
			ktFile.name = "Objects.kt"
			val findings = MatchingDeclarationName().lint(ktFile)
			assertThat(findings).hasLocationStrings("'object O' at (1,1) in /Objects.kt")
		}

		it("should not pass for class declaration") {
			val ktFile = compileContentForTest("class C")
			ktFile.name = "Classes.kt"
			val findings = MatchingDeclarationName().lint(ktFile)
			assertThat(findings).hasLocationStrings("'class C' at (1,1) in /Classes.kt")
		}

		it("should not pass for class declaration with utility functions") {
			val ktFile = compileContentForTest("""
				class C
				fun a() = 5
				fun C.b() = 5
			""")
			ktFile.name = "ClassUtils.kt"
			val findings = MatchingDeclarationName().lint(ktFile)
			assertThat(findings).hasLocationStrings("""'
				class C
				fun a() = 5
				fun C.b() = 5
			' at (1,1) in /ClassUtils.kt""", trimIndent = true)
		}

		it("should not pass for interface declaration") {
			val ktFile = compileContentForTest("interface I")
			ktFile.name = "Not_I.kt"
			val findings = MatchingDeclarationName().lint(ktFile)
			assertThat(findings).hasLocationStrings("'interface I' at (1,1) in /Not_I.kt")
		}

		it("should not pass for enum declaration") {
			val ktFile = compileContentForTest("""
				enum class NOT_E {
					ONE, TWO, THREE
				}
			""")
			ktFile.name = "E.kt"
			val findings = MatchingDeclarationName().lint(ktFile)
			assertThat(findings).hasLocationStrings("""'
				enum class NOT_E {
					ONE, TWO, THREE
				}
			' at (1,1) in /E.kt""", trimIndent = true)
		}
	}
})
