package io.gitlab.arturbosch.detekt

import io.gitlab.arturbosch.detekt.extensions.DetektReports
import io.gitlab.arturbosch.detekt.internal.configurableFileCollection
import io.gitlab.arturbosch.detekt.internal.fileProperty
import io.gitlab.arturbosch.detekt.invoke.BaselineArgument
import io.gitlab.arturbosch.detekt.invoke.BuildUponDefaultConfigArgument
import io.gitlab.arturbosch.detekt.invoke.ConfigArgument
import io.gitlab.arturbosch.detekt.invoke.DebugArgument
import io.gitlab.arturbosch.detekt.invoke.DetektInvoker
import io.gitlab.arturbosch.detekt.invoke.DisableDefaultRuleSetArgument
import io.gitlab.arturbosch.detekt.invoke.FailFastArgument
import io.gitlab.arturbosch.detekt.invoke.HtmlReportArgument
import io.gitlab.arturbosch.detekt.invoke.InputArgument
import io.gitlab.arturbosch.detekt.invoke.ParallelArgument
import io.gitlab.arturbosch.detekt.invoke.PluginsArgument
import io.gitlab.arturbosch.detekt.invoke.XmlReportArgument
import io.gitlab.arturbosch.detekt.output.mergeXmlReports
import org.gradle.api.Action
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.file.Directory
import org.gradle.api.file.FileCollection
import org.gradle.api.file.RegularFile
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.Property
import org.gradle.api.provider.Provider
import org.gradle.api.reporting.ReportingExtension
import org.gradle.api.tasks.CacheableTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.Internal
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

    @InputFile
    @Optional
    @PathSensitive(PathSensitivity.RELATIVE)
    var baseline: RegularFileProperty = project.fileProperty()

    @InputFiles
    @Optional
    @PathSensitive(PathSensitivity.RELATIVE)
    var config: ConfigurableFileCollection = project.configurableFileCollection()

    @Input
    @Optional
    @Deprecated("Use detektPlugins within dependencies instead")
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

    init {
        group = LifecycleBasePlugin.VERIFICATION_GROUP
    }

    @TaskAction
    fun check() {
        val xmlReportTargetFile = xmlReportFile.orNull
        val debugOrDefault = debugProp.getOrElse(false)
        val arguments = mutableListOf(
            InputArgument(source),
            ConfigArgument(config),
            PluginsArgument(plugins.orNull),
            BaselineArgument(baseline.orNull),
            XmlReportArgument(xmlReportTargetFile),
            HtmlReportArgument(htmlReportFile.orNull),
            DebugArgument(debugOrDefault),
            ParallelArgument(parallelProp.getOrElse(false)),
            BuildUponDefaultConfigArgument(buildUponDefaultConfigProp.getOrElse(false)),
            FailFastArgument(failFastProp.getOrElse(false)),
            DisableDefaultRuleSetArgument(disableDefaultRuleSetsProp.getOrElse(false))
        )

        DetektInvoker.invokeCli(project, arguments.toList(), debugOrDefault)

        if (xmlReportTargetFile != null) {
            val xmlReports = project.subprojects.flatMap { subproject ->
                subproject.tasks.mapNotNull { task ->
                    if (task is Detekt) task.xmlReportFile.orNull?.asFile else null
                }
            }
            if (!xmlReports.isEmpty() && debugOrDefault) {
                logger.info("Merging report files of subprojects $xmlReports into $xmlReportTargetFile")
            }
            mergeXmlReports(xmlReportTargetFile.asFile, xmlReports)
        }
    }
}
