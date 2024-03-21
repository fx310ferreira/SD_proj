plugins {
    id("application")
}

application {
    mainClass = "com.barrel.Barrel"
}

dependencies {
    implementation(project(":common"))
}