package dev.detekt.rules.naming

import dev.detekt.api.ActiveByDefault
import dev.detekt.api.Alias
import dev.detekt.api.Config
import dev.detekt.api.Configuration
import dev.detekt.api.Entity
import dev.detekt.api.Finding
import dev.detekt.api.Rule
import dev.detekt.api.config
import dev.detekt.psi.absolutePath
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.psi.KtElement
import org.jetbrains.kotlin.psi.KtPackageDirective

/**
 * Reports when the file location does not match the declared package.
 */
@ActiveByDefault(since = "1.21.0")
@Alias("PackageDirectoryMismatch")
class InvalidPackageDeclaration(config: Config) :
    Rule(config, "Kotlin source files should be stored in the directory corresponding to its package statement.") {

    @Configuration("if specified this part of the package structure is ignored")
    private val rootPackage: String by config("")

    @Configuration("requires the declaration to start with the specified rootPackage")
    private val requireRootInDeclaration: Boolean by config(false)

    override fun visitPackageDirective(directive: KtPackageDirective) {
        super.visitPackageDirective(directive)
        val packageName = directive.fqName
        if (!packageName.isRoot) {
            val rootPackageName = FqName(rootPackage)
            if (requireRootInDeclaration && !packageName.startsWith(rootPackageName)) {
                directive.reportInvalidPackageDeclaration("The package declaration is missing the root package")
                return
            }

            val normalizedFilePath = directive.containingKtFile.absolutePath().parent.toNormalizedForm()
            val expectedPath = packageName.withoutPrefix(rootPackageName).toNormalizedForm()

            val isInRootPackage = expectedPath.isEmpty()
            if (!isInRootPackage && !normalizedFilePath.endsWith("|$expectedPath")) {
                directive.reportInvalidPackageDeclaration(
                    "The package declaration does not match the actual file location."
                )
            }
        }
    }

    private fun KtElement.reportInvalidPackageDeclaration(message: String) {
        report(Finding(Entity.from(this), message))
    }

    private fun <T> Iterable<T>.toNormalizedForm() = joinToString("|")

    private fun FqName.withoutPrefix(prefix: FqName): List<String> {
        val dropCount = if (startsWith(prefix)) prefix.pathSegments().size else 0
        return pathSegments()
            .drop(dropCount)
            .map { segmentName -> segmentName.asString() }
    }
}
