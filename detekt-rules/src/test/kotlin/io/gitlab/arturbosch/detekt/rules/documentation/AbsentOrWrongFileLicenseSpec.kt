package io.gitlab.arturbosch.detekt.rules.documentation

import io.gitlab.arturbosch.detekt.api.Finding
import io.gitlab.arturbosch.detekt.api.YamlConfig
import io.gitlab.arturbosch.detekt.test.assertThat
import io.gitlab.arturbosch.detekt.test.compileContentForTest
import io.gitlab.arturbosch.detekt.test.lint
import io.gitlab.arturbosch.detekt.test.resource
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe
import java.nio.file.Paths

internal class AbsentOrWrongFileLicenseSpec : Spek({

    describe("AbsentOrWrongFileLicense rule") {
        context("file with correct license header") {
            it("reports nothing") {
                val findings = checkLicence("""
                    /* LICENSE */
                    package cases
                """.trimIndent())

                assertThat(findings).isEmpty()
            }
        }

        context("file with incorrect license header") {
            it("reports missed license header") {
                val findings = checkLicence("""
                    /* WRONG LICENSE */
                    package cases
                """.trimIndent())

                assertThat(findings).hasSize(1)
            }
        }

        context("file with absent license header") {
            it("reports missed license header") {
                val findings = checkLicence("""
                    package cases
                """.trimIndent())

                assertThat(findings).hasSize(1)
            }
        }
    }
})

private fun checkLicence(content: String): List<Finding> {
    val file = compileContentForTest(content)

    val config = YamlConfig.load(Paths.get(resource("license-config.yml")))
    LicenceHeaderLoaderExtension().apply {
        init(config)
        onStart(listOf(file))
    }

    return AbsentOrWrongFileLicense().lint(file)
}
