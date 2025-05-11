plugins {
    id("java")
    id("application")
}

application {
    mainClass = "org.example.Main"
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenLocal()
    mavenCentral()
}

dependencies {
    implementation("org.apache.giraph:giraph-core:1.3.0-SNAPSHOT") {
        isChanging = true
    }

    implementation("org.apache.hadoop:hadoop-client:2.7.7")

    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

tasks.test {
    useJUnitPlatform()
}