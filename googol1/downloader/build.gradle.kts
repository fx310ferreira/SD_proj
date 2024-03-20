plugins {
    id("application")
}

application {
    mainClass = "com.downloader.Downloader"
}

dependencies {
    implementation(project(":common"))
    implementation("org.jsoup:jsoup:1.17.2")
}

repositories {
    mavenCentral()
}