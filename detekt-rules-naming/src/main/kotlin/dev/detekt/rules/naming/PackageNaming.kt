package dev.detekt.rules.naming

import dev.detekt.api.ActiveByDefault
import dev.detekt.api.Alias
import dev.detekt.api.Config
import dev.detekt.api.Configuration
import dev.detekt.api.Entity
import dev.detekt.api.Finding
import dev.detekt.api.Rule
import dev.detekt.api.config
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
