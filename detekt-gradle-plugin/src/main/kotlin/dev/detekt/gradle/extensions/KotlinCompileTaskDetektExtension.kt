package dev.detekt.gradle.extensions

import org.gradle.api.NamedDomainObjectContainer
import org.gradle.api.Project
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.Property
import org.gradle.api.provider.SetProperty

open class KotlinCompileTaskDetektExtension(project: Project) {
    val reports: NamedDomainObjectContainer<DetektCompilerPluginReport> =
        project.container(DetektCompilerPluginReport::class.java)

    init {
        reports.create("xml")
        reports.create("html")
        reports.create("sarif")
    }

    private val objects: ObjectFactory = project.objects

    val isEnabled: Property<Boolean> = objects.property(Boolean::class.java)
    val debug: Property<Boolean> = objects.property(Boolean::class.java)
    val buildUponDefaultConfig: Property<Boolean> = objects.property(Boolean::class.java)
    val allRules: Property<Boolean> = objects.property(Boolean::class.java)
    val disableDefaultRuleSets: Property<Boolean> = objects.property(Boolean::class.java)
    val parallel: Property<Boolean> = objects.property(Boolean::class.java)

    val baseline: RegularFileProperty = objects.fileProperty()
    val config: ConfigurableFileCollection = objects.fileCollection()
    val excludes: SetProperty<String> = objects.setProperty(String::class.java)

    fun getXml(): DetektCompilerPluginReport = reports.getByName("xml")
    fun getHtml(): DetektCompilerPluginReport = reports.getByName("html")
    fun getSarif(): DetektCompilerPluginReport = reports.getByName("sarif")
}
