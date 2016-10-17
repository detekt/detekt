package io.gitlab.arturbosch.detekt.rules

import org.jetbrains.kotlin.psi.KtBlockExpression
import org.jetbrains.kotlin.psi.KtExpression

/**
 * @author Artur Bosch
 */

fun KtExpression?.asBlockExpression(): KtBlockExpression? = if (this is KtBlockExpression) this else null
