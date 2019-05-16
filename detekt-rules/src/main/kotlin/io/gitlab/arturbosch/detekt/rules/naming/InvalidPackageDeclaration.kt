package io.gitlab.arturbosch.detekt.rules.naming

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Debt
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.Severity
import io.gitlab.arturbosch.detekt.api.internal.absolutePath
import org.jetbrains.kotlin.psi.KtElement
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.psi.KtPackageDirective
import java.nio.file.Paths

/**
 * Reports when the package declaration is missing or the file location does not match the declared package.
 *
 * @configuration rootPackage - if specified this part of the package structure is ignored (default: `''`)
 *
 * @author Markus Schwarz
 */
class InvalidPackageDeclaration(config: Config = Config.empty) : Rule(config) {

    override val issue = Issue(
        javaClass.simpleName,
        Severity.Maintainability,
        "Kotlin source files should be stored in the directory corresponding to its package statement.",
        debt = Debt.FIVE_MINS
    )

    private val rootPackage: String = valueOrDefault(ROOT_PACKAGE, "")

    private var normalizedPathFromPackageDeclaration: String = ""

    override fun visitPackageDirective(directive: KtPackageDirective) {
        super.visitPackageDirective(directive)
        normalizedPathFromPackageDeclaration = directive.packageNames.map(KtElement::getText).toNormalizedForm()
    }

    override fun postVisit(root: KtFile) {
        super.postVisit(root)
        val declaredPath = normalizedPathFromPackageDeclaration
        if (declaredPath.isBlank()) {
            root.reportInvalidPackageDeclaration("The file does not contain a package declaration.")
        } else {
            val normalizedFilePath = Paths.get(root.absolutePath()).parent.toNormalizedForm()
            val normalizedRootPackage = packageNameToNormalizedForm(rootPackage)
            val expectedPath = if (normalizedRootPackage.isBlank())
                declaredPath
            else
                declaredPath.substringAfter(normalizedRootPackage)

            val isInRootPackage = expectedPath.isBlank()
            if (!isInRootPackage && !normalizedFilePath.endsWith(expectedPath)) {
                root.reportInvalidPackageDeclaration("The package declaration does not match the actual file location.")
            }
        }
    }

    private fun KtFile.reportInvalidPackageDeclaration(message: String) {
        report(CodeSmell(issue, Entity.from(this), message))
    }

    private fun <T> Iterable<T>.toNormalizedForm() = joinToString("|")

    private fun packageNameToNormalizedForm(packageName: String) = packageName.split('.').toNormalizedForm()

    companion object {
        const val ROOT_PACKAGE = "rootPackage"
    }
}
