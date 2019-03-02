package io.gitlab.arturbosch.detekt

import io.gitlab.arturbosch.detekt.internal.fileProperty
import io.gitlab.arturbosch.detekt.invoke.BaselineArgument
import io.gitlab.arturbosch.detekt.invoke.BuildUponDefaultConfigArgument
import io.gitlab.arturbosch.detekt.invoke.ConfigArgument
import io.gitlab.arturbosch.detekt.invoke.CreateBaselineArgument
import io.gitlab.arturbosch.detekt.invoke.DebugArgument
import io.gitlab.arturbosch.detekt.invoke.DetektInvoker
import io.gitlab.arturbosch.detekt.invoke.DisableDefaultRuleSetArgument
import io.gitlab.arturbosch.detekt.invoke.FailFastArgument
import io.gitlab.arturbosch.detekt.invoke.InputArgument
import io.gitlab.arturbosch.detekt.invoke.ParallelArgument
import io.gitlab.arturbosch.detekt.invoke.PluginsArgument
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.file.FileCollection
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.PathSensitive
import org.gradle.api.tasks.PathSensitivity
import org.gradle.api.tasks.SourceTask
import org.gradle.api.tasks.TaskAction
import org.gradle.language.base.plugins.LifecycleBasePlugin

/**
 * @author Artur Bosch
 * @author Marvin Ramin
 * @author Markus Schwarz
 */
open class DetektCreateBaselineTask : SourceTask() {

    init {
        description = "Creates a detekt baseline on the given --baseline path."
        group = LifecycleBasePlugin.VERIFICATION_GROUP
    }

    @OutputFile
    @PathSensitive(PathSensitivity.RELATIVE)
    var baseline: RegularFileProperty = project.fileProperty()

    @Deprecated("Replace with getSource/setSource")
    var input: FileCollection
        get() = source
        set(value) = setSource(value)

    @Input
    @Optional
    @Deprecated("Replace with setIncludes/setExcludes")
    var filters: Property<String> = project.objects.property(String::class.java)

    @InputFiles
    @Optional
    @PathSensitive(PathSensitivity.RELATIVE)
    var config: ConfigurableFileCollection = project.layout.configurableFiles()

    @Input
    @Optional
    var plugins: Property<String> = project.objects.property(String::class.java)

    @Internal
    @Optional
    var debug: Property<Boolean> = project.objects.property(Boolean::class.javaObjectType)

    @Internal
    @Optional
    var parallel: Property<Boolean> = project.objects.property(Boolean::class.javaObjectType)

    @Internal
    @Optional
    var disableDefaultRuleSets: Property<Boolean> = project.objects.property(Boolean::class.javaObjectType)

    @Internal
	@Optional
	var buildUponDefaultConfig: Property<Boolean> = project.objects.property(Boolean::class.javaObjectType)

	@Internal
	@Optional
	var failFast: Property<Boolean> = project.objects.property(Boolean::class.javaObjectType)

	@TaskAction
    fun baseline() {
        val arguments = mutableListOf(
				CreateBaselineArgument,
                BaselineArgument(baseline.get()) ,
                InputArgument(source) ,
                ConfigArgument(config) ,
                PluginsArgument(plugins.orNull) ,
                DebugArgument(debug.getOrElse(false)),
                ParallelArgument(parallel.getOrElse(false)),
                BuildUponDefaultConfigArgument(buildUponDefaultConfig.getOrElse(false)),
				FailFastArgument(failFast.getOrElse(false)),
				DisableDefaultRuleSetArgument(disableDefaultRuleSets.getOrElse(false))
		)

        DetektInvoker.invokeCli(project, arguments.toList(), debug.getOrElse(false))
    }
}
