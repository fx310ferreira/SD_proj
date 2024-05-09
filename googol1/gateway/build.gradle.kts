plugins {
    id("application")
}

application {
    mainClass = "com.gateway.Gateway"
}

dependencies {
    implementation(project(":common"))
}

tasks.jar {
    manifest {
        attributes["Main-Class"] = application.mainClass
    }
    from(project(":common").sourceSets.main.get().output)
}