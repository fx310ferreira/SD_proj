plugins {
    id("application")
}

application {
    mainClass = "com.client.Client"
}

dependencies {
    implementation(project(":common"))
}