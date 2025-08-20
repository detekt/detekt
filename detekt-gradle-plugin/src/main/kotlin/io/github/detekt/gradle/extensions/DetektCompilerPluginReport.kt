package io.github.detekt.gradle.extensions

import org.gradle.api.file.RegularFileProperty
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.Property
import javax.inject.Inject

open class DetektCompilerPluginReport @Inject constructor(val name: String, objects: ObjectFactory) {
    val enabled: Property<Boolean> = objects.property(Boolean::class.java)
    val destination: RegularFileProperty = objects.fileProperty()
}
