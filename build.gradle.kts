plugins {
    id("java")
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    // Core
    implementation("com.google.guava:guava:32.0.0-jre");
    // Testing
    testImplementation(platform("org.junit:junit-bom:5.9.1"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testImplementation("org.mockito:mockito-core:2.1.0")
}

tasks.test {
    jvmArgs("--add-opens", "java.base/java.lang=ALL-UNNAMED")
    useJUnitPlatform()
}