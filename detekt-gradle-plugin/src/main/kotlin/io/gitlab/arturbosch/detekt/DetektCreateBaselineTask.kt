package io.gitlab.arturbosch.detekt

import io.gitlab.arturbosch.detekt.invoke.AllRulesArgument
import io.gitlab.arturbosch.detekt.invoke.AutoCorrectArgument
import io.gitlab.arturbosch.detekt.invoke.BasePathArgument
import io.gitlab.arturbosch.detekt.invoke.BaselineArgument
import io.gitlab.arturbosch.detekt.invoke.BuildUponDefaultConfigArgument
import io.gitlab.arturbosch.detekt.invoke.ClasspathArgument
import io.gitlab.arturbosch.detekt.invoke.CliArgument
import io.gitlab.arturbosch.detekt.invoke.ConfigArgument
import io.gitlab.arturbosch.detekt.invoke.CreateBaselineArgument
import io.gitlab.arturbosch.detekt.invoke.DebugArgument
import io.gitlab.arturbosch.detekt.invoke.DetektInvoker
import io.gitlab.arturbosch.detekt.invoke.DisableDefaultRuleSetArgument
import io.gitlab.arturbosch.detekt.invoke.FailFastArgument
import io.gitlab.arturbosch.detekt.invoke.InputArgument
import io.gitlab.arturbosch.detekt.invoke.JvmTargetArgument
import io.gitlab.arturbosch.detekt.invoke.ParallelArgument
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.file.FileTree
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.Property
import org.gradle.api.provider.Provider
import org.gradle.api.tasks.CacheableTask
import org.gradle.api.tasks.Classpath
import org.gradle.api.tasks.Console
import org.gradle.api.tasks.IgnoreEmptyDirectories
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.PathSensitive
import org.gradle.api.tasks.PathSensitivity
import org.gradle.api.tasks.SkipWhenEmpty
import org.gradle.api.tasks.SourceTask
import org.gradle.api.tasks.TaskAction
import org.gradle.language.base.plugins.LifecycleBasePlugin

@CacheableTask
abstract class DetektCreateBaselineTask : SourceTask() {

    init {
        description = "Creates a detekt baseline on the given --baseline path."
        group = LifecycleBasePlugin.VERIFICATION_GROUP
    }

    @get:OutputFile
    abstract val baseline: RegularFileProperty

    @get:InputFiles
    @get:Optional
    @get:PathSensitive(PathSensitivity.RELATIVE)
    abstract val config: ConfigurableFileCollection

    @get:Classpath
    abstract val detektClasspath: ConfigurableFileCollection

    @get:Classpath
    abstract val pluginClasspath: ConfigurableFileCollection

    @get:Classpath
    @get:Optional
    abstract val classpath: ConfigurableFileCollection

    @get:Console
    abstract val debug: Property<Boolean>

    @get:Internal
    abstract val parallel: Property<Boolean>

    @get:Input
    @get:Optional
    abstract val disableDefaultRuleSets: Property<Boolean>

    @get:Input
    @get:Optional
    abstract val buildUponDefaultConfig: Property<Boolean>

    @get:Input
    @get:Optional
    @Deprecated("Please use the buildUponDefaultConfig and allRules flags instead.", ReplaceWith("allRules"))
    abstract val failFast: Property<Boolean>

    @get:Input
    @get:Optional
    abstract val ignoreFailures: Property<Boolean>

    @get:Input
    @get:Optional
    abstract val allRules: Property<Boolean>

    @get:Input
    @get:Optional
    abstract val autoCorrect: Property<Boolean>

    /**
     * Respect only the file path for incremental build. Using @InputFile respects both file path and content.
     */
    @get:Input
    @get:Optional
    internal abstract val basePathProp: Property<String>
    var basePath: String
        @Internal
        get() = basePathProp.get()
        set(value) = basePathProp.set(value)

    @get:Input
    @get:Optional
    internal abstract val jvmTargetProp: Property<String>
    var jvmTarget: String
        @Internal
        get() = jvmTargetProp.get()
        set(value) = jvmTargetProp.set(value)

    @get:Internal
    internal val arguments: Provider<List<String>> = project.provider {
        listOf(
            CreateBaselineArgument,
            ClasspathArgument(classpath),
            JvmTargetArgument(jvmTargetProp.orNull),
            BaselineArgument(baseline.get()),
            InputArgument(source),
            ConfigArgument(config),
            DebugArgument(debug.getOrElse(false)),
            ParallelArgument(parallel.getOrElse(false)),
            BuildUponDefaultConfigArgument(buildUponDefaultConfig.getOrElse(false)),
            FailFastArgument(@Suppress("DEPRECATION") failFast.getOrElse(false)),
            AutoCorrectArgument(autoCorrect.getOrElse(false)),
            AllRulesArgument(allRules.getOrElse(false)),
            BasePathArgument(basePathProp.orNull),
            DisableDefaultRuleSetArgument(disableDefaultRuleSets.getOrElse(false))
        ).flatMap(CliArgument::toArgument)
    }

    @InputFiles
    @SkipWhenEmpty
    @IgnoreEmptyDirectories
    @PathSensitive(PathSensitivity.RELATIVE)
    override fun getSource(): FileTree = super.getSource()

    @TaskAction
    fun baseline() {
        if (@Suppress("DEPRECATION") failFast.getOrElse(false)) {
            logger.warn("'failFast' is deprecated. Please use 'buildUponDefaultConfig' together with 'allRules'.")
        }

        DetektInvoker.create(task = this).invokeCli(
            arguments = arguments.get(),
            ignoreFailures = ignoreFailures.getOrElse(false),
            classpath = detektClasspath.plus(pluginClasspath),
            taskName = name
        )
    }
}
