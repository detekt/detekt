package io.gitlab.arturbosch.detekt.migration

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.PROJECT
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.Severity
import org.jetbrains.kotlin.com.intellij.psi.impl.source.codeStyle.CodeEditUtil
import org.jetbrains.kotlin.com.intellij.psi.impl.source.tree.CompositeElement
import org.jetbrains.kotlin.psi.KtImportList
import org.jetbrains.kotlin.psi.KtImportsFactory
import org.jetbrains.kotlin.resolve.ImportPath

/**
 * @author Artur Bosch
 */
class MigrateImportsRule(config: Config) : Rule(config) {

	override val issue = Issue("MigrateImports", Severity.Defect, "")

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
						val importPath = ImportPath.fromString(it)
						val newImport = factory.createImportDirectives(listOf(importPath)).toList()[0]
						node.addChild(newImport.importedReference?.node!!)
						report(ImportMigration(key!!, it, Entity.from(import)))
					}
				}
	}

}
