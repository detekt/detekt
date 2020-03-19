package io.gitlab.arturbosch.detekt.rules.empty

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.rules.hasCommentInside
import org.jetbrains.kotlin.psi.KtClassOrObject
import org.jetbrains.kotlin.psi.psiUtil.isObjectLiteral

/**
 * Reports empty classes. Empty blocks of code serve no purpose and should be removed.
 *
 * @active since v1.0.0
 */
class EmptyClassBlock(config: Config) : EmptyRule(config) {

    override fun visitClassOrObject(classOrObject: KtClassOrObject) {
        super.visitClassOrObject(classOrObject)
        if (classOrObject.isObjectLiteral()) return
        if (classOrObject.hasCommentInside()) return

        classOrObject.body?.let { body ->
            if (body.declarations.isEmpty()) {
                report(CodeSmell(issue, Entity.from(body),
                    "The class or object ${classOrObject.name} is empty."))
            }
        }
    }
}
