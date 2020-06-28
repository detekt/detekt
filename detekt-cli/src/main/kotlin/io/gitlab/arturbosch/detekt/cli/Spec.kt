package io.gitlab.arturbosch.detekt.cli

import io.github.detekt.tooling.api.spec.MaxIssuePolicy
import io.github.detekt.tooling.api.spec.ProcessingSpec
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
            excludes = asPatterns(args.includes)
            includes = asPatterns(args.includes)
        }

        rules {
            autoCorrect = args.autoCorrect
            activateExperimentalRules = args.failFast
            policy = MaxIssuePolicy.NoneAllowed() // cli does not yet support this; specified in config
        }

        baseline {
            path = args.baseline
            shouldCreateDuringAnalysis = args.createBaseline
        }

        config {
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
            fromPaths { args.createPlugins() }
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
