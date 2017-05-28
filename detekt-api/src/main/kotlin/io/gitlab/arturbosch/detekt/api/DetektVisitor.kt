package io.gitlab.arturbosch.detekt.api

import org.jetbrains.kotlin.psi.KtTreeVisitorVoid

/**
 * Base visitor for detekt rules.
 *
 * @author Artur Bosch
 */
open class DetektVisitor : KtTreeVisitorVoid()