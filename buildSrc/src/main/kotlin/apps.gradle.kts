plugins {
    id("com.github.johnrengelman.shadow") apply false
}

configure(listOf(project(":detekt-cli"), project(":detekt-generator"))) {
    apply {
        plugin("application")
        plugin("com.github.johnrengelman.shadow")
    }
}
