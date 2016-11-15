package io.gitlab.arturbosch.detekt.test

import io.gitlab.arturbosch.detekt.api.Rule

/**
 * It is advisable to use this interface when using rule extensions as long rule names
 * can take to much space and make the test less readable.
 *
 * @author Artur Bosch
 */
interface RuleTest {
	val rule: Rule
}