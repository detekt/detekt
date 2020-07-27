package io.gitlab.arturbosch.detekt.rules.documentation

import io.github.detekt.test.utils.NullPrintStream
import io.github.detekt.test.utils.compileContentForTest
import io.github.detekt.test.utils.resourceAsPath
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Finding
import io.gitlab.arturbosch.detekt.api.SetupContext
import io.gitlab.arturbosch.detekt.api.UnstableApi
import io.gitlab.arturbosch.detekt.api.internal.YamlConfig
import io.gitlab.arturbosch.detekt.test.assertThat
import io.gitlab.arturbosch.detekt.test.lint
import org.jetbrains.kotlin.resolve.BindingContext
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe
import java.io.PrintStream
import java.net.URI

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

    val resource = resourceAsPath("license-config.yml")
    val config = YamlConfig.load(resource)
    LicenceHeaderLoaderExtension().apply {
        init(object : SetupContext {
            override val configUris: Collection<URI> = listOf(resource.toUri())
            override val config: Config = config
            override val outputChannel: PrintStream = NullPrintStream()
            override val errorChannel: PrintStream = NullPrintStream()
            override val properties: MutableMap<String, Any?> = HashMap()
            override fun register(key: String, value: Any) {
                properties[key] = value
            }
        })
        onStart(listOf(file), BindingContext.EMPTY)
    }

    return AbsentOrWrongFileLicense().lint(file)
}
