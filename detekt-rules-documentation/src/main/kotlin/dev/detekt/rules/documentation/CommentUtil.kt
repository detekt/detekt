package dev.detekt.rules.documentation

import org.jetbrains.kotlin.psi.KtDeclaration
import org.jetbrains.kotlin.psi.psiUtil.isPrivate

internal fun KtDeclaration.hasCommentInPrivateMember() = docComment != null && isPrivate()
