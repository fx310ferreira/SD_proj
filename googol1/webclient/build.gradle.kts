plugins {
    java
    id("application")
    id("org.springframework.boot") version "3.2.5"
    id("io.spring.dependency-management") version "1.1.4"
}

application {
    mainClass = "com.webclient.SpringApplication"
}

configurations {
    compileOnly {
        extendsFrom(configurations.annotationProcessor.get())
    }
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-thymeleaf")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-websocket")
    compileOnly("org.projectlombok:lombok")
    developmentOnly("org.springframework.boot:spring-boot-devtools")
    annotationProcessor("org.projectlombok:lombok")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    implementation("org.webjars:stomp-websocket:2.3.4")
    implementation("org.webjars:webjars-locator:0.52")
    implementation("org.webjars:sockjs-client:1.5.1")
    implementation("org.json:json:20240303")
    implementation(project(":common"))
}

tasks.withType<Test> {
    useJUnitPlatform()
}
