package io.gitlab.arturbosch.detekt.rules

import org.jetbrains.kotlin.builtins.StandardNames
import org.jetbrains.kotlin.psi.KtExpression

fun KtExpression.isUnitExpression() = text == StandardNames.FqNames.unit.shortName().asString()
