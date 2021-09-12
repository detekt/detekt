package io.gitlab.arturbosch.detekt.core.settings

import io.github.detekt.parser.createCompilerConfiguration
import io.github.detekt.parser.createKotlinCoreEnvironment
import io.github.detekt.tooling.api.spec.CompilerSpec
import io.github.detekt.tooling.api.spec.ProjectSpec
import org.jetbrains.kotlin.cli.common.arguments.K2JVMCompilerArguments
import org.jetbrains.kotlin.cli.common.arguments.parseCommandLineArguments
import org.jetbrains.kotlin.cli.jvm.compiler.KotlinCoreEnvironment
import org.jetbrains.kotlin.com.intellij.openapi.Disposable
import org.jetbrains.kotlin.com.intellij.openapi.util.Disposer
import org.jetbrains.kotlin.config.JvmTarget
import org.jetbrains.kotlin.config.LanguageVersion
import org.jetbrains.kotlin.load.kotlin.ModuleVisibilityManager
import java.io.Closeable
import java.io.File

interface EnvironmentAware {

    val disposable: Disposable
    val classpath: List<String>
    val environment: KotlinCoreEnvironment
}

internal class EnvironmentFacade(
    private val projectSpec: ProjectSpec,
    private val compilerSpec: CompilerSpec
) : AutoCloseable, Closeable, EnvironmentAware {

    override val disposable: Disposable = Disposer.newDisposable()

    override val classpath: List<String> = compilerSpec.classpathEntries()

    override val environment: KotlinCoreEnvironment by lazy {
        val compilerArguments = K2JVMCompilerArguments().apply {
            parseCommandLineArguments(compilerSpec.freeCompilerArgs, this)
        }

        val compilerConfiguration = createCompilerConfiguration(
            projectSpec.inputPaths.toList(),
            classpath,
            compilerSpec.parseLanguageVersion(),
            compilerSpec.parseJvmTarget()
        )

        val env = createKotlinCoreEnvironment(compilerConfiguration, disposable)

        val visibilityManager = ModuleVisibilityManager.SERVICE.getInstance(env.project)
        compilerArguments.friendPaths?.forEach(visibilityManager::addFriendPath)

        env
    }

    override fun close() {
        Disposer.dispose(disposable)
    }
}

internal fun CompilerSpec.classpathEntries(): List<String> =
    classpath?.split(File.pathSeparator).orEmpty()

internal fun CompilerSpec.parseLanguageVersion(): LanguageVersion? {
    fun parse(value: String): LanguageVersion {
        val version = LanguageVersion.fromFullVersionString(value)
        return checkNotNull(version) { "Invalid value passed as language version." }
    }
    return languageVersion?.let(::parse)
}

internal fun CompilerSpec.parseJvmTarget(): JvmTarget {
    return checkNotNull(JvmTarget.fromString(jvmTarget)) { "Invalid value passed to --jvm-target" }
}
