package io.github.detekt.parser

import com.intellij.openapi.vfs.impl.jar.CoreJarFileSystem
import com.intellij.openapi.vfs.local.CoreLocalFileSystem
import org.jetbrains.kotlin.analysis.api.standalone.base.projectStructure.StandaloneProjectFactory
import org.jetbrains.kotlin.cli.common.messages.AnalyzerWithCompilerReport
import org.jetbrains.kotlin.cli.common.messages.CompilerMessageSeverity
import org.jetbrains.kotlin.cli.common.messages.CompilerMessageSourceLocation
import org.jetbrains.kotlin.cli.common.messages.MessageCollector
import org.jetbrains.kotlin.cli.common.messages.PlainTextMessageRenderer
import org.jetbrains.kotlin.cli.jvm.compiler.KotlinCoreEnvironment
import org.jetbrains.kotlin.cli.jvm.compiler.NoScopeRecordCliBindingTrace
import org.jetbrains.kotlin.cli.jvm.compiler.TopDownAnalyzerFacadeForJVM
import org.jetbrains.kotlin.cli.jvm.config.jvmClasspathRoots
import org.jetbrains.kotlin.cli.jvm.index.JavaRoot
import org.jetbrains.kotlin.config.languageVersionSettings
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.resolve.BindingContext

fun generateBindingContext(
    environment: KotlinCoreEnvironment,
    files: List<KtFile>,
    debugPrinter: (() -> String) -> Unit,
    warningPrinter: (String) -> Unit,
): BindingContext {
    val messageCollector = DetektMessageCollector(
        minSeverity = CompilerMessageSeverity.ERROR,
        debugPrinter = debugPrinter,
        warningPrinter = warningPrinter,
    )

    val analyzer = AnalyzerWithCompilerReport(
        messageCollector,
        environment.configuration.languageVersionSettings,
        false,
    )

    val vfsFs = CoreJarFileSystem()
    val coreLocalFilesystem = CoreLocalFileSystem()

    val jarJavaRoots = environment.configuration.jvmClasspathRoots
        .filter { it.extension == "jar" }
        .mapNotNull { vfsFs.findFileByPath("${it.absolutePath}!/") }
        .map { JavaRoot(it, JavaRoot.RootType.BINARY) }

    val dirJavaRoots = environment.configuration.jvmClasspathRoots
        .filter { it.extension != "jar" }
        .mapNotNull { coreLocalFilesystem.findFileByPath("${it.absolutePath}") }
        .map { JavaRoot(it, JavaRoot.RootType.BINARY) }

    val packagePartProvider = StandaloneProjectFactory.createPackagePartsProvider(
        jarJavaRoots + dirJavaRoots,
        environment.configuration.languageVersionSettings
    )

    analyzer.analyzeAndReport(files) {
        TopDownAnalyzerFacadeForJVM.analyzeFilesWithJavaIntegration(
            environment.project,
            files,
            NoScopeRecordCliBindingTrace(environment.project),
            environment.configuration,
            packagePartProvider
        )
    }

    messageCollector.printIssuesCountIfAny()

    return analyzer.analysisResult.bindingContext
}

internal class DetektMessageCollector(
    private val minSeverity: CompilerMessageSeverity,
    private val debugPrinter: (() -> String) -> Unit,
    private val warningPrinter: (String) -> Unit,
) : MessageCollector by MessageCollector.NONE {
    private var messages = 0

    override fun report(severity: CompilerMessageSeverity, message: String, location: CompilerMessageSourceLocation?) {
        if (severity.ordinal <= minSeverity.ordinal) {
            debugPrinter { DetektMessageRenderer.render(severity, message, location) }
            messages++
        }
    }

    fun printIssuesCountIfAny() {
        if (messages > 0) {
            warningPrinter(
                "There were $messages compiler errors found during analysis. This affects accuracy of reporting.\n" +
                    "Run detekt CLI with --debug or set `detekt { debug = true }` in Gradle to see the error messages."
            )
        }
    }
}

private object DetektMessageRenderer : PlainTextMessageRenderer() {
    override fun getName() = "detekt message renderer"
    override fun getPath(location: CompilerMessageSourceLocation) = location.path
}
