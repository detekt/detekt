package dev.detekt.gradle

import dev.detekt.gradle.invoke.AllRulesArgument
import dev.detekt.gradle.invoke.ApiVersionArgument
import dev.detekt.gradle.invoke.AutoCorrectArgument
import dev.detekt.gradle.invoke.BasePathArgument
import dev.detekt.gradle.invoke.BaselineArgument
import dev.detekt.gradle.invoke.BuildUponDefaultConfigArgument
import dev.detekt.gradle.invoke.ClasspathArgument
import dev.detekt.gradle.invoke.CliArgument
import dev.detekt.gradle.invoke.ConfigArgument
import dev.detekt.gradle.invoke.CreateBaselineArgument
import dev.detekt.gradle.invoke.DebugArgument
import dev.detekt.gradle.invoke.DetektInvoker
import dev.detekt.gradle.invoke.DetektWorkAction
import dev.detekt.gradle.invoke.DisableDefaultRuleSetArgument
import dev.detekt.gradle.invoke.ExplicitApiArgument
import dev.detekt.gradle.invoke.FreeArgs
import dev.detekt.gradle.invoke.FriendPathArgs
import dev.detekt.gradle.invoke.InputArgument
import dev.detekt.gradle.invoke.JdkHomeArgument
import dev.detekt.gradle.invoke.JvmTargetArgument
import dev.detekt.gradle.invoke.LanguageVersionArgument
import dev.detekt.gradle.invoke.MultiPlatformEnabledArgument
import dev.detekt.gradle.invoke.NoJdkArgument
import dev.detekt.gradle.invoke.OptInArguments
import dev.detekt.gradle.invoke.ParallelArgument
import dev.detekt.gradle.plugin.isWorkerApiEnabled
import org.gradle.api.Incubating
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.file.FileTree
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.Property
import org.gradle.api.provider.ProviderFactory
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
import org.gradle.workers.WorkerExecutor
import javax.inject.Inject

@CacheableTask
abstract class DetektCreateBaselineTask @Inject constructor(
    private val workerExecutor: WorkerExecutor,
    private val providers: ProviderFactory,
) : SourceTask() {

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

    @get:Internal
    abstract val friendPaths: ConfigurableFileCollection

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
    abstract val ignoreFailures: Property<Boolean>

    @get:Input
    @get:Optional
    abstract val allRules: Property<Boolean>

    @get:Input
    abstract val optIn: ListProperty<String>

    @get:Input
    abstract val noJdk: Property<Boolean>

    @get:Input
    abstract val multiPlatformEnabled: Property<Boolean>

    @get:Input
    @get:Optional
    abstract val autoCorrect: Property<Boolean>

    /**
     * Respect only the file path for incremental build. Using @InputFile respects both file path and content.
     */
    @get:Input
    @get:Optional
    abstract val basePath: Property<String>

    @get:Input
    @get:Optional
    abstract val jvmTarget: Property<String>

    @get:Input
    @get:Optional
    abstract val apiVersion: Property<String>

    @get:Input
    @get:Optional
    abstract val languageVersion: Property<String>

    @get:Internal
    abstract val jdkHome: DirectoryProperty

    @get:Input
    @get:Incubating
    abstract val freeCompilerArgs: ListProperty<String>

    @get:Input
    @get:Optional
    internal abstract val explicitApi: Property<String>

    @get:Internal
    internal val arguments
        get() = listOf(
            CreateBaselineArgument,
            ClasspathArgument(classpath),
            ApiVersionArgument(apiVersion.orNull),
            LanguageVersionArgument(languageVersion.orNull),
            JvmTargetArgument(jvmTarget.orNull),
            JdkHomeArgument(jdkHome),
            BaselineArgument(baseline.get()),
            InputArgument(source),
            ConfigArgument(config),
            DebugArgument(debug.get()),
            ParallelArgument(parallel.get()),
            BuildUponDefaultConfigArgument(buildUponDefaultConfig.get()),
            AutoCorrectArgument(autoCorrect.get()),
            AllRulesArgument(allRules.get()),
            BasePathArgument(basePath.orNull),
            DisableDefaultRuleSetArgument(disableDefaultRuleSets.get()),
            FreeArgs(freeCompilerArgs.get()),
            OptInArguments(optIn.get()),
            FriendPathArgs(friendPaths),
            NoJdkArgument(noJdk.get()),
            ExplicitApiArgument(explicitApi.orNull),
            MultiPlatformEnabledArgument(multiPlatformEnabled.get()),
        ).flatMap(CliArgument::toArgument)
            .plus("-no-stdlib")
            .plus("-no-reflect")

    @InputFiles
    @SkipWhenEmpty
    @IgnoreEmptyDirectories
    @PathSensitive(PathSensitivity.RELATIVE)
    override fun getSource(): FileTree = super.getSource()

    @TaskAction
    fun baseline() {
        if (providers.isWorkerApiEnabled()) {
            logger.info("Executing $name using Worker API")
            val workQueue = workerExecutor.processIsolation()

            workQueue.submit(DetektWorkAction::class.java) { workParameters ->
                workParameters.arguments.set(arguments)
                workParameters.classpath.setFrom(detektClasspath, pluginClasspath)
                workParameters.ignoreFailures.set(ignoreFailures)
                workParameters.taskName.set(name)
            }
        } else {
            logger.info("Executing $name using DetektInvoker")
            DetektInvoker.create().invokeCli(
                arguments = arguments,
                ignoreFailures = ignoreFailures.get(),
                classpath = detektClasspath.plus(pluginClasspath).files,
                taskName = name
            )
        }
    }
}
