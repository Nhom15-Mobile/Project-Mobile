plugins {
    alias(libs.plugins.android.library)
    // Nếu module này có Kotlin, bật thêm:
    // alias(libs.plugins.kotlin.android)
}

android {
    namespace = "com.example.auth"
    compileSdk = 36

    defaultConfig {
        minSdk = 24
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
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

    // Dùng JDK 17 cho AGP 8.x
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    // Nếu có Kotlin:
    // kotlinOptions { jvmTarget = "17" }
}

dependencies {
    // UI (nếu module auth có màn hình/fragment)
    implementation(libs.appcompat)
    implementation(libs.material)

    // --- Quan trọng: để dùng retrofit2.Call, @POST, @Body ---
    implementation("com.squareup.retrofit2:retrofit:2.11.0")

    // Dùng DTO từ domain và client cấu hình sẵn từ core:network
    implementation(project(":domain"))
    implementation(project(":core:network"))
    implementation(project(":core:util"))
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.9.3")
}
