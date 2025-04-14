package io.gitlab.arturbosch.detekt.test

import io.github.detekt.test.utils.KotlinEnvironmentContainer
import io.github.detekt.test.utils.KotlinScriptEngine
import io.github.detekt.test.utils.compileContentForTest
import io.gitlab.arturbosch.detekt.api.CompilerResources
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Finding
import io.gitlab.arturbosch.detekt.api.RequiresAnalysisApi
import io.gitlab.arturbosch.detekt.api.RequiresFullAnalysis
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.RuleSet
import io.gitlab.arturbosch.detekt.core.suppressors.isSuppressedBy
import kotlinx.coroutines.CoroutineScope
import org.intellij.lang.annotations.Language
import org.jetbrains.kotlin.analysis.api.KaExperimentalApi
import org.jetbrains.kotlin.analysis.api.projectStructure.KaModule
import org.jetbrains.kotlin.analysis.api.projectStructure.contextModule
import org.jetbrains.kotlin.analysis.api.standalone.buildStandaloneAnalysisAPISession
import org.jetbrains.kotlin.analysis.project.structure.builder.buildKtLibraryModule
import org.jetbrains.kotlin.analysis.project.structure.builder.buildKtSdkModule
import org.jetbrains.kotlin.analysis.project.structure.builder.buildKtSourceModule
import org.jetbrains.kotlin.config.languageVersionSettings
import org.jetbrains.kotlin.platform.jvm.JvmPlatforms
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.psi.KtPsiFactory
import org.jetbrains.kotlin.resolve.calls.smartcasts.DataFlowValueFactoryImpl
import java.io.File
import java.nio.file.Path
import kotlin.io.path.Path

private val shouldCompileTestSnippets: Boolean =
    System.getProperty("compile-test-snippets", "false")!!.toBoolean()

fun Rule.lint(
    @Language("kotlin") content: String,
    compilerResources: CompilerResources = FakeCompilerResources(),
    compile: Boolean = true,
): List<Finding> {
    require(this !is RequiresFullAnalysis) {
        "${this.ruleName} requires full analysis so you should use lintWithContext instead of lint"
    }
    if (compile && shouldCompileTestSnippets) {
        KotlinScriptEngine.compile(content)
    }
    val ktFile = compileContentForTest(content)
    return visitFile(ktFile, compilerResources = compilerResources).filterSuppressed(this)
}

fun <T> T.lintWithContext(
    environment: KotlinEnvironmentContainer,
    @Language("kotlin") content: String,
    @Language("kotlin") vararg additionalContents: String,
    compilerResources: CompilerResources = CompilerResources(
        environment.configuration.languageVersionSettings,
        DataFlowValueFactoryImpl(environment.configuration.languageVersionSettings)
    ),
    compile: Boolean = true,
): List<Finding> where T : Rule, T : RequiresFullAnalysis {
    if (compile && shouldCompileTestSnippets) {
        KotlinScriptEngine.compile(content)
    }
    val ktFile = compileContentForTest(content)
    val additionalKtFiles = additionalContents.mapIndexed { index, additionalContent ->
        compileContentForTest(additionalContent, "AdditionalTest$index.kt")
    }
    setBindingContext(
        createBindingContext(
            listOf(ktFile) + additionalKtFiles,
            environment.configuration,
            environment.project
        )
    )

    return visitFile(ktFile, compilerResources).filterSuppressed(this)
}

fun <T> T.lintWithContext(
    environment: KotlinEnvironmentContainer,
    @Language("kotlin") content: String,
    @Language("kotlin") vararg additionalContents: String,
    compile: Boolean = true,
): List<Finding> where T : Rule, T : RequiresAnalysisApi {
    val ktFile = KotlinAnalysisApiEngine.compile(content, additionalContents)

    val compilerResources = CompilerResources(
        environment.configuration.languageVersionSettings,
        DataFlowValueFactoryImpl(environment.configuration.languageVersionSettings)
    )

    return visitFile(ktFile, compilerResources).filterSuppressed(this)
}

fun Rule.lint(ktFile: KtFile, compilerResources: CompilerResources = FakeCompilerResources()): List<Finding> {
    require(this !is RequiresFullAnalysis) {
        "${this.ruleName} requires full analysis so you should use lintWithContext instead of lint"
    }
    return visitFile(ktFile, compilerResources = compilerResources).filterSuppressed(this)
}

private fun List<Finding>.filterSuppressed(rule: Rule): List<Finding> =
    filterNot {
        it.entity.ktElement.isSuppressedBy(rule.ruleName.value, rule.aliases, RuleSet.Id("NoARuleSetId"))
    }

private val Rule.aliases: Set<String> get() = config.valueOrDefault(Config.ALIASES_KEY, emptyList<String>()).toSet()

object KotlinAnalysisApiEngine {
    private lateinit var sourceModule: KaModule

    private val session = buildStandaloneAnalysisAPISession(unitTestMode = false) {
        buildKtModuleProvider {
            val targetPlatform = JvmPlatforms.defaultJvmPlatform
            platform = targetPlatform

            val jdk = addModule(
                buildKtSdkModule {
                    addBinaryRootsFromJdkHome(Path(System.getProperty("java.home")), true)
                    platform = targetPlatform
                    libraryName = "sdk"
                }
            )

            val stdlib = addModule(
                buildKtLibraryModule {
                    addBinaryRoot(File(CharRange::class.java.protectionDomain.codeSource.location.path).toPath())
                    platform = targetPlatform
                    libraryName = "stdlib"
                }
            )

            val coroutinesCore = addModule(
                buildKtLibraryModule {
                    addBinaryRoot(kotlinxCoroutinesCorePath())
                    platform = targetPlatform
                    libraryName = "coroutines-core"
                }
            )

            sourceModule = addModule(
                buildKtSourceModule {
                    addRegularDependency(jdk)
                    addRegularDependency(stdlib)
                    addRegularDependency(coroutinesCore)
                    platform = targetPlatform
                    moduleName = "source"
                }
            )
        }
    }

    private val factory = KtPsiFactory(session.project, markGenerated = true)

    /**
     * Compiles a given code string using Kotlin's Analysis API.
     *
     * @throws IllegalStateException if the given code snippet does not compile
     */
    @OptIn(KaExperimentalApi::class)
    fun compile(@Language("kotlin") code: String, additionalContents: Array<out String>): KtFile {
        additionalContents.forEach {
            factory.createFile(it).contextModule = sourceModule
        }

        return factory.createFile(code).apply {
            contextModule = sourceModule
        }
    }

    private fun kotlinxCoroutinesCorePath(): Path =
        File(CoroutineScope::class.java.protectionDomain.codeSource.location.path).toPath()
}
