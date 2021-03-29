package io.gitlab.arturbosch.detekt.generator.collection

import io.gitlab.arturbosch.detekt.generator.collection.exception.InvalidDocumentationException

sealed class DefaultActivationStatus {
    abstract val active: Boolean
}

object Inactive : DefaultActivationStatus() {
    override val active = false
}

data class Active(val since: String) : DefaultActivationStatus() {
    override val active = true

    init {
        if (!since.matches(SEMANTIC_VERSION_PATTERN)) {
            throw InvalidDocumentationException(
                "'$since' must match the semantic version pattern <major>.<minor>.<patch>"
            )
        }
    }

    companion object {
        private val SEMANTIC_VERSION_PATTERN = """^\d+\.\d+.\d+$""".toRegex()
    }
}
