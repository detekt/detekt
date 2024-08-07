package io.gitlab.arturbosch.detekt.rules.style

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.RequiresTypeResolution
import io.gitlab.arturbosch.detekt.api.Rule
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.resolve.calls.util.getResolvedCall
import org.jetbrains.kotlin.resolve.descriptorUtil.fqNameOrNull

/**
 * Instantiation of an object's "empty" state should use the object's "empty" initializer for clarity purposes.
 *
 * <noncompliant>
 * arrayOf()
 * listOf() // or listOfNotNull()
 * mapOf()
 * sequenceOf()
 * setOf()
 * </noncompliant>
 *
 * <compliant>
 * emptyArray()
 * emptyList()
 * emptyMap()
 * emptySequence()
 * emptySet()
 * </compliant>
 *
 */
class UseEmptyCounterpart(config: Config) :
    Rule(
        config,
        """Instantiation of an object's "empty" state should use the object's "empty" initializer."""
    ),
    RequiresTypeResolution {
    override fun visitCallExpression(expression: KtCallExpression) {
        super.visitCallExpression(expression)

        val resolvedCall = expression.getResolvedCall(bindingContext) ?: return
        val fqName = resolvedCall.resultingDescriptor.fqNameOrNull() ?: return
        val emptyCounterpart = exprsWithEmptyCounterparts[fqName] ?: return

        if (expression.valueArguments.isEmpty()) {
            val message = "${fqName.shortName()} can be replaced with $emptyCounterpart"
            report(CodeSmell(Entity.from(expression), message))
        }
    }
}

private val exprsWithEmptyCounterparts = mapOf(
    FqName("kotlin.arrayOf") to "emptyArray",
    FqName("kotlin.collections.listOf") to "emptyList",
    FqName("kotlin.collections.listOfNotNull") to "emptyList",
    FqName("kotlin.collections.mapOf") to "emptyMap",
    FqName("kotlin.sequences.sequenceOf") to "emptySequence",
    FqName("kotlin.collections.setOf") to "emptySet"
)
