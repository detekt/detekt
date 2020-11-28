package io.gitlab.arturbosch.detekt.cli

import com.beust.jcommander.Parameter
import org.jetbrains.kotlin.config.JvmTarget
import org.jetbrains.kotlin.config.LanguageVersion
import java.nio.file.Path

class CliArgs {

    @Parameter(
        names = ["--input", "-i"],
        description = "Input paths to analyze. Multiple paths are separated by comma. If not specified the " +
            "current working directory is used."
    )
    var input: String? = null

    @Parameter(
        names = ["--includes", "-in"],
        description = "Globing patterns describing paths to include in the analysis. " +
            "Useful in combination with 'excludes' patterns."
    )
    var includes: String? = null

    @Parameter(
        names = ["--excludes", "-ex"],
        description = "Globing patterns describing paths to exclude from the analysis."
    )
    var excludes: String? = null

    @Parameter(
        names = ["--config", "-c"],
        description = "Path to the config file (path/to/config.yml). " +
            "Multiple configuration files can be specified with ',' or ';' as separator."
    )
    var config: String? = null

    @Parameter(
        names = ["--config-resource", "-cr"],
        description = "Path to the config resource on detekt's classpath (path/to/config.yml)."
    )
    var configResource: String? = null

    @Parameter(
        names = ["--generate-config", "-gc"],
        description = "Export default config. " +
            "Path can be specified with --config option (default path: default-detekt-config.yml)"
    )
    var generateConfig: Boolean = false

    @Parameter(
        names = ["--plugins", "-p"],
        description = "Extra paths to plugin jars separated by ',' or ';'."
    )
    var plugins: String? = null

    @Parameter(
        names = ["--parallel"],
        description = "Enables parallel compilation and analysis of source files." +
            " Do some benchmarks first before enabling this flag." +
            " Heuristics show performance benefits starting from 2000 lines of Kotlin code."
    )
    var parallel: Boolean = false

    @Parameter(
        names = ["--baseline", "-b"],
        description = "If a baseline xml file is passed in," +
            " only new code smells not in the baseline are printed in the console.",
        converter = PathConverter::class
    )
    var baseline: Path? = null

    @Parameter(
        names = ["--create-baseline", "-cb"],
        description = "Treats current analysis findings as a smell baseline for future detekt runs."
    )
    var createBaseline: Boolean = false

    @Parameter(
        names = ["--report", "-r"],
        description = "Generates a report for given 'report-id' and stores it on given 'path'. " +
            "Entry should consist of: [report-id:path]. " +
            "Available 'report-id' values: 'txt', 'xml', 'html'. " +
            "These can also be used in combination with each other " +
            "e.g. '-r txt:reports/detekt.txt -r xml:reports/detekt.xml'"
    )
    private var reports: List<String>? = null

    @Parameter(
        names = ["--disable-default-rulesets", "-dd"],
        description = "Disables default rule sets."
    )
    var disableDefaultRuleSets: Boolean = false

    @Parameter(
        names = ["--build-upon-default-config"],
        description = "Preconfigures detekt with a bunch of rules and some opinionated defaults for you. " +
            "Allows additional provided configurations to override the defaults."
    )
    var buildUponDefaultConfig: Boolean = false

    @Parameter(
        names = ["--fail-fast"],
        description = "Same as 'build-upon-default-config' but explicitly running all available rules. " +
            "With this setting only exit code 0 is returned when the analysis does not find a single code smell. " +
            "Additional configuration files can override rule properties which includes turning off specific rules."
    )
    var failFast: Boolean = false

    @Parameter(
        names = ["--auto-correct", "-ac"],
        description = "Allow rules to auto correct code if they support it. " +
            "The default rule sets do NOT support auto correcting and won't change any line in the users code base. " +
            "However custom rules can be written to support auto correcting. " +
            "The additional 'formatting' rule set, added with '--plugins', does support it and needs this flag."
    )
    var autoCorrect: Boolean = false

    @Parameter(
        names = ["--debug"],
        description = "Prints extra information about configurations and extensions."
    )
    var debug: Boolean = false

    @Parameter(
        names = ["--help", "-h"],
        help = true, description = "Shows the usage."
    )
    var help: Boolean = false

    @Parameter(
        names = ["--run-rule"],
        description = "Specify a rule by [RuleSet:Rule] pattern and run it on input.",
        hidden = true
    )
    var runRule: String? = null

    @Parameter(
        names = ["--print-ast"],
        description = "Prints the AST for given [input] file. Must be no directory.",
        hidden = true
    )
    var printAst: Boolean = false

    /*
        The following @Parameters are used for type resolution. When additional parameters are required the
        names should mirror the names found in this file (e.g. "classpath", "language-version", "jvm-target"):
        https://github.com/JetBrains/kotlin/blob/master/compiler/cli/cli-common/src/org/jetbrains/kotlin/cli/common/arguments/K2JVMCompilerArguments.kt
    */
    @Parameter(
        names = ["--classpath", "-cp"],
        description = "EXPERIMENTAL: Paths where to find user class files and depending jar files. " +
            "Used for type resolution."
    )
    var classpath: String? = null

    @Parameter(
        names = ["--language-version"],
        converter = LanguageVersionConverter::class,
        description = "EXPERIMENTAL: Compatibility mode for Kotlin language version X.Y, reports errors for all " +
            "language features that came out later (1.0, 1.1, 1.2, 1.3, 1.4)"
    )
    var languageVersion: LanguageVersion? = null

    @Parameter(
        names = ["--jvm-target"],
        converter = JvmTargetConverter::class,
        description = "EXPERIMENTAL: Target version of the generated JVM bytecode that was generated during " +
            "compilation and is now being used for type resolution (1.6, 1.8, 9, 10, 11 or 12)"
    )
    var jvmTarget: JvmTarget = JvmTarget.DEFAULT

    @Parameter(
        names = ["--version"],
        description = "Prints the detekt CLI version."
    )
    var showVersion: Boolean = false

    val inputPaths: List<Path> by lazy {
        MultipleExistingPathConverter().convert(input ?: System.getProperty("user.dir"))
    }

    val reportPaths: List<ReportPath> by lazy {
        reports?.map { ReportPath.from(it) } ?: emptyList()
    }

    companion object {
        /**
         * When embedding the cli inside a tool, this closure style configuration
         * of the arguments should be used.
         */
        operator fun invoke(init: CliArgs.() -> Unit): CliArgs = CliArgs().apply(init)
    }
}
