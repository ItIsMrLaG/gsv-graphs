import org.apache.tools.ant.taskdefs.Available
import org.gradle.internal.classpath.Instrumented.systemProperty
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.Date

plugins {
    id("java")
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenLocal()
    mavenCentral()
}

dependencies {
    implementation("org.apache.hadoop:hadoop-client:2.7.7")
    implementation("org.apache.giraph:giraph-core:1.3.0-SNAPSHOT") {
        isChanging = true
    }

    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

tasks.test {
    useJUnitPlatform()
}

val GIRAPH_OUTPUT_DIR = File("src/main/resources/giraph/output/")

tasks.jar {
    from(configurations.runtimeClasspath.get().map { if (it.isDirectory) it else zipTree(it) })
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}

val exampleJar by tasks.registering(Jar::class) {
    archiveClassifier.set("example")
    from(sourceSets.main.get().output)
    manifest {
        attributes("Main-Class" to "org.myexample.Fun")
    }
    from(configurations.runtimeClasspath.get().map { if (it.isDirectory) it else zipTree(it) })
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}

tasks.register<JavaExec>("runExample") {
    group = "Execution"

    // exec properties
    dependsOn(exampleJar)
    classpath = files(exampleJar.get().archiveFile) +
               configurations.runtimeClasspath.get()
    mainClass.set("org.myexample.Fun")

    //  log4j properties
    val dateFormat: DateFormat = SimpleDateFormat("yyyy-MM-dd-HH-mm-ss")
    val timestamp = dateFormat.format(Date())
    val outputPath = "$GIRAPH_OUTPUT_DIR/example/" + timestamp
    systemProperty("giraph.log.dir", outputPath)

    jvmArgs = listOf("-Xmx512m")
}
