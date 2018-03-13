package io.gitlab.arturbosch.detekt.rules.style

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.rules.Case
import io.gitlab.arturbosch.detekt.test.TestConfig
import io.gitlab.arturbosch.detekt.test.lint
import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.spek.api.dsl.given
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.subject.SubjectSpek

/**
 * @author Ivan Balaksha
 */
class UseDataClassSpec : SubjectSpek<UseDataClass>({

	subject { UseDataClass(Config.empty) }

	given("several classes") {

		it("reports potential data classes") {
			assertThat(subject.lint(Case.UseDataClassPositive.path())).hasSize(5)
		}

		it("does not report invalid data class candidates") {
			assertThat(subject.lint(Case.UseDataClassNegative.path())).hasSize(0)
		}
	}

	given("a class with an annotation which is ignored") {

		it("does not report a potential data class") {
			val code = """
				import kotlin.SinceKotlin

				@SinceKotlin("1.0.0")
				class AnnotatedClass(val i: Int) {}
				"""
			val config = TestConfig(mapOf(UseDataClass.EXCLUDE_ANNOTATED_CLASSES to "kotlin.*"))
			assertThat(UseDataClass(config).lint(code)).hasSize(0)
		}
	}
})
