@file:Suppress("UnstableApiUsage")

pluginManagement {
    repositories {
        mavenCentral()
        gradlePluginPortal()
    }
}

include(":desktop")
include(":core")
include(":html")

dependencyResolutionManagement {
    versionCatalogs { create("libs") }
}
