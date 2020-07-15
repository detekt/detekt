package io.github.detekt.test.utils

import org.jetbrains.kotlin.cli.jvm.compiler.KotlinCoreEnvironment
import org.jetbrains.kotlin.com.intellij.openapi.Disposable
import org.jetbrains.kotlin.com.intellij.openapi.util.Disposer
import org.jetbrains.kotlin.psi.KtPsiFactory

/**
 * Make sure to always call [dispose] or use a [use] block when working with [KotlinCoreEnvironment]s.
 */
class KotlinCoreEnvironmentWrapper(
    private var environment: KotlinCoreEnvironment?,
    private val disposable: Disposable
) {

    val env: KotlinCoreEnvironment
        get() = checkNotNull(environment) { "Environment already disposed." }

    fun dispose() {
        Disposer.dispose(disposable)
        environment = null
    }
}

fun createEnvironment(): KotlinCoreEnvironmentWrapper = KtTestCompiler.createEnvironment()

fun createPsiFactory(): KtPsiFactory = KtPsiFactory(KtTestCompiler.project(), false)
