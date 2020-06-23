package io.github.detekt.tooling.dsl

import io.github.detekt.tooling.api.spec.BaselineSpec
import io.github.detekt.tooling.api.spec.CompilerSpec
import io.github.detekt.tooling.api.spec.ConfigSpec
import io.github.detekt.tooling.api.spec.ExecutionSpec
import io.github.detekt.tooling.api.spec.ExtensionsSpec
import io.github.detekt.tooling.api.spec.ProcessingSpec
import io.github.detekt.tooling.api.spec.ProjectSpec
import io.github.detekt.tooling.api.spec.ReportsSpec
import io.gitlab.arturbosch.detekt.api.UnstableApi

internal fun processingSpec(init: ProcessingSpecBuilder.() -> Unit): ProcessingSpec =
    ProcessingSpecBuilder().apply(init).build()

@ProcessingModelDsl
class ProcessingSpecBuilder : Builder<ProcessingSpec> {

    var debug: Boolean = false

    @OptIn(UnstableApi::class)
    var autoCorrect: Boolean = false

    private val baseline = BaselineSpecBuilder()
    private val compiler = CompilerSpecBuilder()
    private val config = ConfigSpecBuilder()
    private val execution = ExecutionSpecBuilder()
    private val extensions = ExtensionsSpecBuilder()
    private val project = ProjectSpecBuilder()
    private val reports = ReportsSpecBuilder()

    fun baseline(init: BaselineSpecBuilder.() -> Unit): Unit = init(baseline)
    fun compiler(init: CompilerSpecBuilder.() -> Unit): Unit = init(compiler)
    fun config(init: ConfigSpecBuilder.() -> Unit): Unit = init(config)
    fun execution(init: ExecutionSpecBuilder.() -> Unit): Unit = init(execution)
    fun extensions(init: ExtensionsSpecBuilder.() -> Unit): Unit = init(extensions)
    fun project(init: ProjectSpecBuilder.() -> Unit): Unit = init(project)
    fun reports(init: ReportsSpecBuilder.() -> Unit): Unit = init(reports)

    override fun build(): ProcessingSpec = ProcessingModel(
        debug,
        autoCorrect,
        baseline.build(),
        compiler.build(),
        config.build(),
        execution.build(),
        extensions.build(),
        project.build(),
        reports.build(),
    )
}

@OptIn(UnstableApi::class)
internal data class ProcessingModel(
    override val debug: Boolean,
    override val autoCorrect: Boolean,
    override val baselineSpec: BaselineSpec,
    override val compilerSpec: CompilerSpec,
    override val configSpec: ConfigSpec,
    override val executionSpec: ExecutionSpec,
    override val extensionsSpec: ExtensionsSpec,
    override val projectSpec: ProjectSpec,
    override val reportsSpec: ReportsSpec,
) : ProcessingSpec
