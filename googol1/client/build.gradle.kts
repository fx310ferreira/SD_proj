plugins {
    id("application")
    id("com.github.johnrengelman.shadow") version "7.1.2"
}

application {
    mainClass = "com.client.Client"
}

dependencies {
    implementation(project(":common"))
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