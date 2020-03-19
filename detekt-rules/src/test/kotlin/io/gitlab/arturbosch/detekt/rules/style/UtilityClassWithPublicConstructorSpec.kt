package io.gitlab.arturbosch.detekt.rules.style

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Finding
import io.gitlab.arturbosch.detekt.rules.Case
import io.gitlab.arturbosch.detekt.test.lint
import org.assertj.core.api.Assertions.assertThat
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

class UtilityClassWithPublicConstructorSpec : Spek({

    val subject by memoized { UtilityClassWithPublicConstructor(Config.empty) }
    describe("UtilityClassWithPublicConstructor rule") {

        context("several UtilityClassWithPublicConstructor rule violations") {

            lateinit var findings: List<Finding>

            beforeEachTest {
                findings = subject.lint(Case.UtilityClassesPositive.path())
            }

            it("reports utility classes with a public constructor") {
                assertThat(findings).hasSize(6)
            }

            it("reports utility classes which are marked as open") {
                val count = findings.count { it.message.contains("The utility class OpenUtilityClass should be final.") }
                assertThat(count).isEqualTo(1)
            }
        }

        context("several classes which adhere to the UtilityClassWithPublicConstructor rule") {

            it("does not report given classes") {
                val findings = subject.lint(Case.UtilityClassesNegative.path())
                assertThat(findings).isEmpty()
            }
        }

        context("annotations class") {

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
            """
                assertThat(subject.lint(code)).isEmpty()
            }
        }
    }
})
