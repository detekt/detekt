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
 * the rule checks whether the input file header starts with text from `licenseTemplate` configuration option.
 * If `licenseTemplateIsRegex = true` the rule matches the header with a regular expression produced from the passed
 * template license.
 */
class AbsentOrWrongFileLicense(config: Config) : Rule(config, "License text is absent or incorrect.") {

    @Configuration("Whether or not the license header template is a regex template")
    private val licenseTemplateIsRegex: Boolean by config(false)

    @Configuration("License header template")
    private val licenseTemplate: String by config("")

    private val matchesLicense: (String) -> Boolean = if (licenseTemplateIsRegex) {
        val regex = licenseTemplate.toRegex(RegexOption.MULTILINE)
        fun matcher(text: String): Boolean = regex.find(text)?.range?.start == 0
        ::matcher
    } else {
        { it.startsWith(licenseTemplate) }
    }

    override fun visitKtFile(file: KtFile) {
        if (!matchesLicense(file.text)) {
            report(
                Finding(
                    Entity.atPackageOrFirstDecl(file),
                    "Expected license not found or incorrect in the file: ${file.name}."
                )
            )
        }
    }
}
