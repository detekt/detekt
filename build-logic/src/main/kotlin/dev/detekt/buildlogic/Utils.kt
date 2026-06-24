package dev.detekt.buildlogic

import org.apache.tools.ant.taskdefs.condition.Os
import org.gradle.api.Task

private val operativeSystem: String = when {
    Os.isFamily(Os.FAMILY_WINDOWS) -> Os.FAMILY_WINDOWS
    Os.isFamily(Os.FAMILY_MAC) -> Os.FAMILY_MAC
    Os.isFamily(Os.FAMILY_UNIX) -> Os.FAMILY_UNIX
    else -> System.getProperty("os.name").lowercase()
}

fun Task.osDependent() {
    inputs.property("os.name", operativeSystem)
}
