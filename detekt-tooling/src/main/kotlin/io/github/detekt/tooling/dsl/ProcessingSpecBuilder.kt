package io.github.detekt.tooling.dsl

import io.github.detekt.tooling.api.spec.BaselineSpec
import io.github.detekt.tooling.api.spec.CompilerSpec
import io.github.detekt.tooling.api.spec.ConfigSpec
import io.github.detekt.tooling.api.spec.ExecutionSpec
import io.github.detekt.tooling.api.spec.ExtensionsSpec
import io.github.detekt.tooling.api.spec.RulesSpec
import io.github.detekt.tooling.api.spec.LoggingSpec
import io.github.detekt.tooling.api.spec.ProcessingSpec
import io.github.detekt.tooling.api.spec.ProjectSpec
import io.github.detekt.tooling.api.spec.ReportsSpec

internal fun processingSpec(init: ProcessingSpecBuilder.() -> Unit): ProcessingSpec =
    ProcessingSpecBuilder().apply(init).build()

@ProcessingModelDsl
class ProcessingSpecBuilder : Builder<ProcessingSpec> {

    private val baseline = BaselineSpecBuilder()
    private val compiler = CompilerSpecBuilder()
    private val config = ConfigSpecBuilder()
    private val execution = ExecutionSpecBuilder()
    private val extensions = ExtensionsSpecBuilder()
    private val issues = RulesSpecBuilder()
    private val logging = LoggingSpecBuilder()
    private val project = ProjectSpecBuilder()
    private val reports = ReportsSpecBuilder()

    fun baseline(init: BaselineSpecBuilder.() -> Unit): Unit = init(baseline)
    fun compiler(init: CompilerSpecBuilder.() -> Unit): Unit = init(compiler)
    fun config(init: ConfigSpecBuilder.() -> Unit): Unit = init(config)
    fun execution(init: ExecutionSpecBuilder.() -> Unit): Unit = init(execution)
    fun extensions(init: ExtensionsSpecBuilder.() -> Unit): Unit = init(extensions)
    fun rules(init: RulesSpecBuilder.() -> Unit): Unit = init(issues)
    fun logging(init: LoggingSpecBuilder.() -> Unit): Unit = init(logging)
    fun project(init: ProjectSpecBuilder.() -> Unit): Unit = init(project)
    fun reports(init: ReportsSpecBuilder.() -> Unit): Unit = init(reports)

    override fun build(): ProcessingSpec = ProcessingModel(
        baseline.build(),
        compiler.build(),
        config.build(),
        execution.build(),
        extensions.build(),
        issues.build(),
        logging.build(),
        project.build(),
        reports.build()
    )
}

internal data class ProcessingModel(
    override val baselineSpec: BaselineSpec,
    override val compilerSpec: CompilerSpec,
    override val configSpec: ConfigSpec,
    override val executionSpec: ExecutionSpec,
    override val extensionsSpec: ExtensionsSpec,
    override val rulesSpec: RulesSpec,
    override val loggingSpec: LoggingSpec,
    override val projectSpec: ProjectSpec,
    override val reportsSpec: ReportsSpec
) : ProcessingSpec
