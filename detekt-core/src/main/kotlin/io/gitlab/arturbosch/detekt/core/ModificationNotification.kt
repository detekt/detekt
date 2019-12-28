package io.gitlab.arturbosch.detekt.core

import io.gitlab.arturbosch.detekt.api.Notification
import java.nio.file.Path

class ModificationNotification(path: Path) : Notification {

    override val message: String = "File $path was modified."
    override val level: Notification.Level = Notification.Level.Error
    override fun toString(): String = message
}
