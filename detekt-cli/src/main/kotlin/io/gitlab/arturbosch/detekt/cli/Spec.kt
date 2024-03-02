package io.gitlab.arturbosch.detekt.cli

import io.github.detekt.tooling.api.spec.ProcessingSpec
import io.github.detekt.tooling.api.spec.RulesSpec
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.RuleSet
import io.gitlab.arturbosch.detekt.api.internal.PathFilters
import java.nio.file.Path
import kotlin.io.path.ExperimentalPathApi
import kotlin.io.path.absolute
import kotlin.io.path.extension
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
            basePath = args.basePath
            val pathFilters = PathFilters.of(
                args.excludes?.let(::asPatterns).orEmpty(),
                args.includes?.let(::asPatterns).orEmpty(),
            )
            inputPaths = args.inputPaths.walk()
                .map { it.absolute().normalize() }
                .filter { pathFilters?.isIgnored(it) != false }
                .filter { path ->
                    path.isKotlinFile()
                        .also {
                            if (!it && args.debug) output.appendLine("Ignoring a file detekt cannot handle: $path")
                        }
                }
                .toSet()
        }

        rules {
            autoCorrect = args.autoCorrect
            activateAllRules = args.allRules
            failurePolicy = args.failurePolicy
            excludeCorrectable = false // not yet supported; loaded from config
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
            classpath = args.classpath?.trim()
            jdkHome = args.jdkHome
        }
    }
}

@OptIn(ExperimentalPathApi::class)
private fun Iterable<Path>.walk(): Sequence<Path> {
    return asSequence().flatMap { it.walk() }
}

private fun Path.isKotlinFile() = extension in KT_ENDINGS

private val KT_ENDINGS = setOf("kt", "kts")

private fun asPatterns(rawValue: String): List<String> = rawValue.trim()
    .splitToSequence(",", ";")
    .filter { it.isNotBlank() }
    .map { it.trim() }
    .toList()

private fun CliArgs.toRunPolicy(): RulesSpec.RunPolicy {
    val parts = runRule?.split(":") ?: return RulesSpec.RunPolicy.NoRestrictions
    require(parts.size == 2) { "Pattern 'RuleSetId:RuleId' expected." }
    return RulesSpec.RunPolicy.RestrictToSingleRule(RuleSet.Id(parts[0]), Rule.Id(parts[1]))
}
