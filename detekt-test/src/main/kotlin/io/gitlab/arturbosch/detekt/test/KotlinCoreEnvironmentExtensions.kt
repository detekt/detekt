package io.gitlab.arturbosch.detekt.test

import org.jetbrains.kotlin.cli.jvm.compiler.KotlinCoreEnvironment
import org.jetbrains.kotlin.cli.jvm.compiler.NoScopeRecordCliBindingTrace
import org.jetbrains.kotlin.cli.jvm.compiler.TopDownAnalyzerFacadeForJVM
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.resolve.BindingContext

fun KotlinCoreEnvironment.createBindingContext(files: List<KtFile>): BindingContext =
    TopDownAnalyzerFacadeForJVM.analyzeFilesWithJavaIntegration(
        this.project,
        files,
        NoScopeRecordCliBindingTrace(),
        this.configuration,
        this::createPackagePartProvider
    ).bindingContext
