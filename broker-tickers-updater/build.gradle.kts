import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm")
    kotlin("plugin.serialization")
    id("com.google.cloud.tools.jib") version libs.versions.jib
}

dependencies {
    implementation(project(":common"))
    implementation(project(":pudding:client"))
    implementation("net.perfectdreams.tradingviewscraper:TradingViewScraper:0.0.11")
    implementation(libs.exposed.core)
    implementation(libs.exposed.jdbc)
    implementation(libs.exposed.javatime)
    implementation("io.ktor:ktor-client-cio:${Versions.KTOR}")
}

tasks.test {
    useJUnitPlatform()
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = Versions.JVM_TARGET
}

jib {
    container {
        ports = listOf("8080")
    }

    to {
        image = "ghcr.io/lorittabot/broker-tickers-updater"

        auth {
            username = System.getProperty("DOCKER_USERNAME") ?: System.getenv("DOCKER_USERNAME")
            password = System.getProperty("DOCKER_PASSWORD") ?: System.getenv("DOCKER_PASSWORD")
        }
    }

    from {
        image = "eclipse-temurin:21-jammy"
    }
}

tasks {
    processResources {
        from("../../resources/") // Include folders from the resources root folder
    }
}