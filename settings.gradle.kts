pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
        maven (url = "https://jitpack.io")

    }
}

rootProject.name = "PrinterLibsSampleApp"
include(":app")
include(":printerlibs")
include(":libs")
