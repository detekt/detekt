package dev.detekt.cli

import com.github.ajalt.clikt.core.CoreCliktCommand
import com.github.ajalt.clikt.core.FileNotFound
import com.github.ajalt.clikt.core.MissingOption
import com.github.ajalt.clikt.core.UsageError
import com.github.ajalt.clikt.core.context
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.arguments.multiple
import com.github.ajalt.clikt.parameters.options.NullableOption
import com.github.ajalt.clikt.parameters.options.OptionWithValues
import com.github.ajalt.clikt.parameters.options.convert
import com.github.ajalt.clikt.parameters.options.default
import com.github.ajalt.clikt.parameters.options.defaultLazy
import com.github.ajalt.clikt.parameters.options.flag
import com.github.ajalt.clikt.parameters.options.multiple
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.options.validate
import com.github.ajalt.clikt.parameters.types.enum
import com.github.ajalt.clikt.parameters.types.path
import dev.detekt.tooling.api.spec.RulesSpec
import dev.detekt.tooling.api.spec.RulesSpec.FailurePolicy.FailOnSeverity
import dev.detekt.tooling.api.spec.RulesSpec.FailurePolicy.NeverFail
import org.jetbrains.kotlin.config.ApiVersion
import org.jetbrains.kotlin.config.JvmTarget
import org.jetbrains.kotlin.config.LanguageVersion
import java.io.File.pathSeparator
import java.io.IOException
import java.net.URL
import java.nio.file.Path
import kotlin.io.path.Path
import kotlin.io.path.isRegularFile
import kotlin.io.path.notExists
import kotlin.io.path.readText

class CliArgs : CoreCliktCommand(name = "detekt") {

    val inputPaths: List<Path> by option(
        "-i",
        "--input",
        help = "Input paths to analyze. Multiple paths are separated by ':' on *nix or ';' on Windows. " +
            "If not specified the current working directory is used.",
    )
        .path(mustExist = true, canBeFile = true, canBeDir = true)
        .splitBatch(pathSeparator, default = { listOf(Path(System.getProperty("user.dir"))) })

    val analysisMode: AnalysisMode by option(
        "--analysis-mode",
        help = "Analysis mode used by detekt. " +
            "'full' analysis mode is comprehensive but requires the correct compiler options to be provided. " +
            "'light' analysis cannot utilise compiler information and some rules cannot be run in this mode.",
    )
        .enum<AnalysisMode>()
        .default(AnalysisMode.light)

    val includes: List<String> by option(
        "-in",
        "--includes",
        help = "Globbing patterns describing paths to include in the analysis. " +
            "Useful in combination with 'excludes' patterns.",
    )
        .splitBatch(",", ";")
        .validate { list ->
            if (list.any { it.isBlank() }) {
                fail("Value passed to --includes contains empty globs.")
            }
            if (list.any { it.trim() != it }) {
                fail("Value passed to --includes contains globs that start or end with space.")
            }
        }

    val excludes: List<String> by option(
        "-ex",
        "--excludes",
        help = "Globbing patterns describing paths to exclude from the analysis.",
    )
        .splitBatch(",", ";")
        .validate { list ->
            if (list.any { it.isBlank() }) {
                fail("Value passed to --excludes contains empty globs.")
            }
            if (list.any { it.trim() != it }) {
                fail("Value passed to --excludes contains globs that start or end with space.")
            }
        }

    val config: List<Path> by option(
        "-c",
        "--config",
        help = "Path to the config file (path/to/config.yml). " +
            "Multiple configuration files can be specified with ':' on *nix or ';' on Windows as separator.",
    )
        .path(mustExist = true, canBeFile = true, canBeDir = true)
        .splitBatch(pathSeparator)

    val configResource: List<URL> by option(
        "-cr",
        "--config-resource",
        help = "Path to the config resource on detekt's classpath (path/to/config.yml).",
    )
        .convert { resource ->
            val relativeResource = if (resource.startsWith("/")) resource else "/$resource"
            CliArgs::class.java.getResource(relativeResource)
                ?: fail("Classpath resource '$resource' does not exist!")
        }
        .splitBatch(pathSeparator)

    val generateConfig: Path? by option(
        "-gc",
        "--generate-config",
        help = "Export default config to the provided path.",
    ).path()

    val plugins: List<Path> by option(
        "-p",
        "--plugins",
        help = "Extra paths to plugin jars separated by ':' on *nix or ';' on Windows.",
    )
        .path(mustExist = true, canBeFile = true, canBeDir = true)
        .splitBatch(pathSeparator)

    val parallel: Boolean by option(
        "--parallel",
        help = "Enables parallel compilation and analysis of source files. " +
            "Do some benchmarks first before enabling this flag. " +
            "Heuristics show performance benefits starting from 2000 lines of Kotlin code.",
    ).flag()

    val baseline: Path? by option(
        "-b",
        "--baseline",
        help = "If a baseline xml file is passed in, " +
            "only new findings not in the baseline are printed in the console.",
    ).path()

    val createBaseline: Boolean by option(
        "-cb",
        "--create-baseline",
        help = "Treats current analysis findings as a smell baseline for future detekt runs.",
    ).flag()

    val reportPaths: List<ReportPath> by option(
        "-r",
        "--report",
        help = "Generates a report for given 'report-id' and stores it on given 'path'. " +
            "Entry should consist of: [report-id:path]. " +
            "Available 'report-id' values: 'checkstyle', 'html', 'md', 'sarif'. " +
            "These can also be used in combination with each other " +
            "e.g. '-r html:reports/detekt.html -r checkstyle:reports/detekt.xml'",
    )
        .convert {
            try {
                ReportPath.from(it)
            } catch (e: IllegalArgumentException) {
                fail(e.message ?: "Invalid report: $it")
            }
        }
        .multiple()

    val failOnSeverity: FailureSeverity by option(
        "--fail-on-severity",
        help = "Specifies the minimum severity that causes the build to fail. " +
            "When the value is set to 'Never' detekt will not fail regardless of the number " +
            "of issues and their severities.",
    )
        .convert {
            try {
                FailureSeverity.fromString(it)
            } catch (e: IllegalArgumentException) {
                fail(e.message ?: "Invalid severity: $it")
            }
        }
        .default(FailureSeverity.Error)

    val basePath: Path by option(
        "-bp",
        "--base-path",
        help = "Specifies a directory as the base path. " +
            "Currently it impacts all file paths in the formatted reports. " +
            "File paths in console output are not affected and remain as absolute paths.",
    )
        .path(mustExist = true, canBeFile = false, canBeDir = true)
        .defaultLazy { Path(System.getProperty("user.dir")) }

    val disableDefaultRuleSets: Boolean by option(
        "-dd",
        "--disable-default-rulesets",
        help = "Disables default rule sets.",
    ).flag()

    val buildUponDefaultConfig: Boolean by option(
        "--build-upon-default-config",
        help = "Preconfigures detekt with a bunch of rules and some opinionated defaults for you. " +
            "Allows additional provided configurations to override the defaults.",
    ).flag()

    val allRules: Boolean by option(
        "--all-rules",
        help = "Activates all available (even unstable) rules.",
    ).flag()

    val autoCorrect: Boolean by option(
        "-ac",
        "--auto-correct",
        help = "Allow rules to auto correct code if they support it. " +
            "The default rule sets do NOT support auto correcting and won't change any line in the users code base. " +
            "However custom rules can be written to support auto correcting. " +
            "The additional 'ktlint' rule set, added with '--plugins', does support it and needs this flag.",
    ).flag()

    val debug: Boolean by option(
        "--debug",
        help = "Prints extra information about configurations and extensions.",
    ).flag()

    val runRule: String? by option(
        "--run-rule",
        help = "Specify a rule by [RuleSet:RuleId] pattern and run it on input.",
        hidden = true,
    )

    val classpath: List<Path> by option(
        "-cp",
        "--classpath",
        help = "Paths where to find user class files and depending jar files. Used for type resolution.",
    )
        .path(mustExist = true, canBeFile = true, canBeDir = true)
        .splitBatch(pathSeparator)

    val apiVersion: ApiVersion? by option(
        "--api-version",
        help = "Kotlin API version used by the code under analysis. Some rules use this " +
            "information to provide more specific rule violation messages.",
    ).convert {
        val languageVersion = LanguageVersion.fromFullVersionString(it)
        if (languageVersion == null || languageVersion.isUnsupported) {
            fail("\"$it\" passed to --api-version, expected one of [$supportedLanguageVersions]")
        }
        ApiVersion.createByLanguageVersion(languageVersion)
    }

    val languageVersion: LanguageVersion? by option(
        "--language-version",
        help = "Compatibility mode for Kotlin language version X.Y, reports errors for all " +
            "language features that came out later",
    ).convert {
        val languageVersion = LanguageVersion.fromFullVersionString(it)
        if (languageVersion == null || languageVersion.isUnsupported) {
            fail("\"$it\" passed to --language-version, expected one of [$supportedLanguageVersions]")
        }
        languageVersion
    }

    val jvmTarget: JvmTarget by option(
        "--jvm-target",
        help = "Target version of the generated JVM bytecode that was generated during " +
            "compilation and is now being used for type resolution",
    ).convert {
        JvmTarget.fromString(it) ?: fail(
            "Invalid value passed to --jvm-target, expected one of [${JvmTarget.entries.joinToString()}]"
        )
    }.default(JvmTarget.DEFAULT)

    val jdkHome: Path? by option(
        "--jdk-home",
        help = "Use a custom JDK home directory to include into the classpath",
    ).path(mustExist = true, canBeFile = false, canBeDir = true)

    val showVersion: Boolean by option(
        "--version",
        help = "Prints the detekt CLI version.",
    ).flag()

    val freeCompilerArgs: List<String> by argument(
        name = "freeCompilerArgs",
        help = "Options to pass to the Kotlin compiler.",
    ).multiple()

    val failurePolicy: RulesSpec.FailurePolicy
        get() {
            return when (val minSeverity = failOnSeverity) {
                FailureSeverity.Never -> NeverFail

                FailureSeverity.Error,
                FailureSeverity.Warning,
                FailureSeverity.Info,
                -> FailOnSeverity(minSeverity.toSeverity())
            }
        }

    init {
        context {
            readArgumentFile = {
                try {
                    Path(it).readText()
                } catch (_: IOException) {
                    throw FileNotFound(it)
                }
            }
        }
    }

    override val treatUnknownOptionsAsArgs: Boolean = true

    override fun run() {
        val currentBaseline = baseline
        val violation = when {
            createBaseline && currentBaseline == null ->
                "Creating a baseline.xml requires the --baseline parameter to specify a path."

            !createBaseline && currentBaseline != null && currentBaseline.notExists() ->
                "The file specified by --baseline should exist '$currentBaseline'."

            !createBaseline && currentBaseline != null && !currentBaseline.isRegularFile() ->
                "The path specified by --baseline should be a file '$currentBaseline'."

            else -> null
        }
        if (violation != null) {
            throw UsageError(violation)
        }
    }

    companion object {
        private val supportedLanguageVersions = LanguageVersion.entries
            .filterNot(LanguageVersion::isUnsupported)
            .joinToString { it.toString() }
    }
}

internal fun <EachT : Any, ValueT> NullableOption<EachT, ValueT>.splitBatch(
    vararg delimiters: String,
    default: () -> List<ValueT> = { emptyList() },
    required: Boolean = false,
): OptionWithValues<List<ValueT>, List<ValueT>, ValueT> =
    copy(
        transformValue = transformValue,
        transformEach = { it },
        transformAll = { calls ->
            when {
                calls.isEmpty() && required -> throw MissingOption(option)
                calls.isEmpty() && !required -> default()
                else -> calls.flatten()
            }
        },
        validator = {},
        nvalues = 1..1,
        valueSplit = { it.split(*delimiters) }
    )
