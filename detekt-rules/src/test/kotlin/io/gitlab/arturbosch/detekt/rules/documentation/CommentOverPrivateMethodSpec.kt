package io.gitlab.arturbosch.detekt.rules.documentation

import io.gitlab.arturbosch.detekt.rules.CommonSpec
import org.jetbrains.spek.subject.SubjectSpek
import org.jetbrains.spek.subject.itBehavesLike

/**
 * @author Artur Bosch
 */
class CommentOverPrivateMethodSpec : SubjectSpek<CommentOverPrivateFunction>({
	subject { CommentOverPrivateFunction() }
	itBehavesLike(CommonSpec())
})
