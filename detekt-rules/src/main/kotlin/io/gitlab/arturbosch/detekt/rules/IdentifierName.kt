package io.gitlab.arturbosch.detekt.rules

import org.jetbrains.kotlin.name.SpecialNames
import org.jetbrains.kotlin.psi.KtNamedDeclaration

internal fun KtNamedDeclaration.identifierName() = nameIdentifier?.text ?: SpecialNames.NO_NAME_PROVIDED.asString()
