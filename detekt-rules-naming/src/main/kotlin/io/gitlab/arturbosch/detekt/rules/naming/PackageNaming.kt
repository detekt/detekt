package io.gitlab.arturbosch.detekt.rules.naming

import io.gitlab.arturbosch.detekt.api.ActiveByDefault
import io.gitlab.arturbosch.detekt.api.Alias
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Configuration
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Finding
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.config
import org.jetbrains.kotlin.psi.KtPackageDirective

/**
 * Reports package names that do not follow the specified naming convention.
 */
@ActiveByDefault(since = "1.0.0")
@Alias("PackageName")
class PackageNaming(config: Config) : Rule(
    config,
    "Package names should follow the naming convention set in detekt's configuration."
) {

    @Configuration("naming pattern")
    private val packagePattern: Regex by config("""[a-z]+(\.[a-z][A-Za-z0-9]*)*""") { it.toRegex() }

    override fun visitPackageDirective(directive: KtPackageDirective) {
        val name = directive.qualifiedName
        if (name.isNotEmpty() && !name.matches(packagePattern)) {
            report(
                Finding(
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
