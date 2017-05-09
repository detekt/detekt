package io.gitlab.arturbosch.detekt.rules

import io.gitlab.arturbosch.detekt.rules.documentation.CommentOverPrivateProperty
import org.jetbrains.spek.subject.SubjectSpek
import org.jetbrains.spek.subject.itBehavesLike

/**
 * @author Artur Bosch
 */
class CommentOverPrivatePropertiesSpec : SubjectSpek<CommentOverPrivateProperty>({
	subject { CommentOverPrivateProperty() }
	itBehavesLike(CommonSpec())
})