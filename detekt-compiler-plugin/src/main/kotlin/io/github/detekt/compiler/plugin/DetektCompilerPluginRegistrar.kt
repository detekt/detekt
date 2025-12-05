package io.github.detekt.compiler.plugin

import io.github.detekt.compiler.plugin.internal.toSpec
import org.jetbrains.kotlin.cli.common.messages.MessageCollector
import org.jetbrains.kotlin.compiler.plugin.CompilerPluginRegistrar
import org.jetbrains.kotlin.config.CommonConfigurationKeys
import org.jetbrains.kotlin.config.CompilerConfiguration
import org.jetbrains.kotlin.resolve.jvm.extensions.AnalysisHandlerExtension
import kotlin.io.path.Path

class DetektCompilerPluginRegistrar : CompilerPluginRegistrar() {

    override val supportsK2 = false

    override val pluginId = "dev.detekt"

    override fun ExtensionStorage.registerExtensions(configuration: CompilerConfiguration) {
        if (configuration.get(Keys.IS_ENABLED) == false) {
            return
        }

        val messageCollector = configuration.get(CommonConfigurationKeys.MESSAGE_COLLECTOR_KEY, MessageCollector.NONE)

        AnalysisHandlerExtension.registerExtension(
            DetektAnalysisExtension(
                messageCollector,
                configuration.toSpec(messageCollector),
                configuration.get(Keys.ROOT_PATH, Path(System.getProperty("user.dir"))),
                configuration.getList(Keys.EXCLUDES)
            )
        )
    }
}
