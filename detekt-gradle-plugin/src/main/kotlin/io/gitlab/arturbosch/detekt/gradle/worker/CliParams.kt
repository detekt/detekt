package io.gitlab.arturbosch.detekt.gradle.worker

import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.Property
import org.gradle.workers.WorkParameters

interface CliParams : WorkParameters {

    val cliArguments: ListProperty<String>
    val ignoreFailures: Property<Boolean>
}
