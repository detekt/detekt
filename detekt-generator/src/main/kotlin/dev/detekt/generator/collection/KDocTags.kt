package dev.detekt.generator.collection

import org.jetbrains.kotlin.kdoc.psi.impl.KDocSection
import org.jetbrains.kotlin.psi.KtClassOrObject

fun KtClassOrObject.kDocSection(): KDocSection? = docComment?.getDefaultSection()

fun KtClassOrObject.hasKDocTag(tagName: String) = kDocSection()?.findTagByName(tagName) != null

fun KtClassOrObject.hasConfigurationKDocTag() = hasKDocTag(TAG_CONFIGURATION)

private const val TAG_CONFIGURATION = "configuration"
