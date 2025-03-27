plugins {
    alias(libs.plugins.android.application)
    //id("com.android.application")
    // Add the Google services Gradle plugin
    id("com.google.gms.google-services")
}

android {
    namespace = "edu.northeastern.finalproject_group_1"
    compileSdk = 35

    defaultConfig {
        applicationId = "edu.northeastern.finalproject_group_1"
        minSdk = 27
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
    // Import the Firebase BoM
    implementation(platform("com.google.firebase:firebase-bom:33.10.0"))
    // Flexbox layout library for horizontal/vertical wrapping (used in Repeat -> Weekdays)
    implementation("com.google.android.flexbox:flexbox:3.0.0")
    // Edit user uploaded photo/icon
    implementation("com.github.yalantis:ucrop:2.2.8-native")
    // Color Picker
    implementation("com.github.skydoves:colorpickerview:2.2.4")
}