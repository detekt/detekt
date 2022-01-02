package io.gitlab.arturbosch.detekt.rules.documentation

import org.jetbrains.kotlin.kdoc.psi.impl.KDocTag
import org.jetbrains.kotlin.psi.KtDeclaration
import org.jetbrains.kotlin.psi.KtFunction
import org.jetbrains.kotlin.psi.psiUtil.isPrivate

internal fun KtDeclaration.hasCommentInPrivateMember() = docComment != null && isPrivate()

internal fun KtFunction.getEntriesForTagAndSubject(tagName: String, subject: String? = null): List<KDocTag> =
    docComment?.getAllSections()?.flatMap {
        it.findTagsByName(tagName).filter { tag ->
            when (subject) {
                null -> true
                else -> tag.getSubjectName() == subject
            }
        }
    } ?: listOf()
