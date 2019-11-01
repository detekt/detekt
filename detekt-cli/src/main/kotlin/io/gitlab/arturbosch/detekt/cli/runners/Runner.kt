package io.gitlab.arturbosch.detekt.cli.runners

import io.gitlab.arturbosch.detekt.api.Detektion
import io.gitlab.arturbosch.detekt.api.Notification
import io.gitlab.arturbosch.detekt.api.internal.CommaSeparatedPattern
import io.gitlab.arturbosch.detekt.api.internal.DEFAULT_PROPERTY_EXCLUDES
import io.gitlab.arturbosch.detekt.api.internal.SimpleNotification
import io.gitlab.arturbosch.detekt.api.internal.validateConfig
import io.gitlab.arturbosch.detekt.cli.BuildFailure
import io.gitlab.arturbosch.detekt.cli.CliArgs
import io.gitlab.arturbosch.detekt.cli.InvalidConfig
import io.gitlab.arturbosch.detekt.cli.OutputFacade
import io.gitlab.arturbosch.detekt.cli.createClasspath
import io.gitlab.arturbosch.detekt.cli.createFilters
import io.gitlab.arturbosch.detekt.cli.createPlugins
import io.gitlab.arturbosch.detekt.cli.getOrComputeWeightedAmountOfIssues
import io.gitlab.arturbosch.detekt.cli.isValidAndSmallerOrEqual
import io.gitlab.arturbosch.detekt.cli.loadConfiguration
import io.gitlab.arturbosch.detekt.cli.loadDefaultConfig
import io.gitlab.arturbosch.detekt.cli.maxIssues
import io.gitlab.arturbosch.detekt.core.DetektFacade
import io.gitlab.arturbosch.detekt.core.ProcessingSettings

class Runner(private val arguments: CliArgs) : Executable {

    override fun execute() {
        createSettings().use { settings ->
            checkConfiguration(settings)
            val (time, result) = measure { DetektFacade.create(settings).run() }
            result.add(SimpleNotification("detekt finished in $time ms."))
            OutputFacade(arguments, result, settings).run()
            if (!arguments.createBaseline) {
                checkBuildFailureThreshold(result, settings)
            }
        }
    }

    private fun checkConfiguration(settings: ProcessingSettings) {
        settings.debug { "\n${settings.config}\n" }
        val props = settings.config.subConfig("config")
        val shouldValidate = props.valueOrDefault("validation", true)

        fun patterns(): Set<Regex> {
            val excludes = props.valueOrDefault("excludes", "") + ",$DEFAULT_PROPERTY_EXCLUDES"
            return CommaSeparatedPattern(excludes).mapToRegex()
        }

        if (shouldValidate) {
            val notifications = validateConfig(settings.config, loadDefaultConfig(), patterns())
            if (notifications.isNotEmpty()) {
                notifications.map(Notification::message).forEach(settings::info)
                val propsString = if (notifications.size == 1) "property" else "properties"
                throw InvalidConfig("Run failed with ${notifications.size} invalid config $propsString.")
            }
        }
    }

    private fun checkBuildFailureThreshold(result: Detektion, settings: ProcessingSettings) {
        val amount = result.getOrComputeWeightedAmountOfIssues(settings.config)
        val maxIssues = settings.config.maxIssues()
        if (maxIssues.isValidAndSmallerOrEqual(amount)) {
            throw BuildFailure("Build failed with $amount weighted issues (threshold defined was $maxIssues).")
        }
    }

    inline fun <T> measure(block: () -> T): Pair<Long, T> {
        val start = System.currentTimeMillis()
        val result = block()
        return System.currentTimeMillis() - start to result
    }

    private fun createSettings(): ProcessingSettings = with(arguments) {
        ProcessingSettings(
            inputPaths = inputPaths,
            config = loadConfiguration(),
            pathFilters = createFilters(),
            parallelCompilation = parallel,
            autoCorrect = autoCorrect,
            excludeDefaultRuleSets = disableDefaultRuleSets,
            pluginPaths = createPlugins(),
            classpath = createClasspath(),
            languageVersion = languageVersion,
            jvmTarget = jvmTarget,
            debug = arguments.debug
        )
    }
}
