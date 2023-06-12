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
import io.gitlab.arturbosch.detekt.invoke.DetektWorkAction
import io.gitlab.arturbosch.detekt.invoke.DisableDefaultRuleSetArgument
import io.gitlab.arturbosch.detekt.invoke.InputArgument
import io.gitlab.arturbosch.detekt.invoke.JvmTargetArgument
import io.gitlab.arturbosch.detekt.invoke.ParallelArgument
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.file.FileTree
import org.gradle.api.file.RegularFileProperty
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

    @get:Input
    @get:Optional
    internal abstract val languageVersionProp: Property<String>
    var languageVersion: String
        @Internal
        get() = languageVersionProp.get()
        set(value) = languageVersionProp.set(value)

    @get:Input
    @get:Optional
    internal abstract val jvmTargetProp: Property<String>
    var jvmTarget: String
        @Internal
        get() = jvmTargetProp.get()
        set(value) = jvmTargetProp.set(value)

    @get:Internal
    abstract val jdkHome: DirectoryProperty

    @get:Internal
    internal abstract val debugProp: Property<Boolean>
    var debug: Boolean
        @Console
        get() = debugProp.getOrElse(false)
        set(value) = debugProp.set(value)

    @get:Internal
    internal abstract val parallelProp: Property<Boolean>
    var parallel: Boolean
        @Internal
        get() = parallelProp.getOrElse(false)
        set(value) = parallelProp.set(value)

    @get:Internal
    internal abstract val disableDefaultRuleSetsProp: Property<Boolean>
    var disableDefaultRuleSets: Boolean
        @Input
        get() = disableDefaultRuleSetsProp.getOrElse(false)
        set(value) = disableDefaultRuleSetsProp.set(value)

    @get:Internal
    internal abstract val buildUponDefaultConfigProp: Property<Boolean>
    var buildUponDefaultConfig: Boolean
        @Input
        get() = buildUponDefaultConfigProp.getOrElse(false)
        set(value) = buildUponDefaultConfigProp.set(value)

    @get:Internal
    internal abstract val allRulesProp: Property<Boolean>
    var allRules: Boolean
        @Input
        get() = allRulesProp.getOrElse(false)
        set(value) = allRulesProp.set(value)

    @get:Input
    @get:Optional
    abstract val ignoreFailures: Property<Boolean>

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

    @get:Internal
    internal val arguments
        get() = listOf(
            CreateBaselineArgument,
            InputArgument(source),
            ClasspathArgument(classpath),
            LanguageVersionArgument(languageVersionProp.orNull),
            JvmTargetArgument(jvmTargetProp.orNull),
            JdkHomeArgument(jdkHome),
            ConfigArgument(config),
            BaselineArgument(baseline.get()),
            DebugArgument(debugProp.getOrElse(false)),
            ParallelArgument(parallelProp.getOrElse(false)),
            BuildUponDefaultConfigArgument(buildUponDefaultConfigProp.getOrElse(false)),
            AllRulesArgument(allRulesProp.getOrElse(false)),
            AutoCorrectArgument(autoCorrectProp.getOrElse(false)),
            BasePathArgument(basePathProp.orNull),
            DisableDefaultRuleSetArgument(disableDefaultRuleSetsProp.getOrElse(false))
        ).flatMap(CliArgument::toArgument)

    @InputFiles
    @SkipWhenEmpty
    @IgnoreEmptyDirectories
    @PathSensitive(PathSensitivity.RELATIVE)
    override fun getSource(): FileTree = super.getSource()

    @TaskAction
    fun baseline() {
        if (providers.gradleProperty(USE_WORKER_API).getOrElse("false") == "true") {
            logger.info("Executing $name using Worker API")
            val workQueue = workerExecutor.processIsolation { workerSpec ->
                workerSpec.classpath.from(detektClasspath)
                workerSpec.classpath.from(pluginClasspath)
            }

            workQueue.submit(DetektWorkAction::class.java) { workParameters ->
                workParameters.arguments.set(arguments)
                workParameters.ignoreFailures.set(ignoreFailures)
                workParameters.taskName.set(name)
            }
        } else {
            logger.info("Executing $name using DetektInvoker")
            DetektInvoker.create().invokeCli(
                arguments = arguments,
                ignoreFailures = ignoreFailures.getOrElse(false),
                classpath = detektClasspath.plus(pluginClasspath),
                taskName = name
            )
        }
    }
}
