plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.google.android.libraries.mapsplatform.secrets.gradle.plugin)
    id("kotlin-kapt")

}

android {
    namespace = "com.example.cenphone"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.cenphone"
        minSdk = 24
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        viewBinding = true
    }
}

dependencies {

    implementation("androidx.cardview:cardview:1.0.0")
    implementation("com.google.android.material:material:1.0.0")
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.play.services.maps)
    implementation(libs.androidx.room.common)
    implementation(libs.androidx.room.common.jvm)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    val room_version = "2.6.1"


    implementation("androidx.room:room-runtime:$room_version")
    kapt("androidx.room:room-compiler:$room_version")
    implementation("androidx.room:room-ktx:$room_version")
    testImplementation("androidx.room:room-testing:2.6.1")

//    implementation ("com.google.ar.sceneform:core:1.17.1")
//    implementation ("com.google.ar.sceneform.ux:sceneform-ux:1.17.1")
//    implementation("com.gorisse.thomas.sceneform:sceneform:1.22.0")

    implementation("androidx.cardview:cardview:1.0.0")
    implementation ("com.google.android.filament:filament-android:1.41.0")
    implementation ("com.google.android.filament:filament-utils-android:1.41.0")
    implementation ("com.google.android.filament:gltfio-android:1.41.0")
//    implementation ("com.google.android.filament:filament-ktx:1.41.0")

    implementation("com.stripe:stripe-android:21.2.1")
    implementation("com.github.kittinunf.fuel:fuel:2.3.1")
   implementation("com.github.kittinunf.fuel:fuel-json:2.3.1")


}