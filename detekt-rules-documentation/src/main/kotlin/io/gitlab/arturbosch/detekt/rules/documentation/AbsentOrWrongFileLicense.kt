package io.gitlab.arturbosch.detekt.rules.documentation

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Debt
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.Severity
import io.gitlab.arturbosch.detekt.api.config
import io.gitlab.arturbosch.detekt.api.internal.Configuration
import org.jetbrains.kotlin.psi.KtFile

/**
 * This rule will report every Kotlin source file which doesn't have the required license header.
 * The rule validates each Kotlin source and operates in two modes: if `licenseTemplateIsRegex = false` (or missing)
 * the rule checks whether the input file header starts with the read text from the passed file in the
 * `licenseTemplateFile` configuration option. If `licenseTemplateIsRegex = true` the rule matches the header with
 * a regular expression produced from the passed template license file (defined via `licenseTemplateFile` configuration
 * option).
 */
class AbsentOrWrongFileLicense(config: Config = Config.empty) : Rule(config) {

    override val issue = Issue(
        id = RULE_NAME,
        severity = Severity.Maintainability,
        description = "License text for the given source code file is absent or incorrect.",
        debt = Debt.FIVE_MINS
    )

    @Suppress("unused")
    @Configuration("path to file with license header template resolved relatively to config file")
    private val licenseTemplateFile: String by config(DEFAULT_LICENSE_TEMPLATE_FILE)

    @Suppress("unused")
    @Configuration("whether or not the license header template is a regex template")
    private val licenseTemplateIsRegex: Boolean by config(DEFAULT_LICENSE_TEMPLATE_IS_REGEX)

    override fun visitCondition(root: KtFile): Boolean =
        super.visitCondition(root) && (root.hasLicenseHeader() || root.hasLicenseHeaderRegex())

    override fun visitKtFile(file: KtFile) {
        if (!file.hasValidLicense()) {
            report(
                CodeSmell(
                    issue,
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
