plugins {
    id("com.github.johnrengelman.shadow")
    module
    application
}

application {
    mainClassName = "io.gitlab.arturbosch.detekt.cli.Main"
}

dependencies {
    implementation("com.beust:jcommander")
    implementation(project(":detekt-tooling"))
    implementation(project(":detekt-parser"))
    runtimeOnly(project(":detekt-core"))
    runtimeOnly(project(":detekt-rules"))

    testImplementation(project(":detekt-test"))
}

tasks.shadowJar {
    mergeServiceFiles()
}

tasks.register<Copy>("moveJarForIntegrationTest") {
    from(tasks.shadowJar)
    into(rootProject.buildDir)
    rename { "detekt-cli-all.jar" }
}
