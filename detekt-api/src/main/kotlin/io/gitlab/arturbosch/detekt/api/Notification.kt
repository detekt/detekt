package io.gitlab.arturbosch.detekt.api

/**
 * Any kind of notification which should be printed to the console.
 * For example when using the formatting rule set, any change to
 * your kotlin file is a notification.
 */
interface Notification {
    val message: String
    val level: Level

    /**
     * Level of severity of the notification
     */
    enum class Level {
        Warning,
        Error
    }
}
