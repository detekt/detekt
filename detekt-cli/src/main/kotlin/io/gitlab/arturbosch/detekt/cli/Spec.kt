package io.gitlab.arturbosch.detekt.cli

import io.github.detekt.tooling.api.spec.ProcessingSpec
import io.github.detekt.tooling.api.spec.RulesSpec
import io.github.detekt.tooling.api.spec.RulesSpec.RunPolicy.DisableDefaultRuleSets
import io.github.detekt.tooling.api.spec.RulesSpec.RunPolicy.NoRestrictions
import io.github.detekt.utils.PathFilters
import io.gitlab.arturbosch.detekt.api.RuleSet
import java.nio.file.Path
import kotlin.io.path.ExperimentalPathApi
import kotlin.io.path.absolute
import kotlin.io.path.extension
import kotlin.io.path.relativeTo
import kotlin.io.path.walk

@Suppress("LongMethod")
internal fun CliArgs.createSpec(output: Appendable, error: Appendable): ProcessingSpec {
    val args = this
    return ProcessingSpec {
        logging {
            debug = args.debug
            outputChannel = output
            errorChannel = error
        }

        project {
            basePath = args.basePath.absolute()
            val pathFilters = PathFilters.of(
                args.excludes?.let(::asPatterns).orEmpty(),
                args.includes?.let(::asPatterns).orEmpty(),
            )
            val absoluteBasePath = basePath.absolute()
            inputPaths = args.inputPaths.walk()
                .filter { path -> path.isKotlinFile() }
                .map { path -> path.absolute().relativeTo(absoluteBasePath) }
                .filter { path -> pathFilters?.isIgnored(path) != false }
                .map { path -> absoluteBasePath.resolve(path).normalize() }
                .toSet()
            analysisMode = args.analysisMode
        }

        rules {
            autoCorrect = args.autoCorrect
            activateAllRules = args.allRules
            failurePolicy = args.failurePolicy
            runPolicy = args.toRunPolicy()
        }

        baseline {
            path = args.baseline
            shouldCreateDuringAnalysis = args.createBaseline
        }

        config {
            useDefaultConfig = args.buildUponDefaultConfig
            shouldValidateBeforeAnalysis = null
            configPaths = args.config
            resources = args.configResource
        }

        execution {
            parallelParsing = args.parallel
            parallelAnalysis = args.parallel
        }

        extensions {
            disableDefaultRuleSets = args.disableDefaultRuleSets
            fromPaths { args.plugins }
        }

        reports {
            args.reportPaths.forEach {
                report { it.kind to it.path }
            }
        }

        compiler {
            jvmTarget = args.jvmTarget.toString()
            languageVersion = args.languageVersion?.versionString
            apiVersion = args.apiVersion?.versionString
            classpath = args.classpath?.trim()
            jdkHome = args.jdkHome
            freeCompilerArgs = args.freeCompilerArgs
        }
    }
}

@OptIn(ExperimentalPathApi::class)
private fun Iterable<Path>.walk(): Sequence<Path> = asSequence().flatMap { it.walk() }

private fun Path.isKotlinFile() = extension in KT_ENDINGS

private val KT_ENDINGS = setOf("kt", "kts")

private fun asPatterns(rawValue: String): List<String> = rawValue.trim()
    .splitToSequence(",", ";")
    .filter { it.isNotBlank() }
    .map { it.trim() }
    .toList()

private fun CliArgs.toRunPolicy(): RulesSpec.RunPolicy {
    val parts = runRule?.split(":")
        ?: return if (disableDefaultRuleSets) DisableDefaultRuleSets else NoRestrictions
    require(parts.size == 2) { "Pattern 'RuleSetId:RuleName' expected." }
    return RulesSpec.RunPolicy.RestrictToSingleRule(RuleSet.Id(parts[0]), parts[1])
}
