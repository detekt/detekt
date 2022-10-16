rootProject.name = "detekt-gradle-plugin"

pluginManagement {
    includeBuild("../build-logic")
}

includeBuild("..")

dependencyResolutionManagement {
    versionCatalogs {
        create("libs") {
            from(files("../gradle/libs.versions.toml"))
        }
    }
}
