package io.gitlab.arturbosch.detekt.rules.naming

import io.github.detekt.psi.absolutePath
import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Debt
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.Severity
import org.jetbrains.kotlin.psi.KtElement
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.psi.KtPackageDirective

/**
 * Reports when the package declaration is missing or the file location does not match the declared package.
 *
 * @configuration rootPackage - if specified this part of the package structure is ignored (default: `''`)
 */
class InvalidPackageDeclaration(config: Config = Config.empty) : Rule(config) {

    override val issue = Issue(
        javaClass.simpleName,
        Severity.Maintainability,
        "Kotlin source files should be stored in the directory corresponding to its package statement.",
        debt = Debt.FIVE_MINS
    )

    private val rootPackage: String = valueOrDefault(ROOT_PACKAGE, "")

    private var packageDeclaration: KtPackageDirective? = null

    override fun visitPackageDirective(directive: KtPackageDirective) {
        super.visitPackageDirective(directive)
        packageDeclaration = directive
    }

    override fun postVisit(root: KtFile) {
        super.postVisit(root)
        val packageDeclaration = packageDeclaration
        val declaredPath = packageDeclaration?.packageNames?.map(KtElement::getText)?.toNormalizedForm()
        if (declaredPath.isNullOrBlank()) {
            root.reportInvalidPackageDeclaration("The file does not contain a package declaration.")
        } else {
            val normalizedFilePath = root.absolutePath().parent.toNormalizedForm()
            val normalizedRootPackage = packageNameToNormalizedForm(rootPackage)
            val expectedPath =
                if (normalizedRootPackage.isBlank()) {
                    declaredPath
                } else {
                    declaredPath.substringAfter(normalizedRootPackage)
                }

            val isInRootPackage = expectedPath.isBlank()
            if (!isInRootPackage && !normalizedFilePath.endsWith(expectedPath)) {
                packageDeclaration
                    .reportInvalidPackageDeclaration("The package declaration does not match the actual file location.")
            }
        }
    }

    private fun KtElement.reportInvalidPackageDeclaration(message: String) {
        report(CodeSmell(issue, Entity.from(this), message))
    }

    private fun <T> Iterable<T>.toNormalizedForm() = joinToString("|")

    private fun packageNameToNormalizedForm(packageName: String) = packageName.split('.').toNormalizedForm()

    companion object {
        const val ROOT_PACKAGE = "rootPackage"
    }
}
