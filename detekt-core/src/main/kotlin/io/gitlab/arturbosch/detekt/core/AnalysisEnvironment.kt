package io.gitlab.arturbosch.detekt.core

import org.jetbrains.kotlin.cli.common.CLIConfigurationKeys
import org.jetbrains.kotlin.cli.common.messages.MessageCollector
import org.jetbrains.kotlin.cli.jvm.compiler.CliLightClassGenerationSupport
import org.jetbrains.kotlin.cli.jvm.compiler.EnvironmentConfigFiles
import org.jetbrains.kotlin.cli.jvm.compiler.KotlinCoreEnvironment
import org.jetbrains.kotlin.cli.jvm.compiler.TopDownAnalyzerFacadeForJVM
import org.jetbrains.kotlin.cli.jvm.config.addJavaSourceRoot
import org.jetbrains.kotlin.cli.jvm.config.addJvmClasspathRoot
import org.jetbrains.kotlin.cli.jvm.config.addJvmClasspathRoots
import org.jetbrains.kotlin.com.intellij.openapi.Disposable
import org.jetbrains.kotlin.com.intellij.openapi.util.Disposer
import org.jetbrains.kotlin.config.CommonConfigurationKeys
import org.jetbrains.kotlin.config.CompilerConfiguration
import org.jetbrains.kotlin.config.addKotlinSourceRoot
import org.jetbrains.kotlin.container.ComponentProvider
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.resolve.lazy.declarations.FileBasedDeclarationProviderFactory
import java.io.File

class AnalysisEnvironment : Disposable {
	val configuration = CompilerConfiguration()

	init {
		configuration.put(CLIConfigurationKeys.MESSAGE_COLLECTOR_KEY, MessageCollector.NONE)
		configuration.put(CommonConfigurationKeys.MODULE_NAME, "module for detekt static analysis")
	}

	fun createCoreEnvironment(trace: CliLightClassGenerationSupport.CliBindingTrace, files: List<KtFile>):
			ComponentProvider {
		System.setProperty("idea.io.use.fallback", "true") //otherwise will complain bin/idea.properties is missing
		val environment = KotlinCoreEnvironment.createForProduction(this, configuration, EnvironmentConfigFiles.JVM_CONFIG_FILES)

		return TopDownAnalyzerFacadeForJVM.createContainer(
				environment.project,
				files,
				trace,
				environment.configuration,
				environment::createPackagePartProvider,
				::FileBasedDeclarationProviderFactory
		)
	}

	fun addClasspaths(paths: List<File>) {
		configuration.addJvmClasspathRoots(paths)
	}

	fun addClasspath(path: File) {
		configuration.addJvmClasspathRoot(path)
	}

	fun addSources(list: List<String>) {
		list.forEach {
			configuration.addKotlinSourceRoot(it)
			val file = File(it)
			if (file.isDirectory || file.extension == ".java") {
				configuration.addJavaSourceRoot(file)
			}
		}
	}

	override fun dispose() {
		Disposer.dispose(this)
	}
}
