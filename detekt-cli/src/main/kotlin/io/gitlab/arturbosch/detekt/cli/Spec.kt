package io.gitlab.arturbosch.detekt.cli

import io.github.detekt.tooling.api.spec.ProcessingSpec
import io.github.detekt.tooling.api.spec.RulesSpec
import io.gitlab.arturbosch.detekt.api.commaSeparatedPattern

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
            inputPaths = args.inputPaths
            excludes = asPatterns(args.excludes)
            includes = asPatterns(args.includes)
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
            configPaths = config?.let { MultipleExistingPathConverter().convert(it) }.orEmpty()
            resources = configResource?.let { MultipleClasspathResourceConverter().convert(it) }.orEmpty()
        }

        execution {
            parallelParsing = args.parallel
            parallelAnalysis = args.parallel
        }

        extensions {
            disableDefaultRuleSets = args.disableDefaultRuleSets
            fromPaths { args.plugins?.let { MultipleExistingPathConverter().convert(it) }.orEmpty() }
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

private fun asPatterns(rawValue: String?): List<String> =
    rawValue?.trim()
        ?.commaSeparatedPattern(",", ";")
        ?.toList()
        .orEmpty()

private fun CliArgs.toRunPolicy(): RulesSpec.RunPolicy {
    val parts = runRule?.split(":") ?: return RulesSpec.RunPolicy.NoRestrictions
    require(parts.size == 2) { "Pattern 'RuleSetId:RuleId' expected." }
    return RulesSpec.RunPolicy.RestrictToSingleRule(parts[0] to parts[1])
}
