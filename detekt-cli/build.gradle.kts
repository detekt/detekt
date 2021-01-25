import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    id("com.github.johnrengelman.shadow")
    module
    application
}

application {
    mainClassName = "io.gitlab.arturbosch.detekt.cli.Main"
}

tasks.withType<ShadowJar>().configureEach {
    mergeServiceFiles()
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    implementation("com.beust:jcommander")
    implementation(project(":detekt-tooling"))
    implementation(project(":detekt-parser"))
    runtimeOnly(project(":detekt-core"))
    runtimeOnly(project(":detekt-rules"))

    testImplementation(project(":detekt-test"))
    testImplementation(project(":detekt-rules"))
}

val moveJarForIntegrationTest by tasks.registering {
    dependsOn(tasks.named("shadowJar"))
    doLast {
        copy {
            from(tasks.named("shadowJar"))
            into(rootProject.buildDir)
            rename { "detekt-cli-all.jar" }
        }
    }
}
