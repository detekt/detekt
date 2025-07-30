package dev.detekt.test.utils

import com.intellij.openapi.project.Project
import org.jetbrains.kotlin.config.CompilerConfiguration

class KotlinEnvironmentContainer(val project: Project, val configuration: CompilerConfiguration)
