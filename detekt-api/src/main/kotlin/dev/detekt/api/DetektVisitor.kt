package dev.detekt.api

import org.jetbrains.kotlin.psi.KtTreeVisitorVoid

/**
 * Basic visitor which is used inside detekt.
 * Guarantees a better looking name as the extended base class :).
 */
open class DetektVisitor : KtTreeVisitorVoid()
