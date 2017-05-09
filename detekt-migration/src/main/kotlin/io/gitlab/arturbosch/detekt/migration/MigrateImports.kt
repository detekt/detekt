package io.gitlab.arturbosch.detekt.migration

import com.intellij.openapi.util.Disposer
import io.gitlab.arturbosch.detekt.api.Rule
import org.jetbrains.kotlin.cli.jvm.compiler.EnvironmentConfigFiles
import org.jetbrains.kotlin.cli.jvm.compiler.KotlinCoreEnvironment
import org.jetbrains.kotlin.config.CompilerConfiguration
import org.jetbrains.kotlin.psi.KtImportList
import org.jetbrains.kotlin.psi.KtImportsFactory

/**
 * @author Artur Bosch
 */
class MigrateImports(private val toReplace: String,
					 private val replaceWith: String) : Rule("MigrateImports", Severity.Defect) {

	private val factory = KtImportsFactory(PROJECT)

	override fun visitImportList(importList: KtImportList) {
		importList.imports
		importList.imports.iterator().forEach {
			if (it.importedFqName?.toString() == toReplace) {
				println(it.importedReference?.text)
//				it.delete()
//				importList.add(factory.createImportDirective(ImportPath.fromString(replaceWith)))
//				it.importedReference?.replace()
//				it.replaceChildInternal(it.importedFqName!!, FqName(replaceWith))
//				importList.replaceChild(it,
//						factory.createImportDirective(ImportPath.fromString(replaceWith)))
			}
		}
	}

}

private val PROJECT = KotlinCoreEnvironment.createForProduction(Disposer.newDisposable(),
		CompilerConfiguration(), EnvironmentConfigFiles.JVM_CONFIG_FILES).project