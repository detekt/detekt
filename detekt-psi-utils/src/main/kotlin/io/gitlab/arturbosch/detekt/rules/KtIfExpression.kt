package io.gitlab.arturbosch.detekt.rules

import org.jetbrains.kotlin.KtNodeTypes
import org.jetbrains.kotlin.psi.KtContainerNodeForControlStructureBody
import org.jetbrains.kotlin.psi.KtIfExpression

fun KtIfExpression.isElseIf(): Boolean =
    parent.node.elementType == KtNodeTypes.ELSE &&
        parent.safeAs<KtContainerNodeForControlStructureBody>()?.expression == this
