/*
 * Copyright 2023 Google LLC
 * Copyright 2010-2023 JetBrains s.r.o. and Kotlin Programming Language contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

@file:Suppress("INVISIBLE_REFERENCE", "INVISIBLE_MEMBER")

package com.google.devtools.ksp.standalone

import com.intellij.core.CoreApplicationEnvironment
import com.intellij.openapi.module.Module
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.search.GlobalSearchScope
import org.jetbrains.kotlin.analysis.api.KaExperimentalApi
import org.jetbrains.kotlin.analysis.api.KaImplementationDetail
import org.jetbrains.kotlin.analysis.api.impl.base.util.LibraryUtils
import org.jetbrains.kotlin.analysis.api.projectStructure.KaLibraryModule
import org.jetbrains.kotlin.analysis.api.projectStructure.KaLibrarySourceModule
import org.jetbrains.kotlin.analysis.api.standalone.base.projectStructure.StandaloneProjectFactory
import org.jetbrains.kotlin.analysis.project.structure.builder.KtBinaryModuleBuilder
import org.jetbrains.kotlin.analysis.project.structure.builder.KtModuleBuilderDsl
import org.jetbrains.kotlin.analysis.project.structure.builder.KtModuleProviderBuilder
import org.jetbrains.kotlin.analysis.project.structure.impl.KaLibraryModuleImpl
import java.nio.file.Path
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

/* Copied from https://github.com/google/ksp/blob/5b2fd3c79b60f99ca643f362e564659176d900ac/kotlin-analysis-api/src/main/kotlin/com/google/devtools/ksp/standalone/KspLibraryModuleBuilder.kt
 * Required as a workaround for KT-71706.
 */
@KtModuleBuilderDsl
open class KspLibraryModuleBuilder(
    private val coreApplicationEnvironment: CoreApplicationEnvironment,
    private val project: Project,
) : KtBinaryModuleBuilder() {
    lateinit var libraryName: String
    var librarySources: KaLibrarySourceModule? = null

    override fun build(): KaLibraryModule = build(isSdk = false)

    @OptIn(KaExperimentalApi::class)
    fun build(isSdk: Boolean): KaLibraryModule {
        val binaryRoots = getBinaryRoots()
        val binaryVirtualFiles = getBinaryVirtualFiles()
        val contentScope = LibraryRootsSearchScope(
            StandaloneProjectFactory.getVirtualFilesForLibraryRoots(binaryRoots, coreApplicationEnvironment) +
                binaryVirtualFiles
        )
        return KaLibraryModuleImpl(
            directRegularDependencies,
            directDependsOnDependencies,
            directFriendDependencies,
            contentScope,
            platform,
            project,
            binaryRoots,
            binaryVirtualFiles,
            libraryName,
            librarySources,
            isSdk
        )
    }
}

@OptIn(ExperimentalContracts::class)
inline fun KtModuleProviderBuilder.buildKspLibraryModule(init: KspLibraryModuleBuilder.() -> Unit): KaLibraryModule {
    contract {
        callsInPlace(init, InvocationKind.EXACTLY_ONCE)
    }
    return KspLibraryModuleBuilder(coreApplicationEnvironment, project).apply(init).build()
}

internal class SimpleTrie(paths: List<String>) {
    class TrieNode {
        var isTerminal: Boolean = false
    }

    val root = TrieNode()

    private val m = mutableMapOf<Pair<TrieNode, String>, TrieNode>().apply {
        paths.forEach { path ->
            var p = root
            for (d in path.trim('/').split('/')) {
                p = getOrPut(p to d) { TrieNode() }
            }
            p.isTerminal = true
        }
    }

    @Suppress("ReturnCount")
    fun contains(s: String): Boolean {
        var p = root
        for (d in s.trim('/').split('/')) {
            p = m[p to d]?.also {
                if (it.isTerminal) {
                    return true
                }
            } ?: return false
        }
        return false
    }
}

internal class LibraryRootsSearchScope(roots: List<VirtualFile>) : GlobalSearchScope() {
    val trie: SimpleTrie = SimpleTrie(roots.map { it.path })

    override fun contains(file: VirtualFile): Boolean = trie.contains(file.path)

    override fun isSearchInModuleContent(aModule: Module): Boolean = false

    override fun isSearchInLibraries(): Boolean = true
}

@KtModuleBuilderDsl
class KspSdkModuleBuilder(
    coreApplicationEnvironment: CoreApplicationEnvironment,
    project: Project,
) : KspLibraryModuleBuilder(coreApplicationEnvironment, project) {
    @OptIn(KaImplementationDetail::class)
    fun addBinaryRootsFromJdkHome(jdkHome: Path, isJre: Boolean) {
        val jdkRoots = LibraryUtils.findClassesFromJdkHome(jdkHome, isJre)
        addBinaryRoots(jdkRoots)
    }

    override fun build(): KaLibraryModule = build(isSdk = true)
}

@OptIn(ExperimentalContracts::class)
inline fun KtModuleProviderBuilder.buildKspSdkModule(init: KspSdkModuleBuilder.() -> Unit): KaLibraryModule {
    contract {
        callsInPlace(init, InvocationKind.EXACTLY_ONCE)
    }
    return KspSdkModuleBuilder(coreApplicationEnvironment, project).apply(init).build()
}
