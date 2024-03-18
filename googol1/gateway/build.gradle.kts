plugins {
    id("application")
}

application {
    mainClass = "com.gateway.Gateway"
}

dependencies {
    implementation(project(":common"))
}