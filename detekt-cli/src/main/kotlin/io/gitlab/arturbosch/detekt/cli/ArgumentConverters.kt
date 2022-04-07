package io.gitlab.arturbosch.detekt.cli

import com.beust.jcommander.IStringConverter
import com.beust.jcommander.ParameterException
import org.jetbrains.kotlin.config.JvmTarget
import org.jetbrains.kotlin.config.LanguageVersion
import java.io.File
import java.net.URL
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

class ExistingPathConverter : IStringConverter<Path> {
    override fun convert(value: String): Path {
        require(value.isNotBlank()) { "Provided path '$value' is empty." }
        val config = File(value).toPath()
        if (Files.notExists(config)) {
            throw ParameterException("Provided path '$value' does not exist!")
        }
        return config
    }
}

class PathConverter : IStringConverter<Path> {
    override fun convert(value: String): Path {
        return Paths.get(value)
    }
}

interface DetektInputPathConverter<T> : IStringConverter<List<T>> {
    val converter: IStringConverter<T>
    override fun convert(value: String): List<T> =
        value.splitToSequence(SEPARATOR_COMMA, SEPARATOR_SEMICOLON)
            .map { it.trim() }
            .map { converter.convert(it) }
            .toList()
            .takeIf { it.isNotEmpty() }
            ?: error("Given input '$value' was impossible to parse!")
}

class MultipleClasspathResourceConverter : DetektInputPathConverter<URL> {
    override val converter = ClasspathResourceConverter()
}

class MultipleExistingPathConverter : DetektInputPathConverter<Path> {
    override val converter = ExistingPathConverter()
}

class LanguageVersionConverter : IStringConverter<LanguageVersion> {
    override fun convert(value: String): LanguageVersion =
        checkNotNull(LanguageVersion.fromFullVersionString(value)) { "Invalid value passed to --language-version" }
}

class JvmTargetConverter : IStringConverter<JvmTarget> {
    override fun convert(value: String): JvmTarget =
        checkNotNull(JvmTarget.fromString(value)) { "Invalid value passed to --jvm-target" }
}

class ClasspathResourceConverter : IStringConverter<URL> {
    override fun convert(resource: String): URL {
        val relativeResource = if (resource.startsWith("/")) resource else "/$resource"
        return javaClass.getResource(relativeResource)
            ?: throw ParameterException("Classpath resource '$resource' does not exist!")
    }
}
