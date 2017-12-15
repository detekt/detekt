package io.gitlab.arturbosch.detekt.core

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.RuleSetProvider
import org.jetbrains.kotlin.analyzer.AnalysisResult
import org.jetbrains.kotlin.cli.jvm.compiler.CliLightClassGenerationSupport
import org.jetbrains.kotlin.com.intellij.openapi.util.Disposer
import org.jetbrains.kotlin.container.get
import org.jetbrains.kotlin.descriptors.ModuleDescriptor
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.resolve.BindingContext
import org.jetbrains.kotlin.resolve.LazyTopDownAnalyzer
import org.jetbrains.kotlin.resolve.TopDownAnalysisMode
import org.jetbrains.kotlin.utils.PathUtil
import java.io.File

class DetektResolver(val classpath: List<String>,
					 private val sourcePaths: List<String>,
					 val providers: List<RuleSetProvider>,
					 val config: Config) {

	fun generate(files: List<KtFile>) : BindingContext {
		val environment = createAnalysisEnvironment(sourcePaths)
		return try {
			val trace = CliLightClassGenerationSupport.NoScopeRecordCliBindingTrace()
			val container = environment.createCoreEnvironment(trace, files)
			val module = container.get<ModuleDescriptor>()
			container.get<LazyTopDownAnalyzer>().analyzeDeclarations(TopDownAnalysisMode.TopLevelDeclarations, files)

			AnalysisResult.success(trace.bindingContext, module).bindingContext
		} finally {
			Disposer.dispose(environment)
		}
	}

	private fun createAnalysisEnvironment(sourcePaths: List<String>): AnalysisEnvironment {
		val environment = AnalysisEnvironment()

		environment.apply {
			addClasspaths(PathUtil.getJdkClassesRootsFromCurrentJre())
			for (element in this@DetektResolver.classpath) {
				addClasspath(File(element))
			}

			addSources(sourcePaths)
		}

		return environment
	}
}
