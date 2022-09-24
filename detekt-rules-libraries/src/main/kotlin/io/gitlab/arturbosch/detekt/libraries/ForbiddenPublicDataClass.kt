package io.gitlab.arturbosch.detekt.libraries

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
import io.gitlab.arturbosch.detekt.api.simplePatternToRegex
import org.jetbrains.kotlin.lexer.KtTokens
import org.jetbrains.kotlin.psi.KtClass
import org.jetbrains.kotlin.psi.psiUtil.visibilityModifierTypeOrDefault

/**
 * Data classes are bad for binary compatibility in public APIs. Avoid using them.
 *
 * This rule is aimed at library maintainers. If you are developing a final application you can ignore this issue.
 *
 * More info: [Public API challenges in Kotlin](https://jakewharton.com/public-api-challenges-in-kotlin/)
 *
 * <noncompliant>
 * data class C(val a: String) // violation: public data class
 * </noncompliant>
 *
 * <compliant>
 * internal data class C(val a: String)
 * </compliant>
 */
@ActiveByDefault(since = "1.16.0")
class ForbiddenPublicDataClass(config: Config = Config.empty) : Rule(config) {

    override val issue = Issue(
        javaClass.simpleName,
        Severity.Style,
        "The data classes are bad for the binary compatibility in public APIs. Avoid to use it.",
        Debt.TWENTY_MINS
    )

    @Configuration("ignores classes in the specified packages.")
    private val ignorePackages: List<Regex> by config(listOf("*.internal", "*.internal.*")) { packages ->
        packages.distinct().map(String::simplePatternToRegex)
    }

    override fun visitClass(klass: KtClass) {
        val packageName = klass.containingKtFile.packageDirective?.packageNameExpression?.text

        if (packageName != null && ignorePackages.any { it.matches(packageName) }) {
            return
        }

        val isPublicOrProtected = klass.visibilityModifierTypeOrDefault().let { visibility ->
            visibility != KtTokens.INTERNAL_KEYWORD && visibility != KtTokens.PRIVATE_KEYWORD
        }
        if (isPublicOrProtected) {
            if (klass.isData()) {
                report(CodeSmell(issue, Entity.from(klass.nameIdentifier ?: klass), ""))
            }
            super.visitClass(klass)
        }
    }
}
