plugins {
    id("application")
}

application {
    mainClass = "com.downloader.Downloader"
}

dependencies {
    implementation(project(":common"))
    implementation("org.jsoup:jsoup:1.17.2")
    implementation("org.json:json:20240303")
}

repositories {
    mavenCentral()
}

tasks.withType<Jar> {
    manifest {
        attributes["Main-Class"] = application.mainClass
    }
}