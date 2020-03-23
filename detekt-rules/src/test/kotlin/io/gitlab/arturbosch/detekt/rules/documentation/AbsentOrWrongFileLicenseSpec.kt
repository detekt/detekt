package io.gitlab.arturbosch.detekt.rules.documentation

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Finding
import io.gitlab.arturbosch.detekt.api.SetupContext
import io.gitlab.arturbosch.detekt.api.UnstableApi
import io.gitlab.arturbosch.detekt.api.internal.YamlConfig
import io.gitlab.arturbosch.detekt.test.NullPrintStream
import io.gitlab.arturbosch.detekt.test.assertThat
import io.gitlab.arturbosch.detekt.test.compileContentForTest
import io.gitlab.arturbosch.detekt.test.lint
import io.gitlab.arturbosch.detekt.test.resource
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe
import java.io.PrintStream
import java.net.URI
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

@OptIn(UnstableApi::class)
private fun checkLicence(content: String): List<Finding> {
    val file = compileContentForTest(content)

    val resource = resource("license-config.yml")
    val config = YamlConfig.load(Paths.get(resource))
    LicenceHeaderLoaderExtension().apply {
        init(object : SetupContext {
            override val configUris: Collection<URI> = listOf(resource)
            override val config: Config = config
            override val outPrinter: PrintStream = NullPrintStream()
            override val errPrinter: PrintStream = NullPrintStream()
        })
        onStart(listOf(file))
    }

    return AbsentOrWrongFileLicense().lint(file)
}
