package io.github.detekt.tooling.api

import io.github.detekt.test.utils.createTempFileForTest
import io.github.detekt.tooling.api.spec.BaselineSpec
import io.github.detekt.tooling.api.spec.CompilerSpec
import io.github.detekt.tooling.api.spec.ConfigSpec
import io.github.detekt.tooling.api.spec.ExecutionSpec
import io.github.detekt.tooling.api.spec.ExtensionsSpec
import io.github.detekt.tooling.api.spec.LoggingSpec
import io.github.detekt.tooling.api.spec.ProcessingSpec
import io.github.detekt.tooling.api.spec.ProjectSpec
import io.github.detekt.tooling.api.spec.ReportsSpec
import io.github.detekt.tooling.api.spec.RulesSpec
import io.gitlab.arturbosch.detekt.api.Config
import org.assertj.core.api.Assertions.assertThatCode
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import java.nio.file.Path

class DefaultConfigurationProviderSpec {

    @Nested
    inner class `default configuration` {

        @Test
        fun `loads first found instance`() {
            assertThatCode {
                DefaultConfigurationProvider.load(Spec)
                    .copy(createTempFileForTest("test", "test"))
            }.doesNotThrowAnyException()
        }
    }
}

internal class TestConfigurationProvider : DefaultConfigurationProvider {
    override fun init(spec: ProcessingSpec) {
        // no-op
    }

    override fun get(): Config = Config.empty

    override fun copy(targetLocation: Path) {
        // nothing
    }
}

private object Spec : ProcessingSpec {
    override val baselineSpec: BaselineSpec
        get() = error("No expected call")
    override val compilerSpec: CompilerSpec
        get() = error("No expected call")
    override val configSpec: ConfigSpec
        get() = error("No expected call")
    override val executionSpec: ExecutionSpec
        get() = error("No expected call")
    override val extensionsSpec: ExtensionsSpec
        get() = error("No expected call")
    override val rulesSpec: RulesSpec
        get() = error("No expected call")
    override val loggingSpec: LoggingSpec
        get() = error("No expected call")
    override val projectSpec: ProjectSpec
        get() = error("No expected call")
    override val reportsSpec: ReportsSpec
        get() = error("No expected call")
}
