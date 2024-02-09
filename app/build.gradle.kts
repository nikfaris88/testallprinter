plugins {
    id("com.android.application")
}

android {

    defaultConfig {
        applicationId = "com.example.printerlibssampleapp"
        compileSdkVersion(34)
        versionCode = 1
        versionName = "1.0"
    }
    val manifestPath: File = File(projectDir, "app/src/main/AndroidManifest.xml")

    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            //            manifest = project.file(manifestPath)

        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

dependencies {
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.11.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation(files("libs/printerlibs-debug.aar"))
}