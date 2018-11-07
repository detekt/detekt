package io.gitlab.arturbosch.detekt.cli.baseline

/**
 * @author Artur Bosch
 */
internal data class Whitelist(val sourceSetId: String?,
							  override val ids: Set<String>,
							  override val timestamp: String
) : Listing<Whitelist>
