package io.github.detekt.compiler.plugin.internal

import io.github.detekt.compiler.plugin.Keys
import dev.detekt.tooling.api.spec.ProcessingSpec
import dev.detekt.tooling.api.spec.RulesSpec
import org.jetbrains.kotlin.cli.common.messages.MessageCollector
import org.jetbrains.kotlin.config.CompilerConfiguration
import kotlin.io.path.Path

internal fun CompilerConfiguration.toSpec(log: MessageCollector) = ProcessingSpec.invoke {
    config {
        configPaths = getList(Keys.CONFIG)
        useDefaultConfig = get(Keys.USE_DEFAULT_CONFIG, false)
    }
    baseline {
        path = get(Keys.BASELINE)
    }
    logging {
        debug = get(Keys.DEBUG, false)
        outputChannel = AppendableAdapter { log.info(it) }
        errorChannel = AppendableAdapter { log.error(it) }
    }
    project {
        basePath = get(Keys.ROOT_PATH, Path(System.getProperty("user.dir")))
    }
    reports {
        getMap(Keys.REPORTS).forEach {
            report { it.key to it.value }
        }
    }
    rules {
        activateAllRules = get(Keys.ALL_RULES, false)
        runPolicy = if (get(Keys.DISABLE_DEFAULT_RULE_SETS, false)) {
            RulesSpec.RunPolicy.DisableDefaultRuleSets
        } else {
            RulesSpec.RunPolicy.NoRestrictions
        }
    }
    execution {
        parallelAnalysis = get(Keys.PARALLEL, false)
        parallelParsing = get(Keys.PARALLEL, false)
    }
}
