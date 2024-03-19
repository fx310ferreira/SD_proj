plugins {
    id("application")
}

application {
    mainClass = "com.downloader.Downloader"
}

dependencies {
    implementation(project(":common"))
}