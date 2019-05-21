package io.gitlab.arturbosch.detekt.api

import org.jetbrains.kotlin.cli.common.CLIConfigurationKeys
import org.jetbrains.kotlin.cli.common.config.addKotlinSourceRoots
import org.jetbrains.kotlin.cli.common.messages.MessageRenderer
import org.jetbrains.kotlin.cli.common.messages.PrintingMessageCollector
import org.jetbrains.kotlin.cli.jvm.compiler.EnvironmentConfigFiles
import org.jetbrains.kotlin.cli.jvm.compiler.KotlinCoreEnvironment
import org.jetbrains.kotlin.cli.jvm.config.addJavaSourceRoots
import org.jetbrains.kotlin.cli.jvm.config.addJvmClasspathRoots
import org.jetbrains.kotlin.com.intellij.mock.MockProject
import org.jetbrains.kotlin.com.intellij.openapi.extensions.ExtensionPoint
import org.jetbrains.kotlin.com.intellij.openapi.extensions.Extensions
import org.jetbrains.kotlin.com.intellij.openapi.project.Project
import org.jetbrains.kotlin.com.intellij.openapi.util.Disposer
import org.jetbrains.kotlin.com.intellij.openapi.util.UserDataHolderBase
import org.jetbrains.kotlin.com.intellij.pom.PomModel
import org.jetbrains.kotlin.com.intellij.pom.PomModelAspect
import org.jetbrains.kotlin.com.intellij.pom.PomTransaction
import org.jetbrains.kotlin.com.intellij.pom.impl.PomTransactionBase
import org.jetbrains.kotlin.com.intellij.pom.tree.TreeAspect
import org.jetbrains.kotlin.com.intellij.psi.impl.source.tree.TreeCopyHandler
import org.jetbrains.kotlin.config.CommonConfigurationKeys
import org.jetbrains.kotlin.config.CompilerConfiguration
import org.jetbrains.kotlin.config.JVMConfigurationKeys
import org.jetbrains.kotlin.config.JvmTarget
import org.jetbrains.kotlin.psi.KtPsiFactory
import sun.reflect.ReflectionFactory
import java.io.File
import java.nio.file.Path

/**
 * The initialized kotlin environment which is used to translate kotlin code to a Kotlin-AST.
 */
val psiProject: Project = createProject()

/**
 * Allows to generate different kinds of KtElement's.
 */
val psiFactory: KtPsiFactory = KtPsiFactory(psiProject, false)

fun createKotlinCoreEnvironment(configuration: CompilerConfiguration = CompilerConfiguration()): KotlinCoreEnvironment {
    System.setProperty("idea.io.use.fallback", "true")
    configuration.put(CLIConfigurationKeys.MESSAGE_COLLECTOR_KEY,
            PrintingMessageCollector(System.err, MessageRenderer.PLAIN_FULL_PATHS, false))
    configuration.put(CommonConfigurationKeys.MODULE_NAME, "detekt")
    return KotlinCoreEnvironment.createForProduction(Disposer.newDisposable(),
        configuration, EnvironmentConfigFiles.JVM_CONFIG_FILES).apply {
        makeMutable(project as? MockProject ?: throw IllegalStateException(
            "Unexpected Type for psi project. MockProject expected. Please report this!"))
    }
}

private fun createProject(configuration: CompilerConfiguration = CompilerConfiguration()): Project {
    return createKotlinCoreEnvironment(configuration).project.apply {
        makeMutable(this as? MockProject ?: throw IllegalStateException(
                "Unexpected Type for psi project. MockProject expected. Please report this!"))
    }
}

fun createCompilerConfiguration(
    pathsToAnalyze: List<Path>,
    classpath: List<String>,
    jvmTarget: JvmTarget
): CompilerConfiguration {

    val javaFiles = pathsToAnalyze.flatMap { path ->
        path.toFile().walk()
            .filter { it.isFile && it.extension.equals("java", true) }
            .toList()
    }
    val kotlinFiles = pathsToAnalyze.flatMap { path ->
        path.toFile().walk()
            .filter { it.isFile }
            .filter { it.extension.equals("kt", true) || it.extension.equals("kts", true) }
            .map { it.absolutePath }
            .toList()
    }

    return CompilerConfiguration().apply {
        put(JVMConfigurationKeys.JVM_TARGET, jvmTarget)
        addJavaSourceRoots(javaFiles)
        addKotlinSourceRoots(kotlinFiles)
        addJvmClasspathRoots(classpath.map { File(it) })
    }
}

private fun makeMutable(project: MockProject) {
    // Based on KtLint by Shyiko
    val pomModel: PomModel = object : UserDataHolderBase(), PomModel {

        override fun runTransaction(transaction: PomTransaction) {
            (transaction as? PomTransactionBase)?.run()
        }

        @Suppress("UNCHECKED_CAST")
        override fun <T : PomModelAspect> getModelAspect(aspect: Class<T>): T? {
            if (aspect == TreeAspect::class.java) {
                // using approach described in https://git.io/vKQTo due to the magical bytecode of TreeAspect
                // (check constructor signature and compare it to the source)
                // (org.jetbrains.kotlin:kotlin-compiler-embeddable:1.0.3)
                val constructor = ReflectionFactory.getReflectionFactory().newConstructorForSerialization(
                        aspect, Any::class.java.getDeclaredConstructor())
                return constructor.newInstance() as T
            }
            return null
        }
    }
    val extensionPoint = "org.jetbrains.kotlin.com.intellij.treeCopyHandler"
    val extensionClassName = TreeCopyHandler::class.java.name!!
    arrayOf(Extensions.getArea(project), Extensions.getArea(null))
            .filter { !it.hasExtensionPoint(extensionPoint) }
            .forEach { it.registerExtensionPoint(extensionPoint, extensionClassName, ExtensionPoint.Kind.INTERFACE) }
    project.registerService(PomModel::class.java, pomModel)
}
