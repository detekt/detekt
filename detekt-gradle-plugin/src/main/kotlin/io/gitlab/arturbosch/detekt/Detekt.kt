package io.gitlab.arturbosch.detekt

import io.gitlab.arturbosch.detekt.extensions.CustomDetektReport
import io.gitlab.arturbosch.detekt.extensions.DetektReportType
import io.gitlab.arturbosch.detekt.extensions.DetektReports
import io.gitlab.arturbosch.detekt.internal.configurableFileCollection
import io.gitlab.arturbosch.detekt.internal.fileProperty
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
import io.gitlab.arturbosch.detekt.invoke.ParallelArgument
import io.gitlab.arturbosch.detekt.invoke.PluginsArgument
import io.gitlab.arturbosch.detekt.output.mergeXmlReports
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
import org.gradle.language.base.plugins.LifecycleBasePlugin
import java.io.File

/**
 * @author Artur Bosch
 * @author Marvin Ramin
 * @author Markus Schwarz
 * @author Matthew Haughton
 */
@CacheableTask
open class Detekt : SourceTask() {

    @Deprecated("Replace with getSource/setSource")
    var input: FileCollection
        get() = source
        set(value) = setSource(value)

    @Input
    @Optional
    @Deprecated("Replace with setIncludes/setExcludes")
    var filters: Property<String> = project.objects.property(String::class.java)

    @Classpath
    val detektClasspath = project.configurableFileCollection()

    @Classpath
    val pluginClasspath = project.configurableFileCollection()

    @InputFile
    @Optional
    @PathSensitive(PathSensitivity.RELATIVE)
    var baseline: RegularFileProperty = project.fileProperty()

    @InputFiles
    @Optional
    @PathSensitive(PathSensitivity.RELATIVE)
    var config: ConfigurableFileCollection = project.configurableFileCollection()

    @Classpath
    @Optional
    val classpath = project.configurableFileCollection()

    @Input
    @Optional
    internal val jvmTargetProp: Property<String> = project.objects.property(String::class.javaObjectType)
    var jvmTarget: String
        @Internal
        get() = jvmTargetProp.get()
        set(value) = jvmTargetProp.set(value)

    @Input
    @Optional
    @Deprecated("Set plugins using the detektPlugins configuration " +
            "(see https://arturbosch.github.io/detekt/extensions.html#let-detekt-know-about-your-extensions)")
    var plugins: Property<String> = project.objects.property(String::class.java)

    @Internal
    @Optional
    val debugProp: Property<Boolean> = project.objects.property(Boolean::class.javaObjectType)
    var debug: Boolean
        @Internal
        get() = debugProp.get()
        set(value) = debugProp.set(value)

    @Internal
    @Optional
    val parallelProp: Property<Boolean> = project.objects.property(Boolean::class.javaObjectType)
    var parallel: Boolean
        @Internal
        get() = parallelProp.get()
        set(value) = parallelProp.set(value)

    @Optional
    @Input
    val disableDefaultRuleSetsProp: Property<Boolean> = project.objects.property(Boolean::class.javaObjectType)
    var disableDefaultRuleSets: Boolean
        @Internal
        get() = disableDefaultRuleSetsProp.get()
        set(value) = disableDefaultRuleSetsProp.set(value)

    @Optional
    @Input
    val buildUponDefaultConfigProp: Property<Boolean> = project.objects.property(Boolean::class.javaObjectType)
    var buildUponDefaultConfig: Boolean
        @Internal
        get() = buildUponDefaultConfigProp.get()
        set(value) = buildUponDefaultConfigProp.set(value)

    @Optional
    @Input
    val failFastProp: Property<Boolean> = project.objects.property(Boolean::class.javaObjectType)
    var failFast: Boolean
        @Internal
        get() = failFastProp.get()
        set(value) = failFastProp.set(value)

    @Internal
    var reports = DetektReports(project)

    fun reports(configure: Action<DetektReports>) = configure.execute(reports)

    @Internal
    @Optional
    var reportsDir: Property<File> = project.objects.property(File::class.java)

    val xmlReportFile: Provider<RegularFile>
        @OutputFile
        @Optional
        get() = reports.xml.getTargetFileProvider(effectiveReportsDir)

    val htmlReportFile: Provider<RegularFile>
        @OutputFile
        @Optional
        get() = reports.html.getTargetFileProvider(effectiveReportsDir)

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
        if (plugins.isPresent && !pluginClasspath.isEmpty)
            throw GradleException("Cannot set value for plugins on detekt task and apply detektPlugins configuration " +
                    "at the same time.")
        val xmlReportTargetFileOrNull = xmlReportFile.orNull
        val htmlReportTargetFileOrNull = htmlReportFile.orNull
        val debugOrDefault = debugProp.getOrElse(false)
        val arguments = mutableListOf(
            InputArgument(source),
            ClasspathArgument(classpath),
            JvmTargetArgument(jvmTargetProp.orNull),
            ConfigArgument(config),
            PluginsArgument(plugins.orNull),
            BaselineArgument(baseline.orNull),
            DefaultReportArgument(DetektReportType.XML, xmlReportTargetFileOrNull),
            DefaultReportArgument(DetektReportType.HTML, htmlReportTargetFileOrNull),
            DebugArgument(debugOrDefault),
            ParallelArgument(parallelProp.getOrElse(false)),
            BuildUponDefaultConfigArgument(buildUponDefaultConfigProp.getOrElse(false)),
            FailFastArgument(failFastProp.getOrElse(false)),
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

        DetektInvoker.invokeCli(project, arguments.toList(), detektClasspath.plus(pluginClasspath), debugOrDefault)

        if (xmlReportTargetFileOrNull != null) {
            val xmlReports = project.subprojects.flatMap { subproject ->
                subproject.tasks.mapNotNull { task ->
                    if (task is Detekt) task.xmlReportFile.orNull?.asFile else null
                }
            }
            if (!xmlReports.isEmpty() && debugOrDefault) {
                logger.info("Merging report files of subprojects $xmlReports into $xmlReportTargetFileOrNull")
            }
            mergeXmlReports(xmlReportTargetFileOrNull.asFile, xmlReports)
        }
    }
}
