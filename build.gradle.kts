plugins {
    id("java")
    id("com.github.johnrengelman.shadow") version "7.1.2" apply true
}

group = "org.rsp"
version = "1.0"

repositories {
    mavenCentral()
}

dependencies {
    // Annotation processing
    annotationProcessor("org.projectlombok:lombok:1.18.30")
    compileOnly("org.projectlombok:lombok:1.18.30")
    // Core
    implementation("com.google.guava:guava:32.0.0-jre");
    // Testing
    testImplementation(platform("org.junit:junit-bom:5.9.1"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testImplementation("org.mockito:mockito-core:2.1.0")
}

tasks {
    jar {
        enabled = false
    }
    test {
        jvmArgs("--add-opens", "java.base/java.lang=ALL-UNNAMED")
        useJUnitPlatform()
    }
    shadowJar {
        manifest {
            attributes["Main-Class"] = "org.rsp.GameServer"
        }
        archiveBaseName.set("rsp-game-server")
    }
    build {
        finalizedBy(shadowJar)
    }
}
