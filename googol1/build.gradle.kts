/*
 * This file was generated by the Gradle 'init' task.
 *
 * This is a general purpose Gradle build.
 * To learn more about Gradle by exploring our Samples at https://docs.gradle.org/8.6/samples
 */
plugins {
    java
}

subprojects {
    apply(plugin = "java")

    tasks.withType<JavaExec> {
        if (name == "run") {
            standardInput = System.`in`
        }
    }
}
