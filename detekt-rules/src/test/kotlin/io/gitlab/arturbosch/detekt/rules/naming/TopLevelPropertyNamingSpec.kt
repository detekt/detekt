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

		it("should not detect any constants not complying to the naming rules") {
			val code = compileContentForTest("""
				const val MY_NAME_8 = "Artur"
            	const val MYNAME = "Artur"
			""")
			assertThat(subject.lint(code)).hasSize(0)
		}

		it("should detect five constants not complying to the naming rules") {
			val code = compileContentForTest("""
            	const val MyNAME = "Artur"
				const val name = "Artur"
				const val nAme = "Artur"
				private const val _nAme = "Artur"
            	const val serialVersionUID = 42L
			""")
			assertThat(subject.lint(code)).hasSize(5)
		}
	}

	describe("variables on top level") {

		it("should not detect any constants not complying to the naming rules") {
			val code = compileContentForTest("""
				val name = "Artur"
				val nAme8 = "Artur"
				private val _name = "Artur"
            	val serialVersionUID = 42L
			""")
			assertThat(subject.lint(code)).hasSize(0)
		}

		it("should detect five constants not complying to the naming rules") {
			val code = compileContentForTest("""
				val MY_NAME = "Artur"
        	    val MYNAME = "Artur"
        	    val MyNAME = "Artur"
				val _nAme = "Artur"
				private val NAME = "Artur"
			""")
			assertThat(subject.lint(code)).hasSize(5)
		}
	}
})
