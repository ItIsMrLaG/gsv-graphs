import org.gradle.internal.classpath.Instrumented.systemProperty
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.Date

plugins {
    id("java")
    id("application")
}

application {
    mainClass = "org.myexample.Fun"
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

tasks.named<JavaExec>("run") {
    val dateFormat: DateFormat = SimpleDateFormat("yyyy-MM-dd-HH-mm-ss")
    val timestamp = dateFormat.format(Date())
    val outputPath = "src/main/resources/giraph/output/" + timestamp

    systemProperty("giraph.log.dir", outputPath)
    systemProperty("giraph.job.timestamp", timestamp)

    jvmArgs = listOf(
        "-Dlog4j.configuration=file:${project.buildDir}/resources/main/log4j.properties"
    )
}

tasks.test {
    useJUnitPlatform()
}