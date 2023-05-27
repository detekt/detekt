package io.gitlab.arturbosch.detekt.rules.style

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Debt
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.Severity
import io.gitlab.arturbosch.detekt.api.config
import io.gitlab.arturbosch.detekt.api.internal.ActiveByDefault
import io.gitlab.arturbosch.detekt.api.internal.Configuration
import io.gitlab.arturbosch.detekt.api.internal.RequiresTypeResolution
import io.gitlab.arturbosch.detekt.rules.identifierName
import org.jetbrains.kotlin.backend.common.pop
import org.jetbrains.kotlin.backend.common.push
import org.jetbrains.kotlin.descriptors.ClassDescriptor
import org.jetbrains.kotlin.psi.KtClassOrObject
import org.jetbrains.kotlin.psi.KtEnumEntry
import org.jetbrains.kotlin.resolve.BindingContext
import org.jetbrains.kotlin.resolve.descriptorUtil.fqNameOrNull
import org.jetbrains.kotlin.resolve.descriptorUtil.getSuperInterfaces

/**
 * Detects enum entries declared out of alphabetical order when either the enum itself or one of its super interfaces
 * is decorated with the configured annotation. Keeping enum entries in order, where appropriate, allows for
 * easy scanning of a large enum and makes for smaller diffs and less merge conflicts in pull requests.
 *
 *  <noncompliant>
 *  @@Alphabetical
 *  enum class Fruit {
 *      BANANA, APPLE
 *  }
 *  </noncompliant>
 *  <compliant>
 *  @@Alphabetical
 *  enum class Fruit {
 *      APPLE, BANANA
 *  }
 *  </compliant>
 *  <noncompliant>
 *  @@Alphabetical
 *  interface Edible {
 *      val calories: Int
 *  }
 *  enum class Fruit(override val calories: Int): Edible {
 *      BANANA(100),
 *      APPLE(75)
 *  }
 *  </noncompliant>
 *  <compliant>
 *  @@Alphabetical
 *  interface Edible {
 *      val calories: Int
 *  }
 *  enum class Fruit(override val calories: Int): Edible {
 *      APPLE(75),
 *      BANANA(100)
 *  }
 *  </noncompliant>
 */
@RequiresTypeResolution
@ActiveByDefault(since = "1.24.0")
class EnumEntryOrder(config: Config = Config.empty) : Rule(config) {

    override val issue: Issue = Issue(
        id = "EnumEntryOrder",
        Severity.Style,
        "Enum entries are not declared in alphabetical order.",
        debt = Debt.FIVE_MINS
    )

    @Configuration(
        "A list of fully-qualified names of annotations that can decorate an enum or one of its super interfaces."
    )
    private val includeAnnotations: List<String> by config(
        listOf("io.gitlab.arturbosch.detekt.annotations.Alphabetical")
    )

    override fun visitClassOrObject(classOrObject: KtClassOrObject) {
        super.visitClassOrObject(classOrObject)

        val enumEntries = classOrObject.body?.enumEntries ?: return
        if (enumEntries.isEmpty()) {
            return
        }

        val classDescriptor = bindingContext[BindingContext.CLASS, classOrObject] ?: return

        // use BFS because we assume the annotation is more likely to be found in shallower nodes
        // when we start searching from the enum class descriptor itself
        val classWithAnnotation =
            classDescriptor.breadthFirstSearchInSuperInterfaces { it.anyIncludeAnnotations() } ?: return

        val zipped = enumEntries.sortedBy { it.identifierName() }
            .zip(enumEntries) { expected: KtEnumEntry, actual: KtEnumEntry ->
                EntryPair(expected, actual)
            }

        val firstOutOfOrder =
            zipped.firstOrNull { it.expected.identifierName() != it.actual.identifierName() } ?: return

        report(
            CodeSmell(
                issue,
                Entity.from(firstOutOfOrder.actual),
                buildMessage(classOrObject, classWithAnnotation, firstOutOfOrder)
            )
        )
    }

    private fun ClassDescriptor.breadthFirstSearchInSuperInterfaces(
        predicate: (ClassDescriptor) -> Boolean
    ): ClassDescriptor? {
        val deque = ArrayDeque(listOf(this))
        while (deque.isNotEmpty()) {
            val head = deque.pop()
            if (predicate(head)) {
                return head
            }
            for (superInterface in head.getSuperInterfaces()) {
                deque.push(superInterface)
            }
        }
        return null
    }

    private fun ClassDescriptor.anyIncludeAnnotations() =
        annotations.any { it.fqName?.asString() in includeAnnotations }

    private class EntryPair(
        val expected: KtEnumEntry,
        val actual: KtEnumEntry,
    ) {
        override fun toString(): String {
            return "EntryPair(expected=${expected.name}, actual=${actual.name})"
        }
    }

    private fun buildMessage(
        enum: KtClassOrObject,
        classWithAnnotation: ClassDescriptor,
        firstOutOfOrder: EntryPair
    ) = buildString {
        append("Entries for enum `${enum.identifierName()}` ")
        if (enum.fqName != classWithAnnotation.fqNameOrNull()) {
            append("(which implements `${classWithAnnotation.name.identifier}`) ")
        }
        append("are not declared in alphabetical order. ")
        append("Reorder so that `${firstOutOfOrder.expected.identifierName()}` ")
        append("is before `${firstOutOfOrder.actual.identifierName()}`.")
    }

    companion object {
        const val INCLUDE_ANNOTATIONS = "includeAnnotations"
    }
}
