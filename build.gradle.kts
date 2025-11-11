// C:/Users/comadmin/Desktop/staff/build.gradle.kts

plugins {// Use the alias from libs.versions.toml for consistency
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.compose) apply false // <-- Add this line
    id("com.google.gms.google-services") version "4.4.2" apply false
}

tasks.register<Delete>("clean") {
    delete(rootProject.buildDir)
}
