plugins {
    id("application")
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
}