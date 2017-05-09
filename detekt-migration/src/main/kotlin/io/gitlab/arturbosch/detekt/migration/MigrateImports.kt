package io.gitlab.arturbosch.detekt.migration

import com.intellij.psi.impl.source.codeStyle.CodeEditUtil
import com.intellij.psi.impl.source.tree.CompositeElement
import io.gitlab.arturbosch.detekt.api.PROJECT
import io.gitlab.arturbosch.detekt.api.Rule
import org.jetbrains.kotlin.psi.KtImportList
import org.jetbrains.kotlin.psi.KtImportsFactory
import org.jetbrains.kotlin.resolve.ImportPath

/**
 * @author Artur Bosch
 */
class MigrateImports(private val toReplace: String,
					 private val replaceWith: String) : Rule("MigrateImports", Severity.Defect) {

	private val factory = KtImportsFactory(PROJECT)

	override fun visitImportList(importList: KtImportList) {
		importList.imports
				.filter { it.importedFqName?.toString() == toReplace }
				.forEach {
					val node = it.importedReference?.node as CompositeElement
					CodeEditUtil.removeChildren(node, node.firstChildNode, node.lastChildNode)
					val newImport = factory.createImportDirective(ImportPath.fromString(replaceWith))
					node.addChild(newImport.importedReference?.node!!)
				}
	}

}
