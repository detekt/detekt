package dev.detekt.rules.comments

import org.jetbrains.kotlin.psi.KtDeclaration
import org.jetbrains.kotlin.psi.psiUtil.isPrivate

internal fun KtDeclaration.hasKDocInPrivateMember() = docComment != null && isPrivate()
