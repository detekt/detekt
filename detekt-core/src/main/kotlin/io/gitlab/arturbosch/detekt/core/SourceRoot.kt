package io.gitlab.arturbosch.detekt.core

import java.io.File

class SourceRoot(path: String) {
	val path: String = File(path).absolutePath
}
