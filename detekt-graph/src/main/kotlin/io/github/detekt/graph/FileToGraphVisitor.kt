package io.github.detekt.graph

import io.github.detekt.graph.api.Edge
import io.github.detekt.graph.api.Node
import org.jetbrains.kotlin.psi.KtClass
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.psi.KtNamedFunction
import org.jetbrains.kotlin.psi.KtPackageDirective
import org.jetbrains.kotlin.psi.KtTreeVisitorVoid
import java.util.ArrayDeque
import java.util.Deque

internal class FileToGraphVisitor(
    private val graph: DefaultGraph
) : KtTreeVisitorVoid() {

    private lateinit var fileNode: FileNode
    private val scope: Deque<Node> = ArrayDeque()

    private fun addToScopeNode(target: Node, edgeType: Edge.Type = Edge.Type.DECLARES) {
        val scopeNode = scope.peek()
        graph.addVertex(target)
        graph.addEdge(scopeNode, target, DefaultEdge(scopeNode, target, edgeType))
    }

    private fun withNewScope(newScope: Node, block: () -> Unit) {
        scope.push(newScope)
        block()
        scope.pop()
    }

    override fun visitKtFile(file: KtFile) {
        fileNode = FileNode(file.name)
        graph.addVertex(fileNode)
        withNewScope(fileNode) { super.visitKtFile(file) }
    }

    override fun visitPackageDirective(directive: KtPackageDirective) {
        val pkgNode = PackageNode(directive.qualifiedName)
        graph.addVertex(pkgNode)
        graph.addEdge(pkgNode, fileNode, DefaultEdge(pkgNode, fileNode, Edge.Type.ENCLOSES))
        super.visitPackageDirective(directive)
    }

    override fun visitClass(klass: KtClass) {
        val node = ClassNode(checkNotNull(klass.name))
        addToScopeNode(node)
        withNewScope(node) { super.visitClass(klass) }
    }

    override fun visitNamedFunction(function: KtNamedFunction) {
        val node = FunctionNode(checkNotNull(function.name))
        addToScopeNode(node)
        withNewScope(node) { super.visitNamedFunction(function) }
    }
}
