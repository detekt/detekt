plugins {
    id("module")
    id("public-api")
}

dependencies {
    api(projects.detektApi)
    api(projects.detektTestJunit)
    api(projects.detektTestUtils)
    api(libs.kotlin.compiler)
    implementation(projects.detektKotlinAnalysisApiStandalone)
    implementation(projects.detektUtils)
    implementation(libs.kotlin.reflect)
    implementation(projects.detektCore)
}

//tasks.test {
//    javaLauncher = javaToolchains.launcherFor {
//        languageVersion = JavaLanguageVersion.of(17)
//    }
//}
//
//tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinJvmCompile>().configureEach {
//    compilerOptions {
//        jvmTarget = JvmTarget.fromTarget("17")
//    }
//}
