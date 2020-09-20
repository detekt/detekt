package io.gitlab.arturbosch.detekt

import io.gitlab.arturbosch.detekt.extensions.DetektExtension
import io.gitlab.arturbosch.detekt.extensions.DetektReport
import io.gitlab.arturbosch.detekt.extensions.DetektReportType
import io.gitlab.arturbosch.detekt.extensions.DetektReports
import io.gitlab.arturbosch.detekt.invoke.AutoCorrectArgument
import io.gitlab.arturbosch.detekt.invoke.BaselineArgument
import io.gitlab.arturbosch.detekt.invoke.BuildUponDefaultConfigArgument
import io.gitlab.arturbosch.detekt.invoke.ClasspathArgument
import io.gitlab.arturbosch.detekt.invoke.ConfigArgument
import io.gitlab.arturbosch.detekt.invoke.CustomReportArgument
import io.gitlab.arturbosch.detekt.invoke.DebugArgument
import io.gitlab.arturbosch.detekt.invoke.DefaultReportArgument
import io.gitlab.arturbosch.detekt.invoke.DetektInvoker
import io.gitlab.arturbosch.detekt.invoke.DisableDefaultRuleSetArgument
import io.gitlab.arturbosch.detekt.invoke.FailFastArgument
import io.gitlab.arturbosch.detekt.invoke.InputArgument
import io.gitlab.arturbosch.detekt.invoke.JvmTargetArgument
import io.gitlab.arturbosch.detekt.invoke.LanguageVersionArgument
import io.gitlab.arturbosch.detekt.invoke.ParallelArgument
import org.gradle.api.Action
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.file.Directory
import org.gradle.api.file.FileTree
import org.gradle.api.file.RegularFile
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.Property
import org.gradle.api.provider.Provider
import org.gradle.api.reporting.ReportingExtension
import org.gradle.api.tasks.CacheableTask
import org.gradle.api.tasks.Classpath
import org.gradle.api.tasks.Console
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.OutputFiles
import org.gradle.api.tasks.PathSensitive
import org.gradle.api.tasks.PathSensitivity
import org.gradle.api.tasks.SkipWhenEmpty
import org.gradle.api.tasks.SourceTask
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.VerificationTask
import org.gradle.language.base.plugins.LifecycleBasePlugin
import java.io.File
import javax.inject.Inject

@CacheableTask
open class Detekt @Inject constructor(
    private val objects: ObjectFactory
) : SourceTask(), VerificationTask {

    @get:Classpath
    val detektClasspath: ConfigurableFileCollection = objects.fileCollection()

    @get:Classpath
    val pluginClasspath: ConfigurableFileCollection = objects.fileCollection()

    @get:InputFile
    @get:Optional
    @get:PathSensitive(PathSensitivity.RELATIVE)
    val baseline: RegularFileProperty = project.objects.fileProperty()

    @get:InputFiles
    @get:Optional
    @get:PathSensitive(PathSensitivity.RELATIVE)
    val config: ConfigurableFileCollection = objects.fileCollection()

    @get:Classpath
    @get:Optional
    val classpath: ConfigurableFileCollection = objects.fileCollection()

    @get:Input
    @get:Optional
    internal val languageVersionProp: Property<String> = project.objects.property(String::class.javaObjectType)
    var languageVersion: String
        @Internal
        get() = languageVersionProp.get()
        set(value) = languageVersionProp.set(value)

    @get:Input
    @get:Optional
    internal val jvmTargetProp: Property<String> = project.objects.property(String::class.javaObjectType)
    var jvmTarget: String
        @Internal
        get() = jvmTargetProp.get()
        set(value) = jvmTargetProp.set(value)

    @get:Internal
    internal val debugProp: Property<Boolean> = project.objects.property(Boolean::class.javaObjectType)
    var debug: Boolean
        @Console
        get() = debugProp.getOrElse(false)
        set(value) = debugProp.set(value)

    @get:Internal
    internal val parallelProp: Property<Boolean> = project.objects.property(Boolean::class.javaObjectType)
    var parallel: Boolean
        @Internal
        get() = parallelProp.getOrElse(false)
        set(value) = parallelProp.set(value)

    @get:Internal
    internal val disableDefaultRuleSetsProp: Property<Boolean> = project.objects.property(Boolean::class.javaObjectType)
    var disableDefaultRuleSets: Boolean
        @Input
        get() = disableDefaultRuleSetsProp.getOrElse(false)
        set(value) = disableDefaultRuleSetsProp.set(value)

    @get:Internal
    internal val buildUponDefaultConfigProp: Property<Boolean> = project.objects.property(Boolean::class.javaObjectType)
    var buildUponDefaultConfig: Boolean
        @Input
        get() = buildUponDefaultConfigProp.getOrElse(false)
        set(value) = buildUponDefaultConfigProp.set(value)

    @get:Internal
    internal val failFastProp: Property<Boolean> = project.objects.property(Boolean::class.javaObjectType)
    var failFast: Boolean
        @Input
        get() = failFastProp.getOrElse(false)
        set(value) = failFastProp.set(value)

    @get:Internal
    internal val ignoreFailuresProp: Property<Boolean> = project.objects.property(Boolean::class.javaObjectType)

    @get:Internal
    internal val autoCorrectProp: Property<Boolean> = project.objects.property(Boolean::class.javaObjectType)
    var autoCorrect: Boolean
        @Input
        get() = autoCorrectProp.getOrElse(false)
        set(value) = autoCorrectProp.set(value)

    @get:Internal
    var reports = DetektReports()

    @get:Internal
    val reportsDir: Property<File> = project.objects.property(File::class.java)

    internal val xmlReportFile: Provider<RegularFile>
        @OutputFile
        @Optional
        get() = getTargetFileProvider(reports.xml)

    internal val htmlReportFile: Provider<RegularFile>
        @OutputFile
        @Optional
        get() = getTargetFileProvider(reports.html)

    internal val txtReportFile: Provider<RegularFile>
        @OutputFile
        @Optional
        get() = getTargetFileProvider(reports.txt)

    internal val customReportFiles: ConfigurableFileCollection
        @OutputFiles
        @Optional
        get() = objects.fileCollection().from(reports.custom.mapNotNull { it.destination })

    private val defaultReportsDir: Directory = project.layout.buildDirectory.get()
        .dir(ReportingExtension.DEFAULT_REPORTS_DIR_NAME)
        .dir("detekt")

    init {
        group = LifecycleBasePlugin.VERIFICATION_GROUP
    }

    private val invoker: DetektInvoker = DetektInvoker.create(project)

    @InputFiles
    @SkipWhenEmpty
    @PathSensitive(PathSensitivity.RELATIVE)
    override fun getSource(): FileTree = super.getSource()

    @Input
    override fun getIgnoreFailures(): Boolean = ignoreFailuresProp.getOrElse(false)

    override fun setIgnoreFailures(value: Boolean) = ignoreFailuresProp.set(value)

    fun reports(configure: Action<DetektReports>) = configure.execute(reports)

    @Suppress("DEPRECATION")
    @TaskAction
    fun check() {
        val arguments = mutableListOf(
            InputArgument(source),
            ClasspathArgument(classpath),
            LanguageVersionArgument(languageVersionProp.orNull),
            JvmTargetArgument(jvmTargetProp.orNull),
            ConfigArgument(config),
            BaselineArgument(baseline.orNull),
            DefaultReportArgument(DetektReportType.XML, xmlReportFile.orNull),
            DefaultReportArgument(DetektReportType.HTML, htmlReportFile.orNull),
            DefaultReportArgument(DetektReportType.TXT, txtReportFile.orNull),
            DebugArgument(debugProp.getOrElse(false)),
            ParallelArgument(parallelProp.getOrElse(false)),
            BuildUponDefaultConfigArgument(buildUponDefaultConfigProp.getOrElse(false)),
            FailFastArgument(failFastProp.getOrElse(false)),
            AutoCorrectArgument(autoCorrectProp.getOrElse(false)),
            DisableDefaultRuleSetArgument(disableDefaultRuleSetsProp.getOrElse(false))
        )
        arguments.addAll(convertCustomReportsToArguments())

        invoker.invokeCli(
            arguments = arguments.toList(),
            ignoreFailures = ignoreFailures,
            classpath = detektClasspath.plus(pluginClasspath),
            taskName = name
        )
    }

    private fun convertCustomReportsToArguments(): List<CustomReportArgument> = reports.custom.map {
        val reportId = it.reportId
        val destination = it.destination

        checkNotNull(reportId) { "If a custom report is specified, the reportId must be present" }
        check(!DetektReportType.isWellKnownReportId(reportId)) {
            "The custom report reportId may not be same as one of the default reports"
        }
        checkNotNull(destination) { "If a custom report is specified, the destination must be present" }
        check(!destination.isDirectory) { "If a custom report is specified, the destination must be not a directory" }

        CustomReportArgument(reportId, objects.fileProperty().getOrElse { destination })
    }

    private fun getTargetFileProvider(report: DetektReport): RegularFileProperty {
        val isEnabled = report.enabled ?: DetektExtension.DEFAULT_REPORT_ENABLED_VALUE
        val provider = objects.fileProperty()
        if (isEnabled) {
            val destination = report.destination ?: reportsDir.getOrElse(defaultReportsDir.asFile)
                .resolve("${DetektReport.DEFAULT_FILENAME}.${report.type.extension}")
            provider.set(destination)
        }
        return provider
    }
}
