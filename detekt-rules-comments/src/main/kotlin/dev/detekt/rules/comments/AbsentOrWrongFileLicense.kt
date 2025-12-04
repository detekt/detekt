package dev.detekt.rules.comments

import dev.detekt.api.Config
import dev.detekt.api.Configuration
import dev.detekt.api.Entity
import dev.detekt.api.Finding
import dev.detekt.api.Rule
import dev.detekt.api.config
import org.jetbrains.kotlin.psi.KtFile

/**
 * This rule will report every Kotlin source file which doesn't have the required license header.
 * The rule validates each Kotlin source and operates in two modes: if `licenseTemplateIsRegex = false` (or missing)
 * the rule checks whether the input file header starts with the read text from the passed file in the
 * `licenseTemplateFile` configuration option. If `licenseTemplateIsRegex = true` the rule matches the header with
 * a regular expression produced from the passed template license file (defined via `licenseTemplateFile` configuration
 * option).
 */
class AbsentOrWrongFileLicense(config: Config) : Rule(
    config,
    "License text is absent or incorrect."
) {

    @Suppress("unused")
    @Configuration("path to file with license header template resolved relatively to config file")
    private val licenseTemplateFile: String by config(DEFAULT_LICENSE_TEMPLATE_FILE)

    @Suppress("unused")
    @Configuration("whether or not the license header template is a regex template")
    private val licenseTemplateIsRegex: Boolean by config(DEFAULT_LICENSE_TEMPLATE_IS_REGEX)

    override fun visitKtFile(file: KtFile) {
        if ((file.hasLicenseHeader() || file.hasLicenseHeaderRegex()) && !file.hasValidLicense()) {
            report(
                Finding(
                    Entity.atPackageOrFirstDecl(file),
                    "Expected license not found or incorrect in the file: ${file.name}."
                )
            )
        }
    }

    private fun KtFile.hasValidLicense(): Boolean =
        if (hasLicenseHeaderRegex()) {
            getLicenseHeaderRegex().find(text)?.range?.start == 0
        } else {
            text.startsWith(getLicenseHeader())
        }

    companion object {
        const val PARAM_LICENSE_TEMPLATE_FILE = "licenseTemplateFile"
        const val DEFAULT_LICENSE_TEMPLATE_FILE = "license.template"
        const val PARAM_LICENSE_TEMPLATE_IS_REGEX = "licenseTemplateIsRegex"
        const val DEFAULT_LICENSE_TEMPLATE_IS_REGEX = false
        val RULE_NAME: String = AbsentOrWrongFileLicense::class.java.simpleName
    }
}
