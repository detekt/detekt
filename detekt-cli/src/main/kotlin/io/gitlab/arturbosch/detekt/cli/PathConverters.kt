package io.gitlab.arturbosch.detekt.cli

import com.beust.jcommander.IStringConverter
import com.beust.jcommander.ParameterException
import java.net.URL
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

/**
 * @author Artur Bosch
 */
class ExistingPathConverter : IStringConverter<Path> {
	override fun convert(value: String): Path {
		val config = Paths.get(value)
		if (Files.notExists(config))
			throw ParameterException("Provided path '$value' does not exist!")
		return config
	}
}

class PathConverter : IStringConverter<Path> {
	override fun convert(value: String): Path {
		return Paths.get(value)
	}
}

/**
 * @author Sean Flanigan <a href="mailto:sflaniga@redhat.com">sflaniga@redhat.com</a>
 */
class ClasspathResourceConverter : IStringConverter<URL> {
	override fun convert(resource: String): URL {
		val relativeResource = if (resource.startsWith("/")) resource else "/" + resource
		val url = javaClass.getResource(relativeResource) ?:
				throw ParameterException("Classpath resource '$resource' does not exist!")
		return url
	}
}