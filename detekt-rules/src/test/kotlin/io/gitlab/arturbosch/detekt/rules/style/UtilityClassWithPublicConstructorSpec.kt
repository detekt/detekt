package io.gitlab.arturbosch.detekt.rules.style

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.rules.Case
import io.gitlab.arturbosch.detekt.test.lint
import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.spek.api.dsl.given
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.subject.SubjectSpek

class UtilityClassWithPublicConstructorSpec : SubjectSpek<UtilityClassWithPublicConstructor>({

    subject { UtilityClassWithPublicConstructor(Config.empty) }

    given("several UtilityClassWithPublicConstructor rule violations") {

        val findings = subject.lint(Case.UtilityClassesPositive.path())

        it("reports utility classes with a public constructor") {
            assertThat(findings).hasSize(6)
        }

        it("reports utility classes which are marked as open") {
            val count = findings.count { it.message.contains("The utility class OpenUtilityClass should be final.") }
            assertThat(count).isEqualTo(1)
        }
    }

    given("several classes which adhere to the UtilityClassWithPublicConstructor rule") {

        it("does not report given classes") {
            val findings = subject.lint(Case.UtilityClassesNegative.path())
            assertThat(findings).isEmpty()
        }
    }

    given("annotations class") {

        it("should not get triggered for utility class") {
            val code = """
				@Retention(AnnotationRetention.SOURCE)
				@StringDef(
					Gender.MALE,
					Gender.FEMALE
				)
				annotation class Gender {
					companion object {
						const val MALE = "male"
						const val FEMALE = "female"
					}
				}
			""".trimIndent()
            assertThat(subject.lint(code)).isEmpty()
        }
    }
})
