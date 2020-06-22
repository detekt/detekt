package io.gitlab.arturbosch.detekt

import io.gitlab.arturbosch.detekt.internal.configurableFileCollection
import io.gitlab.arturbosch.detekt.invoke.AutoCorrectArgument
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
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.CacheableTask
import org.gradle.api.tasks.Classpath
import org.gradle.api.tasks.Console
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

@CacheableTask
open class DetektCreateBaselineTask : SourceTask() {

    init {
        description = "Creates a detekt baseline on the given --baseline path."
        group = LifecycleBasePlugin.VERIFICATION_GROUP
    }

    @get:OutputFile
    val baseline: RegularFileProperty = project.objects.fileProperty()

    @get:InputFiles
    @get:Optional
    @PathSensitive(PathSensitivity.RELATIVE)
    val config: ConfigurableFileCollection = project.configurableFileCollection()

    @get:Classpath
    val detektClasspath = project.configurableFileCollection()

    @get:Classpath
    val pluginClasspath = project.configurableFileCollection()

    @get:Console
    val debug: Property<Boolean> = project.objects.property(Boolean::class.javaObjectType)

    @get:Internal
    val parallel: Property<Boolean> = project.objects.property(Boolean::class.javaObjectType)

    @get:Input
    @get:Optional
    val disableDefaultRuleSets: Property<Boolean> = project.objects.property(Boolean::class.javaObjectType)

    @get:Input
    @get:Optional
    val buildUponDefaultConfig: Property<Boolean> = project.objects.property(Boolean::class.javaObjectType)

    @get:Input
    @get:Optional
    val failFast: Property<Boolean> = project.objects.property(Boolean::class.javaObjectType)

    @get:Input
    @get:Optional
    val ignoreFailures: Property<Boolean> = project.objects.property(Boolean::class.javaObjectType)

    @get:Input
    @get:Optional
    val autoCorrect: Property<Boolean> = project.objects.property(Boolean::class.javaObjectType)

    private val invoker: DetektInvoker = DetektInvoker.create(project)

    @TaskAction
    fun baseline() {
        val arguments = mutableListOf(
            CreateBaselineArgument,
            BaselineArgument(baseline.get()),
            InputArgument(source),
            ConfigArgument(config),
            DebugArgument(debug.getOrElse(false)),
            ParallelArgument(parallel.getOrElse(false)),
            BuildUponDefaultConfigArgument(buildUponDefaultConfig.getOrElse(false)),
            FailFastArgument(failFast.getOrElse(false)),
            AutoCorrectArgument(autoCorrect.getOrElse(false)),
            DisableDefaultRuleSetArgument(disableDefaultRuleSets.getOrElse(false))
        )

        invoker.invokeCli(
            arguments = arguments.toList(),
            ignoreFailures = ignoreFailures.getOrElse(false),
            classpath = detektClasspath.plus(pluginClasspath),
            taskName = name
        )
    }
}
