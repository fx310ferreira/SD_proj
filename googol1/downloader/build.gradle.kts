plugins {
    id("application")
    id("com.github.johnrengelman.shadow") version "7.1.2"
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
    from(project(":common").sourceSets.main.get().output)
}


tasks.withType<com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar> {
    archiveBaseName.set("downloader")
    archiveClassifier.set("")
    manifest {
        attributes["Main-Class"] = application.mainClass
    }
}