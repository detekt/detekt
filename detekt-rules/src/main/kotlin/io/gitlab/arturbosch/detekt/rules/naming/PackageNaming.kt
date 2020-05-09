package io.gitlab.arturbosch.detekt.rules.naming

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Debt
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.LazyRegex
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.Severity
import org.jetbrains.kotlin.psi.KtPackageDirective

/**
 * Reports when package names which do not follow the specified naming convention are used.
 *
 * @configuration packagePattern - naming pattern (default: `'[a-z]+(\.[a-z][A-Za-z0-9]*)*'`)
 * @active since v1.0.0
 */
class PackageNaming(config: Config = Config.empty) : Rule(config) {

    override val issue = Issue(javaClass.simpleName,
            Severity.Style,
            "Package names should match the naming convention set in the configuration.",
            debt = Debt.FIVE_MINS)
    private val packagePattern by LazyRegex(PACKAGE_PATTERN, "[a-z]+(\\.[a-z][A-Za-z0-9]*)*")

    override fun visitPackageDirective(directive: KtPackageDirective) {
        val name = directive.qualifiedName
        if (name.isNotEmpty() && !name.matches(packagePattern)) {
            report(CodeSmell(
                    issue,
                    Entity.from(directive),
                    message = "Package name should match the pattern: $packagePattern"))
        }
    }

    companion object {
        const val PACKAGE_PATTERN = "packagePattern"
    }
}
