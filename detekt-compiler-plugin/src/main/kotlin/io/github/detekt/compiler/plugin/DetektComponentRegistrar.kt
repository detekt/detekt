package io.github.detekt.compiler.plugin

import io.github.detekt.compiler.plugin.internal.toSpec
import org.jetbrains.kotlin.cli.common.CLIConfigurationKeys
import org.jetbrains.kotlin.cli.common.messages.MessageCollector
import org.jetbrains.kotlin.com.intellij.mock.MockProject
import org.jetbrains.kotlin.compiler.plugin.ExperimentalCompilerApi
import org.jetbrains.kotlin.config.CompilerConfiguration
import org.jetbrains.kotlin.resolve.jvm.extensions.AnalysisHandlerExtension
import java.nio.file.Paths

@Suppress("DEPRECATION")
@OptIn(ExperimentalCompilerApi::class)
class DetektComponentRegistrar : org.jetbrains.kotlin.compiler.plugin.ComponentRegistrar {

    override fun registerProjectComponents(
        project: MockProject,
        configuration: CompilerConfiguration
    ) {
        if (configuration.get(Keys.IS_ENABLED) == false) {
            return
        }

        val messageCollector = configuration.get(CLIConfigurationKeys.MESSAGE_COLLECTOR_KEY, MessageCollector.NONE)

        AnalysisHandlerExtension.registerExtension(
            project,
            DetektAnalysisExtension(
                messageCollector,
                configuration.toSpec(messageCollector),
                configuration.get(Keys.ROOT_PATH, Paths.get(System.getProperty("user.dir"))),
                configuration.getList(Keys.EXCLUDES)
            )
        )
    }
}
