plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.gms.google-services")
    id("kotlin-parcelize")
}

android {
    namespace = "com.mariomorenoarroyo.tfg"
    compileSdk = 34

    buildFeatures{
        viewBinding = true
    }


    defaultConfig {
        applicationId = "com.mariomorenoarroyo.tfg"
        minSdk = 24
        targetSdk = 33
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
}

dependencies {

    implementation("androidx.core:core-ktx:1.9.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.10.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.navigation:navigation-fragment-ktx:2.7.6")
    implementation("androidx.navigation:navigation-ui-ktx:2.7.6")
    implementation("com.google.firebase:firebase-auth-ktx:22.3.0")
    implementation("com.google.android.gms:play-services-maps:18.2.0")
    implementation("androidx.preference:preference:1.2.1")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    implementation(platform("com.google.firebase:firebase-bom:32.7.0"))
    implementation("com.google.firebase:firebase-analytics:20.0.1")
    implementation("com.google.firebase:firebase-database:20.0.3")
    implementation("com.google.firebase:firebase-storage:20.0.0")
    implementation("com.google.firebase:firebase-auth:21.0.1")
    implementation("com.google.firebase:firebase-firestore:23.0.3")
    implementation("com.google.android.gms:play-services-auth:20.7.0")
    implementation ("com.github.bumptech.glide:glide:4.12.0")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.3.0")
    implementation("androidx.preference:preference-ktx:1.2.1")
    implementation ("androidx.appcompat:appcompat:1.3.0")
    implementation ("com.google.android.gms:play-services-maps:18.0.1")




}