package io.gitlab.arturbosch.detekt.cli.runners

import io.gitlab.arturbosch.detekt.cli.CliArgs
import io.gitlab.arturbosch.detekt.cli.OutputFacade
import io.gitlab.arturbosch.detekt.cli.createClasspath
import io.gitlab.arturbosch.detekt.cli.createFilters
import io.gitlab.arturbosch.detekt.cli.createPlugins
import io.gitlab.arturbosch.detekt.cli.loadConfiguration
import io.gitlab.arturbosch.detekt.core.DetektFacade
import io.gitlab.arturbosch.detekt.core.ProcessingSettings
import kotlin.system.measureTimeMillis

/**
 * @author Artur Bosch
 * @author Marvin Ramin
 */
class Runner(private val arguments: CliArgs) : Executable {

    override fun execute() {
        val settings = createSettings()

        val time = measureTimeMillis {
            val detektion = DetektFacade.create(settings).run()
            OutputFacade(arguments, detektion, settings).run()
        }

        println("\ndetekt finished in $time ms.")
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
            jvmTarget = jvmTarget
        )
    }
}
