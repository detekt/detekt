package io.gitlab.arturbosch.detekt.rules.documentation

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Debt
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.Severity
import org.jetbrains.kotlin.psi.KtFile

/**
 * This rule will report every Kotlin source file which doesn't have the required license header.
 * The rule checks each Kotlin source file whether the header starts with the read text from the passed file in the
 * `licenseTemplateFile` configuration option.
 *
 * @configuration licenseTemplateFile - path to file with license header template resolved relatively to config file
 * (default: `'license.template'`)
 */
class AbsentOrWrongFileLicense(config: Config = Config.empty) : Rule(config) {

    override val issue = Issue(
        id = RULE_NAME,
        severity = Severity.Maintainability,
        description = "License text is absent or incorrect in the file.",
        debt = Debt.FIVE_MINS
    )

    override fun visitCondition(root: KtFile): Boolean =
        super.visitCondition(root) && root.hasLicenseHeader()

    override fun visitKtFile(file: KtFile) {
        if (!file.hasValidLicense()) {
            report(CodeSmell(
                issue,
                Entity.atPackageOrFirstDecl(file),
                "Expected license not found or incorrect in the file: ${file.name}."
            ))
        }
    }

    private fun KtFile.hasValidLicense(): Boolean = text.startsWith(getLicenseHeader())

    companion object {
        const val PARAM_LICENSE_TEMPLATE_FILE = "licenseTemplateFile"
        const val DEFAULT_LICENSE_TEMPLATE_FILE = "license.template"
        val RULE_NAME: String = AbsentOrWrongFileLicense::class.java.simpleName
    }
}
