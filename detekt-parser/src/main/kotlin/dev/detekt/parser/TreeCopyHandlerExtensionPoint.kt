package dev.detekt.parser

import com.intellij.core.CoreApplicationEnvironment
import com.intellij.openapi.application.Application
import com.intellij.psi.impl.source.tree.TreeCopyHandler

/**
 * Registers the `treeCopyHandler` extension point. PSI edits such as `add`, `addBefore` and `replace` look it up
 * when they copy an element, and throw "Missing extension point: com.intellij.treeCopyHandler" if it isn't there.
 *
 * This lets autocorrect rules edit a copy of the analysed file (see `KtPsiFactory.createPhysicalFile`) through the
 * PSI API. The analysed files themselves stay read-only.
 *
 * Synchronized because the [application] is shared between analysis sessions.
 */
fun registerTreeCopyHandlerExtensionPoint(application: Application) {
    val extensionArea = application.extensionArea
    synchronized(extensionArea) {
        if (!extensionArea.hasExtensionPoint(TreeCopyHandler.EP_NAME)) {
            CoreApplicationEnvironment.registerExtensionPoint(
                extensionArea,
                TreeCopyHandler.EP_NAME,
                TreeCopyHandler::class.java,
            )
        }
    }
}
