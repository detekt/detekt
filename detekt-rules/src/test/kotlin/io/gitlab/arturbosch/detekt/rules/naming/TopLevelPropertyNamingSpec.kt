package io.gitlab.arturbosch.detekt.rules.naming

import io.gitlab.arturbosch.detekt.test.compileContentForTest
import io.gitlab.arturbosch.detekt.test.lint
import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.subject.SubjectSpek

class TopLevelPropertyNamingSpec : SubjectSpek<TopLevelPropertyNaming>({

	subject { TopLevelPropertyNaming() }

	describe("constants on top level") {
		val code = compileContentForTest("""
			const val MY_NAME_8 = "Artur"
            const val MYNAME = "Artur"
            const val MyNAME = "Artur" // invalid
			const val name = "Artur" // invalid
			const val nAme = "Artur" // invalid
			private const val _nAme = "Artur" // invalid
            const val serialVersionUID = 42L // invalid
		""")

		it("should detect five constants not matching [A-Z][_A-Z\\d]*") {
			val findings = TopLevelPropertyNaming().lint(code)
			assertThat(findings).hasSize(5)
		}
	}

	describe("variables on top level") {
		val code = compileContentForTest("""
			val MY_NAME = "Artur" // invalid
            val MYNAME = "Artur" // invalid
            val MyNAME = "Artur" // invalid
			val name = "Artur"
			val nAme8 = "Artur"
			val _nAme = "Artur" // invalid
			private val _name = "Artur"
			private val NAME = "Artur // invalid
            val serialVersionUID = 42L
		""")

		val findings = TopLevelPropertyNaming().lint(code)

		it("should detect four top level variables not matching [a-z][A-Za-z\\d]*") {
			assertThat(findings).hasSize(5)
		}

		it("should allow underscores in private property") {
			assertThat(findings.find { it.name == "_name" }).isNull()
		}
	}
})
