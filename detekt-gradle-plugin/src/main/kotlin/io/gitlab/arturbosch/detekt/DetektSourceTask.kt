package io.gitlab.arturbosch.detekt

import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Classpath
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.PathSensitive
import org.gradle.api.tasks.PathSensitivity
import org.gradle.api.tasks.SourceTask
import org.gradle.language.base.plugins.LifecycleBasePlugin
import org.gradle.work.DisableCachingByDefault

@DisableCachingByDefault(because = "Abstract super-class, not to be instantiated directly")
abstract class DetektSourceTask : SourceTask(), DetektTask {
    @get:Classpath
    @get:Optional
    abstract val classpath: ConfigurableFileCollection

    @get:Input
    @get:Optional
    internal abstract val jvmTargetProp: Property<String>
    var jvmTarget: String
        @Internal
        get() = jvmTargetProp.get()
        set(value) = jvmTargetProp.set(value)

    @get:InputDirectory
    @get:PathSensitive(PathSensitivity.ABSOLUTE)
    @get:Optional
    abstract val jdkHome: DirectoryProperty

    @get:InputFiles
    @get:Optional
    @get:PathSensitive(PathSensitivity.RELATIVE)
    abstract val config: ConfigurableFileCollection

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

    init {
        group = LifecycleBasePlugin.VERIFICATION_GROUP
    }
}
