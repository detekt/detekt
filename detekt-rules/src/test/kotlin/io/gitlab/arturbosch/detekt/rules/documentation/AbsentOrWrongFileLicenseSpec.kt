package io.gitlab.arturbosch.detekt.rules.documentation

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.rules.Case
import io.gitlab.arturbosch.detekt.test.assertThat
import io.gitlab.arturbosch.detekt.test.lint
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

internal class AbsentOrWrongFileLicenseSpec : Spek({

    val subject by memoized { AbsentOrWrongFileLicense(Config.empty) }

    /*describe("AbsentOrWrongFileLicense rule") {

        context("file with correct license header") {
            it("reports nothing") {
                val path = Case.FileWithCorrectLicenseHeader.path()
                val findings = subject.lint(path)

                assertThat(findings).isEmpty()
            }
        }

        context("file with incorrect license header") {
            it("reports missed license header") {
                val path = Case.FileWithIncorrectLicenseHeader.path()
                val findings = subject.lint(path)

                //TODO()
            }
        }

        context("file with absent license header") {
            it("reports missed license header") {
                val path = Case.FileWithAbsentLicenseHeader.path()
                val findings = subject.lint(path)

                //TODO()
            }
        }
    }*/
})
