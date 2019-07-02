package io.gitlab.arturbosch.detekt.api

import java.util.Properties

object DetektVersion {

    private val properties: Properties = run {
        val loaderClass = DetektVersion::class.java
        val resourceName = "/META-INF/detekt.properties"
        val resourceUrl = loaderClass.getResource(resourceName)
            ?: throw IllegalStateException("$loaderClass: Classpath resource can't be found: $resourceName")
        val connection = resourceUrl.openConnection()
        connection.useCaches = false
        connection.getInputStream().use { inputStream ->
            Properties().apply { load(inputStream) }
        }
    }

    val current: String = properties.getProperty("version")
}
