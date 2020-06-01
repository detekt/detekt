package io.github.detekt.graph

import io.github.detekt.graph.api.Edge
import io.github.detekt.graph.api.Node
import io.github.detekt.psi.absolutePath
import org.jetbrains.kotlin.psi.KtClass
import org.jetbrains.kotlin.psi.KtConstructor
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.psi.KtNamedFunction
import org.jetbrains.kotlin.psi.KtPackageDirective
import org.jetbrains.kotlin.psi.KtPrimaryConstructor
import org.jetbrains.kotlin.psi.KtSecondaryConstructor
import org.jetbrains.kotlin.psi.KtTreeVisitorVoid
import org.jetbrains.kotlin.psi.psiUtil.getStrictParentOfType
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
        fileNode = FileNode(file.name, file.absolutePath())
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
        val node = ClassNode(checkNotNull(klass.fqName?.asString()))
        addToScopeNode(node)
        withNewScope(node) { super.visitClass(klass) }
    }

    override fun visitNamedFunction(function: KtNamedFunction) {
        val node = FunctionNode(checkNotNull(function.fqName?.asString()))
        addToScopeNode(node)
        withNewScope(node) { super.visitNamedFunction(function) }
    }

    override fun visitPrimaryConstructor(constructor: KtPrimaryConstructor) {
        val node = createConstructorNode(constructor) ?: return
        withNewScope(node) { super.visitPrimaryConstructor(constructor) }
    }

    private fun createConstructorNode(constructor: KtConstructor<*>): ConstructorNode? {
        val classFqName = constructor.getStrictParentOfType<KtClass>()
            ?.fqName
            ?.asString()
            ?: return null

        return ConstructorNode("$classFqName.$CONSTRUCTOR_DEFAULT_IDENTIFIER")
    }

    override fun visitSecondaryConstructor(constructor: KtSecondaryConstructor) {
        val node = createConstructorNode(constructor) ?: return
        withNewScope(node) { super.visitSecondaryConstructor(constructor) }
    }
}
