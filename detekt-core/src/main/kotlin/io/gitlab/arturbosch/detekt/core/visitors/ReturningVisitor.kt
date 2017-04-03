package io.gitlab.arturbosch.detekt.core.visitors

import io.gitlab.arturbosch.detekt.api.DetektVisitor
import org.jetbrains.kotlin.psi.KtFile

/**
 * @author Artur Bosch
 */
abstract class ReturningVisitor<T> : DetektVisitor() {

	protected abstract var value: T

	fun visitAndReturn(file: KtFile): T {
		file.accept(this)
		val result = value
		reset()
		return result
	}

	protected abstract fun reset()
}