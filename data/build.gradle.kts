plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.ksp)
}

android {
    namespace = "com.emdp.rickandmorty.data"
    compileSdk = 36

    defaultConfig {
        minSdk = 26
        consumerProguardFiles("consumer-rules.pro")

        buildConfigField("Long", "CHAR_LIST_TTL_MS", "24L * 60L * 60L * 1000L")
        buildConfigField("Long", "CHAR_DETAIL_TTL_MS", "72L * 60L * 60L * 1000L")
        buildConfigField("Long", "EPISODES_TTL_MS", "7L * 24L * 60L * 60L * 1000L")
    }

    buildFeatures { buildConfig = true }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlin {
        jvmToolchain(17)
    }

    testOptions {
        unitTests { isReturnDefaultValues = true }
        unitTests.all { it.useJUnitPlatform() }
    }
}

dependencies {
    implementation(projects.domain)

    implementation(libs.retrofit)
    implementation(libs.retrofit.kotlinx.serialization)
    implementation(libs.okhttp)
    implementation(libs.okhttp.logging)
    implementation(libs.moshi)
    implementation(libs.moshi.kotlin)
    implementation(libs.converter.moshi)

    implementation(libs.room.runtime)
    implementation(libs.room.ktx)
    ksp(libs.room.compiler)

    implementation(libs.paging.runtime)

    implementation(libs.kotlinx.coroutines.android)

    implementation(libs.koin.core)

    testImplementation(libs.junit.api)
    testImplementation(libs.junit.params)
    testRuntimeOnly(libs.junit.engine)
    testImplementation(libs.mockito.core)
    testImplementation(libs.mockito.kotlin)
}