plugins {
    id("com.github.johnrengelman.shadow") apply false
}

configure(listOf(project(":detekt-cli"))) {
    apply {
        plugin("application")
        plugin("com.github.johnrengelman.shadow")
    }
}
