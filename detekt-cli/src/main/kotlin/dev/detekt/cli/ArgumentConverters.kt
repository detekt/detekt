package dev.detekt.cli

import com.beust.jcommander.IParameterValidator
import com.beust.jcommander.IStringConverter
import com.beust.jcommander.IValueValidator
import com.beust.jcommander.ParameterException
import com.beust.jcommander.converters.IParameterSplitter
import dev.detekt.tooling.api.AnalysisMode
import org.jetbrains.kotlin.config.ApiVersion
import org.jetbrains.kotlin.config.JvmTarget
import org.jetbrains.kotlin.config.LanguageVersion
import java.net.URL
import java.nio.file.Path
import kotlin.io.path.exists
import kotlin.io.path.isDirectory

class ApiVersionConverter : IStringConverter<ApiVersion> {
    override fun convert(value: String): ApiVersion {
        val languageVersion = LanguageVersion.fromFullVersionString(value)
        requireNotNull(languageVersion) {
            val validValues = LanguageVersion.entries.joinToString { it.toString() }
            "\"$value\" passed to --api-version, expected one of [$validValues]"
        }
        return ApiVersion.createByLanguageVersion(languageVersion)
    }
}

class LanguageVersionConverter : IStringConverter<LanguageVersion> {
    override fun convert(value: String): LanguageVersion =
        requireNotNull(LanguageVersion.fromFullVersionString(value)) {
            val validValues = LanguageVersion.entries.joinToString { it.toString() }
            "\"$value\" passed to --language-version, expected one of [$validValues]"
        }
}

class JvmTargetConverter : IStringConverter<JvmTarget> {
    override fun convert(value: String): JvmTarget =
        checkNotNull(JvmTarget.fromString(value)) {
            val validValues = JvmTarget.entries.joinToString { it.toString() }
            "Invalid value passed to --jvm-target, expected one of [$validValues]"
        }
}

class ClasspathResourceConverter : IStringConverter<URL> {
    override fun convert(resource: String): URL {
        val relativeResource = if (resource.startsWith("/")) resource else "/$resource"
        return javaClass.getResource(relativeResource)
            ?: throw ParameterException("Classpath resource '$resource' does not exist!")
    }
}

class AnalysisModeConverter : IStringConverter<AnalysisMode> {
    override fun convert(value: String): AnalysisMode =
        when (value) {
            "light" -> AnalysisMode.light
            "full" -> AnalysisMode.full
            else -> throw ParameterException("Invald value $value")
        }
}

class AnalysisModeValidator : IParameterValidator {
    override fun validate(name: String, value: String) {
        if (value !in setOf("light", "full")) {
            throw ParameterException("Invalid value for $name parameter. Allowed values:[full, light]")
        }
    }
}

class FailureSeverityConverter : IStringConverter<FailureSeverity> {
    override fun convert(value: String): FailureSeverity = FailureSeverity.fromString(value)
}

class ReportPathConverter : IStringConverter<ReportPath> {
    override fun convert(value: String): ReportPath = ReportPath.from(value)
}

class PathSplitter : IParameterSplitter {
    override fun split(value: String): List<String> = value.split(',', ';')
}

class PathValidator : IValueValidator<List<Path>> {
    override fun validate(name: String, value: List<Path>) {
        value.forEach {
            if (!it.exists()) throw ParameterException("Input path does not exist: '$it'")
        }
    }
}

class DirectoryValidator : IValueValidator<Path> {
    override fun validate(name: String, value: Path) {
        if (!value.isDirectory()) throw ParameterException("Value passed to $name must be a directory.")
    }
}
