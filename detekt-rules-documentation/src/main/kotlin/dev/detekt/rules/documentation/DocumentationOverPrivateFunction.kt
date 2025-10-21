package dev.detekt.rules.documentation

import dev.detekt.api.Config
import dev.detekt.api.Entity
import dev.detekt.api.Finding
import dev.detekt.api.Rule
import org.jetbrains.kotlin.psi.KtNamedFunction

/**
 * This rule reports documentation that has been added to private functions. This documentation is reported because
 * it probably explains the functionality of the private function. However, private functions should be small enough
 * and have an understandable name so that they are self-explanatory and do not need this documentation in the first
 * place.
 *
 * Instead of simply removing this documentation to solve this issue, prefer to split the function into smaller
 * functions with better names if necessary. Giving the function a better, more descriptive name can also help
 * solve this issue.
 */
class DocumentationOverPrivateFunction(config: Config) : Rule(
    config,
    "Comments for private functions should be avoided. " +
        "Prefer giving the function an expressive name. " +
        "Split it up in smaller, self-explaining functions if necessary."
) {

    override fun visitNamedFunction(function: KtNamedFunction) {
        if (function.hasKDocInPrivateMember()) {
            report(
                Finding(
                    Entity.atName(function),
                    "The function ${function.nameAsSafeName} " +
                        "has a comment. Prefer renaming the function giving it a more self-explanatory name."
                )
            )
        }
    }
}
