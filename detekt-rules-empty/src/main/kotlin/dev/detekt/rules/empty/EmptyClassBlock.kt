package dev.detekt.rules.empty

import dev.detekt.api.ActiveByDefault
import dev.detekt.api.Config
import dev.detekt.api.Entity
import dev.detekt.api.Finding
import dev.detekt.rules.empty.internal.hasCommentInside
import org.jetbrains.kotlin.psi.KtClassOrObject
import org.jetbrains.kotlin.psi.psiUtil.isObjectLiteral

/**
 * Reports empty classes. Empty blocks of code serve no purpose and should be removed.
 */
@ActiveByDefault(since = "1.0.0")
class EmptyClassBlock(config: Config) : EmptyRule(config) {

    override fun visitClassOrObject(classOrObject: KtClassOrObject) {
        super.visitClassOrObject(classOrObject)
        if (classOrObject.isObjectLiteral()) return
        if (classOrObject.hasCommentInside()) return

        classOrObject.body?.let { body ->
            if (body.declarations.isEmpty()) {
                report(
                    Finding(
                        Entity.from(body),
                        "The class or object ${classOrObject.name} is empty."
                    )
                )
            }
        }
    }
}
