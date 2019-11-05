package io.gitlab.arturbosch.detekt.rules.documentation

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Debt
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.Severity
import org.jetbrains.kotlin.psi.KtFile
import java.io.File

/**
 * This rule will report every Kotlin source file which doesn't have required license header.
 *
 * @configuration licenseTemplateFile - path to file with license header template
 * (default: `config/detekt/license.template`)
 */
class AbsentOrWrongFileLicense(config: Config = Config.empty) : Rule(config) {

    private val expectedLicense: String by lazy(LazyThreadSafetyMode.NONE) { loadLicenseFromFile() }

    override val issue = Issue(
        id = "AbsentOrWrongFileLicense",
        severity = Severity.Maintainability,
        description = "License text is absent or incorrect in the file.",
        debt = Debt.FIVE_MINS
    )

    override fun visitKtFile(file: KtFile) {
        if (!file.hasValidLicense) {
            reportCodeSmell(file)
        }
    }

    private inline val KtFile.hasValidLicense: Boolean
        get() = text.startsWith(expectedLicense)

    private fun reportCodeSmell(file: KtFile) {
        report(CodeSmell(
            issue, Entity.from(file),
            "Expected license not found or incorrect in the file: ${file.name}."
        ))
    }

    // TODO cache this action
    private fun loadLicenseFromFile(): String {
        val pathToTemplate = valueOrDefault(PARAM_LICENSE_TEMPLATE_FILE, DEFAULT_LICENSE_TEMPLATE_FILE)
        val file = File(pathToTemplate)

        require(file.exists()) {
            """
                License template file not found at `${file.absolutePath}`.
                Create file license header file or check your running path.
            """.trimIndent()
        }

        return file.readText()
    }
}

private const val PARAM_LICENSE_TEMPLATE_FILE = "licenseTemplateFile"

private const val DEFAULT_LICENSE_TEMPLATE_FILE = "config/detekt/license.template"
