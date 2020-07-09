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
            inputPaths = args.inputPaths
            excludes = asPatterns(args.excludes)
            includes = asPatterns(args.includes)
        }

        rules {
            autoCorrect = args.autoCorrect
            activateExperimentalRules = args.failFast
            maxIssuePolicy = RulesSpec.MaxIssuePolicy.NonSpecified // not yet supported; prefer to read from config
            excludeCorrectable = false // not yet supported; loaded from config
            runPolicy = args.toRunPolicy()
        }

        baseline {
            path = args.baseline
            shouldCreateDuringAnalysis = args.createBaseline
        }

        config {
            useDefaultConfig = args.buildUponDefaultConfig
            shouldValidateBeforeAnalysis = false
            knownPatterns = emptyList()
            // ^^ cli does not have these properties yet; specified in yaml config for now
            configPaths = config?.let { MultipleExistingPathConverter().convert(it) } ?: emptyList()
            resources = configResource?.let { MultipleClasspathResourceConverter().convert(it) } ?: emptyList()
        }

        execution {
            parallelParsing = args.parallel
            parallelAnalysis = args.parallel
        }

        extensions {
            disableDefaultRuleSets = args.disableDefaultRuleSets
            fromPaths { args.plugins?.let { MultipleExistingPathConverter().convert(it) } ?: emptyList() }
        }

        reports {
            args.reportPaths.forEach {
                report { it.kind to it.path }
            }
        }

        compiler {
            jvmTarget = args.jvmTarget.description
            languageVersion = args.languageVersion?.versionString
            classpath = args.classpath?.trim()
        }
    }
}

private fun asPatterns(rawValue: String?): List<String> =
    rawValue?.trim()
        ?.commaSeparatedPattern(",", ";")
        ?.toList()
        ?: emptyList()

private fun CliArgs.toRunPolicy(): RulesSpec.RunPolicy {
    val parts = runRule?.split(":") ?: return RulesSpec.RunPolicy.NoRestrictions
    require(parts.size == 2) { "Pattern 'RuleSetId:RuleId' expected." }
    return RulesSpec.RunPolicy.RestrictToSingleRule(parts[0] to parts[1])
}
