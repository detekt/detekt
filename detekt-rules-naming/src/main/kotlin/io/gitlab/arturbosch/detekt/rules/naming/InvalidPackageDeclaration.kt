package io.gitlab.arturbosch.detekt.rules.naming

import io.github.detekt.psi.absolutePath
import io.gitlab.arturbosch.detekt.api.ActiveByDefault
import io.gitlab.arturbosch.detekt.api.Alias
import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Configuration
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.config
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.psi.KtElement
import org.jetbrains.kotlin.psi.KtFile

/**
 * Reports when the file location does not match the declared package.
 */
@ActiveByDefault(since = "1.21.0")
@Alias("PackageDirectoryMismatch")
class InvalidPackageDeclaration(config: Config) : Rule(
    config,
    "Kotlin source files should be stored in the directory corresponding to its package statement."
) {

    @Configuration("if specified this part of the package structure is ignored")
    private val rootPackage: String by config("")

    @Configuration("requires the declaration to start with the specified rootPackage")
    private val requireRootInDeclaration: Boolean by config(false)

    override fun visit(root: KtFile) {
        val directive = root.packageDirective ?: return
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
        report(CodeSmell(Entity.from(this), message))
    }

    private fun <T> Iterable<T>.toNormalizedForm() = joinToString("|")

    private fun FqName.withoutPrefix(prefix: FqName): List<String> {
        val dropCount = if (startsWith(prefix)) prefix.pathSegments().size else 0
        return pathSegments()
            .drop(dropCount)
            .map { segmentName -> segmentName.asString() }
    }
}
