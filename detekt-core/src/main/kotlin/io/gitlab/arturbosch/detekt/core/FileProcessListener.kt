package io.gitlab.arturbosch.detekt.core

import org.jetbrains.kotlin.com.intellij.openapi.util.Key

/**
 * @author Artur Bosch
 */
val COMPLEXITY_KEY = Key<Int>("complexity")
val LLOC_KEY = Key<Int>("lloc")
val NUMBER_OF_CLASSES_KEY = Key<Int>("number of classes")
val NUMBER_OF_METHODS_KEY = Key<Int>("number of methods")
val NUMBER_OF_FIELDS_KEY = Key<Int>("number of fields")
val NUMBER_OF_FILES_KEY = Key<Int>("number of kt files")
val NUMBER_OF_PACKAGES_KEY = Key<String>("number of packages")
