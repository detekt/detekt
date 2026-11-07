package dev.detekt.rules.style

import dev.detekt.api.Config
import dev.detekt.test.assertj.assertThat
import dev.detekt.test.junit.KotlinCoreEnvironmentTest
import dev.detekt.test.lintWithContext
import dev.detekt.test.utils.KotlinAnalysisApiEngine
import dev.detekt.test.utils.KotlinEnvironmentContainer
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.intellij.lang.annotations.Language
import org.jetbrains.kotlin.config.ApiVersion
import org.jetbrains.kotlin.config.LanguageFeature
import org.jetbrains.kotlin.config.LanguageVersion
import org.jetbrains.kotlin.config.LanguageVersionSettingsImpl
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

@KotlinCoreEnvironmentTest
class UseContextSensitiveResolutionSpec(private val env: KotlinEnvironmentContainer) {
    private val subject = UseContextSensitiveResolution(Config.empty)

    @Nested
    inner class `function arguments` {
        @Test
        fun `reports enum entry as a positional argument`() =
            assertReported("fun foo() = takesRole(Role.Admin)", "Role.Admin" to "Admin")

        @Test
        fun `reports enum entry as a named argument`() =
            assertReported("fun foo() = takesRole(role = Role.Admin)", "Role.Admin" to "Admin")

        @Test
        fun `reports enum entry as a constructor argument`() =
            assertReported("fun foo() = User(Role.Admin)", "Role.Admin" to "Admin")

        @Test
        fun `reports fully qualified enum entry`() =
            assertReported("fun foo() = takesRole(com.example.Role.Admin)", "com.example.Role.Admin" to "Admin")

        @Test
        fun `reports sealed object as an argument`() =
            assertReported("fun foo() = takesState(State.Loading)", "State.Loading" to "Loading")

        @Test
        fun `reports generic sealed object as an argument`() =
            assertReported("fun foo() = takesResponse(Response.Empty)", "Response.Empty" to "Empty")

        @Test
        fun `reports companion property as an argument`() =
            assertReported("fun foo() = takesState(State.Default)", "State.Default" to "Default")

        @Test
        fun `reports enum entry as a Java argument`() =
            assertReported(
                "fun foo(unit: java.util.concurrent.TimeUnit) = unit.convert(1, java.util.concurrent.TimeUnit.SECONDS)",
                "java.util.concurrent.TimeUnit.SECONDS" to "SECONDS",
            )

        @Test
        fun `reports enum entry in an assertion`() =
            assertReported(
                "fun foo(role: Role) = Assert(role).isEqualTo(Role.Admin)",
                "Role.Admin" to "Admin",
            )

        @Test
        fun `reports sealed object in an assertion on a property`() =
            assertReported(
                "fun foo(screen: Assert<Screen>) = screen.prop(Screen::state).isEqualTo(State.Loading)",
                "State.Loading" to "Loading",
            )

        @Test
        fun `reports enum entry with explicit type arguments`() =
            assertReported("fun foo() = listOf<Role>(Role.Admin)", "Role.Admin" to "Admin")

        @Test
        fun `reports enum entry in an argument branch`() =
            assertReported(
                "fun foo(set: Boolean) = takesRole(if (set) Role.Admin else Role.Basic)",
                "Role.Admin" to "Admin",
                "Role.Basic" to "Basic",
            )

        @Test
        fun `does not report sealed object in an argument branch`() =
            assertNotReported(
                "fun foo(set: Boolean) = takesResponse(if (set) Response.Success(1) else Response.Empty)",
                "Response.Empty" to "Empty",
            )

        @Test
        fun `does not report an if branch in an argument that decides the type`() =
            assertNotReported(
                "fun foo(set: Boolean) = listOf(if (set) Role.Admin else Role.Basic)",
                "Role.Admin" to "Admin",
                "Role.Basic" to "Basic",
                together = true,
            )

        @Test
        fun `does not report a when branch in an argument that decides the type`() =
            assertNotReported(
                """
                fun foo(set: Boolean) = listOf(
                    when {
                        set -> Role.Admin
                        else -> Role.Basic
                    }
                )
                """.trimIndent(),
                "Role.Admin" to "Admin",
                "Role.Basic" to "Basic",
                together = true,
            )

        @Test
        fun `does not report a companion property in an argument branch`() =
            assertNotReported("fun foo(set: Boolean) = takesColor(if (set) Color.Red else Color.Green)")

        @Test
        fun `does not report when the argument decides the type`() =
            assertNotReported("fun foo() = listOf(Role.Admin)", "Role.Admin" to "Admin")

        @Test
        fun `reports enum entries when another argument decides the type`() =
            assertReported(
                "fun foo() = listOf(Role.Admin, makeRole(), Role.Basic)",
                "Role.Admin" to "Admin",
                "Role.Basic" to "Basic",
            )

        @Test
        fun `reports enum entry when a nullable argument decides the type`() =
            assertReported("fun foo(role: Role?) = listOf(Role.Admin, role)", "Role.Admin" to "Admin")

        @Test
        fun `does not report when another argument decides a subtype`() =
            assertNotReported(
                "fun foo(failure: State.Failure) = listOf(State.Loading, failure)",
                "State.Loading" to "Loading",
            )

        @Test
        fun `does not report when the other argument is a branch of qualified names`() =
            assertNotReported(
                "fun foo(set: Boolean) = listOf(Role.Admin, if (set) Role.Basic else Role.Admin)",
                "Role.Admin" to "Admin",
                "Role.Basic" to "Basic",
                together = true,
            )

        @Test
        fun `does not report when a constructor argument decides the type`() =
            assertNotReported("fun foo() = Assert(Role.Admin)", "Role.Admin" to "Admin")

        @Test
        fun `does not report when overloads disagree on the type`() =
            assertNotReported("fun foo() = overloaded(Role.Admin)", "Role.Admin" to "Admin")

        @Test
        fun `reports a named argument that only one overload has`() =
            assertReported("fun foo() = overloaded(role = Role.Admin)", "Role.Admin" to "Admin")

        @Test
        fun `does not report a positional argument when an overload takes another type first`() =
            assertNotReported(
                """
                fun choose(value: Role, count: Int) = value
                fun choose(value: String, count: Int) = value

                fun foo() = choose(Role.Admin, 1)
                """.trimIndent(),
                "Role.Admin" to "Admin",
            )

        @Test
        fun `does not report a named argument when an overload has the same parameter with another type`() =
            assertNotReported(
                """
                fun choose(value: Role, count: Int) = value
                fun choose(value: String, count: Int) = value

                fun foo() = choose(value = Role.Admin, count = 1)
                """.trimIndent(),
                "Role.Admin" to "Admin",
            )

        @Test
        fun `does not report an infix call argument`() =
            assertNotReported("fun foo() = 1 to Role.Admin", "Role.Admin" to "Admin")

        @Test
        fun `does not report a sealed object nested more than one level down`() =
            assertNotReported(
                """
                sealed interface Event {
                    sealed interface Error : Event {
                        data object Timeout : Error
                    }
                }

                fun takesEvent(event: Event) = event

                fun foo() = takesEvent(Event.Error.Timeout)
                """.trimIndent(),
                "Event.Error.Timeout" to "Timeout",
            )

        @Test
        fun `does not report a companion property of another type`() =
            assertNotReported("fun foo() = takesRole(Color.Fallback)", "Color.Fallback" to "Fallback")

        @Test
        fun `does not report a companion property of a supertype`() =
            assertNotReported(
                "fun foo() = alignHorizontally(Alignment.CenterHorizontally)",
                "Alignment.CenterHorizontally" to "CenterHorizontally",
            )

        @Test
        fun `reports a companion property of a sealed supertype the type is nested in`() =
            assertReported(
                "fun foo() = arrangeHorizontally(Arrangement.SpaceEvenly)",
                "Arrangement.SpaceEvenly" to "SpaceEvenly",
            )

        @Test
        fun `reports a sealed object of a sealed supertype the type is nested in`() =
            assertReported("fun foo() = takesRound(Shape.Circle)", "Shape.Circle" to "Circle")

        @Test
        fun `does not report a companion property of a sealed supertype the type is not nested in`() =
            assertNotReported(
                """
                sealed interface Direction {
                    companion object {
                        val Default: Up = Up()
                    }
                }

                class Up : Direction

                fun takesUp(up: Up) = up

                fun foo() = takesUp(Direction.Default)
                """.trimIndent(),
                "Direction.Default" to "Default",
            )

        @Test
        fun `does not report a companion property of a sealed class the type is nested in but does not extend`() =
            assertNotReported(
                """
                sealed interface Layout {
                    class Grid
                    companion object {
                        val Default: Grid = Grid()
                    }
                }

                fun takesGrid(grid: Layout.Grid) = grid

                fun foo() = takesGrid(Layout.Default)
                """.trimIndent(),
                "Layout.Default" to "Default",
            )
    }

    @Nested
    inner class `operators and elvis` {
        @Test
        fun `reports enum entry in an equality check`() =
            assertReported(
                "fun foo(role: Role) = role == Role.Admin || role != Role.Basic",
                "Role.Admin" to "Admin",
                "Role.Basic" to "Basic",
            )

        @Test
        fun `does not report enum entry on the left of an equality check`() =
            assertNotReported("fun foo(role: Role) = Role.Admin == role", "Role.Admin" to "Admin")

        @Test
        fun `reports enum entry compared to a smart cast value`() =
            assertReported("fun foo(any: Any) = any is Role && any == Role.Admin", "Role.Admin" to "Admin")

        @Test
        fun `reports enum entry compared to a nullable value`() =
            assertReported("fun foo(role: Role?) = role == Role.Admin", "Role.Admin" to "Admin")

        @Test
        fun `reports enum entry in a comparison`() =
            assertReported("fun foo(role: Role) = role > Role.Admin", "Role.Admin" to "Admin")

        @Test
        fun `reports enum entry in an in check`() =
            assertReported("fun foo(roles: Set<Role>) = Role.Admin in roles", "Role.Admin" to "Admin")

        @Test
        fun `reports enum entry in an infix call`() =
            assertReported("fun foo(role: Role) = role sameAs Role.Admin", "Role.Admin" to "Admin")

        @Test
        fun `reports companion property as an operator argument`() =
            assertReported("fun foo(color: Color) = color + Color.Red", "Color.Red" to "Red")

        @Test
        fun `does not report the receiver of an operator call`() =
            assertNotReported("fun foo(color: Color) = Color.Red + color", "Color.Red" to "Red")

        @Test
        fun `does not report a compound assignment`() =
            assertNotReported(
                "fun foo(roles: MutableList<Role>) { roles += Role.Admin }",
                "Role.Admin" to "Admin",
            )

        @Test
        fun `reports an elvis operand with no expected type`() =
            assertReported("fun foo(role: Role?) = role ?: Role.Admin", "Role.Admin" to "Admin")

        @Test
        fun `reports an elvis operand in an argument that decides the type`() =
            assertReported("fun foo(role: Role?) = listOf(role ?: Role.Admin)", "Role.Admin" to "Admin")

        @Test
        fun `does not report an elvis operand of another type`() =
            assertNotReported("fun foo(any: Any?) = any ?: Role.Admin", "Role.Admin" to "Admin")

        @Test
        fun `does not report an elvis operand that is a sibling of the left side's sealed subtype`() =
            assertNotReported(
                "fun foo(failure: State.Failure?) = failure ?: State.Loading",
                "State.Loading" to "Loading",
            )

        @Test
        fun `does not report an elvis operand that is a sibling of the left side's sealed subtype in an argument`() =
            assertNotReported(
                "fun foo(reason: String?) = takesState(reason?.let(State::Failure) ?: State.Loading)",
                "State.Loading" to "Loading",
            )
    }

    @Nested
    inner class `declarations and returns` {
        @Test
        fun `reports enum entry as a typed property initializer`() =
            assertReported("val role: Role = Role.Admin", "Role.Admin" to "Admin")

        @Test
        fun `reports enum entry in parentheses`() =
            assertReported("val role: Role = (Role.Admin)", "Role.Admin" to "Admin")

        @Test
        fun `reports enum entry as a default parameter value`() =
            assertReported("fun foo(role: Role = Role.Admin) = role", "Role.Admin" to "Admin")

        @Test
        fun `reports enum entry as an assignment`() =
            assertReported(
                """
                fun foo() {
                    var role: Role = makeRole()
                    role = Role.Admin
                }
                """.trimIndent(),
                "Role.Admin" to "Admin",
            )

        @Test
        fun `reports enum entry as a return value`() =
            assertReported(
                """
                fun foo(): Role {
                    return Role.Admin
                }
                """.trimIndent(),
                "Role.Admin" to "Admin",
            )

        @Test
        fun `reports sealed object in a return branch`() =
            assertReported(
                "fun foo(set: Boolean): Response<Int> = if (set) Response.Success(1) else Response.Empty",
                "Response.Empty" to "Empty",
            )

        @Test
        fun `reports an override with a declared type`() =
            assertReported(
                """
                class Impl : HasRole {
                    override val role: Role = Role.Admin
                    override fun other(): Role = makeRole()
                }
                """.trimIndent(),
                "Role.Admin" to "Admin",
            )

        @Test
        fun `does not report when there is no expected type`() =
            assertNotReported("val role = Role.Admin", "Role.Admin" to "Admin")

        @Test
        fun `does not report when the expected type is a supertype`() =
            assertNotReported("fun foo(): Any = Role.Admin", "Role.Admin" to "Admin")

        @Test
        fun `does not report an override with no declared type`() =
            assertNotReported(
                """
                class Impl : HasRole {
                    override val role = Role.Admin
                    override fun other() = Role.Basic
                }
                """.trimIndent(),
                "Role.Admin" to "Admin",
                "Role.Basic" to "Basic",
            )

        @Test
        fun `does not report an override getter with no declared type`() =
            assertNotReported(
                """
                class Impl : HasRole {
                    override val role get() = Role.Admin
                    override fun other() = makeRole()
                }
                """.trimIndent(),
                "Role.Admin" to "Admin",
            )

        @Test
        fun `does not report branches of a property with no declared type`() =
            assertNotReported(
                """
                fun foo(set: Boolean): Role {
                    val role = if (set) Role.Admin else Role.Basic
                    return role
                }
                """.trimIndent(),
                "Role.Admin" to "Admin",
                "Role.Basic" to "Basic",
                together = true,
            )

        @Test
        fun `does not report when branches of a property with no declared type`() =
            assertNotReported(
                """
                fun foo(role: Role): Color {
                    val color = when (role) {
                        Admin -> Color.Red
                        Basic -> Color.Green
                    }
                    return color
                }
                """.trimIndent(),
                "Color.Red" to "Red",
                "Color.Green" to "Green",
                together = true,
            )
    }

    @Nested
    inner class `when, is and as` {
        @Test
        fun `reports enum entry in a when branch`() =
            assertReported(
                """
                fun foo(role: Role) = when (role) {
                    Role.Admin -> 1
                    Role.Basic -> 2
                }
                """.trimIndent(),
                "Role.Admin" to "Admin",
                "Role.Basic" to "Basic",
            )

        @Test
        fun `reports enum entry in a when branch with a subject variable`() =
            assertReported(
                """
                fun foo(user: User) = when (val role = user.role) {
                    Role.Admin -> 1
                    else -> role.ordinal
                }
                """.trimIndent(),
                "Role.Admin" to "Admin",
            )

        @Test
        fun `reports sealed subtype in a when is-branch`() =
            assertReported(
                """
                fun foo(state: State) = when (state) {
                    is State.Failure -> 1
                    else -> 2
                }
                """.trimIndent(),
                "State.Failure" to "Failure",
            )

        @Test
        fun `reports sealed subtype in a when is-branch with a subject variable`() =
            assertReported(
                """
                fun foo(screen: Screen) = when (val state = screen.state) {
                    is State.Failure -> state.reason
                    else -> ""
                }
                """.trimIndent(),
                "State.Failure" to "Failure",
            )

        @Test
        fun `reports sealed subtype in an is-check`() =
            assertReported("fun foo(state: State) = state is State.Failure", "State.Failure" to "Failure")

        @Test
        fun `reports sealed subtype in a cast`() =
            assertReported("fun foo(state: State) = state as State.Failure", "State.Failure" to "Failure")

        @Test
        fun `reports sealed subtype in a cast to a nullable type`() =
            assertReported("fun foo(state: State?) = state as State.Failure?", "State.Failure?" to "Failure?")

        @Test
        fun `reports generic sealed subtype in an is-check`() =
            assertReported(
                "fun foo(response: Response<String>) = response is Response.Success<*>",
                "Response.Success<*>" to "Success<*>",
            )

        @Test
        fun `reports sealed subtype of a sealed supertype the type is nested in`() =
            assertReported("fun foo(round: Shape.Round) = round is Shape.Oval", "Shape.Oval" to "Oval")

        @Test
        fun `does not report a cast from a supertype`() =
            assertNotReported("fun foo(state: Any) = state as State.Failure", "State.Failure" to "Failure")

        @Test
        fun `reports sealed subtype in an is-check on a smart cast value`() =
            assertReported(
                "fun foo(any: Any) = any is State && any is State.Failure",
                "State.Failure" to "Failure",
            )
    }

    @Nested
    inner class `lambdas and try` {
        @Test
        fun `reports a lambda result with a known type`() =
            assertReported(
                """
                fun foo(role: Assert<Role>) = role.map { if (it == makeRole()) Role.Admin else Role.Basic }
                """.trimIndent(),
                "Role.Admin" to "Admin",
                "Role.Basic" to "Basic",
            )

        @Test
        fun `reports a lambda result with a declared function type`() =
            assertReported(
                """
                val factory: () -> Role = { Role.Admin }

                fun foo(factory: () -> Role = { Role.Basic }) = factory()
                """.trimIndent(),
                "Role.Admin" to "Admin",
                "Role.Basic" to "Basic",
            )

        @Test
        fun `reports a labeled return from a lambda with a known type`() =
            assertReported(
                """
                fun foo(role: Assert<Role>) = role.map {
                    if (it == makeRole()) return@map Role.Admin
                    Role.Basic
                }
                """.trimIndent(),
                "Role.Admin" to "Admin",
                "Role.Basic" to "Basic",
            )

        @Test
        fun `reports a non-local return from a lambda`() =
            assertReported(
                """
                fun foo(numbers: List<Int>): Role {
                    numbers.forEach { if (it > 0) return Role.Admin }
                    return Role.Basic
                }
                """.trimIndent(),
                "Role.Admin" to "Admin",
                "Role.Basic" to "Basic",
            )

        @Test
        fun `reports enum entry as a try result`() =
            assertReported(
                "fun foo(): Role = try { makeRole() } catch (e: Exception) { Role.Admin }",
                "Role.Admin" to "Admin",
            )

        @Test
        fun `does not report a lambda result with no declared type`() =
            assertNotReported("val factory = { Role.Admin }", "Role.Admin" to "Admin")

        @Test
        fun `does not report a lambda result that decides the type`() =
            assertNotReported("fun foo() = listOf(1).associateWith { Role.Admin }", "Role.Admin" to "Admin")

        @Test
        fun `does not report a labeled return from a lambda that decides the type`() =
            assertNotReported(
                """
                fun foo() = listOf(1).associateWith {
                    if (it > 0) return@associateWith Role.Admin
                    Role.Basic
                }
                """.trimIndent(),
                "Role.Admin" to "Admin",
                "Role.Basic" to "Basic",
                together = true,
            )
    }

    @Nested
    inner class `type arguments` {
        @Test
        fun `reports enum entries that decide a collection's type`() =
            assertReported(
                "fun foo() = listOf(Role.Admin, Role.Basic)",
                "listOf(Role.Admin, Role.Basic)" to "listOf<Role>(Admin, Basic)",
                reason = TYPE_ARGUMENT,
            )

        @Test
        fun `reports companion properties that decide a collection's type`() =
            assertReported(
                "fun foo() = setOf(Color.Red, Color.Green)",
                "setOf(Color.Red, Color.Green)" to "setOf<Color>(Red, Green)",
                reason = TYPE_ARGUMENT,
            )

        @Test
        fun `reports enum entries split over lines with a one-line message`() {
            val code = """
                fun foo() =
                    setOf(
                        Role.Admin,
                        Role.Basic,
                    )
            """.trimIndent()
            val findings = subject.lintWithContext(env, "$PREAMBLE\n$code", languageVersionSettings = CSR)
            assertThat(findings).singleElement().hasMessage(
                "'setOf(Role.Admin, Role.Basic)' can be written as 'setOf<Role>(Admin, Basic)', $TYPE_ARGUMENT.",
            )
        }

        @Test
        fun `reports enum entries that decide a generic function's type`() =
            assertReported(
                "fun foo() = both(Role.Admin, Role.Basic)",
                "both(Role.Admin, Role.Basic)" to "both<Role>(Admin, Basic)",
                reason = TYPE_ARGUMENT,
            )

        @Test
        fun `reports enum entries in a collection with a declared type`() =
            assertReported(
                "val roles: List<Role> = listOf(Role.Admin, Role.Basic)",
                "listOf(Role.Admin, Role.Basic)" to "listOf(Admin, Basic)",
            )

        @Test
        fun `reports enum entries in a collection passed as an argument`() =
            assertReported(
                "fun foo() = takesRoles(listOf(Role.Admin, Role.Basic))",
                "listOf(Role.Admin, Role.Basic)" to "listOf(Admin, Basic)",
            )

        @Test
        fun `reports enum entries with different qualifiers for the same enum`() =
            assertReported(
                "fun foo() = listOf(Role.Admin, com.example.Role.Basic)",
                "listOf(Role.Admin, com.example.Role.Basic)" to "listOf<Role>(Admin, Basic)",
                reason = TYPE_ARGUMENT,
            )

        @Test
        fun `does not report entries of different types`() =
            assertNotReported(
                "fun foo() = listOf(Role.Admin, Method.Header)",
                "Role.Admin" to "Admin",
                "Method.Header" to "Header",
            )

        @Test
        fun `does not report when one argument is a constructor call`() =
            assertNotReported("""fun foo() = listOf(State.Loading, State.Failure("x"))""")

        @Test
        fun `does not report when one entry is shadowed`() =
            assertNotReported(
                "fun foo() = listOf(Method.Password, Method.Header)",
                "listOf(Method.Password, Method.Header)" to "listOf<Method>(Password, Header)",
            )

        @Test
        fun `does not report when the expected type widens the collection`() =
            assertNotReported(
                "val roles: MutableList<Any> = mutableListOf(Role.Admin, Role.Basic)",
                "Role.Admin" to "Admin",
                "Role.Basic" to "Basic",
            )

        @Test
        fun `does not report a function with more than one type parameter`() =
            assertNotReported(
                "fun foo() = pairOf(Role.Admin, Role.Basic)",
                "Role.Admin" to "Admin",
                "Role.Basic" to "Basic",
            )

        @Test
        fun `does not report sealed objects of a generic type that decide a collection's type`() =
            assertNotReported(
                """
                sealed interface Reply<out T> {
                    data object None : Reply<Nothing>
                    data object Gone : Reply<Nothing>
                }

                fun foo() = listOf(Reply.None, Reply.Gone)
                """.trimIndent(),
                "Reply.None" to "None",
                "Reply.Gone" to "Gone",
            )

        @Test
        fun `reports sealed objects of a generic type in a collection with a declared type`() =
            assertReported(
                """
                sealed interface Reply<out T> {
                    data object None : Reply<Nothing>
                    data object Gone : Reply<Nothing>
                }

                val replies: List<Reply<Nothing>> = listOf(Reply.None, Reply.Gone)
                """.trimIndent(),
                "listOf(Reply.None, Reply.Gone)" to "listOf(None, Gone)",
            )

        @Test
        fun `reports a single enum entry in a collection with a declared type`() =
            assertReported(
                "val roles: List<Role> = listOf(Role.Admin)",
                "listOf(Role.Admin)" to "listOf(Admin)",
            )
    }

    @Nested
    inner class `shadowed names` {
        @Test
        fun `reports entries with the same name in different enums`() =
            assertReported(
                "fun foo(state: State, progress: Progress) = state == State.Loading && progress == Progress.Loading",
                "State.Loading" to "Loading",
                "Progress.Loading" to "Loading",
            )

        @Test
        fun `reports sealed subtypes with the same name in different hierarchies`() =
            assertReported(
                "fun foo(state: State, result: Result) = state is State.Failure && result is Result.Failure",
                "State.Failure" to "Failure",
                "Result.Failure" to "Failure",
            )

        @Test
        fun `does not report an entry shadowed by a class`() =
            assertNotReported("fun foo() = takesMethod(Method.Password)", "Method.Password" to "Password")

        @Test
        fun `does not report an entry shadowed by a function`() =
            assertNotReported("fun foo() = takesRole(Role.Basic)\nfun Basic(x: Int) = x", "Role.Basic" to "Basic")

        @Test
        fun `reports an entry with the same name as an extension property that has no receiver`() =
            assertReported(
                """
                object Icons

                val Icons.Admin: Int get() = 0

                fun foo() = takesRole(Role.Admin)
                """.trimIndent(),
                "Role.Admin" to "Admin",
            )

        @Test
        fun `does not report an entry shadowed by an extension property on an implicit receiver`() =
            assertNotReported(
                """
                object Icons

                val Icons.Admin: Int get() = 0

                fun Icons.foo() = takesRole(Role.Admin)
                """.trimIndent(),
                "Role.Admin" to "Admin",
            )

        @Test
        fun `reports a sealed subtype type with the same name as a function`() =
            assertReported(
                "fun Failure() = 1\nfun foo(state: State) = state is State.Failure",
                "State.Failure" to "Failure",
            )

        @Test
        fun `does not report an entry shadowed by a local variable`() =
            assertNotReported(
                """
                fun foo(): Role {
                    val Admin = Role.Basic
                    return Role.Admin
                }
                """.trimIndent(),
            )

        @Test
        fun `does not report an entry shadowed by an entry of the enclosing enum`() =
            assertNotReported(
                """
                enum class Phase {
                    Loading,
                    Done;

                    fun state(): State = State.Loading
                }
                """.trimIndent(),
                "State.Loading" to "Loading",
            )

        @Test
        fun `does not report a sealed subtype shadowed by a nested class of the enclosing class`() =
            assertNotReported(
                """
                class Outcome {
                    class Failure

                    fun isFailed(state: State) = state is State.Failure
                }
                """.trimIndent(),
                "State.Failure" to "Failure",
            )

        @Test
        fun `does not report an entry shadowed by an imported entry of another enum`() {
            val code = """
                package com.example.screens

                import com.example.State
                import com.example.Progress.Loading

                fun foo(): State = State.Loading
            """.trimIndent()
            assertThat(subject.lintWithContext(env, code, PREAMBLE, languageVersionSettings = CSR)).isEmpty()
        }
    }

    @Nested
    inner class `source locations` {
        private fun findings(@Language("kotlin") code: String) =
            subject.lintWithContext(env, "package com.example\n\n$code", PREAMBLE, languageVersionSettings = CSR)

        @Test
        fun `reports a qualified name at the qualified name`() {
            assertThat(findings("fun foo() = takesRole(Role.Admin)"))
                .singleElement()
                .hasStartSourceLocation(3, 23)
                .hasEndSourceLocation(3, 33)
        }

        @Test
        fun `reports a type at the type`() {
            assertThat(findings("fun foo(state: State) = state is State.Failure"))
                .singleElement()
                .hasStartSourceLocation(3, 34)
                .hasEndSourceLocation(3, 47)
        }

        @Test
        fun `reports a collection call at the call`() {
            assertThat(findings("fun foo() = listOf(Role.Admin, Role.Basic)"))
                .singleElement()
                .hasStartSourceLocation(3, 13)
                .hasEndSourceLocation(3, 43)
        }
    }

    @Nested
    inner class `other cases` {
        @Test
        fun `does not report an unqualified entry`() = assertNotReported("fun foo() = takesRole(Admin)")

        @Test
        fun `does not report an entry inside its own enum`() =
            assertNotReported(
                """
                enum class Level {
                    Low,
                    High;

                    fun next(): Level = Level.High
                }
                """.trimIndent(),
            )

        @Test
        fun `does not report a companion property inside its own class`() =
            assertNotReported(
                """
                class Palette {
                    fun default(): Palette = Palette.Red

                    companion object {
                        val Red = Palette()
                    }
                }
                """.trimIndent(),
            )

        @Test
        fun `does not report a member access on the entry`() =
            assertNotReported("fun foo(): String = Role.Admin.name", "Role.Admin" to "Admin")

        @Test
        fun `does not report a sealed subtype constructor call`() =
            assertNotReported(
                """fun foo() = takesState(State.Failure("x"))""",
                "State.Failure" to "Failure",
            )

        @Test
        fun `does not report the package directive or imports`() {
            val code = """
                package com.example.screens

                import com.example.Role
                import com.example.Role.Admin
                import com.example.State.Failure

                fun foo(): Role = Admin

                fun bar(state: Any) = state is Failure
            """.trimIndent()
            assertThat(subject.lintWithContext(env, code, PREAMBLE, languageVersionSettings = CSR)).isEmpty()
        }

        @Test
        fun `does not report when context-sensitive resolution is off`() {
            val code = "$PREAMBLE\nfun foo() = takesRole(Role.Admin)"
            assertThat(subject.lintWithContext(env, code)).isEmpty()
        }
    }

    // Checks the findings match the fixes exactly, then that the code will still compile with every fix applied
    private fun assertReported(
        @Language("kotlin") code: String,
        vararg fixes: Pair<String, String>,
        reason: String = EXPECTED_TYPE,
    ) {
        val source = PREAMBLE + "\n" + code.trimIndent()
        val expected = fixes.map { (from, to) -> "'$from' can be written as '$to', $reason." }
        val messages = subject.lintWithContext(env, source, languageVersionSettings = CSR).map { it.message }
        assertThat(messages).containsExactlyElementsOf(expected)
        compile(PREAMBLE + "\n" + applyFixes(code, fixes.toList()))
    }

    // Checks there are no findings. If fixes are given, also checks the code fails to compile with each one applied,
    // which proves the qualifier is needed rather than missed by the rule. Some qualifiers are only needed when the
    // others are dropped too, like those in both branches of an untyped if, so those tests pass together = true
    private fun assertNotReported(
        @Language("kotlin") code: String,
        vararg fixes: Pair<String, String>,
        together: Boolean = false,
    ) {
        val source = PREAMBLE + "\n" + code.trimIndent()
        assertThat(subject.lintWithContext(env, source, languageVersionSettings = CSR)).isEmpty()
        val attempts = if (together) listOf(fixes.toList()) else fixes.map { listOf(it) }
        attempts.filter { it.isNotEmpty() }.forEach { applied ->
            assertThatThrownBy({ compile(PREAMBLE + "\n" + applyFixes(code, applied)) }, "Fixed with %s", applied)
                .isInstanceOf(IllegalStateException::class.java)
        }
    }

    private fun applyFixes(code: String, fixes: List<Pair<String, String>>): String =
        fixes.fold(code.trimIndent()) { acc, (from, to) -> acc.replace(from, to) }

    private fun compile(code: String) {
        KotlinAnalysisApiEngine().use { engine ->
            engine.compile(
                code = code,
                javaSourceRoots = env.javaSourceRoots,
                jvmClasspathRoots = env.jvmClasspathRoots,
                allowCompilationErrors = false,
                languageVersionSettings = CSR,
            )
        }
    }

    private companion object {
        const val EXPECTED_TYPE = "the expected type is known"
        const val TYPE_ARGUMENT = "a type argument gives the names an expected type"

        val CSR = LanguageVersionSettingsImpl(
            languageVersion = LanguageVersion.LATEST_STABLE,
            apiVersion = ApiVersion.LATEST_STABLE,
            specificFeatures = mapOf(
                LanguageFeature.ContextSensitiveResolutionUsingExpectedType to LanguageFeature.State.ENABLED,
            ),
        )

        @Language("kotlin")
        val PREAMBLE = """
            package com.example

            enum class Role { Admin, Basic }

            enum class Method { Password, Header }

            class Password

            enum class Progress { Loading, Done }

            sealed interface Result {
                data object Loading : Result
                data class Failure(val code: Int) : Result
            }

            sealed interface State {
                data object Loading : State
                data object Idle : State
                data class Failure(val reason: String) : State
                companion object {
                    val Default: State = Loading
                }
            }

            @JvmInline
            value class Color(val rgb: Int) {
                companion object {
                    val Red = Color(0)
                    val Green = Color(1)
                    val Fallback: Role = makeRole()
                }
            }

            fun takesColor(color: Color?) = color

            operator fun Color.plus(other: Color) = Color(rgb + other.rgb)

            infix fun Role.sameAs(other: Role) = this == other

            sealed interface Response<out T> {
                data class Success<out T>(val body: T) : Response<T>
                data object Empty : Response<Nothing>
            }

            fun takesResponse(response: Response<Int>) = response

            interface Alignment {
                class Horizontal : Alignment
                companion object {
                    val CenterHorizontally: Horizontal = Horizontal()
                }
            }

            fun alignHorizontally(alignment: Alignment.Horizontal) = alignment

            sealed interface Arrangement {
                class Horizontal : Arrangement
                companion object {
                    val SpaceEvenly: Horizontal = Horizontal()
                }
            }

            fun arrangeHorizontally(arrangement: Arrangement.Horizontal) = arrangement

            sealed interface Shape {
                sealed interface Round : Shape
                data object Circle : Round
                class Oval : Round
            }

            fun takesRound(round: Shape.Round) = round

            class User(val role: Role)

            interface HasRole {
                val role: Role
                fun other(): Role
            }

            class Screen(val state: State)

            class Assert<out T>(val actual: T)

            fun <T> Assert<T>.isEqualTo(expected: T) = actual == expected

            fun Assert<String?>.isEqualTo(other: String?, ignoreCase: Boolean = false) =
                actual.equals(other, ignoreCase)

            fun <T> Assert<Array<T>>.isEqualTo(expected: Array<T>) = actual.contentEquals(expected)

            fun <T, P> Assert<T>.prop(property: kotlin.reflect.KProperty1<T, P>) = Assert(property.get(actual))

            fun makeRole(): Role = Role.entries.last()

            fun <T> Assert<T>.map(transform: (T) -> T) = Assert(transform(actual))

            fun takesRole(role: Role) = role

            fun takesRoles(roles: List<Role>) = roles

            fun overloaded(role: Role) = role

            fun overloaded(number: Int) = number

            fun takesState(state: State) = state

            fun takesMethod(method: Method) = method

            fun <T> both(first: T, second: T) = first to second

            fun <A, B> pairOf(first: A, second: B) = first to second
        """.trimIndent()
    }
}
