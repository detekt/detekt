package io.gitlab.arturbosch.detekt.migration

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.PROJECT
import io.gitlab.arturbosch.detekt.api.Rule
import org.jetbrains.kotlin.com.intellij.psi.impl.source.codeStyle.CodeEditUtil
import org.jetbrains.kotlin.com.intellij.psi.impl.source.tree.CompositeElement
import org.jetbrains.kotlin.psi.KtImportList
import org.jetbrains.kotlin.psi.KtImportsFactory
import org.jetbrains.kotlin.resolve.ImportPath

/**
 * @author Artur Bosch
 */
class MigrateImportsRule(config: Config) : Rule("MigrateImports", Severity.Defect) {

	private val replacements: HashMap<String, String> = config.valueOrDefault("imports", HashMap<String, String>())
	private val toReplaces = replacements.keys

	private val factory = KtImportsFactory(PROJECT)

	override fun visitImportList(importList: KtImportList) {

		importList.imports
				.filter { it.importedFqName?.toString() in toReplaces }
				.forEach { import ->
					val key = import.importedFqName?.toString()
					val node = import.importedReference?.node as CompositeElement
					replacements[key]?.let {
						CodeEditUtil.removeChildren(node, node.firstChildNode, node.lastChildNode)
						val newImport = factory.createImportDirective(ImportPath.fromString(it))
						node.addChild(newImport.importedReference?.node!!)
						addFindings(ImportMigration(key!!, it, Entity.from(import)))
					}
				}
	}

}
