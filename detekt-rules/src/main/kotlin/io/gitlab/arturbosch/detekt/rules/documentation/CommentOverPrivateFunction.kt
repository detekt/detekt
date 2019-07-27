package io.gitlab.arturbosch.detekt.rules.documentation

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Debt
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.Severity
import org.jetbrains.kotlin.psi.KtNamedFunction

/**
 * This rule reports comments and documentation that has been added to private functions. These comments get reported
 * because they probably explain the functionality of the private function. However private functions should be small
 * enough and have an understandable name so that they are self-explanatory and do not need this comment in the first
 * place.
 *
 * Instead of simply removing this comment to solve this issue prefer to split up the function into smaller functions
 * with better names if necessary. Giving the function a better, more descriptive name can also help in
 * solving this issue.
 */
class CommentOverPrivateFunction(config: Config = Config.empty) : Rule(config) {

    override val issue = Issue("CommentOverPrivateFunction",
            Severity.Maintainability,
            "Comments for private functions should be avoided. " +
                    "Prefer giving the function an expressive name. " +
                    "Split it up in smaller, self-explaining functions if necessary.",
            Debt.TWENTY_MINS)

    override fun visitNamedFunction(function: KtNamedFunction) {
        if (function.hasCommentInPrivateMember()) {
            report(CodeSmell(issue, Entity.from(function.docComment!!), "The function ${function.nameAsSafeName} " +
                    "has a comment. Prefer renaming the function giving it a more self-explanatory name."))
        }
    }
}
