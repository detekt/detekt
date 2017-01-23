package io.gitlab.arturbosch.detekt.cli

import com.beust.jcommander.IStringConverter
import com.beust.jcommander.ParameterException
import java.net.URL

/**
 * @author Sean Flanigan <a href="mailto:sflaniga@redhat.com">sflaniga@redhat.com</a>
 */
class ClasspathResourceConverter : IStringConverter<URL> {
	override fun convert(resource: String): URL {
		val relativeResource = if (resource.startsWith("/")) resource else "/" + resource
		val url = javaClass.getResource(relativeResource) ?: throw ParameterException("Classpath resource '$resource' does not exist!")
		return url
	}
}
