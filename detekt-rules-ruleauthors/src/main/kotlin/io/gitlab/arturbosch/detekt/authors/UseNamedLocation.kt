package io.gitlab.arturbosch.detekt.authors

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Debt
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.Severity
import io.gitlab.arturbosch.detekt.api.internal.ActiveByDefault
import org.jetbrains.kotlin.psi.KtNamedDeclaration

/**
 * If a rule reports issues using [Entity.from] with [KtNamedDeclaration.getNameIdentifier],
 * then it can be replaced with [Entity.atName] for more semantic code and better baseline support.
 */
@ActiveByDefault("1.22.0")
class UseNamedLocation(config: Config = Config.empty) : Rule(config) {

    override val issue = Issue(
        "UseNamedLocation",
        Severity.Defect,
        "Prefer Entity.atName to Entity.from(....nameIdentifier).",
        Debt.FIVE_MINS
    )
}
