package dev.detekt.api

import dev.drewhamilton.poko.Poko

/**
 * Any kind of notification which should be printed to the console.
 * For example when using the ktlint rule set, any change to
 * your kotlin file is a notification.
 */
@Poko
class Notification(val message: String, val level: Level) {

    /**
     * Level of severity of the notification
     */
    enum class Level {
        Info,
        Warning,
        Error,
    }

    override fun toString() = "$level: $message"
}
