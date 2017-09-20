package io.gitlab.arturbosch.detekt.api

import org.jetbrains.kotlin.cli.jvm.compiler.EnvironmentConfigFiles
import org.jetbrains.kotlin.cli.jvm.compiler.KotlinCoreEnvironment
import org.jetbrains.kotlin.com.intellij.openapi.util.Disposer
import org.jetbrains.kotlin.config.CompilerConfiguration
import org.jetbrains.kotlin.psi.KtPsiFactory

val PROJECT = KotlinCoreEnvironment.createForProduction(Disposer.newDisposable(),
		CompilerConfiguration(), EnvironmentConfigFiles.JVM_CONFIG_FILES).project

val FACTORY = KtPsiFactory(PROJECT, false)
