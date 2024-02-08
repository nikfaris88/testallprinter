plugins {
    id("com.android.library")
}

android {
    namespace = "com.example.printerlibs"
    compileSdk = 34

    defaultConfig {
        minSdk = 21
        targetSdk = 34
        aarMetadata {
            minCompileSdk = 29
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }

    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }


}

dependencies {

//    implementation("androidx.appcompat:appcompat:1.6.1")
//    implementation("com.google.android.material:material:1.11.0")
//    implementation("androidx.constraintlayout:constraintlayout:2.1.4")

    // iMinPrinterSDK dependencies
    implementation(files("libs/imin/iminPrinterSDK.jar"))
    implementation(files("libs/imin/IminLibs1.0.15.jar"))
    implementation(files("libs/wiseasy/SDK4BaseBinderV2.2.12.jar"))
    implementation(files("libs/wiseasy/WiseSdk_P_1.19_00a_23081701.aar"))

    // SunmiPrinter dependencies
    implementation ("com.sunmi:printerx:1.0.15")
}