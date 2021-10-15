package io.gitlab.arturbosch.detekt.rules.bugs

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Debt
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.Severity
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.psi.KtPackageDirective

/**
 * Reports when the package declaration is missing.
 */
class MissingPackageDeclaration(config: Config = Config.empty) : Rule(config) {

    override val issue = Issue(
        javaClass.simpleName,
        Severity.Maintainability,
        "Kotlin source files should define a package",
        debt = Debt.FIVE_MINS
    )

    private var packageDeclaration: KtPackageDirective? = null

    override fun visitPackageDirective(directive: KtPackageDirective) {
        super.visitPackageDirective(directive)
        packageDeclaration = directive
    }

    override fun postVisit(root: KtFile) {
        super.postVisit(root)
        if (packageDeclaration?.text.isNullOrBlank()) {
            report(CodeSmell(issue, Entity.from(root), "The file does not contain a package declaration."))
        }
    }
}
