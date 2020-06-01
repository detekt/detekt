package io.github.detekt.graph

import io.github.detekt.graph.api.Edge
import io.github.detekt.graph.api.Node
import io.github.detekt.graph.psi.extractFullName
import org.jetbrains.kotlin.descriptors.ClassConstructorDescriptor
import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.psi.KtNamedFunction
import org.jetbrains.kotlin.psi.KtTreeVisitorVoid
import org.jetbrains.kotlin.psi.psiUtil.getTopmostParentOfType
import org.jetbrains.kotlin.resolve.BindingContext
import org.jetbrains.kotlin.resolve.calls.callUtil.getResolvedCall

class CallEdgesVisitor(
    private val graph: DefaultGraph,
    private val context: BindingContext
) : KtTreeVisitorVoid() {

    @Suppress("detekt.ReturnCount")
    override fun visitCallExpression(expression: KtCallExpression) {
        val callerFunction = expression.getTopmostParentOfType<KtNamedFunction>()
            ?.fqName
            ?.asString()
            ?: return
        val call = expression.getResolvedCall(context) ?: return
        val callerNode = graph.nodeByFqName(callerFunction) ?: return

        when (val decl = call.resultingDescriptor) {
            is ClassConstructorDescriptor -> {
                val constructorNode = getOrCreateConstructorNode(decl)
                addCall(callerNode, constructorNode)
            }
            else -> {
                val fqName = extractFullName(decl)
                val calledNode = graph.nodeByFqName(fqName)

                if (calledNode != null) {
                    addCall(callerNode, calledNode)
                }
            }
        }
        super.visitCallExpression(expression)
    }

    private fun addCall(source: Node, target: Node) {
        graph.addEdge(source, target, DefaultEdge(source, target, Edge.Type.CALL))
    }

    private fun getOrCreateConstructorNode(constructorDescriptor: ClassConstructorDescriptor): Node {
        val classFqName = extractFullName(constructorDescriptor.containingDeclaration)
        val constructorFqName = "$classFqName.${constructorDescriptor.name.asString()}"
        var constructorNode = graph.nodeByFqName(constructorFqName)
        if (constructorNode != null) {
            return constructorNode
        }
        val classNode = graph.nodeByFqName(classFqName) ?: error("Class node '$classFqName' expected.")
        constructorNode = ConstructorNode(constructorFqName)
        graph.addVertex(constructorNode)
        graph.addEdge(classNode, constructorNode, DefaultEdge(classNode, constructorNode, Edge.Type.CALL))
        return constructorNode
    }
}
