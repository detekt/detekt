package io.gitlab.arturbosch.detekt.rules.naming

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Debt
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.Severity
import io.gitlab.arturbosch.detekt.api.config
import io.gitlab.arturbosch.detekt.api.internal.ActiveByDefault
import io.gitlab.arturbosch.detekt.api.internal.Configuration
import org.jetbrains.kotlin.psi.KtPackageDirective

/**
 * Reports when package names which do not follow the specified naming convention are used.
 */
@ActiveByDefault(since = "1.0.0")
class PackageNaming(config: Config = Config.empty) : Rule(config) {

    override val defaultRuleIdAliases: Set<String> = setOf("PackageDirectoryMismatch")

    override val issue = Issue(
        javaClass.simpleName,
        Severity.Style,
        "Package names should match the naming convention set in the configuration.",
        debt = Debt.FIVE_MINS
    )

    @Configuration("naming pattern")
    private val packagePattern: Regex by config("""[a-z]+(\.[a-z][A-Za-z0-9]*)*""") { it.toRegex() }

    override fun visitPackageDirective(directive: KtPackageDirective) {
        val name = directive.qualifiedName
        if (name.isNotEmpty() && !name.matches(packagePattern)) {
            report(
                CodeSmell(
                    issue,
                    Entity.from(directive),
                    message = "Package name should match the pattern: $packagePattern"
                )
            )
        }
    }

    companion object {
        const val PACKAGE_PATTERN = "packagePattern"
    }
}
