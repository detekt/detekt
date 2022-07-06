package io.gitlab.arturbosch.detekt.test

import org.jetbrains.kotlin.cli.jvm.compiler.KotlinCoreEnvironment
import org.jetbrains.kotlin.cli.jvm.compiler.NoScopeRecordCliBindingTrace
import org.jetbrains.kotlin.cli.jvm.compiler.TopDownAnalyzerFacadeForJVM
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.resolve.BindingContext
import org.jetbrains.kotlin.resolve.lazy.declarations.FileBasedDeclarationProviderFactory

fun KotlinCoreEnvironment.getContextForPaths(paths: List<KtFile>): BindingContext =
    TopDownAnalyzerFacadeForJVM.analyzeFilesWithJavaIntegration(
        this.project,
        paths,
        NoScopeRecordCliBindingTrace(),
        this.configuration,
        this::createPackagePartProvider,
        ::FileBasedDeclarationProviderFactory
    ).bindingContext
