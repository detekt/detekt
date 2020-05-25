package io.github.detekt.graph

import io.github.detekt.graph.psi.extractFullName
import org.jetbrains.kotlin.descriptors.ClassConstructorDescriptor
import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.psi.KtTreeVisitorVoid
import org.jetbrains.kotlin.resolve.BindingContext
import org.jetbrains.kotlin.resolve.calls.callUtil.getResolvedCall

class CallEdgesVisitor(
    private val graph: DefaultGraph,
    private val context: BindingContext
) : KtTreeVisitorVoid() {

    override fun visitCallExpression(expression: KtCallExpression) {
        super.visitCallExpression(expression)
        val call = expression.getResolvedCall(context) ?: return
        val decl = call.resultingDescriptor
        when (decl) {
            is ClassConstructorDescriptor -> return
        }
        val fqName = extractFullName(decl)
        println(fqName)
    }
}
