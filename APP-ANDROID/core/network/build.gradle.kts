plugins {
    alias(libs.plugins.android.library)
}

android {
    namespace = "com.uihealthcare.network" // khớp với package trong code của bạn
    compileSdk = 36

    defaultConfig {
        minSdk = 24
    }

    compileOptions {
        // AGP 8.x nên dùng Java 17
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

dependencies {
    // ---- Network stack cần thiết ----
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")
    implementation("com.squareup.retrofit2:retrofit:2.11.0")
    implementation("com.squareup.retrofit2:converter-gson:2.11.0")

    // (Không cần appcompat/material/test libs cho module network)
}
