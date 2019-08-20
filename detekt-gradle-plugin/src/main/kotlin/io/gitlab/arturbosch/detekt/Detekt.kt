package io.gitlab.arturbosch.detekt

import io.gitlab.arturbosch.detekt.extensions.CustomDetektReport
import io.gitlab.arturbosch.detekt.extensions.DetektReportType
import io.gitlab.arturbosch.detekt.extensions.DetektReports
import io.gitlab.arturbosch.detekt.internal.configurableFileCollection
import io.gitlab.arturbosch.detekt.internal.fileProperty
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
import io.gitlab.arturbosch.detekt.invoke.PluginsArgument
import org.gradle.api.Action
import org.gradle.api.GradleException
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.file.Directory
import org.gradle.api.file.FileCollection
import org.gradle.api.file.RegularFile
import org.gradle.api.file.RegularFileProperty
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
import org.gradle.api.tasks.Nested
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.PathSensitive
import org.gradle.api.tasks.PathSensitivity
import org.gradle.api.tasks.SourceTask
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.VerificationTask
import org.gradle.language.base.plugins.LifecycleBasePlugin
import java.io.File

@CacheableTask
open class Detekt : SourceTask(), VerificationTask {

    @Deprecated("Replace with getSource/setSource")
    var input: FileCollection
        @Internal
        get() = source
        set(value) = setSource(value)

    @Input
    @Optional
    @Deprecated("Replace with setIncludes/setExcludes")
    val filters: Property<String> = project.objects.property(String::class.java)

    @Classpath
    val detektClasspath = project.configurableFileCollection()

    @Classpath
    val pluginClasspath = project.configurableFileCollection()

    @InputFile
    @Optional
    @PathSensitive(PathSensitivity.RELATIVE)
    val baseline: RegularFileProperty = project.fileProperty()

    @InputFiles
    @Optional
    @PathSensitive(PathSensitivity.RELATIVE)
    val config: ConfigurableFileCollection = project.configurableFileCollection()

    @Classpath
    @Optional
    val classpath = project.configurableFileCollection()

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

    @Input
    @Optional
    @Deprecated(
        "Set plugins using the detektPlugins configuration " +
                "(see https://arturbosch.github.io/detekt/extensions.html#let-detekt-know-about-your-extensions)"
    )
    val plugins: Property<String> = project.objects.property(String::class.java)

    @get:Internal
    internal val debugProp: Property<Boolean> = project.objects.property(Boolean::class.javaObjectType)
    var debug: Boolean
        @Input
        get() = debugProp.get()
        set(value) = debugProp.set(value)

    @get:Internal
    internal val parallelProp: Property<Boolean> = project.objects.property(Boolean::class.javaObjectType)
    var parallel: Boolean
        @Console
        get() = parallelProp.get()
        set(value) = parallelProp.set(value)

    @get:Optional
    @get:Input
    internal val disableDefaultRuleSetsProp: Property<Boolean> = project.objects.property(Boolean::class.javaObjectType)
    var disableDefaultRuleSets: Boolean
        @Internal
        get() = disableDefaultRuleSetsProp.get()
        set(value) = disableDefaultRuleSetsProp.set(value)

    @get:Optional
    @get:Input
    internal val buildUponDefaultConfigProp: Property<Boolean> = project.objects.property(Boolean::class.javaObjectType)
    var buildUponDefaultConfig: Boolean
        @Internal
        get() = buildUponDefaultConfigProp.get()
        set(value) = buildUponDefaultConfigProp.set(value)

    @get:Optional
    @get:Input
    internal val failFastProp: Property<Boolean> = project.objects.property(Boolean::class.javaObjectType)
    var failFast: Boolean
        @Internal
        get() = failFastProp.get()
        set(value) = failFastProp.set(value)

    @get:Internal
    internal val ignoreFailuresProp: Property<Boolean> = project.objects.property(Boolean::class.javaObjectType)
    @Input
    override fun getIgnoreFailures(): Boolean = ignoreFailuresProp.getOrElse(false)
    override fun setIgnoreFailures(value: Boolean) = ignoreFailuresProp.set(value)

    @get:Optional
    @get:Input
    internal val autoCorrectProp: Property<Boolean> = project.objects.property(Boolean::class.javaObjectType)
    var autoCorrect: Boolean
        @Internal
        get() = autoCorrectProp.get()
        set(value) = autoCorrectProp.set(value)

    @Internal
    var reports = DetektReports(project)

    fun reports(configure: Action<DetektReports>) = configure.execute(reports)

    @Internal
    @Optional
    val reportsDir: Property<File> = project.objects.property(File::class.java)

    val xmlReportFile: Provider<RegularFile>
        @OutputFile
        @Optional
        get() = reports.xml.getTargetFileProvider(effectiveReportsDir)

    val htmlReportFile: Provider<RegularFile>
        @OutputFile
        @Optional
        get() = reports.html.getTargetFileProvider(effectiveReportsDir)

    val txtReportFile: Provider<RegularFile>
        @OutputFile
        @Optional
        get() = reports.txt.getTargetFileProvider(effectiveReportsDir)

    private val defaultReportsDir: Directory = project.layout.buildDirectory.get()
        .dir(ReportingExtension.DEFAULT_REPORTS_DIR_NAME)
        .dir("detekt")

    private val effectiveReportsDir = project.provider { reportsDir.getOrElse(defaultReportsDir.asFile) }

    val customReports: Provider<Collection<CustomDetektReport>>
        @Nested
        get() = project.provider { reports.custom }

    init {
        group = LifecycleBasePlugin.VERIFICATION_GROUP
    }

    @TaskAction
    fun check() {
        if (plugins.isPresent && !pluginClasspath.isEmpty) {
            throw GradleException(
                "Cannot set value for plugins on detekt task and apply detektPlugins configuration " +
                        "at the same time."
            )
        }
        val xmlReportTargetFileOrNull = xmlReportFile.orNull
        val htmlReportTargetFileOrNull = htmlReportFile.orNull
        val txtReportTargetFileOrNull = txtReportFile.orNull
        val debugOrDefault = debugProp.getOrElse(false)
        val arguments = mutableListOf(
            InputArgument(source),
            ClasspathArgument(classpath),
            LanguageVersionArgument(languageVersionProp.orNull),
            JvmTargetArgument(jvmTargetProp.orNull),
            ConfigArgument(config),
            PluginsArgument(plugins.orNull),
            BaselineArgument(baseline.orNull),
            DefaultReportArgument(DetektReportType.XML, xmlReportTargetFileOrNull),
            DefaultReportArgument(DetektReportType.HTML, htmlReportTargetFileOrNull),
            DefaultReportArgument(DetektReportType.TXT, txtReportTargetFileOrNull),
            DebugArgument(debugOrDefault),
            ParallelArgument(parallelProp.getOrElse(false)),
            BuildUponDefaultConfigArgument(buildUponDefaultConfigProp.getOrElse(false)),
            FailFastArgument(failFastProp.getOrElse(false)),
            AutoCorrectArgument(autoCorrectProp.getOrElse(false)),
            DisableDefaultRuleSetArgument(disableDefaultRuleSetsProp.getOrElse(false))
        )
        arguments.addAll(customReports.get().map {
            val reportId = it.reportIdProp.orNull
            val destination = it.destinationProperty.orNull

            checkNotNull(reportId) { "If a custom report is specified, the reportId must be present" }
            check(!DetektReportType.isWellKnownReportId(reportId)) {
                "The custom report reportId may not be same as one of the default reports"
            }
            checkNotNull(destination) { "If a custom report is specified, the destination must be present" }

            CustomReportArgument(reportId, destination)
        })

        DetektInvoker.create(project).invokeCli(
            arguments = arguments.toList(),
            ignoreFailures = ignoreFailures,
            classpath = detektClasspath.plus(pluginClasspath),
            taskName = name
        )
    }
}
