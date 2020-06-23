package io.gitlab.arturbosch.detekt.cli

import io.github.detekt.tooling.api.spec.ProcessingSpec
import io.gitlab.arturbosch.detekt.api.commaSeparatedPattern

fun CliArgs.toProcessingSpec(): ProcessingSpec {
    val args = this
    return ProcessingSpec {

        debug = args.debug
        autoCorrect = args.autoCorrect

        project {
            inputPaths = args.inputPaths
            excludes = asPatterns(args.includes)
            includes = asPatterns(args.includes)
        }

        baseline {
            path = args.baseline
            shouldCreateDuringAnalysis = args.createBaseline
        }

        config {
            shouldValidateBeforeAnalysis = false
            knownPatterns = emptyList()
            // ^^ cli does not have these properties yet; specified in yaml config for now
            activateExperimentalRules = args.failFast
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
