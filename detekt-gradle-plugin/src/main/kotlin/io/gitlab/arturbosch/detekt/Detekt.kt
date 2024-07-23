package io.gitlab.arturbosch.detekt

import dev.detekt.gradle.plugin.DetektBase
import dev.detekt.gradle.plugin.DetektCliTool
import io.gitlab.arturbosch.detekt.extensions.DetektReportType
import io.gitlab.arturbosch.detekt.extensions.DetektReports
import io.gitlab.arturbosch.detekt.extensions.FailOnSeverity
import io.gitlab.arturbosch.detekt.invoke.AllRulesArgument
import io.gitlab.arturbosch.detekt.invoke.ApiVersionArgument
import io.gitlab.arturbosch.detekt.invoke.AutoCorrectArgument
import io.gitlab.arturbosch.detekt.invoke.BasePathArgument
import io.gitlab.arturbosch.detekt.invoke.BaselineArgumentOrEmpty
import io.gitlab.arturbosch.detekt.invoke.BuildUponDefaultConfigArgument
import io.gitlab.arturbosch.detekt.invoke.ClasspathArgument
import io.gitlab.arturbosch.detekt.invoke.CliArgument
import io.gitlab.arturbosch.detekt.invoke.ConfigArgument
import io.gitlab.arturbosch.detekt.invoke.CustomReportArgument
import io.gitlab.arturbosch.detekt.invoke.DebugArgument
import io.gitlab.arturbosch.detekt.invoke.DefaultReportArgument
import io.gitlab.arturbosch.detekt.invoke.DetektInvoker
import io.gitlab.arturbosch.detekt.invoke.DetektWorkAction
import io.gitlab.arturbosch.detekt.invoke.DisableDefaultRuleSetArgument
import io.gitlab.arturbosch.detekt.invoke.ExplicitApiArgument
import io.gitlab.arturbosch.detekt.invoke.FailOnSeverityArgument
import io.gitlab.arturbosch.detekt.invoke.FreeArgs
import io.gitlab.arturbosch.detekt.invoke.FriendPathArgs
import io.gitlab.arturbosch.detekt.invoke.InputArgument
import io.gitlab.arturbosch.detekt.invoke.JdkHomeArgument
import io.gitlab.arturbosch.detekt.invoke.JvmTargetArgument
import io.gitlab.arturbosch.detekt.invoke.LanguageVersionArgument
import io.gitlab.arturbosch.detekt.invoke.MultiPlatformEnabledArgument
import io.gitlab.arturbosch.detekt.invoke.NoJdkArgument
import io.gitlab.arturbosch.detekt.invoke.OptInArguments
import io.gitlab.arturbosch.detekt.invoke.ParallelArgument
import org.gradle.api.Action
import org.gradle.api.file.FileTree
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.Property
import org.gradle.api.provider.ProviderFactory
import org.gradle.api.tasks.CacheableTask
import org.gradle.api.tasks.IgnoreEmptyDirectories
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.Nested
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.PathSensitive
import org.gradle.api.tasks.PathSensitivity
import org.gradle.api.tasks.SkipWhenEmpty
import org.gradle.api.tasks.SourceTask
import org.gradle.api.tasks.TaskAction
import org.gradle.language.base.plugins.LifecycleBasePlugin
import org.gradle.workers.WorkerExecutor
import javax.inject.Inject

@CacheableTask
abstract class Detekt @Inject constructor(
    private val objects: ObjectFactory,
    private val workerExecutor: WorkerExecutor,
    private val providers: ProviderFactory,
) : DetektBase, DetektCliTool, SourceTask() {

    @get:InputFiles // Why not InputFile? See https://github.com/gradle/gradle/issues/2016
    @get:Optional
    @get:PathSensitive(PathSensitivity.RELATIVE)
    abstract override val baseline: RegularFileProperty

    @get:Input
    @get:Optional
    abstract val failOnSeverity: Property<FailOnSeverity>

    @get:Nested
    /*
    Property must be open (as do the @Nested properties in DetektReports), see
    https://github.com/gradle/gradle/pull/12601 and https://github.com/gradle/gradle/issues/6619
     */
    open val reports: DetektReports = objects.newInstance(DetektReports::class.java)

    private val isDryRun = project.providers.gradleProperty(DRY_RUN_PROPERTY)

    @get:Input
    @get:Optional
    internal abstract val explicitApi: Property<String>

    init {
        group = LifecycleBasePlugin.VERIFICATION_GROUP
    }

    @get:Internal
    internal val arguments
        get() = listOf(
            InputArgument(source),
            ClasspathArgument(classpath),
            ApiVersionArgument(apiVersion.orNull),
            LanguageVersionArgument(languageVersion.orNull),
            JvmTargetArgument(jvmTarget.orNull),
            JdkHomeArgument(jdkHome),
            ConfigArgument(config),
            BaselineArgumentOrEmpty(baseline.orNull),
            DefaultReportArgument(reports.xml),
            DefaultReportArgument(reports.html),
            DefaultReportArgument(reports.sarif),
            DefaultReportArgument(reports.md),
            DebugArgument(debug.get()),
            ParallelArgument(parallel.get()),
            BuildUponDefaultConfigArgument(buildUponDefaultConfig.get()),
            AllRulesArgument(allRules.get()),
            AutoCorrectArgument(autoCorrect.get()),
            FailOnSeverityArgument(
                ignoreFailures = ignoreFailures.get(),
                minSeverity = failOnSeverity.get()
            ),
            BasePathArgument(basePath.orNull),
            DisableDefaultRuleSetArgument(disableDefaultRuleSets.get()),
            FreeArgs(freeCompilerArgs.get()),
            OptInArguments(optIn.get()),
            FriendPathArgs(friendPaths),
            NoJdkArgument(noJdk.get()),
            ExplicitApiArgument(explicitApi.orNull),
            MultiPlatformEnabledArgument(multiPlatformEnabled.get()),
        ).plus(convertCustomReportsToArguments()).flatMap(CliArgument::toArgument)
            .plus("-no-stdlib")
            .plus("-no-reflect")

    @InputFiles
    @SkipWhenEmpty
    @IgnoreEmptyDirectories
    @PathSensitive(PathSensitivity.RELATIVE)
    override fun getSource(): FileTree = super.getSource()

    fun reports(configure: Action<DetektReports>) {
        configure.execute(reports)
    }

    @TaskAction
    fun check() {
        if (providers.isWorkerApiEnabled()) {
            logger.info("Executing $name using Worker API")
            val workQueue = workerExecutor.processIsolation()

            workQueue.submit(DetektWorkAction::class.java) { workParameters ->
                workParameters.arguments.set(arguments)
                workParameters.classpath.setFrom(detektClasspath, pluginClasspath)
                workParameters.ignoreFailures.set(ignoreFailures)
                workParameters.dryRun.set(isDryRun.orNull.toBoolean())
                workParameters.taskName.set(name)
            }
        } else {
            logger.info("Executing $name using DetektInvoker")
            DetektInvoker.create(isDryRun = isDryRun.orNull.toBoolean()).invokeCli(
                arguments = arguments,
                ignoreFailures = ignoreFailures.get(),
                classpath = detektClasspath.plus(pluginClasspath).files,
                taskName = name
            )
        }
    }

    private fun convertCustomReportsToArguments(): List<CustomReportArgument> = reports.custom.map {
        val reportId = it.reportId
        val destination = it.outputLocation.asFile.orNull

        checkNotNull(reportId) { "If a custom report is specified, the reportId must be present" }
        check(!DetektReportType.isWellKnownReportId(reportId)) {
            "The custom report reportId may not be same as one of the default reports"
        }
        checkNotNull(destination) { "If a custom report is specified, the destination must be present" }
        check(!destination.isDirectory) { "If a custom report is specified, the destination must be not a directory" }

        CustomReportArgument(reportId, objects.fileProperty().getOrElse { destination })
    }
}

private const val DRY_RUN_PROPERTY = "detekt-dry-run"
