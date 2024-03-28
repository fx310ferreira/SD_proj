plugins {
    id("application")
}

application {
    mainClass = "com.barrel.Barrel"
}

dependencies {
    implementation(project(":common"))
    implementation("org.postgresql:postgresql:42.7.3");
    implementation("org.json:json:20240303")
}

repositories {
    mavenCentral()
}