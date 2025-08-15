plugins {
    id("packaging")
    id("com.gradleup.shadow") version "9.0.2"
}

dependencies {
    runtimeOnly(libs.ktlint.rulesetStandard) {
        exclude(group = "org.jetbrains.kotlin")
    }
}

tasks.shadowJar {
    relocate("org.jetbrains.kotlin.com.intellij", "com.intellij")
}

java {
    targetCompatibility = JavaVersion.VERSION_1_8
}

configurations.shadowRuntimeElements {
    attributes {
        // This is not needed in shadow plugin 9+: https://github.com/GradleUp/shadow/pull/1199
        attribute(TargetJvmVersion.TARGET_JVM_VERSION_ATTRIBUTE, JavaVersion.VERSION_1_8.majorVersion.toInt())
    }
}

val javaComponent = components["java"] as AdhocComponentWithVariants
javaComponent.withVariantsFromConfiguration(configurations["apiElements"]) {
    skip()
}
javaComponent.withVariantsFromConfiguration(configurations["runtimeElements"]) {
    skip()
}
