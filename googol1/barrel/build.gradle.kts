plugins {
    id("application")
}

application {
    mainClass = "com.barrel.Barrel"
}

dependencies {
    implementation(project(":common"))
    implementation("org.postgresql:postgresql:42.7.3");
}

repositories {
    mavenCentral()
}