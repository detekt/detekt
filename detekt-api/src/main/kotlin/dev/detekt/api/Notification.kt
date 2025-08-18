package dev.detekt.api

/**
 * Any kind of notification which should be printed to the console.
 * For example when using the ktlint rule set, any change to
 * your kotlin file is a notification.
 */
interface Notification {
    val message: String
    val level: Level
    val isError: Boolean
        get() = level == Level.Error

    /**
     * Level of severity of the notification
     */
    enum class Level {
        Info,
        Warning,
        Error,
    }
}
