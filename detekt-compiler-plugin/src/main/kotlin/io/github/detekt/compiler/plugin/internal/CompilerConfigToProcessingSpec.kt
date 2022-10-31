package io.github.detekt.compiler.plugin.internal

import io.github.detekt.compiler.plugin.Keys
import io.github.detekt.tooling.api.spec.ProcessingSpec
import org.jetbrains.kotlin.cli.common.messages.MessageCollector
import org.jetbrains.kotlin.config.CompilerConfiguration

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
    reports {
        getMap(Keys.REPORTS).forEach {
            report { Pair(it.key, it.value) }
        }
    }
    extensions {
        disableDefaultRuleSets = get(Keys.DISABLE_DEFAULT_RULE_SETS, false)
    }
    rules {
        activateAllRules = get(Keys.ALL_RULES, false)
    }
    execution {
        parallelAnalysis = get(Keys.PARALLEL, false)
        parallelParsing = get(Keys.PARALLEL, false)
    }
}
