plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.plugin.parcelize")
    id("dev.detekt")
}

android {
    namespace = "com.example.myapplication"
    compileSdk {
        version = release(36) {
            minorApiLevel = 1
        }
    }

    defaultConfig {
        minSdk = 24
    }
}
