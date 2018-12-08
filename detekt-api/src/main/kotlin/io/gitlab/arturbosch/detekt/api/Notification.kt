package io.gitlab.arturbosch.detekt.api

/**
 * Any kind of notification which should be printed to the console.
 * For example when using the formatting rule set, any change to
 * your kotlin file is a notification.
 *
 * @author Artur Bosch
 */
interface Notification {
	val message: String
}
