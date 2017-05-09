package io.gitlab.arturbosch.detekt.rules

import io.gitlab.arturbosch.detekt.rules.documentation.CommentOverPrivateMethod
import org.jetbrains.spek.subject.SubjectSpek
import org.jetbrains.spek.subject.itBehavesLike

/**
 * @author Artur Bosch
 */
class CommentOverPrivateMethodSpec : SubjectSpek<CommentOverPrivateMethod>({
	subject { CommentOverPrivateMethod() }
	itBehavesLike(CommonSpec())
})