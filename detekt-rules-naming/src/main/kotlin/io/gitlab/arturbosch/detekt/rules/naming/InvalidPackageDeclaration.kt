package io.gitlab.arturbosch.detekt.rules.naming

import io.github.detekt.psi.absolutePath
import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Debt
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.Severity
import io.gitlab.arturbosch.detekt.api.config
import io.gitlab.arturbosch.detekt.api.internal.Configuration
import org.jetbrains.kotlin.psi.KtElement
import org.jetbrains.kotlin.psi.KtPackageDirective

/**
 * Reports when the file location does not match the declared package.
 */
class InvalidPackageDeclaration(config: Config = Config.empty) : Rule(config) {

    override val issue = Issue(
        javaClass.simpleName,
        Severity.Maintainability,
        "Kotlin source files should be stored in the directory corresponding to its package statement.",
        debt = Debt.FIVE_MINS
    )

    @Configuration("if specified this part of the package structure is ignored")
    private val rootPackage: String by config("")

    @Configuration("requires the declaration to start with the specified rootPackage")
    private val requireRootInDeclaration: Boolean by config(false)

    override fun visitPackageDirective(directive: KtPackageDirective) {
        super.visitPackageDirective(directive)
        val declaredPath = directive.packageNames.map(KtElement::getText).toNormalizedForm()
        if (declaredPath.isNotBlank()) {
            val normalizedFilePath = directive.containingKtFile.absolutePath().parent.toNormalizedForm()
            val normalizedRootPackage = packageNameToNormalizedForm(rootPackage)
            if (requireRootInDeclaration && !declaredPath.startsWith(normalizedRootPackage)) {
                directive.reportInvalidPackageDeclaration("The package declaration is missing the root package")
                return
            }

            val expectedPath =
                if (normalizedRootPackage.isBlank()) {
                    declaredPath
                } else {
                    declaredPath.substringAfter(normalizedRootPackage)
                }

            val isInRootPackage = expectedPath.isBlank()
            if (!isInRootPackage && !normalizedFilePath.endsWith(expectedPath)) {
                directive.reportInvalidPackageDeclaration(
                    "The package declaration does not match the actual file location."
                )
            }
        }
    }

    private fun KtElement.reportInvalidPackageDeclaration(message: String) {
        report(CodeSmell(issue, Entity.from(this), message))
    }

    private fun <T> Iterable<T>.toNormalizedForm() = joinToString("|")

    private fun packageNameToNormalizedForm(packageName: String) = packageName.split('.').toNormalizedForm()
}
