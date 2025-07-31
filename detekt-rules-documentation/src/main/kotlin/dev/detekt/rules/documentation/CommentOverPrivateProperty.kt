package dev.detekt.rules.documentation

import dev.detekt.api.Config
import dev.detekt.api.Entity
import dev.detekt.api.Finding
import dev.detekt.api.Rule
import org.jetbrains.kotlin.psi.KtProperty

/**
 * This rule reports comments and documentation above private properties. This can indicate that the property has a
 * confusing name or is not in a small enough context to be understood.
 * Private properties should be named in a self-explanatory way and readers of the code should be able to understand
 * why the property exists and what purpose it solves without the comment.
 *
 * Instead of simply removing the comment to solve this issue, prefer renaming the property to a more self-explanatory
 * name. If this property is inside a bigger class, it makes sense to refactor and split up the class. This can
 * increase readability and make the documentation obsolete.
 */
class CommentOverPrivateProperty(config: Config) : Rule(
    config,
    "Private properties should be named in a self-explanatory manner without the need for a  comment."
) {

    override fun visitProperty(property: KtProperty) {
        if (property.hasCommentInPrivateMember()) {
            report(Finding(Entity.atName(property), description))
        }
    }
}
