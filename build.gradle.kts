// Top-level build file where you can add configuration options common to all sub-projects/modules.

plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.compose.compiler) apply false

    // Hilt for Dependency Injection
    id("com.google.dagger.hilt.android") version "2.60.1" apply false

    // KSP (Kotlin Symbol Processing) for Room and Hilt
    id("com.google.devtools.ksp") version "2.0.21-1.0.26" apply false
}